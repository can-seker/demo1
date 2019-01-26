package ExcelSistemeKayit;

import common.BaseTest;
import common.ReusableSteps;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.MainPage;
import pages.altMenuPages.CreateNewAccountPage;
import pages.pagecomponents.MenuPages;
import pages.ustmenupages.AnasayfaPage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import static data.TestData.docPathWindows;

@Feature("Exceldeki Kullanıcılar Sisteme Kayıdı")
public class ExcelSistemeKayitTest extends BaseTest{

    LoginPage loginPage = new LoginPage();
    MainPage mainPage = new MainPage();

    @Test
    public void TS0007() throws IOException {
        String FILE_NAME = docPathWindows + "data.xlsx";
        int i = 0;
        String[] deger = new String[6];
        FileInputStream excelFile = new FileInputStream(new File(FILE_NAME));
        Workbook workbook = new XSSFWorkbook(excelFile);
        Sheet datatypeSheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = datatypeSheet.iterator();
        while (iterator.hasNext()) {
            Row currentRow = iterator.next();
            Iterator<Cell> cellIterator = currentRow.iterator();
            while (cellIterator.hasNext()) {
                Cell currentCell = cellIterator.next();
                if (currentCell.getCellTypeEnum() == CellType.STRING) {
                    System.out.print(currentCell.getStringCellValue() + "--");
                    if (currentCell.getStringCellValue() != "TS-No" || currentCell.getStringCellValue() != "Test-Name" || currentCell.getStringCellValue() != "Description" || currentCell.getStringCellValue() != "Page Object") {
                        deger[i] = currentCell.getStringCellValue();
                    }
                    i++;
                }
            }

        testRun(deger[0],deger[1],deger[4],deger[3],deger[2],deger[5]);
            i=0;
    }
}
    @Test(description = "{TsNo}: {description}")
    public void testRun(String TsNo, String description,String deger1, String deger2, String deger3, String deger4){
        switch (deger1) {
            case "browserOpen":
                loginPage
                        .sayfaAc();
                break;
            case "browserClose":
                loginPage.sayfaKapat();
                break;

            case "setvalue":
                mainPage
                        .setValue(deger2,deger3,deger4);
                break;
            case "click":
                mainPage
                        .click(deger2,deger3);
                break;
            case "gettext":
                mainPage
                        .getText(deger2,deger3,deger4);
                break;
            default:
                System.out.println("İşlem Gerçekleştirilemedi");
                break;
    }
    }

}
