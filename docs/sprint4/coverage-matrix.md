# Матриця покриття BL — спринт 4

Еталон — SRS 1.1, розділ 5. Модуль — autorate-module v1. Тести — `src/test/java/ua/kpi/tzyapz/autorate/`.
Результат v1 — за прогоном 11.11.2026 (`mvn clean test`, див. [surefire-summary.md](surefire-summary.md)).
Код-рев'ю — за чек-листом 5.3 методички спринта 4; автор тест-класу не рецензує власний клас.

## BL-вимога → правило → тести

| Вимога | Правило | Тест (метод) | Тип перевірки | Дані / межі | v1 | Дефект |
| --- | --- | --- | --- | --- | --- | --- |
| BL-10 | Зважене середнє, HALF_UP до 0,1 | RatingAggregatorTest.averageRoundsHalfUp | межа округлення | (4×3+5×1)/4 = 4,25 → 4,3 | failed | BUG-011 |
| BL-10 | Вага — репутація | RatingAggregatorTest.averageIsWeightedByReputation | нормальний випадок | (2×3+5×3+1×1)/7 = 3,142 → 3,1 | passed | — |
| BL-10 | Оцінки 1..5 | RatingAggregatorTest.singleVoteGivesItsRating | параметризований | 1, 2, 3, 4, 5 | passed | — |
| BL-10 | Репутація 1..3 — межі допустимі | RatingAggregatorTest.reputationBoundsAreAccepted | параметризований | 1, 3 | passed | — |
| BL-10 | Порожній список → 0,0 | RatingAggregatorTest.emptyVotesGiveZero | граничний | [] | passed | — |
| BL-10 | Оцінка/репутація поза межами → IAE | RatingAggregatorTest.outOfRangeVoteThrows | assertThrows, параметризований | оцінка 0, 6; репутація 0, 4 | passed | — |
| BL-10 | null → IAE | RatingAggregatorTest.nullVotesThrow | assertThrows | null | passed | — |
| BL-20 | Поріг ≥3 голоси | RatingAggregatorTest.topNRequiresAtLeastThreeVotes | межа з обох боків | 2 / 3 / 4 голоси | failed [3] | BUG-012 |
| BL-20 | Спадання рейтингу, рівні — за назвою без регістру | RatingAggregatorTest.topNOrdersByRatingThenFullName | assertIterableEquals | 4,8; 4,5 «Audi»; 4,5 «bmw» | passed | — |
| BL-20 | Не більше n, n ≤ 0 → порожньо | RatingAggregatorTest.topNIsLimitedByN | параметризований | n = −1, 0, 1, 2, 5 | passed | — |
| BL-20 | null → IAE | RatingAggregatorTest.topNNullModelsThrow | assertThrows | null | passed | — |
| BL-30 | Коректний голос приймається | VoteValidatorTest.validVoteIsAccepted | assertAll | новий користувач | passed | — |
| BL-30 | Та сама пара (user, model) — «повторний голос» | VoteValidatorTest.sameUserSameModelIsRepeat | негативний | u1 + diablo | passed | — |
| BL-30 | Інша модель — не повторний | VoteValidatorTest.sameUserOtherModelIsNotRepeat | позитивний | u1 + murcielago | failed | BUG-013 |
| BL-30 | null-колекція → IAE | VoteValidatorTest.nullExistingVotesThrow | assertThrows | null | passed | — |
| BL-40 | Оцінка 1..5 включно | VoteValidatorTest.ratingMustBeFromOneToFive | межі з обох боків | 0, 1, 2, 4, 5, 6 | failed [5] | BUG-014 |
| BL-50 | Довжина ≤ 200 | VoteValidatorTest.commentLengthLimitIs200 | межі | 1, 199, 200, 201 | passed | — |
| BL-50 | Порожній коментар — «порожній коментар» | VoteValidatorTest.emptyCommentIsRejected | негативний | null, "", "   " | failed | BUG-015 |
| BL-60 | Префікс марки/моделі без регістру | ModelSearchTest.searchMatchesPrefixIgnoringCase | параметризований, assertIterableEquals | lam, LAMBO, mur, f4, iablo, porsche | passed | — |
| BL-60 | Порядок вхідного списку | ModelSearchTest.searchKeepsInputOrder | assertIterableEquals | 3 моделі | passed | — |
| BL-60 | Порожній/пробільний/null-запит → порожньо | ModelSearchTest.blankQueryGivesEmptyResult | граничний | null, "", "   " | failed | BUG-016 |
| BL-60 | null-каталог → IAE | ModelSearchTest.searchNullModelsThrow | assertThrows | null | passed | — |
| BL-70 | Спадання рейтингу | ModelSearchTest.sortByRatingDescending | assertIterableEquals | 4,6 / 4,2 / 3,9 | failed | BUG-017 |
| BL-70 | Рівні — за повною назвою без регістру | ModelSearchTest.equalRatingsOrderedByFullName | assertIterableEquals | «Audi A4», «bmw X5» | passed | — |
| BL-70 | Новий список, вхідний не змінюється | ModelSearchTest.sortDoesNotModifyInput | assertAll, assertNotSame | копія каталогу | passed | — |
| BL-70 | null → IAE | ModelSearchTest.sortNullModelsThrow | assertThrows | null | passed | — |
| BL-80 | Стоп-слово за замовчуванням — «недопустиме слово» | VoteValidatorStopWordsTest.defaultStopWordIsRejected | параметризований | spam, SPAM, Casino, scam!, казино | passed | — |
| BL-80 | Лише повне слово | VoteValidatorStopWordsTest.wordContainingStopWordIsAllowed | параметризований, assertAll | spammer, casinoroyale | passed | — |
| BL-80 | Порожній коментар не містить стоп-слів | VoteValidatorStopWordsTest.blankCommentHasNoStopWord | граничний | null, "", "   " | passed | — |
| BL-80 | Власний стоп-список через конструктор | VoteValidatorStopWordsTest.customStopListReplacesDefault | assertAll | ["Lada"] | passed | — |
| BL-80 | null-стоп-список → IAE | VoteValidatorStopWordsTest.nullStopListThrows | assertThrows | null | passed | — |

