package common;

import com.codeborne.selenide.*;
import com.codeborne.selenide.ex.InvalidStateException;
import data.TestData;
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static common.BaseTest.loadingTimeout;
import static org.apache.commons.io.FileUtils.deleteDirectory;
import static org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOfElementLocated;

public class BaseLibrary extends ElementsContainer {


    protected static final Logger log = Logger.getLogger(BaseLibrary.class.getName());
    protected static String winHandleBefore = null;
    protected static String uploadPath = null;
    protected static String downloadPath = null;
    private static String browserName = null;
    //private long waitForLoading;
    //private int doWaitLoading;
    private boolean doNotWaitLoading = false;
    public DateTimeFormatter dateFormatWithSecond = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    public DateTimeFormatter dateFormatWithMinute = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    public DateTimeFormatter dateFormatWithHour = DateTimeFormatter.ofPattern("dd.MM.yyyy HH");
    public DateTimeFormatter dateFormatWithDate = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static void killProcess() {

        Runtime rt = Runtime.getRuntime();
        try {
            rt.exec("taskkill /f /im " + "chrome.exe");
            rt.exec("taskkill /f /im " + "chromedriver.exe");
            rt.exec("taskkill /f /im " + "conhost.exe");
            rt.exec("taskkill /f /im " + "firefox.exe");
            rt.exec("taskkill /f /im " + "geckodriver.exe");
            rt.exec("taskkill /f /im " + "iexplore.exe");
            rt.exec("taskkill /f /im " + "iedriver.server");
            rt.exec("taskkill /f /im " + "iedriver.server64");
            //rt.exec("taskkill /f /im " + "WerFault");
            //rt.exec("taskkill /f /im " + "AcroRd32");
            //rt.exec("taskkill /f /im " + "Excel");
        } catch (IOException e) {
            System.out.println("Processler Kill Edilememdi!!!");
        }
    }

    public static String getPCUsername() {
        return System.getProperty("user.name");
    }

    @Step("Browserdaki Cookieleri temizle")
    public void clearCookies() {
        try {
            Selenide.clearBrowserLocalStorage();
            Selenide.clearBrowserCookies();
        } catch (Exception e) {
            log.info("Error clearBrowserLocalStorage and clearBrowserCookies: " + e.getMessage());
        }
    }

    //<editor-fold desc="Allure screenshooter">
    @Attachment(value = "Page screenshot", type = "image/png")
    public byte[] takeScreenshot() {
        byte[] bytes = new byte[]{};
        try {
            //System.out.println("Screenshot will be taken");
            bytes = ((TakesScreenshot) WebDriverRunner.getWebDriver()).getScreenshotAs(OutputType.BYTES);
            //System.out.println("Screenshot has been taken");
        } catch (WebDriverException e) {
            log.warning("Take screenshot error:" + e.getMessage());
        }
        return bytes;
    }

    @Attachment(value = "Page screenshot", type = "image/png")
    public byte[] takeScreenshot(WebDriver driver) {
        byte[] bytes = new byte[]{};
        try {
            bytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        } catch (WebDriverException e) {
            log.warning("Error takeScreenshot:" + e.getMessage());
        }
        return bytes;
    }

    @Attachment(value = "Element screenshot", type = "image/png")
    public byte[] takeScreenshot(SelenideElement element) {
        byte[] bytes = new byte[]{};
        try {
            bytes = Files.readAllBytes(element.screenshot().toPath());
        } catch (Exception e) {
            log.warning("Take screenshot of element error. Full page screenshot will be try on.");
            takeScreenshot();
        }
        return bytes;
    }

