package ua.kpi.tzyapz.autorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Юніт-тести стоп-списку VoteValidator (BL-80, SRS 1.1): збіг повного слова без урахування регістру,
 * стоп-список за замовчуванням і власний через конструктор.
 * Окремий клас, бо BL-80 — самостійне правило зі своїм конструктором і методом containsStopWord.
 * Автор: Поломарьова Дарина (Інженер з тестування, спринт 4).
 */
@Tag("unit")
@DisplayName("VoteValidator — стоп-список коментарів (BL-80)")
class VoteValidatorStopWordsTest {

    private static final String FORBIDDEN = "недопустиме слово";

    private VoteValidator validator;

    @BeforeEach
    void setUp() {
        validator = new VoteValidator();
    }

    private List<String> validateComment(VoteValidator v, String comment) {
        return v.validate("u1", "diablo", 4, comment, List.of());
    }

    @ParameterizedTest(name = "BL-80: «{0}» → «недопустиме слово»")
    @ValueSource(strings = {
            "spam",
            "Це SPAM, не голосуйте",
            "Найкращий Casino у місті",
            "scam!",
            "реклама казино тут"
    })
    void defaultStopWordIsRejected(String comment) {
        assertTrue(validateComment(validator, comment).contains(FORBIDDEN));
    }

    @ParameterizedTest(name = "BL-80: «{0}» не містить окремого стоп-слова")
    @ValueSource(strings = {
            "spammer",
            "casinoroyale",
            "Чудова модель, рекомендую"
    })
    void wordContainingStopWordIsAllowed(String comment) {
        assertAll("коментар без окремого стоп-слова",
                () -> assertFalse(validator.containsStopWord(comment)),
                () -> assertFalse(validateComment(validator, comment).contains(FORBIDDEN)));
    }

    @ParameterizedTest(name = "BL-80: containsStopWord(«{0}») = false")
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void blankCommentHasNoStopWord(String comment) {
        assertFalse(validator.containsStopWord(comment));
    }

    @Test
    @DisplayName("BL-80: власний стоп-список замінює список за замовчуванням")
    void customStopListReplacesDefault() {
        VoteValidator custom = new VoteValidator(List.of("Lada"));
        assertAll("власний стоп-список",
                () -> assertTrue(validateComment(custom, "lada найкраща").contains(FORBIDDEN)),
                () -> assertFalse(validateComment(custom, "spam").contains(FORBIDDEN)));
    }

    @Test
    @DisplayName("BL-80: null замість стоп-списку → IllegalArgumentException")
    void nullStopListThrows() {
        assertThrows(IllegalArgumentException.class, () -> new VoteValidator(null));
    }
}
