package Jenkins;

import common.BaseTest;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.altMenuPages.CreateNewAccountPage;
import pages.ustmenupages.AnasayfaPage;

import static com.codeborne.selenide.Selenide.open;

@Feature("Kullanıcı Oluşturma")
public class Jenkins extends BaseTest {


    AnasayfaPage anasayfaPage = new AnasayfaPage();
    CreateNewAccountPage createNewAccountPage = new CreateNewAccountPage();

    @Test(description = "TS0002 Kullanıcı Oluşturma")
    public void TS0002() {

        LoginPage loginPage = new LoginPage();
        String isim = createRandomText(10);
        String soyad = createRandomText(10);
        String birthDay = getSysNextDateRequest(0, 0, 0);
        String mailing = createRandomText(10) + "@gmail.com";
        String city = "London";
        String state = "Alabama";
        String code = createRandomNumber(8);
        String password = createRandomNumber(8);

        loginPage
                .sayfaAc();

        anasayfaPage
                .signUp();

        createNewAccountPage
                .firstNameDoldur(isim)
                .lastNameDoldur(soyad)
                .birthDayDoldur(birthDay)
                .EmailDoldur(mailing)
                .mailingAdressDoldur(mailing)
                .cityDoldur(city)
                .stateSec(state)
                .postalCodeDoldur(code)
                .passwordDoldur(password)
                .signUp();

    }
}