    public void waitForLoadingJS(WebDriver driver, long timeoutSec) {
        AtomicInteger isJsFinished = new AtomicInteger();
        AtomicInteger isAjaxFinished = new AtomicInteger();
        final int jsCompleteCheck = 3;
        new WebDriverWait(driver, timeoutSec, 10).until(d ->
        {
            JavascriptExecutor js = (JavascriptExecutor) d;
            try {
                if ((boolean) js.executeScript("return (document.readyState == \"complete\" || document.readyState == \"interactive\")"))
                    isJsFinished.set(jsCompleteCheck);
                //Allure.addAttachment("Şu kadar süre loading beklendi: " + timeoutSec, "");
            } catch (Exception e) {
                isJsFinished.getAndIncrement();
                //System.out.println("Load: isJsFinished error: " + e.getMessage());
            }

            try {
                if ((boolean) js.executeScript("var result = true; try { result = (typeof jQuery != 'undefined') ? jQuery.active == 0 : true } catch (e) {}; return result;"))
                    isAjaxFinished.set(jsCompleteCheck);
            } catch (Exception e) {
                isAjaxFinished.getAndIncrement();
            }

            boolean loaderVisible = false;
            try {
                //div[contains(@class,'ui-dialog') and .//*[@class='loading'] and contains(@style,'visibility: visible')]
                //blockBackgroundDialogId
                //progressBarDialog
                //isLoaderVisible = (boolean) js.executeScript("return document.querySelectorAll('div[id*=\"bekleyiniz\"][style*=\"visibility: visible\"]').length == 0");
                //isLoaderVisible = (boolean) js.executeScript("return document.querySelectorAll('.loading[style*=\"visibility: visible\"]').length == 0 && document.querySelectorAll('#blockBackgroundDialogId[style*=\"visibility: visible\"]').length == 0");
                loaderVisible = (boolean) js.executeScript(
                        "var visible = false;\n" +
                                "document.querySelectorAll('.loading').forEach(function(element) { \n" +
                                "\tif(window.getComputedStyle(element.parentNode, null).getPropertyValue('display')!='none' && window.getComputedStyle(element, null).getPropertyValue('visibility')=='visible'){\n" +
                                "\t\tvisible = true;\n" +
                                "\t\treturn;\n" +
                                "\t}\n" +
                                "});\n" +
                                "return visible;");
                if (loaderVisible){
                    isJsFinished.set(0);
                    isAjaxFinished.set(0);
                }
            } catch (Exception e) {
                System.out.println("Load: isLoaderVisible error: " + Arrays.toString(e.getStackTrace()));
            }

            return isJsFinished.get() >= jsCompleteCheck && isAjaxFinished.get() >= jsCompleteCheck && !loaderVisible ;
        });
    }

    public void waitForLoadingJS(WebDriver driver) {
        waitForLoadingJS(driver, loadingTimeout);
    }

    public void maximazeBrowser() {
        try {
            if (Configuration.browserSize != null) {
                try {
                    String[] size = Configuration.browserSize.split("x");
                    int width = Integer.parseInt(size[0]);
                    int height = Integer.parseInt(size[1]);
                    Dimension browserSize = new Dimension(width, height);
                    WebDriverRunner.getWebDriver().manage().window().setSize(browserSize);
//                    System.out.println("custom maximize()");
                } catch (NumberFormatException e) {
                    WebDriverRunner.getWebDriver().manage().window().maximize();
//                    System.out.println("manage().window().maximize()");
                }
            } else {
                try {
                    WebDriverRunner.getWebDriver().manage().window().maximize();
                    //System.out.println("manage().window().maximize()");
                }catch (Exception e) {
                    System.out.println("maximize:" + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("maximize:" + e.getMessage());
        }
    }

    public String getTimeFromText(String text) {
        String result = "";
        String regex = "([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find())
            result = matcher.group();

        return result;
    }

    public String getDateTimeFromText(String text) {
        String result = "";
        String regex = "([0-3]?[0-9].[0-3]?[0-9].(?:[0-9]{2})?[0-9]{2}) (([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9])";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find())
            result = matcher.group();

        return result;
    }

    @Deprecated
    private static String[] getDateTime(String text) {
        int count=0;
        String[] allMatches = new String[2];
        Matcher m = Pattern.compile("\\d{2}.\\d{2}.\\d{4} \\d{2}:\\d{2}:\\d{2}").matcher(text);
        while (m.find()) {
            allMatches[count] = m.group();
            count++;
        }
        return allMatches;
    }

    /**
     * JavaSctipt ile click yapılır
     *
     * @param element
     */
    public void clickJs(SelenideElement element) {
        executeJavaScript("arguments[0].click();", element);
        waitForLoadingJS(WebDriverRunner.getWebDriver());
    }

    /**
     * JavaSctipt ile click yapılır
     *
     * @param element
     */
    @Step("{stepDescription}")
    public void clickJs(SelenideElement element, String stepDescription) {
        executeJavaScript("arguments[0].click();", element);
    }

    /**
     * JavaSctipt ile click yapılır
     *
     * @param element
     */
    public void clickJs(WebElement element) {
        executeJavaScript("arguments[0].click();", element);
    }

    /**
     * if input not visible, otherwise use selenide setselected
     */
    public void checkboxSelect(SelenideElement element, boolean setSelected) {
        element = element.getTagName().equalsIgnoreCase("input") ? element : element.$("input");
        if (element.isSelected() ^ setSelected) {
            if (element.getAttribute("readonly") != null)
                throw new InvalidStateException("Cannot change value of readonly element");
            clickJs(element);
        }
    }

    public void selectOptionJs(SelenideElement element, String option){
        boolean selected = executeJavaScript("for (var i = 0; i < arguments[0].options.length; i++) {if (arguments[0].options[i].text === arguments[1]){arguments[0].value=arguments[0].options[i].value; return true}} return false;"
                , element
                , option);
        Assert.assertTrue(selected, String.format("%s select element '%s' option not selected",element.getSearchCriteria(), option));

    }

//    private String closeAlertAndGetItsText() {
//        try {
//            Alert alert = driver.switchTo().alert();
//            String alertText = alert.getText();
//            if (acceptNextAlert) {
//                alert.accept();
//            } else {
//                alert.dismiss();
//            }
//            return alertText;
//        } finally {
//            acceptNextAlert = true;
//        }
//    }

    //Dosya ekler
    public void uploadFile(SelenideElement element, String pathToFile) {
        try {
            element.toWebElement().sendKeys(pathToFile);
            log.info("Dosya yüklemeye başlandı.");
        } catch (Exception e) {
            log.info("Dosya yükleme başarısız. : " + e);
            throw new RuntimeException(e);
        }
    }

    //Random numara üretir.
    public String createRandomNumber(int length) {
        Random r = new Random();
        List<Integer> digits = new ArrayList<>();
        String number = "";

        for (int i = 0; i < length - 1; i++) {
            digits.add(i);
        }

        for (int i = length - 1; i > 0; i--) {
            int randomDigit = r.nextInt(i);
            number += digits.get(randomDigit);
            digits.remove(randomDigit);
        }

        number = "1" + number;

        return number;
    }

    //Random text üretir.
    public String createRandomText(int textSize) {
        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < textSize; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }

        return sb.toString();
    }

    //Random text üretir.
    public String createRandomTextWithLineBreaks(int textSize) {
        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        int lineBreakEveryChar = 10;
        int i = 0;
        while (sb.length() < textSize){
            if (i!=0 && i%lineBreakEveryChar==0)
                sb.append(' ');
            sb.append(chars[random.nextInt(chars.length)]);
            i++;
        }
        return sb.toString();
    }

    //yyyyMMddHHmmss formatına göre sysdate alır.
    public String getSysDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime now = LocalDateTime.now();
//        System.out.println(dtf.format(now)); // 2016/11/16 12:08:43

        return dtf.format(now);
    }

