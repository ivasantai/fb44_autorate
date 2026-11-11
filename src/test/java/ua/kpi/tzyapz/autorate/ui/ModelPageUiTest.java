package ua.kpi.tzyapz.autorate.ui;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UI-тест сторінки моделі для відвідувача (TC-019 спринта 3).
 * Запуск: mvn test -DexcludedGroups=none -Dtest=ModelPageUiTest
 * Автор: Поломарьова Дарина (Інженер з тестування, спринт 4).
 */
@Tag("ui")
@DisplayName("Сторінка моделі — відвідувач без входу")
class ModelPageUiTest extends UiTestBase {

    @Test
    @DisplayName("TC-019: відвідувач бачить «You need to be logged in to vote.», форми голосування немає")
    void guestCannotVote() {
        driver.get(DIABLO_URL);
        String message = visible(By.cssSelector("p.card-text")).getText();
        assertAll("блок Votes для відвідувача",
                () -> assertEquals("You need to be logged in to vote.", message),
                () -> assertTrue(driver.findElements(By.id("comment")).isEmpty(), "Поле Comment не має бути"),
                () -> assertFalse(isShown(By.xpath("//button[normalize-space()='Vote!']")), "Кнопки Vote! не має бути"));
    }
}
