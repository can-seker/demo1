package pages.ustmenupages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.testng.Assert;
import pages.MainPage;

import static com.codeborne.selenide.Selenide.$;

public class AnasayfaPage extends MainPage {
    SelenideElement btnSignUp = $(By.id("login-form:signup"));
    SelenideElement btnLogOut = $(By.id("logout-form:logout"));
    @Step("Giriş alanında isim kontrolu ")
    public AnasayfaPage girisIsimKontrolu(String kontroledilen){
        String isim = $("[for='logout-form']").getText();
        Assert.assertEquals(isim,kontroledilen);
        Allure.addAttachment("Girilen İsim doğru şekide gelmiştir","");
        takeScreenshot();
        return this;
    }

    @Step("SignUp tıklanır")
    public AnasayfaPage signUp(){
        btnSignUp.click();
        return this;
    }

    @Step("LogOut tıklanır")
    public AnasayfaPage logOut(){
        btnLogOut.click();
        return this;
    }
}
