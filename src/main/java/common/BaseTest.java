package common;

import com.codeborne.selenide.*;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import listeners.DriverEventListener;
import listeners.ResultListener;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestRunner;
import org.testng.annotations.*;
import pages.LoginPage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.codeborne.selenide.Selenide.$$;
import static data.TestData.Url;
import static io.qameta.allure.util.ResultsUtils.firstNonEmpty;

//BrowserPerTest.class
@Listeners({ResultListener.class
        //, MethodInterceptor.class
})
//@Listeners({RerunFailedTests.class})
public class BaseTest extends BaseLibrary {

    //Seconds
    private static final int timeout = 30;

    public static final int loadingTimeout = 90;

    private Locale turkishLocal = new Locale("tr", "TR");
    {
        if (!Locale.getDefault().equals(turkishLocal)) Locale.setDefault(turkishLocal);
    }

    @BeforeSuite(alwaysRun = true)
    public void driverSetUp() {
        String sysProperties = "";
        sysProperties += "Setup started";
        sysProperties += "\nfile.encoding: " + String.format("file.encoding: %s", System.getProperty("file.encoding"));
        sysProperties += "\ndefault charset=" + Charset.defaultCharset();
        sysProperties += "\njava.specification.version" + System.getProperty("java.specification.version");
        sysProperties += "\njava.runtime.version" + System.getProperty("java.runtime.version");
        sysProperties += "\nlocale default:" + Locale.getDefault();
        sysProperties += "\nlocale: " + Locale.getDefault();

        WebDriverRunner.addListener(new DriverEventListener());
        Configuration.baseUrl = (System.getProperty("URL") == null) ? Url : System.getProperty("URL");
        Configuration.browser = (System.getProperty("browser") == null) ? "chrome" : System.getProperty("browser");
        Configuration.browserVersion = System.getProperty("node");
        Configuration.driverManagerEnabled = false;
        Configuration.remote = System.getProperty("hub");
        Configuration.reportsFolder = "test-result/reports";
        Configuration.screenshots = false; //Configuration.remote == null;
        Configuration.savePageSource = false;
        Configuration.collectionsTimeout = timeout * 1000;
        Configuration.holdBrowserOpen = (Configuration.remote == null);
        Configuration.timeout = timeout * 1000;
        Configuration.startMaximized = true;
        Configuration.pollingInterval = 100;
        Configuration.collectionsPollingInterval = 100;
        Configuration.headless = false;
        sysProperties += "\nremote: " + Configuration.remote;
        sysProperties += "\nbrowser: " + Configuration.browser;
        sysProperties += "\nbrowser.version: " + Configuration.browserVersion;
        sysProperties += "\nurl: " + Configuration.baseUrl;
        log.info(sysProperties);
        AllureEnvironmentUtils.createEnvironments();
        AllureEnvironmentUtils.copyCategories();
    }

    @BeforeSuite(enabled = true)
    public void beforeSuite(ITestContext context) {
        if (System.getProperty("buildName") != null && !System.getProperty("buildName").isEmpty())
            context.getSuite().getXmlSuite().setName(System.getProperty("buildName"));
        else
            context.getSuite().getXmlSuite().setName("Suite");

        ((TestRunner) context).getTest().setName("Tests");
    }

    @BeforeMethod(alwaysRun = true, enabled = true)
    public void beforeMethod(ITestContext context, Method test) {
      /*if (Boolean.parseBoolean(System.getProperty("highlightElements", "false")))
        SelenideLogger.addListener("SelenideListener", new SelenideListener());*/

        String testResults = "";
        /*if (test.getDeclaringClass().isAnnotationPresent(io.qameta.allure.Feature.class))
            ((TestRunner) context).getTest().setName(test.getDeclaringClass().getAnnotation(io.qameta.allure.Feature.class).value());
        else
            ((TestRunner) context).getTest().setName(test.getDeclaringClass().getSimpleName());
        */
        String testName = firstNonEmpty(
                test.getDeclaredAnnotation(org.testng.annotations.Test.class).description(),
                test.getName())
                .orElse("Unknown");

        final String desc = test.getDeclaredAnnotation(org.testng.annotations.Test.class).toString();
        Allure.addAttachment("Annotations", desc);
        testResults += "\n///////////////////////////////////////////////////////" + "\n";
        //System.out.println("Total Test Classes: " + ((TestRunner) context).getTestClasses().size());
        testResults += "\nTotal Tests: " + context.getAllTestMethods().length;
        testResults += "\nPassed Tests: " + context.getPassedTests().size();
        testResults += "\nFailed Tests: " + context.getFailedTests().size();
        testResults += "\nSkipped Tests: " + context.getSkippedTests().size();
        testResults += "\nLeft Tests: " + Integer.valueOf(context.getAllTestMethods().length - (context.getPassedTests().size() + context.getFailedTests().size() + context.getSkippedTests().size())).toString() + "\n";
        testResults += "\n///////////////////////////////////////////////////////";
        testResults += "\nTEST CLASS: " + test.getDeclaringClass().getSimpleName() + "\n";
        testResults += "\nTEST: " + testName + "\n";
        testResults += "\nSTATUS: Started: " + "\n";
        testResults += "\nTEST ANNOTATIONS: " + test.getDeclaredAnnotation(org.testng.annotations.Test.class).toString();
        testResults += "\n///////////////////////////////////////////////////////";
        testResults += "\n///////////////////////////////////////////////////////";
        log.info(testResults);

    }

