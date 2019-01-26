package drivers;

import com.codeborne.selenide.WebDriverProvider;
import common.BaseTest;
import data.TestData;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.logging.Level;

public class Firefox extends BaseTest implements WebDriverProvider {

    @Override
    public WebDriver createDriver(DesiredCapabilities capabilities) {
        FirefoxProfile profile = new FirefoxProfile();
        /*profile.setPreference("browser.download.folderList", 0);
        //capabilities.setCapability("browser.download.dir", TestData.docDownloadPathWindows);
        profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/excel");
        profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/vnd.ms-excel");
        profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/x-excel");
        profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/x-msexcel");
        capabilities.setCapability(FirefoxDriver.PROFILE, profile);*/
        //return new FirefoxDriver(capabilities);
        FirefoxOptions options = new FirefoxOptions();
        options.addPreference("browser.download.folderList", 1);
        options.addPreference("browser.helperApps.alwaysAsk.force", false);
        options.addPreference("browser.helperApps.neverAsk.openFile", "true");
        options.addPreference("browser.helperApps.neverAsk.saveToDisk", "true");
        options.addPreference("browser.download.manager.showWhenStarting", false);
        options.addPreference("browser.helperApps.neverAsk.saveToDisk", "application/excel");
        options.addPreference("browser.helperApps.neverAsk.saveToDisk", "application/vnd.ms-excel");
        options.addPreference("browser.helperApps.neverAsk.saveToDisk", "application/x-excel");
        options.addPreference("browser.helperApps.neverAsk.saveToDisk", "application/x-msexcel");
        options.addPreference("browser.helperApps.neverAsk.saveToDisk", "application/pdf");
        options.merge(capabilities);
        options.setProfile(profile);
        return new FirefoxDriver(options);
    }
}
