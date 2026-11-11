package ua.kpi.tzyapz.autorate.ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Спільний життєвий цикл UI-тестів demo «АвтоРейт» (Buggy Cars Rating): новий браузер на кожен тест,
 * явні очікування, driver.quit() у @AfterEach. Тести лише читають сторінки й заповнюють форми
 * без надсилання — дані спільного demo не змінюються.
 *
 * Параметри запуску: -Dbrowser=chrome|firefox|edge (за замовчуванням chrome),
 * -Dheadless=false — показати вікно браузера (для демо).
 * Автор: Поломарьова Дарина (Інженер з тестування, спринт 4).
 */
abstract class UiTestBase {

    static final String BASE_URL = "https://buggy.justtestit.org/";
    static final String DIABLO_URL = BASE_URL + "model/ckl2phsabijs71623vk0%7Cckl2phsabijs71623vqg";

    WebDriver driver;
    WebDriverWait wait;

    @BeforeEach
    void openBrowser() {
        boolean headless = !"false".equalsIgnoreCase(System.getProperty("headless", "true"));
        String browser = System.getProperty("browser", "chrome").toLowerCase();
        switch (browser) {
            case "firefox" -> {
                FirefoxOptions options = new FirefoxOptions();
                if (headless) {
                    options.addArguments("-headless");
                }
                options.addArguments("--width=1280", "--height=900");
                driver = new FirefoxDriver(options);
            }
            case "edge" -> {
                EdgeOptions options = new EdgeOptions();
                if (headless) {
                    options.addArguments("--headless=new");
                }
                options.addArguments("--window-size=1280,900");
                driver = new EdgeDriver(options);
            }
            default -> {
                ChromeOptions options = new ChromeOptions();
                if (headless) {
                    options.addArguments("--headless=new");
                }
                options.addArguments("--window-size=1280,900");
                driver = new ChromeDriver(options);
            }
        }
        // demo відповідає повільно, тому тайм-аут 15 с
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterEach
    void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    /** Чекає, поки хоча б один елемент за локатором стане видимим, і повертає його. */
    WebElement visible(By locator) {
        return wait.until(d -> {
            List<WebElement> found = d.findElements(locator);
            return found.stream().filter(WebElement::isDisplayed).findFirst().orElse(null);
        });
    }

    /** Чи видно зараз на сторінці хоча б один елемент за локатором (без очікування). */
    boolean isShown(By locator) {
        return driver.findElements(locator).stream().anyMatch(WebElement::isDisplayed);
    }

    /** Локатор повідомлення про помилку форми з точним текстом. */
    static By alert(String text) {
        return By.xpath("//div[contains(@class,'alert-danger')][normalize-space()='" + text + "']");
    }
}