    @AfterMethod(alwaysRun = true, enabled = true)
    public void afterMethod(ITestResult testResult) {
        String testResults = "";
        int SUCCESS = 1;
        int FAILURE = 2;
        int SKIP = 3;
        int SUCCESS_PERCENTAGE_FAILURE = 4;
        int STARTED = 16;
        String result = "unknown";
        switch (testResult.getStatus()) {
            case 1:
                result = "SUCCESS";
                break;
            case 2:
                result = "FAILURE";
                break;
            case 3:
                result = "SKIP";
                break;
            case 4:
                result = "SUCCESS_PERCENTAGE_FAILURE";
                break;
            case 16:
                result = "STARTED";
                break;
        }

        /*if (testResult.getStatus() == ITestResult.FAILURE)
            takeScreenshot();*/
        testResults += "///////////////////////////////////////////////////////";
        testResults += "///////////////////////////////////////////////////////";
        testResults += "\nTEST: " + testResult.getMethod().getMethodName() + "\n";
        testResults += "\nSTATUS: " + result + "\n";
        testResults += "\nDESCRIPTION: " + testResult.getMethod().getDescription() + "\n";
        if (testResult.getThrowable() != null) {
            //testResults += "\nERROR: " + testResult.getThrowable().getMessage() + "\n";
            testResults += "\nERROR: " + getStackTraceAsString(testResult.getThrowable()) + "\n";
        }
        //        System.out.println("Test Annotations: " + testResult.getMethod().getMethod().getDeclaredAnnotation(org.testng.annotations.Test.class).toString());
        testResults += "///////////////////////////////////////////////////////";
        testResults += "///////////////////////////////////////////////////////";

        //Parallelde hatası vermemesi WebDriverRunner.closeWebDriver() eklendi.
        //login da WebDriverRunner.clearBrowserCache(); eklendi
        //Selenide.close();
        //WebDriverRunner.getAndCheckWebDriver().quit();
        log.info(testResults);

        try {
            Selenide.close();
            //WebDriverRunner.getWebDriver().quit();
            //WebDriverRunner.closeWebDriver();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getStackTraceAsString(final Throwable throwable) {
        final StringWriter stringWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

    @AfterClass(alwaysRun = true)
    public void afterClass() {
        Selenide.close();
        log.info("Browser has been closed.");
    }

    public void useFirefox() {
        try {
            FirefoxOptions firefoxOptions = new FirefoxOptions();
            firefoxOptions.setCapability(CapabilityType.VERSION, Configuration.browserVersion);
            //firefoxOptions.setCapability(CapabilityType.PLATFORM_NAME, Platform.ANY);
            //firefoxOptions.setCapability(CapabilityType.BROWSER_NAME, "firefox");
            /*DesiredCapabilities capabilities = DesiredCapabilities.firefox();
            capabilities.setAcceptInsecureCerts(true);
            capabilities.setVersion(Configuration.browserVersion);*/

            EventFiringWebDriver driver;
            if (Configuration.remote == null) {
                WebDriver firefox = new FirefoxDriver();
                driver = new EventFiringWebDriver(firefox).register(new DriverEventListener());
            } else {
                RemoteWebDriver firefox = new RemoteWebDriver(new URL(Configuration.remote), firefoxOptions);
                firefox.setFileDetector(new LocalFileDetector());
                driver = new EventFiringWebDriver(firefox).register(new DriverEventListener());
            }

            /*WebDriver driver = Configuration.remote == null ?
                    new EventFiringWebDriver(new FirefoxDriver()).register(new DriverEventListener())
                    : new EventFiringWebDriver(new RemoteWebDriver(new URL(Configuration.remote), capabilities)).register(new DriverEventListener());*/
            //: new EventFiringWebDriver(new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), capabilities)).register(new DriverEventListener());

            //System.setProperty("webdriver.chrome.driver", "C:\\drivers\\geckodriver.exe");
            /*WebDriver driver = System.getProperty("hub") == null ?
                    new FirefoxDriver()
                    : new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), capabilities);*/
            /*WebDriver driver = System.getProperty("hub") == null ?
                    new FirefoxDriver()
                    : new RemoteWebDriver(firefoxOptions);*/
            //C:\drivers

            if (WebDriverRunner.hasWebDriverStarted())
                WebDriverRunner.getWebDriver().quit();

            WebDriverRunner.setWebDriver(driver);
            /*WebDriverRunner.setWebDriver(new FirefoxDriver(firefoxOptions));
            System.out.println(getCapabilities().getCapability(CapabilityType.BROWSER_VERSION));
            Configuration.remote = System.getProperty("hub");*/

        } catch (Exception e) {
            throw new RuntimeException(String.format("Error new RemoteWebDriver: %s error %s", Configuration.remote, e.getMessage()), e);
        }

        //System.out.println("Browser: " + getCapabilities().getBrowserName());
    }

    /**
     * @param testName
     *
     * @return downloadPath
     */
    public String useChromeBuildMachine(String testName) {
        //C:\Users\optiim\Downloads
        ///home/optiim/Downloads
        //String downloadPath = TestData.docDownloadPathWindows + testName;
        //String downloadPath = System.getProperty("user.dir")+ File.separator + "Downloads" + File.separator + testName;
        //String downloadPath = System.getProperty("user.home") + File.separator + "Downloads" + File.separator + testName;
        /*System.out.println("OS: " + System.getProperty("os.name"));
        String downloadPath = System.getProperty("os.name").contains("Windows")? "C:\\Users\\optiim\\Downloads\\"+testName:"home/optiim/Downloads/"+testName;
        */

        String downloadPath = Paths.get("").toAbsolutePath().normalize().toString() + File.separator + "downloads" + File.separator + testName;
        //deleteAllFiles(downloadPath);
        try {
            Files.walk(Paths.get(downloadPath))
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Assert.assertFalse(Files.exists(Paths.get(downloadPath)), "Download directory should not exists, it will be created by browser" + downloadPath);


        System.out.println("Download Path: " + downloadPath);

        try {
            Map<String, Object> prefs = new HashMap<>();
            prefs.put("download.default_directory", downloadPath);

            ChromeOptions options = new ChromeOptions();
            options.setExperimentalOption("prefs", prefs);
            //options.setCapability(CapabilityType.PLATFORM_NAME, Platform.WINDOWS);
            //options.setCapability(CapabilityType.BROWSER_VERSION, "151");
            //options.addArguments("disable-infobars","true");
            options.setAcceptInsecureCerts(true);
            /*WebDriver driver = Configuration.remote == null ?
                    new ChromeDriver(options)
                    : new RemoteWebDriver(new URL(Configuration.remote), options);*//*
            WebDriver driver = Configuration.remote == null ?
                    new EventFiringWebDriver(new ChromeDriver(options)).register(new DriverEventListener())
                    : new EventFiringWebDriver(new RemoteWebDriver(new URL(Configuration.remote), options)).register(new DriverEventListener());
            */
            WebDriver driver = new EventFiringWebDriver(new ChromeDriver(options)).register(new DriverEventListener());

            WebDriverRunner.setWebDriver(driver);
        } catch (Exception e) {
            //throw new RuntimeException("Invalid 'remote' parameter: " + Configuration.remote, e);
            throw new RuntimeException("useChromeBuildMachine: " + e.getMessage());
        }
        return downloadPath;
    }


    @Step("Test Numarası : {testid} {status} ")
    public void testStatus(String testid, String status) { }

    @Step("{name} : {description}")
    public void step(String name, String description) { }

    @Step("Login")
    public void login() {
        new LoginPage().login();
    }

    @Step("Login")
    @Deprecated
    public void login(String username, String password) {
        new LoginPage().login(username, password);
    }

}