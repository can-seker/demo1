package common;

import io.qameta.allure.Step;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pages.altMenuPages.CreateNewAccountPage;
import pages.pagecomponents.MenuPages;
import pages.ustmenupages.AnasayfaPage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

public class ReusableSteps extends BaseLibrary {

    @Step("Excel Oku ve Sisteme Kaydet")
    public void ExcelOkuSistemKaydet() throws IOException {
        MenuPages menuPages = new MenuPages();
        AnasayfaPage anasayfaPage = new AnasayfaPage();
        CreateNewAccountPage createNewAccountPage = new CreateNewAccountPage();
        String FILE_NAME = getUploadPath() + "data.xlsx";
        int i = 0;
        String[] deger = new String[7];
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
                    deger[i] = currentCell.getStringCellValue();
                    i++;
                }
            }
            createNewAccountPage.
                    firstNameDoldur(deger[0])
                    .lastNameDoldur(deger[1])
                    .birthDayDoldur(getSysNextDateRequest(0, 0, 0))
                    .EmailDoldur(createRandomText(12) + deger[2])
                    .mailingAdressDoldur(createRandomText(12) + deger[3])
                    .cityDoldur(deger[4])
                    .stateSec(deger[5])
                    .postalCodeDoldur(createRandomNumber(8))
                    .passwordDoldur(deger[6])
                    .signUp()
                    .continueTikla();

            menuPages
                    .home();

            anasayfaPage
                    .logOut()
                    .signUp();
            i = 0;

        }
    }

}
