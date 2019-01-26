package pages.altMenuPages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import pages.MainPage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pages.ustmenupages.AnasayfaPage;
import pages.pagecomponents.MenuPages;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import static com.codeborne.selenide.Selenide.$;

public class CreateNewAccountPage extends MainPage {
    SelenideElement txtFirstName = $(By.id("signup:fname"));
    SelenideElement txtLastName = $(By.id("signup:lname"));
    SelenideElement txtBirthDay = $(By.id("BirthDate"));
    SelenideElement txtEmailAdress = $(By.id("signup:email"));
    SelenideElement txtMailingAdress = $(By.id("signup:street"));
    SelenideElement txtCity = $(By.id("signup:city"));
    SelenideElement cmbState = $(By.id("signup:state"));
    SelenideElement txtPostalCode = $(By.id("signup:zip"));
    SelenideElement txtPassword = $(By.id("signup:password"));
    SelenideElement btnSignUp = $(By.id("signup:signup"));
    SelenideElement btnContinue = $(By.id("signup:continue"));

    @Step("Continue tıklanır")
    public CreateNewAccountPage continueTikla() {
        btnContinue.click();
        return this;
    }

    @Step("Signup tıklanır")
    public CreateNewAccountPage signUp() {
        btnSignUp.click();
        return this;
    }

    @Step("Password kod alanı '{password}' doldurulur")
    public CreateNewAccountPage passwordDoldur(String password) {
        txtPassword.setValue(password);
        return this;
    }

    @Step("Postal kod alanı '{postalCode}' doldurulur")
    public CreateNewAccountPage postalCodeDoldur(String postalCode) {
        txtPostalCode.setValue(postalCode);
        return this;
    }

    @Step("State alanında '{state}' seçilir")
    public CreateNewAccountPage stateSec(String state) {
        cmbState.selectOption(state);
        return this;
    }

    @Step("City alanı '{city}' doldurulur")
    public CreateNewAccountPage cityDoldur(String city) {
        txtCity.setValue(city);
        return this;
    }

    @Step("Mailling Adress alanı '{mail}' doldurulur")
    public CreateNewAccountPage mailingAdressDoldur(String mail) {
        txtMailingAdress.setValue(mail);
        return this;
    }

    @Step("Email alanı '{email}' doldurulur")
    public CreateNewAccountPage EmailDoldur(String email) {
        txtEmailAdress.setValue(email);
        return this;
    }

    @Step("Birth Day alanı '{birthDay}' doldurulur")
    public CreateNewAccountPage birthDayDoldur(String birthDay) {
        txtBirthDay.setValue(birthDay);
        return this;
    }

    @Step("First Name alanı '{name}' doldurulur")
    public CreateNewAccountPage firstNameDoldur(String name) {
        txtFirstName.setValue(name);
        return this;
    }

    @Step("Last Name alanı '{lastname}' doldurulur")
    public CreateNewAccountPage lastNameDoldur(String lastname) {
        txtLastName.setValue(lastname);
        return this;
    }
}
