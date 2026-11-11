package ua.kpi.tzyapz.autorate.ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UI-тести форми реєстрації (TC-003, TC-004, TC-012 спринта 3). Форма ніколи не надсилається:
 * перевіряється лише клієнтська валідація, тож облікові записи на demo не створюються.
 * Запуск: mvn test -DexcludedGroups=none -Dtest=RegistrationUiTest
 * Автор: Поломарьова Дарина (Інженер з тестування, спринт 4).
 */
@Tag("ui")
@DisplayName("Форма реєстрації — клієнтська валідація")
class RegistrationUiTest extends UiTestBase {

    private static final By LOGIN = By.id("username");
    private static final By FIRST_NAME = By.id("firstName");
    private static final By LAST_NAME = By.id("lastName");
    private static final By PASSWORD = By.id("password");
    private static final By CONFIRM = By.id("confirmPassword");
    private static final By REGISTER = By.cssSelector("my-register button[type='submit']");
    private static final By CANCEL = By.xpath("//my-register//a[normalize-space()='Cancel']");

    /** Червона ліва межа порожнього поля (GUI-40), колір зафіксовано у журналі спринта 3. */
    private static final String RED = "rgba(169, 68, 66, 1)";

    @BeforeEach
    void openRegistration() {
        driver.get(BASE_URL + "register");
        visible(LOGIN);
    }

    private WebElement register() {
        return driver.findElement(REGISTER);
    }

    @Test
    @DisplayName("TC-003: порожня форма — п'ять полів, Register і Cancel; Register неактивна")
    void emptyFormHasAllFieldsAndDisabledRegister() {
        assertAll("порожня форма реєстрації",
                () -> assertEquals("", driver.findElement(LOGIN).getAttribute("value")),
                () -> assertEquals("", driver.findElement(FIRST_NAME).getAttribute("value")),
                () -> assertEquals("", driver.findElement(LAST_NAME).getAttribute("value")),
                () -> assertEquals("", driver.findElement(PASSWORD).getAttribute("value")),
                () -> assertEquals("", driver.findElement(CONFIRM).getAttribute("value")),
                () -> assertTrue(isShown(CANCEL)),
                () -> assertFalse(register().isEnabled(), "Register має бути неактивною"));
    }

    @Test
    @DisplayName("TC-004: очищене First Name — «First Name is required» і червона межа, після введення зникають")
    void clearedFirstNameShowsRequiredError() {
        WebElement firstName = driver.findElement(FIRST_NAME);
        firstName.sendKeys("Test");
        // clear() змінює значення без події input, і Angular не бачить порожнього поля;
        // тому після очищення вводимо і стираємо символ — як користувач клавішею Backspace
        firstName.clear();
        firstName.sendKeys("x");
        firstName.sendKeys(Keys.BACK_SPACE);
        driver.findElement(LAST_NAME).click();

        visible(alert("First Name is required"));
        assertAll("порожнє обов'язкове поле",
                () -> assertEquals(RED, firstName.getCssValue("border-left-color")),
                () -> assertFalse(register().isEnabled()));

        firstName.sendKeys("Test");
        wait.until(d -> !isShown(alert("First Name is required")));
        assertNotEquals(RED, firstName.getCssValue("border-left-color"));
    }

    @Test
    @DisplayName("TC-012: розбіжність паролів — «Passwords do not match», після виправлення помилка зникає")
    void passwordMismatchShowsErrorUntilFixed() {
        driver.findElement(LOGIN).sendKeys("autorate_ui_20261028");
        driver.findElement(FIRST_NAME).sendKeys("Test");
        driver.findElement(LAST_NAME).sendKeys("User");
        driver.findElement(PASSWORD).sendKeys("Auto1234");
        WebElement confirm = driver.findElement(CONFIRM);
        confirm.sendKeys("Auto5678");
        driver.findElement(LAST_NAME).click();

        visible(alert("Passwords do not match"));
        assertFalse(register().isEnabled(), "Register має бути неактивною, поки паролі різні");

        confirm.clear();
        confirm.sendKeys("Auto1234");
        wait.until(d -> !isShown(alert("Passwords do not match")));
        assertTrue(wait.until(ExpectedConditions.elementToBeClickable(REGISTER)).isEnabled());
    }
}
