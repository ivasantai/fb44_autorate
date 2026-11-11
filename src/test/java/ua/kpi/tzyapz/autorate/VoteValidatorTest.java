package ua.kpi.tzyapz.autorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Юніт-тести VoteValidator.validate: один голос на модель (BL-30), оцінка (BL-40),
 * коментар (BL-50) за SRS 1.1.
 * Автор: Асташов Іван (Скрам-майстер / рецензент, спринт 4).
 */
@Tag("unit")
@DisplayName("VoteValidator — повторний голос (BL-30), оцінка (BL-40), коментар (BL-50)")
class VoteValidatorTest {

    private static final String REPEAT = "повторний голос";
    private static final String BAD_RATING = "оцінка поза межами 1..5";
    private static final String TOO_LONG = "коментар задовгий";
    private static final String EMPTY = "порожній коментар";
    private static final String OK_COMMENT = "Чудова модель";

    private VoteValidator validator;
    private List<Vote> existing;

    @BeforeEach
    void setUp() {
        validator = new VoteValidator();
        existing = List.of(new Vote("u1", "diablo", 4, 2, "Сподобалась"));
    }

    @Test
    @DisplayName("BL-30: коректний голос нового користувача → порожній перелік порушень")
    void validVoteIsAccepted() {
        List<String> errors = validator.validate("u2", "diablo", 4, OK_COMMENT, existing);
        assertAll("коректний голос",
                () -> assertTrue(errors.isEmpty(), "Порушення: " + errors),
                () -> assertTrue(validator.isValid("u2", "diablo", 4, OK_COMMENT, existing)));
    }

    @Test
    @DisplayName("BL-30: той самий користувач і та сама модель → «повторний голос»")
    void sameUserSameModelIsRepeat() {
        // оцінка 4, а не 5: тест має одну причину впасти і не залежить від BL-40 (BUG-014)
        List<String> errors = validator.validate("u1", "diablo", 4, OK_COMMENT, existing);
        assertTrue(errors.contains(REPEAT), "Порушення: " + errors);
    }

    @Test
    @DisplayName("BL-30: той самий користувач за іншу модель — не повторний голос")
    void sameUserOtherModelIsNotRepeat() {
        // BUG-013: модуль v1 порівнює лише userId і відхиляє голос за іншу модель
        List<String> errors = validator.validate("u1", "murcielago", 4, OK_COMMENT, existing);
        assertFalse(errors.contains(REPEAT), "Порушення: " + errors);
    }

    @ParameterizedTest(name = "BL-40: оцінка {0} допустима: {1}")
    @CsvSource({
            "0, false",
            "1, true",
            "2, true",
            "4, true",
            "5, true",    // BUG-014: модуль v1 відхиляє оцінку 5
            "6, false"
    })
    void ratingMustBeFromOneToFive(int rating, boolean expectedValid) {
        List<String> errors = validator.validate("u2", "diablo", rating, OK_COMMENT, existing);
        assertEquals(!expectedValid, errors.contains(BAD_RATING), "Порушення: " + errors);
    }

    @ParameterizedTest(name = "BL-50: коментар із {0} символів задовгий: {1}")
    @CsvSource({
            "1, false",
            "199, false",
            "200, false",
            "201, true"
    })
    void commentLengthLimitIs200(int length, boolean expectedTooLong) {
        String comment = "a".repeat(length);
        List<String> errors = validator.validate("u2", "diablo", 4, comment, existing);
        assertEquals(expectedTooLong, errors.contains(TOO_LONG), "Порушення: " + errors);
    }

    @ParameterizedTest(name = "BL-50: коментар «{0}» → «порожній коментар»")
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void emptyCommentIsRejected(String comment) {
        // BUG-015: модуль v1 дозволяє порожній коментар (правило SRS 1.0), SRS 1.1 (RD-10) — забороняє
        List<String> errors = validator.validate("u2", "diablo", 4, comment, existing);
        assertTrue(errors.contains(EMPTY), "Порушення: " + errors);
    }

    @Test
    @DisplayName("BL-30: null замість переліку наявних голосів → IllegalArgumentException")
    void nullExistingVotesThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> validator.validate("u2", "diablo", 4, OK_COMMENT, null));
    }
}