Нетривіальні публічні методи модуля: `average`, `topN`, `validate`, `isValid`, `containsStopWord`, `search`, `sortByRating`, конструктор `VoteValidator(Collection)` — усі мають тести.

## Тест-класи: автори і код-рев'ю

| Тест-клас | Вимоги / TC | Тест-методів | Автор | Рецензент | Рев'ю |
| --- | --- | --- | --- | --- | --- |
| RatingAggregatorTest | BL-10, BL-20 | 11 | Рушай Євген | Асташов Іван | 11.11.2026 — 2 зауваження, виправлено |
| VoteValidatorTest | BL-30, BL-40, BL-50 | 7 | Асташов Іван | Кінаш Єгор | 11.11.2026 — 1 зауваження, виправлено |
| VoteValidatorStopWordsTest | BL-80 | 5 | Поломарьова Дарина | Асташов Іван | 11.11.2026 — 1 зауваження, виправлено |
| ModelSearchTest | BL-60, BL-70 | 8 | Кінаш Єгор | Асташов Іван | 11.11.2026 — 1 зауваження, виправлено |
| ui/HomePageUiTest | TC-001, TC-013 | 2 | Поломарьова Дарина | Асташов Іван | 11.11.2026 — 1 зауваження, виправлено |
| ui/RegistrationUiTest | TC-003, TC-004, TC-012 | 3 | Поломарьова Дарина | Асташов Іван | 11.11.2026 — 1 зауваження, виправлено |
| ui/OverallRatingUiTest | TC-015, TC-022 | 2 | Поломарьова Дарина | Асташов Іван | 11.11.2026 — без зауважень |
| ui/ModelPageUiTest | TC-019 | 1 | Поломарьова Дарина | Асташов Іван | 11.11.2026 — без зауважень |

`ExampleTest` — зразок із пакета модуля (3 методи), до нормативу не зараховується.

## Журнал code review (чек-лист 5.3)

| Клас | Пункт чек-листа | Зауваження | Виправлення |
| --- | --- | --- | --- |
| RatingAggregatorTest | 1 — очікуване значення з вимоги | `averageIsWeightedByReputation` повторював дані `ExampleTest` (5×2 + 3×1) і не додавав нової перевірки | нові дані: (2×3 + 5×3 + 1×1)/7 = 3,142 → 3,1 |
| RatingAggregatorTest | 4 — межі з обох боків | межі репутації перевірено лише як недопустимі (0, 4); допустимі 1 і 3 окремо не перевірено | додано `reputationBoundsAreAccepted` (1, 3) |
| VoteValidatorTest | 2 — одна причина впасти | `sameUserSameModelIsRepeat` використовував оцінку 5, яку v1 відхиляє (BUG-014): падіння могло мати другу причину | оцінку змінено на 4, причину пояснено в коментарі |
| VoteValidatorStopWordsTest | 4 — граничні значення | `containsStopWord` для null / порожнього / пробільного коментаря не перевірявся | додано `blankCommentHasNoStopWord` |
| ModelSearchTest | 1 — тест перевіряє правило | `searchMatchesPrefixIgnoringCase` перевіряв лише кількість знайдених моделей — пройшов би і з неправильними моделями | порівняння переліку назв через `assertIterableEquals`, доданий запит «f4» |
| HomePageUiTest | 8 / звіт | переходи TC-001 перевірялись лише очікуванням: при збої — Error (TimeoutException), а не Failure з поясненням | явні `assertTrue(...)` з повідомленням для кожного переходу |
| RegistrationUiTest | 2 — читабельність | прийом «ввести x + Backspace» після `clear()` не пояснено; повне ім'я `org.openqa.selenium.Keys` у коді | коментар про подію input в Angular; імпорт `Keys` |
| OverallRatingUiTest, ModelPageUiTest | 1–8 | зауважень немає: явні очікування, стійкі локатори, новий браузер на тест | — |
