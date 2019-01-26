package pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import static com.codeborne.selenide.Selenide.$;
import static data.UserData.password;
import static data.UserData.username;

public class LoginPage extends MainPage {

    private SelenideElement txtUsername = $(By.id("user"));
    private SelenideElement txtPassword = $(By.id("pass"));
    private SelenideElement btnLogin = $(By.id("buttonlogin"));

    private LoginPage open() {
        WebDriverRunner.clearBrowserCache();
        Selenide.open("");
        System.out.println("================================");
        System.out.println("Driver: " + getCapabilities().toString());
        System.out.println("================================");
        maximazeBrowser();
        return this;
    }

    public LoginPage sayfaAc() {
        WebDriverRunner.clearBrowserCache();
        Selenide.open("");

        System.out.println("================================");
        System.out.println("Driver: " + getCapabilities().toString());
        System.out.println("================================");
        maximazeBrowser();
        return this;
    }

    public LoginPage sayfaKapat() {
        WebDriverRunner.closeWebDriver();
        Selenide.close();
        return this;
    }


    @Step("Giriş yap")
    public LoginPage login() {
        open();
        txtUsername.sendKeys(username);
        txtPassword.sendKeys(password);
        btnLogin.shouldBe(Condition.visible).click();
        return this;
    }

    @Step("\"{username}\" kullanıcısı ile giriş yap")
    public LoginPage login(String username, String password) {
        open();
        txtUsername.sendKeys(username);
        txtPassword.sendKeys(password);
        btnLogin.click();
        return this;
    }
}