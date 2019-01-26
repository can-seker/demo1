package drivers;

import com.codeborne.selenide.WebDriverProvider;
import common.BaseTest;
import data.TestData;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.HashMap;
import java.util.Map;
public class Chrome extends BaseTest implements WebDriverProvider {
    @Override
    public WebDriver createDriver(DesiredCapabilities capabilities) {
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("download.default_directory", TestData.docPathWindows);
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("prefs", prefs);
        capabilities.merge(capabilities);
        return new ChromeDriver(options);
    }
}