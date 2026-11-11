package ua.kpi.tzyapz.autorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Юніт-тести ModelSearch: пошук за префіксом (BL-60) і сортування за рейтингом (BL-70) за SRS 1.1.
 * Автор: Кінаш Єгор (Керівник тестування, спринт 4).
 */
@Tag("unit")
@DisplayName("ModelSearch — пошук за префіксом (BL-60) і сортування (BL-70)")
class ModelSearchTest {

    private ModelSearch search;
    private CarModel ferrari;
    private CarModel diablo;
    private CarModel murcielago;
    private List<CarModel> catalog;

    @BeforeEach
    void setUp() {
        search = new ModelSearch();
        ferrari = new CarModel("Ferrari", "F40", 4.2, 8);
        diablo = new CarModel("Lamborghini", "Diablo", 4.6, 12);
        murcielago = new CarModel("Lamborghini", "Murcielago", 3.9, 6);
        catalog = List.of(ferrari, diablo, murcielago);
    }

    // ---------- BL-60: search ----------

    @ParameterizedTest(name = "BL-60: запит «{0}» → [{1}]")
    @CsvSource({
            "lam, Diablo;Murcielago",   // префікс марки
            "LAMBO, Diablo;Murcielago", // регістр не враховується
            "mur, Murcielago",          // префікс моделі
            "f4, F40",                  // префікс моделі іншої марки
            "iablo, ''",                // частина назви не з початку — не префікс
            "porsche, ''"
    })
    void searchMatchesPrefixIgnoringCase(String query, String expectedNames) {
        List<String> expected = expectedNames.isEmpty() ? List.of() : List.of(expectedNames.split(";"));
        List<String> found = search.search(query, catalog).stream().map(CarModel::name).toList();
        assertIterableEquals(expected, found);
    }

    @Test
    @DisplayName("BL-60: результати зберігають порядок вхідного списку")
    void searchKeepsInputOrder() {
        List<CarModel> found = search.search("lamborghini", List.of(murcielago, ferrari, diablo));
        assertIterableEquals(List.of(murcielago, diablo), found);
    }

    @ParameterizedTest(name = "BL-60: запит «{0}» → порожній результат")
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void blankQueryGivesEmptyResult(String query) {
        // BUG-016: модуль v1 для порожнього, пробільного і null-запиту повертає весь каталог
        assertTrue(search.search(query, catalog).isEmpty());
    }

    @Test
    @DisplayName("BL-60: null замість каталогу → IllegalArgumentException")
    void searchNullModelsThrow() {
        assertThrows(IllegalArgumentException.class, () -> search.search("lam", null));
    }

    // ---------- BL-70: sortByRating ----------

    @Test
    @DisplayName("BL-70: моделі впорядковано за рейтингом за спаданням: 4,6 → 4,2 → 3,9")
    void sortByRatingDescending() {
        // BUG-017: модуль v1 сортує за зростанням рейтингу
        List<CarModel> sorted = search.sortByRating(catalog);
        assertIterableEquals(List.of(diablo, ferrari, murcielago), sorted);
    }

    @Test
    @DisplayName("BL-70: однаковий рейтинг — за повною назвою без урахування регістру")
    void equalRatingsOrderedByFullName() {
        CarModel bmw = new CarModel("bmw", "X5", 4.5, 5);
        CarModel audi = new CarModel("Audi", "A4", 4.5, 5);
        assertIterableEquals(List.of(audi, bmw), search.sortByRating(List.of(bmw, audi)));
    }

    @Test
    @DisplayName("BL-70: повертається новий список, вхідний не змінюється")
    void sortDoesNotModifyInput() {
        List<CarModel> input = new ArrayList<>(catalog);
        List<CarModel> sorted = search.sortByRating(input);
        assertAll("незмінність вхідного списку",
                () -> assertNotSame(input, sorted),
                () -> assertIterableEquals(catalog, input));
    }

    @Test
    @DisplayName("BL-70: null замість каталогу → IllegalArgumentException")
    void sortNullModelsThrow() {
        assertThrows(IllegalArgumentException.class, () -> search.sortByRating(null));
    }
}
