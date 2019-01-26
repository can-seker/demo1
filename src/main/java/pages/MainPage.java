package pages;

import com.codeborne.selenide.SelenideElement;
import common.BaseLibrary;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.testng.Assert;

import static com.codeborne.selenide.Selenide.$;

//TODO Ortak alan ve nesneler için Page Object Base Library baglantı alanı
public class MainPage extends BaseLibrary {

    @Step("{description}")
    public MainPage click(String element,String description){
        $(By.cssSelector(element)).click();
        return this;
    }

    @Step("{description}")
    public MainPage sendKeys(String element,String description,String paramater){
        $(By.cssSelector(element)).sendKeys();
        return this;
    }

    @Step("{description}")
    public MainPage setValue(String element,String description,String paramater){
        $(By.cssSelector(element)).setValue(paramater);
        return this;
    }

    @Step("{description}")
    public MainPage displayed(String element,String description,String paramater){
        Assert.assertEquals($(By.cssSelector(element)).isDisplayed(),true);
        takeScreenshot();
        return this;
    }

    @Step("{description}")
    public MainPage getText(String element,String description,String paramater){
        Assert.assertEquals($(By.cssSelector(element)).getText(),paramater);
        takeScreenshot();
        return this;
    }

}
