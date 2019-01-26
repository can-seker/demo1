package pages.pagecomponents;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import pages.MainPage;

import static com.codeborne.selenide.Selenide.$;

public class MenuPages extends MainPage {

    @Step("Home Tıklanır")
    public MenuPages home(){
        $(By.id("home")).click();
        return this;
    }
}
