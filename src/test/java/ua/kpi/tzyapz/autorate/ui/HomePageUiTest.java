package ua.kpi.tzyapz.autorate.ui;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UI-тести головної сторінки і верхньої панелі (TC-001, TC-013 спринта 3).
 * Запуск: mvn test -DexcludedGroups=none -Dtest=HomePageUiTest
 * Автор: Поломарьова Дарина (Інженер з тестування, спринт 4).
 */
@Tag("ui")
@DisplayName("Головна сторінка і верхня панель")
class HomePageUiTest extends UiTestBase {

    private static final By POPULAR_MAKE = By.cssSelector("a[href^='/make/'] img");
    private static final By POPULAR_MODEL = By.cssSelector("a[href^='/model/'] img");
    private static final By OVERALL = By.cssSelector("a[href='/overall'] img");

    private void openHome() {
        driver.get(BASE_URL);
        visible(POPULAR_MAKE);
    }

    @Test
    @DisplayName("TC-001: три блоки головної ведуть на марку, модель і загальний рейтинг")
    void homeBlocksOpenMakeModelAndOverall() {
        openHome();
        String page = driver.findElement(By.tagName("body")).getText();
        assertAll("блоки головної сторінки",
                () -> assertTrue(page.contains("Popular Make")),
                () -> assertTrue(page.contains("Popular Model")),
                () -> assertTrue(page.contains("Overall Rating")),
                () -> assertTrue(page.contains("List of all registered models.")));

        driver.findElement(POPULAR_MAKE).click();
        assertTrue(wait.until(ExpectedConditions.urlContains("/make/")), "Popular Make → сторінка марки");

        openHome();
        driver.findElement(POPULAR_MODEL).click();
        assertTrue(wait.until(ExpectedConditions.urlContains("/model/")), "Popular Model → сторінка моделі");

        openHome();
        driver.findElement(OVERALL).click();
        assertTrue(wait.until(ExpectedConditions.urlMatches("/overall$")), "Overall Rating → /overall");
    }

    @Test
    @DisplayName("TC-013: верхня панель — поле Login з підказкою, пароль приховано, кнопки Login і Register")
    void headerLoginFormMasksPassword() {
        driver.get(BASE_URL + "register");
        WebElement login = visible(By.cssSelector("input[name='login']"));
        WebElement password = driver.findElement(By.cssSelector("input[name='password']"));
        assertAll("верхня панель",
                () -> assertEquals("Login", login.getAttribute("placeholder")),
                () -> assertEquals("password", password.getAttribute("type")),
                () -> assertTrue(isShown(By.xpath("//button[contains(@class,'btn-success')][normalize-space()='Login']"))),
                () -> assertTrue(isShown(By.xpath("//a[contains(@class,'btn-success-outline')][normalize-space()='Register']"))));
    }
}
