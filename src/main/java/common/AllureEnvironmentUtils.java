package common;

import com.codeborne.selenide.Configuration;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

import static java.lang.System.getenv;
import static java.util.Optional.ofNullable;
class AllureEnvironmentUtils {

    static void createEnvironments() {
        FileOutputStream fos = null;
        try {
            Properties props = new Properties();
            new File("allure-results").mkdir();
            new File("allure-results/environment.properties").createNewFile();

            fos = new FileOutputStream("allure-results/environment.properties");

            ofNullable(Configuration.baseUrl).ifPresent(s -> props.setProperty("url", s));
            ofNullable(Configuration.browser).ifPresent(s -> props.setProperty("browser", s));
            ofNullable(Configuration.remote).ifPresent(s -> props.setProperty("hub", s));
            ofNullable(Configuration.timeout).ifPresent(s -> props.setProperty("timeout", String.valueOf(s / 1000) + " sec."));
            /*ofNullable(getProperty("Selenide.browser")).ifPresent(s -> props.setProperty("Browser", s));
            ofNullable(getProperty("Selenide.baseUrl")).ifPresent(s -> props.setProperty("Base url", s));*/
            ofNullable(getenv("BUILD_URL")).ifPresent(s -> props.setProperty("Jenkins build URL", s));

            props.store(fos, "See https://github.com/allure-framework/allure-app/wiki/Environment");
            fos.close();
        } catch (IOException e) {
            System.out.println("IO problem when writing allure properties file: " + e.getMessage());
        } finally {
            IOUtils.closeQuietly(fos);
        }
    }

    static void copyCategories() {
        try {
            /*File propFile = new File("allure-results");
            propFile.mkdir();
            propFile = new File("allure-results/environment.properties");*/

            File categoriesFile = new File("src/test/resources/categories.json");
            if (categoriesFile.exists())
                Files.copy(categoriesFile.toPath(),
                        (new File(new File("allure-results") + File.separator + categoriesFile.getName())).toPath(),
                        StandardCopyOption.REPLACE_EXISTING);

        } catch (Exception e) {
            System.out.println("Problem when writing allure categories file: " + e.getMessage());
        }
    }

}