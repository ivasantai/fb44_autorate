package ua.kpi.tzyapz.autorate.ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UI-тести сторінки загального рейтингу (TC-015, TC-022 спринта 3).
 * Запуск: mvn test -DexcludedGroups=none -Dtest=OverallRatingUiTest
 * Автор: Поломарьова Дарина (Інженер з тестування, спринт 4).
 */
@Tag("ui")
@DisplayName("Загальний рейтинг (Overall Rating)")
class OverallRatingUiTest extends UiTestBase {

    private static final By ROWS = By.cssSelector("table.cars tbody tr");
    private static final By HEADERS = By.cssSelector("table.cars thead th");
    private static final By PAGER_TEXT = By.cssSelector("my-pager .pull-xs-right");
    private static final By PREV = By.xpath("//my-pager//a[normalize-space()='«']");
    private static final By NEXT = By.xpath("//my-pager//a[normalize-space()='»']");
    private static final By DIABLO_MAKE_LINK =
            By.xpath("//table[contains(@class,'cars')]//tr[td/a[normalize-space()='Diablo']]/td[2]/a");

    @BeforeEach
    void openOverall() {
        driver.get(BASE_URL + "overall");
        wait.until(d -> !d.findElements(ROWS).isEmpty());
    }

    private String firstModelName() {
        return driver.findElement(By.cssSelector("table.cars tbody tr td:nth-child(3) a")).getText();
    }

    @Test
    @DisplayName("TC-015: заголовки Make, Model, Rank, Votes, Engine, Comments; марка — посилання на сторінку марки")
    void tableHasHeadersAndMakeLinkOpensMake() {
        List<String> headers = driver.findElements(HEADERS).stream().map(WebElement::getText).toList();
        assertAll("заголовки таблиці",
                () -> assertTrue(headers.containsAll(
                        List.of("Make", "Model", "Rank", "Votes", "Engine", "Comments")), "Заголовки: " + headers),
                () -> assertTrue(isShown(By.linkText("View more"))));

        wait.until(ExpectedConditions.elementToBeClickable(DIABLO_MAKE_LINK)).click();
        wait.until(ExpectedConditions.urlContains("/make/"));
        assertTrue(visible(By.xpath("//my-make//*[normalize-space()='Lamborghini']")).isDisplayed());
    }

    @Test
    @DisplayName("TC-022: » відкриває другу сторінку — «page 2 of Y», інший набір, не більше 5 рядків")
    void nextButtonOpensSecondPage() {
        String firstPageModel = firstModelName();
        assertAll("перша сторінка",
                () -> assertEquals(5, driver.findElements(ROWS).size()),
                () -> assertTrue(driver.findElement(PREV).getAttribute("class").contains("disabled")),
                () -> assertTrue(driver.findElement(PAGER_TEXT).getText().contains("page 1 of")));

        driver.findElement(NEXT).click();
        wait.until(ExpectedConditions.textToBePresentInElementLocated(PAGER_TEXT, "page 2 of"));
        wait.until(d -> !firstModelName().equals(firstPageModel));
        assertAll("друга сторінка",
                () -> assertTrue(driver.findElements(ROWS).size() <= 5),
                () -> assertNotEquals(firstPageModel, firstModelName()));
    }
}