    public String getDatePlusDay(int plusDay) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDateTime now = LocalDateTime.now();
//        System.out.println(dtf.format(now)); // 2016/11/16 12:08:43

        return dtf.format(now.plusDays(plusDay));
    }

    //dd.MM.yyyy HH:mm:ss formatına göre sysdate alır.
    public String getDateTime() {
        return DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").format(LocalDateTime.now());
    }

    //dd.MM.yyyy formatına göre sysdate alır.
    public String getSysDateForKis() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDateTime now = LocalDateTime.now();
//        System.out.println(dtf.format(now)); // 2016/11/16 12:08:43

        return dtf.format(now);
    }


    //dd.MM.yyyy HH formatına göre sysdate alır.
    public String getSysDateForTarihSaat() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH");
        LocalDateTime now = LocalDateTime.now();
//        System.out.println(dtf.format(now)); // 2016/11/16 12:08:43

        return dtf.format(now);
    }
    //dd.MM.yyyy formatına göre / koyarak sysdate alır.
    public String getSysDateForKis2() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDateTime now = LocalDateTime.now();
//        System.out.println(dtf.format(now)); // 2016/11/16 12:08:43

        return dtf.format(now);
    }


    public String getSysNextDateRequest(int day, int month, int year) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH,day);
        cal.add(Calendar.MONTH,month);
        cal.add(Calendar.YEAR, year);
        return dateFormat.format(cal.getTime());

    }

    //Bugün tarihinden sonraki bir yıl sonrayı alır.
    public String getAfterSysYear() {
        String untildate = getSysDateForKis();// can take any date in current
        // format
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 1);
        return dateFormat.format(cal.getTime());

    }


    //Günün tarihinden sonraki bir tarihi alır.
    public String getAfterSysDate(int i) throws ParseException {
        String untildate = getSysDateForKis();// can take any date in current
        // format
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateFormat.parse(untildate));
        cal.add(Calendar.DATE, i);
        String convertedDate = dateFormat.format(cal.getTime());
