package listeners;

import com.codeborne.selenide.WebDriverRunner;
import common.BaseLibrary;
import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.internal.IResultListener2;

public class ResultListener extends BaseLibrary implements IResultListener2 {

    @Attachment(value = "Error screenshot", type = "image/png")
    public byte[] takeScreenshotOnFail() {
        byte[] bytes = new byte[]{};
        try {
            return ((TakesScreenshot) WebDriverRunner.getWebDriver()).getScreenshotAs(OutputType.BYTES);
        } catch (WebDriverException e) {
            System.out.println("Error takeScreenshot:" + e.getMessage());
        }
        return bytes;
    }

    public void registerDriverEvenListener() {
        //<editor-fold desc="Register DriverEventListener to implement loading pages wait">
        WebDriverRunner.setWebDriver((new EventFiringWebDriver(WebDriverRunner.getWebDriver()).register(new DriverEventListener())));
        //</editor-fold>

    }

    @Override
    public void beforeConfiguration(ITestResult tr) {

    }

    @Override
    public void onConfigurationSuccess(ITestResult itr) {

    }

    @Override
    public void onConfigurationFailure(ITestResult itr) {
        System.out.println("///////////////////////////////////////////////////////");
        System.out.println("Test Configuration Failed: " + itr.getTestName() + "    " + itr.getName());
        System.out.println("///////////////////////////////////////////////////////");

        System.out.println("///////////////////////////////////////////////////////");
        System.out.println("///////////////////////////////////////////////////////");
        System.out.println("Test Configuration Failed: " + itr.getMethod().getMethodName());
        System.out.println("Test Class: " + itr.getInstanceName());
        System.out.println("");
        System.out.println("STATUS: failed");
        System.out.println("");
        if (itr.getThrowable() != null) {
            System.out.println("");
            System.out.println("ERROR: " + itr.getThrowable().getMessage());
        }
        System.out.println("///////////////////////////////////////////////////////");
        System.out.println("///////////////////////////////////////////////////////");

        /*if (WebDriverRunner.hasWebDriverStarted())
            takeScreenshot();*/
    }

    @Override
    public void onConfigurationSkip(ITestResult itr) {

    }

    @Override
    public void onTestStart(ITestResult result) {

    }

    @Override
    public void onTestSuccess(ITestResult result) {

    }

    @Override
    public void onTestFailure(ITestResult result) {
            try {
                takeScreenshotOnFail();
            }
            catch (Exception e){
                System.out.println("Take Screenshot error: " + e);
            }
    }

    @Override
    public void onTestSkipped(ITestResult result) {

    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {

    }

    @Override
    public void onStart(ITestContext context) {

    }

    @Override
    public void onFinish(ITestContext context) {

    }
}
