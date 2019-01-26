package SingleConnect;

import common.BaseTest;
import org.testng.annotations.Test;
import static data.UserData.*;

public class SingleConnectTest extends BaseTest {

    @Test(description = "SCT-1703 SAPM Split Password Checkout Managerial Approval")
    public void SCT1703(){

        String isim = createRandomText(10);
        String soyad = createRandomText(10);
        String birthDay = getSysNextDateRequest(0,0,0);
        String mailing = createRandomText(10)+"@gmail.com";
        String city = "London";
        String state = "Alabama";
        String code = createRandomNumber(8);

        login(username,password);
    }
}
