package ua.kpi.tzyapz.autorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Юніт-тести RatingAggregator: зважене середнє (BL-10) і Top-N (BL-20) за SRS 1.1.
 * Очікувані значення обчислено вручну з вимог, а не з коду модуля.
 * Автор: Рушай Євген (Тест-аналітик, спринт 4).
 */
@Tag("unit")
@DisplayName("RatingAggregator — рейтинг моделі (BL-10) і Top-N (BL-20)")
class RatingAggregatorTest {

    private static final double EXACT = 1e-9;

    private RatingAggregator aggregator;

    @BeforeEach
    void setUp() {
        aggregator = new RatingAggregator();
    }

    private static Vote vote(int rating, int reputation) {
        return new Vote("u-" + rating + "-" + reputation, "m1", rating, reputation, "коментар");
    }

    private static CarModel model(String make, String name, double rating, int votes) {
        return new CarModel(make, name, rating, votes);
    }

    // ---------- BL-10: average ----------

    @Test
    @DisplayName("BL-10: (4×3 + 5×1) / (3 + 1) = 4,25 → 4,3 за HALF_UP")
    void averageRoundsHalfUp() {
        // BUG-011: модуль v1 округлює вниз (RoundingMode.DOWN) і повертає 4,2
        // arrange
        List<Vote> votes = List.of(vote(4, 3), vote(5, 1));
        // act
        double rating = aggregator.average(votes);
        // assert
        assertEquals(4.3, rating, EXACT);
    }

    @Test
    @DisplayName("BL-10: (2×3 + 5×3 + 1×1) / (3 + 3 + 1) = 22/7 = 3,142… → 3,1")
    void averageIsWeightedByReputation() {
        List<Vote> votes = List.of(vote(2, 3), vote(5, 3), vote(1, 1));
        assertEquals(3.1, aggregator.average(votes), EXACT);
    }

    @ParameterizedTest(name = "BL-10: репутація {0} (межа 1..3) допустима, рейтинг 4,0")
    @ValueSource(ints = {1, 3})
    void reputationBoundsAreAccepted(int reputation) {
        assertEquals(4.0, aggregator.average(List.of(vote(4, reputation))), EXACT);
    }

    @ParameterizedTest(name = "BL-10: один голос з оцінкою {0} → рейтинг {0},0")
    @ValueSource(ints = {1, 2, 3, 4, 5})
    void singleVoteGivesItsRating(int rating) {
        assertEquals(rating, aggregator.average(List.of(vote(rating, 2))), EXACT);
    }

    @Test
    @DisplayName("BL-10: порожній список голосів → 0,0")
    void emptyVotesGiveZero() {
        assertEquals(0.0, aggregator.average(List.of()), EXACT);
    }

    @ParameterizedTest(name = "BL-10: оцінка {0}, репутація {1} → IllegalArgumentException")
    @CsvSource({
            "0, 1",   // оцінка нижче межі 1..5
            "6, 1",   // оцінка вище межі 1..5
            "3, 0",   // репутація нижче межі 1..3
            "3, 4"    // репутація вище межі 1..3
    })
    void outOfRangeVoteThrows(int rating, int reputation) {
        List<Vote> votes = List.of(vote(rating, reputation));
        assertThrows(IllegalArgumentException.class, () -> aggregator.average(votes));
    }

    @Test
    @DisplayName("BL-10: null замість списку голосів → IllegalArgumentException")
    void nullVotesThrow() {
        assertThrows(IllegalArgumentException.class, () -> aggregator.average(null));
    }

    // ---------- BL-20: topN ----------

    @ParameterizedTest(name = "BL-20: модель із {0} голосами потрапляє в Top-N: {1}")
    @CsvSource({
            "2, false",
            "3, true",    // BUG-012: модуль v1 вимагає більше ніж 3 голоси
            "4, true"
    })
    void topNRequiresAtLeastThreeVotes(int votes, boolean expectedInTop) {
        CarModel candidate = model("Lamborghini", "Diablo", 4.6, votes);
        List<CarModel> top = aggregator.topN(List.of(candidate), 10);
        assertEquals(expectedInTop, top.contains(candidate));
    }

    @Test
    @DisplayName("BL-20: за спаданням рейтингу, рівні — за повною назвою без урахування регістру")
    void topNOrdersByRatingThenFullName() {
        CarModel ferrari = model("Ferrari", "F40", 4.8, 10);
        CarModel bmw = model("bmw", "X5", 4.5, 10);
        CarModel audi = model("Audi", "A4", 4.5, 10);
        List<CarModel> top = aggregator.topN(List.of(bmw, ferrari, audi), 3);
        assertIterableEquals(List.of(ferrari, audi, bmw), top);
    }

    @ParameterizedTest(name = "BL-20: n = {0} → не більше ніж {1} моделей")
    @CsvSource({
            "-1, 0",
            "0, 0",
            "1, 1",
            "2, 2",
            "5, 3"
    })
    void topNIsLimitedByN(int n, int expectedSize) {
        List<CarModel> models = List.of(
                model("Audi", "A4", 4.0, 5),
                model("BMW", "X5", 4.2, 5),
                model("Ferrari", "F40", 4.8, 5));
        assertEquals(expectedSize, aggregator.topN(models, n).size());
    }

    @Test
    @DisplayName("BL-20: null замість списку моделей → IllegalArgumentException")
    void topNNullModelsThrow() {
        assertThrows(IllegalArgumentException.class, () -> aggregator.topN(null, 3));
    }
}