//        System.out.println("Gunun tarihinden 10 gun sonrasi: " + convertedDate);

        return convertedDate;

    }

    // sistem yılını alır
    public String getSysYear() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy");
        LocalDateTime now = LocalDateTime.now();
//        System.out.println(dtf.format(now));

        return dtf.format(now);
    }

    // sistem ayını alır
    public String getSysMonth() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM");
        LocalDateTime now = LocalDateTime.now();
//        System.out.println(dtf.format(now));

        return dtf.format(now);
    }

    //Dosyanın bilgisayara inip inmediğini kontrol eder.
    public boolean isFileDownloaded(String downloadPath, String fileName) {
        boolean flag = false;
        File dir = new File(downloadPath);
        File[] dir_contents = dir.listFiles();

        for (int i = 0; i < dir_contents.length; i++) {
            if (dir_contents[i].getName().equals(fileName))
                return flag = true;
        }

        return flag;
    }

    //Bilgisayara indirilen dosyaları siler.
    public boolean deleteFile(String pathToFile) {
        try {
            File file = new File(pathToFile);

            if (file.delete()) {
//                System.out.println(file.getName() + " dosyasi silindi.");
//                LogPASS(file.getName() + " dosyasi silindi.");
            } else {
//                System.out.println("Dosya silme islemi basarisiz.");
                //logger.error("Error : Dosya silme islemi basarisiz.");
//                LogFAIL("Error : Dosya silme islemi basarisiz. ");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void deleteAllFiles(File path) {
        if( path.exists() ) {
            File[] files = path.listFiles();
            for (File file : files) {
                file.delete();
            }
        }
    }

    public String getFileExtension(String file) {
        if(file.lastIndexOf(".") != -1 && file.lastIndexOf(".") != 0)
            return file.substring(file.lastIndexOf(".")+1);
        else return "";
    }

    public String renameFile(String filePath, String newName){
        File file = new File(filePath);

        if (!file.exists() || Objects.requireNonNull(file.listFiles()).length == 0)
            return "";

        File oldFile = Objects.requireNonNull(file.listFiles())[0];
        String extension = getFileExtension(oldFile.getAbsolutePath());
        File newFile = new File(filePath + File.separator + newName + "." + extension);
        if(oldFile.renameTo(newFile)){
            System.out.println("File rename success");
            return newFile.getAbsolutePath();
        }else{
            System.out.println("File rename failed");
            return "";
        }
    }


    //Random tc yaratır mernis sorgusundan geçecek şekilde.
    public String createMernisTCKN() {
        Vector<Integer> array = new Vector<>();
        Random randomGenerator = new Random();
        array.add(new Integer(1 + randomGenerator.nextInt(9)));

        for (int i = 1; i < 9; i++) array.add(randomGenerator.nextInt(10));

        int t1 = 0;
        for (int i = 0; i < 9; i += 2) t1 += array.elementAt(i);

        int t2 = 0;
        for (int i = 1; i < 8; i += 2) t2 += array.elementAt(i);

        int x = ((t1 * 7) - t2) % 10;
        array.add(new Integer(x));

        x = 0;
        for (int i = 0; i < 10; i++) x += array.elementAt(i);

        x = x % 10;
        array.add(new Integer(x));

        String res = "";
        for (int i = 0; i < 11; i++) res = res + Integer.toString(array.elementAt(i));

//        System.out.println("Olusturulan TC Kimlik No:" + res);

        return res;
    }

    //Textin ilk harfini büyük yapar.
    public String toUpperCaseFirst(String text) {
        char ilkHarf = Character.toUpperCase(text.charAt(0));
        text = ilkHarf + text.substring(1);
        return text;
    }

    //Texti split edip : 'dan sonrasını alır.
    public String splitString(String str) {
        String[] parts = str.split(": ");// "004: 034556"
        String part1 = parts[0]; // 004

        return parts[1];
    }

    /* columnInput ile gönderilen değer, columnIndex ile belirtilen sütunda
       aratılır. columnInput olan satırın elementini döndürür. columnInput araması tüm sayfalarda yapılır.*/
    protected WebElement findElementOnTableByColumnInputInAllPages(SelenideElement byTable, int columnIndex, String columnInput) {
        SelenideElement next = $(("[class='ui-paginator-next ui-state-default ui-corner-all']"));
        // SelenideElement nextDisable = $(("[class*='ui-state-disabled']"));

        SelenideElement element = null;
        while (element == null) {
            element = findElementOnTableByColumnInput(byTable, columnIndex, columnInput);
            if (element == null) {
                if (next.isDisplayed() == false) {
//                    System.out.println("Element tablodaki hiç bir sayfada bulunamadı.");
                    return null; // Element hiç bir sayfada bulunamazsa null döner.
                }
                next.click();
            }
        }
//        System.out.println("Tabloda element bulundu.");
        return element;
    }

    /*  columnInput ile gönderilen değer, columnIndex ile belirtilen sütunda
       aratılır. columnInput olan satırın elementini döndürür. */
    protected WebElement findElementOnTableByColumnInputO(SelenideElement byTable, int columnIndex, String columnInput) {
        WebElement table = $(byTable).$(By.tagName("tbody"));
        int rowCount = 0;

        List<WebElement> allRows = table.findElements(By.tagName("tr"));
        rowCount = allRows.size();
        WebElement elem = null;
        for (WebElement row : allRows) {
            elem = row.findElements(By.tagName("td")).get(columnIndex - 1);
            if (elem.getText().equals(columnInput)) {
                return elem;
            }
        }
        return null;
    }

    protected SelenideElement findElementOnTableByColumnInput(SelenideElement byTable, int columnIndex, String columnInput) {
        int rowCount = 0;

        ElementsCollection allRows = $(byTable).$(By.tagName("tbody")).$$(By.tagName("tr"));
        rowCount = allRows.size();
        if (rowCount == 0)
            return null;

        SelenideElement elem = null;
        for (SelenideElement row : allRows) {
            elem = row.$$(By.tagName("td")).get(columnIndex - 1).shouldBe(visible);
            if (elem.text().equals(columnInput)) {
                return elem;
            }
        }
        return null;
    }

    public String getNumberFromText(By by) {
        String x = WebDriverRunner.getWebDriver().findElement(by).getText();
        Pattern y = Pattern.compile("\\d+");
        Matcher m = y.matcher(x);
        m.find();
        String number = m.group();
//        System.out.println(number);

        return number;
    }

    public String getNumberFromText(String text) {
        Pattern y = Pattern.compile("\\d+");
        Matcher m = y.matcher(text);
        m.find();
        //        System.out.println("Get number from text: \"" + text + "\" number: " + number);
        return m.group();
    }

    // Store the current window handle
    public String windowHandleBefore() {
        winHandleBefore = WebDriverRunner.getWebDriver().getWindowHandle();
        return winHandleBefore;
    }

    // Perform the click operation that opens new window
    // Switch to new window opened
    public void switchToNewWindow() throws InterruptedException {
        Thread.sleep(6000);
        for (String winHandle : WebDriverRunner.getWebDriver().getWindowHandles()) {
            WebDriverRunner.getWebDriver().switchTo().window(winHandle);
        }
    }

    // Switch to default window
    public void switchToDefaultWindow() throws InterruptedException {
        Thread.sleep(3000);
        WebDriverRunner.getWebDriver().close();
        // driver.switchTo().defaultContent();
        WebDriverRunner.getWebDriver().switchTo().window(winHandleBefore);
    }

    public void closeNewWindow() {
        WebDriverRunner.getWebDriver().close();
    }

    //region Neden burada?
    public String cssSE(String element, String attribute, String startsWith, String endsWith) {

        if (element != "" || element == null) {
            return "[" + attribute + "^=''][" + attribute + "$='']";
        } else {
            return element + "[" + attribute + "^=''][" + attribute + "$='']";
        }

    }

    @Step("\"{element}\" alanının değeri \"{value}\" olmalı.")
    public void alanDegeriKontrolEt(SelenideElement element, String value, boolean shouldHaveValue, boolean exactText) {
        if (shouldHaveValue == true) {
            if (exactText == true)
                element.shouldHave(exactValue(value));
            else {
                String _value = element.getValue();
                Assert.assertEquals(_value.contains(value), true);
            }
        } else {
            if (exactText == true)
                element.shouldNotHave(exactValue(value));

            else {
                String _value = element.getValue();
                Assert.assertEquals(_value.contains(value), false);
            }
        }
    }


    public boolean findElementOnTableAllPages(String form, SelenideElement element) {

        SelenideElement next = $(("[id='" + form + "'] [class='ui-paginator-next ui-state-default ui-corner-all']"));

        boolean status = false;
        while (status == false) {
            status = element.isDisplayed();
            if (status == false) {
                if (next.isDisplayed() == false) {
                    //System.out.println("Element hiç bir sayfada bulunamadı.");
                    return status;
                }
                next.click();
            }
        }
        //System.out.println("Element bulundu.");
        return status;
    }

    //Bilgisayarda uzantısını verdiğiniz klasordeki dosyalardan gönderdiğiniz ismi içinde içeriyorsa o dosyayı siler.
    @Step("Gönderilen klasöreki dosyayı siler. Path : \"{path}\" \n Filename : \"{fileName}\" ")
    public boolean deleteFile(String path, String fileName) throws IOException {

        boolean flag = false;
        File directory = new File(path);
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (null != files) {
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        deleteDirectory(files[i]);
                        flag = true;
                    } else {
                        if (files[i].getName().contains(fileName)) {
                            files[i].delete();
                            //System.out.println("dosya silindi");
                            flag = true;
                        } else
                            System.out.println("Klasörde istenilen isimde dosya bulunamadı.");
                    }
                }
            } else
                System.out.println("Klasör boş.");
        }
        return flag;
    }

    // İşlem penceresi kapatma onay - popup
    @Step("Popup : İşlem penceresi kapatma onayi: \"{secim}\" ")
    public void islemPenceresiKapatmaOnayiPopup(String secim) {

        SelenideElement btnKapat = $(By.id("kapatButton"));
        SelenideElement btnIptal = $(By.id("kapatButton"));
        SelenideElement islemPenceresiKapatmaPopup = $(By.id("closeWindowConfirm"));

        for (int i = 0; i < 10; i++) {
            if (islemPenceresiKapatmaPopup.exists())
                break;
            sleep(1000);
        }

        if (islemPenceresiKapatmaPopup.isDisplayed()) {
            switch (secim) {
                case "Kapat":
                    btnKapat.click();
                    break;
                case "İptal":
                    btnIptal.click();
                    break;
            }
        }

    }

    @Step("Silme Onayı: Kaydı silmek istediğinize emin misiniz?: {secim}")
    public void silmeOnayiEvrakSilPopup(String secim) {

        SelenideElement btnEvet = $(By.id("mainPreviewForm:evrakSilEvetButton"));
        SelenideElement btnHayir = $(By.id("mainPreviewForm:evrakSilHayirButton"));

        switch (secim) {
            case "Evet":
                btnEvet.click();
                break;
            case "Hayır":
                btnHayir.click();
                break;
        }
    }

    // İşlem penceresi kapatma onay - popup
    @Step("Popup : İşlem penceresi kaydet: {secim}")
    public void islemPenceresiKaydetPopup(String secim) {

        SelenideElement islemKaydetPopup = $(By.id("saveOnCloseWindowConfirm"));
        SelenideElement btnEvet = $(By.id("kapatKaydetEvetButton"));
        SelenideElement btnHayir = $(By.id("kapatKaydetHayirButton"));
        SelenideElement btnIptal = $(By.id("kapatKaydetIptalButton"));

        switch (secim) {
            case "Evet":
                btnEvet.pressEnter();
                break;
            case "Hayır":
                btnHayir.pressEnter();
                break;
            case "İptal":
                btnIptal.click();
                break;
        }
    }

    //endregion

    @Step("Popup İşlem Onayı: {secim}")
    public void islemOnayi(String secim) {

        SelenideElement btnIslemOnayiEvet = $(By.id("baseConfirmationDialog:confirmButton"));
        SelenideElement btnIslemOnayiHayir = $(By.id("baseConfirmationDialog:baseConfirmationDialogCancelButton"));
        btnIslemOnayiEvet.shouldBe(visible);

        switch (secim) {
            case "Evet":
                clickJs(btnIslemOnayiEvet);
                break;
            case "Hayır":
                clickJs(btnIslemOnayiHayir);
                break;
        }
    }

    @Step("\"{fileName}\" isimli dosya silindi")
    public BaseLibrary deleteSpecificFile(String fileName) {

        File folder = new File("C://users//" + System.getProperty("user.name") + "//Downloads//");
        final File[] files = folder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(final File dir,
                                  final String name) {
                return name.matches("Rapor_.*\\.");
            }
        });
        for (File file1 : files) {
            if (!file1.delete()) {
                System.err.println("Dosya silinemedi: " + file1.getAbsolutePath());
            }
        }

        return this;
    }

    //Dosyanın bilgisayara inip inmediğini kontrol eder.
    @Step("Dosya arama. Path: \"{downloadPath}\" \nFilename: \"{fileName}\"")
    public boolean searchDownloadedFileWithName(String downloadPath, String fileName) {

        File dir = new File(downloadPath);
        File[] dir_contents = dir.listFiles();
        Pattern y = Pattern.compile("[^0-9]");
        String s;

        for (int i = 0; i < dir_contents.length; i++) {
            String file = dir_contents[i].getName();
            s = "";
            Matcher m = y.matcher(file);
            while (m.find()) {
                s = s + m.group();
            }

            if (s.contains(fileName)) {
                //System.out.println("dosya indirilmiştir.");
                Allure.addAttachment(dir_contents[i].getName(), "raporu indirilmiştir");
                return true;
            }
        }

        return false;
    }

    @Step("Dosya arama. Path: \"{path}\" \nFile Pattern: \"{fileNamePattern}\"")
    public boolean searchFile(String path, String fileNamePattern) {
        File[] files = new File(path).listFiles();
        Assert.assertNotNull(files, String.format("%s klasörde dosyalar bulunmalı", path));
        for (File file:files) {
            if (Pattern.compile(fileNamePattern).matcher(file.getName()).matches())
                return true;
        }
        return false;
    }

    @Step("Dosya arama. Path: \"{path}\" \nFile Pattern:\"{fileNamePattern}\"")
    public String getFileAbsolutePath(String path, String fileNamePattern) {
        File[] files = new File(path).listFiles();
        for (File file:files) {
            if (Pattern.compile(fileNamePattern).matcher(file.getName()).matches())
                return file.getAbsolutePath();
        }
        return null;
    }

    @Step("Dosya arama. Path :  \"{path}\" \nFile Pattern: \"{fileNamePattern}\"")
    public String getFileName(String path, String fileNamePattern) {
        File[] files = new File(path).listFiles();
        Assert.assertNotNull(files, String.format("%s klasörde dosyalar bulunmalı", path));
        for (File file:files) {
            if (Pattern.compile(fileNamePattern).matcher(file.getName()).matches())
                return file.getAbsolutePath();
        }
        return "";
    }

    @Step("Dosya arama. Path :  \"{path}\" \nFile Pattern: \"{fileNamePattern}\"")
    public File findFile(String path, String fileNamePattern) {
        File[] files = new File(path).listFiles();
        Assert.assertNotNull(files, String.format("%s klasörde dosyalar bulunmalı", path));
        for (File file:files) {
            if (Pattern.compile(fileNamePattern).matcher(file.getName()).matches())
                return file;
        }
        return null;
    }

    @Step("İndirilen dosyayı ara")
    public String getDownloadedFileAbsolutePath(String filePath, String pattern) {
        String path;
        for (int i = 0; i < Configuration.timeout/1000; i++) {
            path = getFileAbsolutePath(filePath, pattern);
            if (path != null)
                return path;
            sleep(1000);
        }
        return null;
    }

    @Step("İndirilen Rapor dosyayı ara")
    public boolean indirilenRaporuBul(String filePath, String fileExtension) {
        for (int i = 0; i < Configuration.timeout/1000; i++) {
            if (searchFile(filePath,"Rapor_[0-9]+.*\\." + fileExtension))
                return true;
            sleep(1000);
        }
        Allure.addAttachment("Rapor bulunamadı","Rapor_..." + fileExtension + " file " + filePath + " klasörde bulunamadı.");
        return false;
    }

    @Step("İndirilen Rapor dosyayı bul ve isim değiştir")
    public boolean indirilenRaporuBulveIsimDegistir(String filePath, String fileExtension, String yeniIsim) {
        for (int i = 0; i < Configuration.timeout/1000; i++) {
            File file = findFile(filePath,"Rapor_[0-9]+.*\\." + fileExtension);
            if (file!=null){
                return file.renameTo(new File(filePath + File.separator + yeniIsim + "." + fileExtension));
            }
            sleep(1000);
        }
        Allure.addAttachment("Rapor bulunamadı","Rapor_..." + fileExtension + " file " + filePath + " klasörde bulunamadı.");
        return false;
    }


    //region Capabilities
    public String getOS() {
        Capabilities caps = getCapabilities();
        String platformName = caps.getCapability("platformName").toString();
        //System.out.println("Operation System: " + platformName);
        return platformName;
    }

    public String getBrowserName() {
        //System.out.println("Browser Name : " + browserName);
        return getCapabilities().getBrowserName();
    }

    @Deprecated
    private String setDocPath() {

        // Get Browser name and version.
        Capabilities caps = ((RemoteWebDriver) WebDriverRunner.getWebDriver()).getCapabilities();
        // String browserName = caps.getBrowserName();
        // String browserVersion = caps.getVersion();
        Platform operationSystem = caps.getPlatform();
        //System.out.println("Operation System: " + operationSystem.name());

        if (operationSystem.is(Platform.WINDOWS)) {
            uploadPath = "C:\\TestAutomation\\comPOCV1\\documents\\";
        } else if (operationSystem.is(Platform.XP)) {
            uploadPath = "C:\\TestAutomation\\comPOCV1\\documents\\";
        } else if (operationSystem.is(Platform.LINUX)) {
            //TODO: Linux pathi verilecek
            uploadPath = System.getProperty("user.name") + "/comPOCV1/documents";
        } else if (operationSystem.is(Platform.MAC)) {
            //TODO: Mac pathi verilecek
            uploadPath = System.getProperty("user.name") + "/comPOCV1/documents";
        }
        //System.out.println("File path: " + uploadPath);
        return uploadPath;
    }

    public String getUploadPath() {
        Capabilities caps = getCapabilities();
        Platform operationSystem = caps.getPlatform();

        //String uploadPath = "";
        if (operationSystem.is(Platform.WINDOWS) || operationSystem.is(Platform.XP))
            uploadPath = TestData.docPathWindows;
        log.info("Upload file path: " + uploadPath);

        return uploadPath;
    }

    public String getDownloadPath() {
        Capabilities caps = getCapabilities();
        Platform operationSystem = caps.getPlatform();
        //String downloadPath = "";
        if (operationSystem.is(Platform.WINDOWS) || operationSystem.is(Platform.XP))
            downloadPath = TestData.docDownloadPathWindows;
        log.info("Downloads file path: " + downloadPath);

        return downloadPath;
    }

    /**
     * Get Capabilities of EventFiringWebDriver
     */
    public Capabilities getCapabilities() {
        //This 'if' handle case when LoggingWebDriver.driver is instanceof EventFiringWebDriver .
        //In this case RemoteWebDriver is field within EventFiringWebDriver while EventFiringWebDriver don't support getting Capabilities
        WebDriver driver = WebDriverRunner.getWebDriver();

        if (driver instanceof EventFiringWebDriver
                && ((EventFiringWebDriver) driver).getWrappedDriver() instanceof HasCapabilities) {
            return ((HasCapabilities) ((EventFiringWebDriver) driver)
                    .getWrappedDriver()).getCapabilities();
        } else if (driver instanceof HasCapabilities) {
            return ((HasCapabilities) driver).getCapabilities();
        }
        throw new UnsupportedOperationException(
                "Underlying driver instance does not support capabilities");
    }

    public RemoteWebDriver getDriverAsRemoteWebDriver() {
        //This 'if' handle case when LoggingWebDriver.driver is instanceof EventFiringWebDriver .
        //In this case RemoteWebDriver is field within EventFiringWebDriver while EventFiringWebDriver don't support getting Capabilities
        WebDriver driver = WebDriverRunner.getWebDriver();

        if (driver instanceof EventFiringWebDriver
                && ((EventFiringWebDriver) driver).getWrappedDriver() instanceof HasCapabilities) {
            return (RemoteWebDriver) ((EventFiringWebDriver) driver)
                    .getWrappedDriver();
        } else if (driver instanceof HasCapabilities) {
            return (RemoteWebDriver) driver;
        }
        throw new UnsupportedOperationException(
                "Underlying driver instance does not support capabilities");
    }

    public void waitForFileUploading(WebDriver driver, long timeoutSec) {

        try {

            //System.out.println("Count:" + driver.findElements(By.cssSelector("div[style*='display: block;'] .template-upload")).size());
            new WebDriverWait(driver, timeoutSec, 10).
                    until(ExpectedConditions.invisibilityOfAllElements(driver.findElements(
                            By.className("template-upload"))));

        } catch (Exception e) {
            System.out.println("File uploading error: " + e.getMessage());
        }

    }

    private String setValueByJs(SelenideElement element, String text) {
        return executeJavaScript(
                "return (function(webelement, text) {" +
                        "if (webelement.getAttribute('readonly') != undefined) return 'Cannot change value of readonly element';" +
                        "if (webelement.getAttribute('disabled') != undefined) return 'Cannot change value of disabled element';" +
                        "webelement.focus();" +
                        "var maxlength = webelement.getAttribute('maxlength') == null ? -1 : parseInt(webelement.getAttribute('maxlength'));" +
                        "webelement.value = " +
                        "maxlength == -1 ? text " +
                        ": text.length <= maxlength ? text " +
                        ": text.substring(0, maxlength);" +
                        "return null;" +
                        "})(arguments[0], arguments[1]);",
                element, text);
    }

    //endregion


}
