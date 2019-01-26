package listeners;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedHashTreeMap;
import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.*;
public class MethodInterceptor implements IMethodInterceptor {

    /*String tests= 'json text here'
    return "<input name=\"value\" value=\"${tests}\" class=\"setting-input\" type=\"text\" style='width:100%;'>"*/

    List<ITestNGMethod> tests;
    List<String> dependList;
    ArrayList<Map<String, Object>> finalList = new ArrayList<>();

    @Override
    public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext context) {
        //Document doc = null;
        String testClassName;
        Class<?> testClass;
        String testDescription;

        tests = Arrays.asList(context.getAllTestMethods());

        //try {
        //    doc = createXMLDocument();
        //} catch (ParserConfigurationException e) {
        //    e.printStackTrace();
        //}

        for (ITestNGMethod test : tests) {
            if (!test.isTest() || !test.getEnabled()) continue;

            testClass = test.getTestClass().getRealClass();

            //get package name. Allure @Feature annotation if exist else break cammel style package name to text
            testClassName = testClass.isAnnotationPresent(io.qameta.allure.Feature.class)
                    ? testClass.getAnnotation(io.qameta.allure.Feature.class).value()
                    : testClass.getSimpleName().replaceAll(
                    String.format("%s|%s|%s", "(?<=[A-Z])(?=[A-Z][a-z])", "(?<=[^A-Z])(?=[A-Z])", "(?<=[A-Za-z])(?=[^A-Za-z])")
                    , " ");

            //get test description. remove test name and " - base on "TS0001: my "first" description" to "my first description"
            if (test.getDescription() == null)
                continue;

            String[] descriptionSplit = test.getDescription().split(":", 2);
            testDescription = descriptionSplit.length > 1 ? descriptionSplit[1].trim() : descriptionSplit[0].trim();
            testDescription = testDescription
                    .replace("\"", "")
                    .replace(",", ";")
                    .replace("'", " ");

            //get simple name of tests depends on
            dependList = new LinkedList<>();

            //collect all depends on methods recursively
            getMethodsDependsOn(test);

            //add to final list
            Map<String, Object> element = new LinkedHashTreeMap<>();
            element.put("testClass", testClassName);
            element.put("test", test.getMethodName());
            element.put("description", testDescription);
            element.put("methodsDependsOn", dependList);
            finalList.add(element);
        }

        //convert to json file
        String json = new Gson().toJson(finalList);
        //json = json.replace("\\u0027"," ");
        //json = "String tests='" + json + "'\n\n" + "return \"<input name='value' value='${tests}' class='setting-input' type='text' style='width:100%;'>\"";

        File jsonFile = new File("testsJenkins.json");
        if (jsonFile.exists())
            jsonFile.delete();
        writeToFile(jsonFile, json);

        //context.getCurrentXmlTest().setXmlClasses(null);

        System.exit(0);
        return null;
    }

    private void getMethodsDependsOn(ITestNGMethod test) {
        String[] methodsDependsOn = test.getMethodsDependedUpon();
        for (String m : methodsDependsOn) {

            final String t = m.substring(methodsDependsOn[0].lastIndexOf(".") + 1);
            if (dependList.contains(t))
                continue;
            dependList.add(t);
            getMethodsDependsOn(tests.stream().filter(t1 -> t1.getMethodName().equals(t)).findFirst().get());
        }
    }

    private void writeToFile(File file, String content) {
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

            bufferedWriter.write(content);
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Document createXMLDocument() throws ParserConfigurationException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();

        //<suite thread-count="1" name="Tanimli Test Paketler" parallel="classes" verbose="1">
        Element suiteElement = doc.createElement("suite");
        doc.appendChild(suiteElement);
        suiteElement.setAttribute("thread-count", "1");
        suiteElement.setAttribute("name", "Tanimli Test Paketler");
        suiteElement.setAttribute("parallel", "classes");
        suiteElement.setAttribute("verbose", "1");

        return doc;
    }

    private static void writeDocumentToFile(Document document, File file) throws TransformerException {

        DOMImplementation domImpl = document.getImplementation();
        DocumentType doctype = domImpl.createDocumentType("doctype", "SYSTEM", "http://testng.org/testng-1.0.dtd");

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, doctype.getPublicId());
        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doctype.getSystemId());
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        //transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        //transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        //transformer.setOutputProperty(OutputKeys.STANDALONE, "no");

        // Mark the document as a DOM (XML) source
        DOMSource source = new DOMSource(document);

        // Say where we want the XML to go
        StreamResult result = new StreamResult(file);

        // Write the XML to file
        transformer.transform(source, result);
    }

    private void appendTest(Document doc, String name, String packageName) {
        Element testElement = doc.createElement("test");
        testElement.setAttribute("name", name);
        doc.getElementsByTagName("suite").item(0).appendChild(testElement);

        Element packagesElement = doc.createElement("packages");
        testElement.appendChild(packagesElement);

        Element packageElement = doc.createElement("package");
        packageElement.setAttribute("name", packageName);
        packagesElement.appendChild(packageElement);
}
}