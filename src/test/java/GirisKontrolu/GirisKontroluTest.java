package GirisKontrolu;

import common.BaseTest;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.altMenuPages.CreateNewAccountPage;
import pages.ustmenupages.AnasayfaPage;
import static data.UserData.*;

@Feature("Giriş Kontrolu")
public class GirisKontroluTest extends BaseTest {

    AnasayfaPage anasayfaPage = new AnasayfaPage();

    @Test(description = "TS0001 Senaryo Başlangıcı")
    public void TS0001(){
        String isim ="John Smith";

        login(username,password);

        anasayfaPage
                .girisIsimKontrolu(isim);

    }
}
