# Реєстр дефектів модуля autorate-module v1 — спринт 4

Еталон — SRS 1.1 (розділ 5, BL-10…BL-80). Мітка всіх записів: `module`. Нумерація продовжує реєстр demo спринта 3 (BUG-001…BUG-010).
Кожен дефект зафіксовано юніт-тестом, що падає на v1; тест лишається «червоним» і у спринті 5 перевірить виправлення у v2.

| Bug ID | Вимога | Тест (клас.метод) | Severity | Priority | Status | Reporter |
| --- | --- | --- | --- | --- | --- | --- |
| BUG-011 | BL-10 | RatingAggregatorTest.averageRoundsHalfUp | Major | P3 | New | Рушай Євген |
| BUG-012 | BL-20 | RatingAggregatorTest.topNRequiresAtLeastThreeVotes [2] | Major | P3 | New | Рушай Євген |
| BUG-013 | BL-30 | VoteValidatorTest.sameUserOtherModelIsNotRepeat | Critical | P3 | New | Асташов Іван |
| BUG-014 | BL-40 | VoteValidatorTest.ratingMustBeFromOneToFive [5] | Major | P3 | New | Асташов Іван |
| BUG-015 | BL-50 | VoteValidatorTest.emptyCommentIsRejected [1–3] | Minor | P3 | New | Асташов Іван |
| BUG-016 | BL-60 | ModelSearchTest.blankQueryGivesEmptyResult [1–3] | Normal | P3 | New | Кінаш Єгор |
| BUG-017 | BL-70 | ModelSearchTest.sortByRatingDescending | Major | P3 | New | Кінаш Єгор |

Спільне середовище для всіх звітів (ENV-M1): autorate-module **v1** (1.0.0, пакет `ua.kpi.tzyapz.autorate`); Eclipse Temurin JDK 17.0.21; Apache Maven 3.9.9; JUnit 5.10.2; Windows 11; прогін 11.11.2026.
Priority P3 для всіх: дефекти модуля не блокують виконання інших тестів — набір виконується повністю.

## BUG-011. RatingAggregator.average округлює вниз замість HALF_UP

| Поле | Значення |
| --- | --- |
| Bug ID | BUG-011 |
| Title | RatingAggregator.average округлює рейтинг вниз: 4,25 → 4,2 замість 4,3 |
| Severity / Priority | Major / P3 |
| Linked test / requirement | RatingAggregatorTest.averageRoundsHalfUp; BL-10 |
| Reporter | Рушай Євген, 11.11.2026 |
| Reviewer | Асташов Іван, 11.11.2026 — чек-лист 7/7 |
| Status | New |

**Description.** BL-10 вимагає округлення рейтингу до 0,1 за правилом HALF_UP. Метод використовує `RoundingMode.DOWN`, тому кожне значення з другим знаком ≥ 5 занижується на 0,1. Severity Major: рейтинг — головна цінність продукту, частина моделей отримує неправильний рейтинг і місце у «рейтингу тижня». Priority P3: інших тестів не блокує.

**Steps to reproduce.**
1. Створити `new RatingAggregator()`.
2. Викликати `average(List.of(new Vote("u1","m1",4,3,"x"), new Vote("u2","m1",5,1,"x")))`.
3. Або запустити `mvn test -Dtest=RatingAggregatorTest#averageRoundsHalfUp`.

**Actual result.** `4.2` (`expected: <4.3> but was: <4.2>`).

**Expected result.** (4×3 + 5×1) / (3 + 1) = 4,25 → **4,3** за HALF_UP (BL-10).

**Environment.** ENV-M1; `mvn test -Dtest=RatingAggregatorTest`.

## BUG-012. RatingAggregator.topN не включає моделі рівно з 3 голосами

| Поле | Значення |
| --- | --- |
| Bug ID | BUG-012 |
| Title | RatingAggregator.topN відкидає модель із 3 голосами, хоча поріг — «щонайменше 3» |
| Severity / Priority | Major / P3 |
| Linked test / requirement | RatingAggregatorTest.topNRequiresAtLeastThreeVotes, набір [2] «3, true»; BL-20 |
| Reporter | Рушай Євген, 11.11.2026 |
| Reviewer | Асташов Іван, 11.11.2026 — чек-лист 7/7 |
| Status | New |

**Description.** BL-20: модель потрапляє у Top-N лише з ≥3 голосами. Метод фільтрує `votes() > MIN_VOTES` (строго більше 3), тож межове значення 3 відкидається. Значення 2 (немає) і 4 (є) працюють правильно. Severity Major: «рейтинг тижня» неповний — моделі з рівно трьома голосами зникають з нього. Priority P3.

**Steps to reproduce.**
1. `CarModel m = new CarModel("Lamborghini", "Diablo", 4.6, 3);`
2. `new RatingAggregator().topN(List.of(m), 10)`.
3. Або `mvn test -Dtest=RatingAggregatorTest#topNRequiresAtLeastThreeVotes`.

**Actual result.** Порожній список; тест: `expected: <true> but was: <false>` для набору [2].

**Expected result.** Список містить модель Diablo (3 голоси ≥ 3, BL-20).

**Environment.** ENV-M1; `mvn test -Dtest=RatingAggregatorTest`.

## BUG-013. VoteValidator вважає повторним голос того самого користувача за іншу модель

| Поле | Значення |
| --- | --- |
| Bug ID | BUG-013 |
| Title | VoteValidator.validate повертає «повторний голос» для голосу за іншу модель |
| Severity / Priority | Critical / P3 |
| Linked test / requirement | VoteValidatorTest.sameUserOtherModelIsNotRepeat; BL-30, F-100 |
| Reporter | Асташов Іван, 11.11.2026 |
| Reviewer | Кінаш Єгор, 11.11.2026 — чек-лист 7/7 |
| Status | New |

**Description.** BL-30: голос повторний, якщо серед наявних є голос із **тією самою парою** (userId, modelId). Метод порівнює лише `userId`, тому після першого голосу користувач не може проголосувати за жодну іншу модель. Severity Critical: блокується ключова функція продукту — голосування. Priority P3: інших тестів не блокує.

**Steps to reproduce.**
1. `List<Vote> existing = List.of(new Vote("u1", "diablo", 4, 2, "Сподобалась"));`
2. `new VoteValidator().validate("u1", "murcielago", 4, "Чудова модель", existing)`.
3. Або `mvn test -Dtest=VoteValidatorTest#sameUserOtherModelIsNotRepeat`.

**Actual result.** `[повторний голос]`.

**Expected result.** Порожній перелік — пара (u1, murcielago) ще не голосувала (BL-30).

**Environment.** ENV-M1; `mvn test -Dtest=VoteValidatorTest`.

## BUG-014. VoteValidator відхиляє оцінку 5

| Поле | Значення |
| --- | --- |
| Bug ID | BUG-014 |
| Title | VoteValidator.validate відхиляє допустиму оцінку 5 як «оцінка поза межами 1..5» |
| Severity / Priority | Major / P3 |
| Linked test / requirement | VoteValidatorTest.ratingMustBeFromOneToFive, набір [5] «5, true»; BL-40 |
| Reporter | Асташов Іван, 11.11.2026 |
| Reviewer | Кінаш Єгор, 11.11.2026 — чек-лист 7/7 |
| Status | New |

**Description.** BL-40: оцінка — ціле від 1 до 5 **включно**. Умова `rating >= 5` робить верхню межу недопустимою; 0 і 6 відхиляються правильно, 1–4 приймаються. Severity Major: найвищу оцінку неможливо поставити, рейтинги систематично занижені. Priority P3.

**Steps to reproduce.**
1. `new VoteValidator().validate("u2", "diablo", 5, "Чудова модель", List.of())`.
2. Або `mvn test -Dtest=VoteValidatorTest#ratingMustBeFromOneToFive`.

**Actual result.** `[оцінка поза межами 1..5]`.

**Expected result.** Порушення «оцінка поза межами 1..5» немає (5 ∈ 1..5, BL-40).

**Environment.** ENV-M1; `mvn test -Dtest=VoteValidatorTest`.

## BUG-015. VoteValidator приймає порожній коментар

| Поле | Значення |
| --- | --- |
| Bug ID | BUG-015 |
| Title | VoteValidator.validate приймає null, порожній і пробільний коментар без «порожній коментар» |
| Severity / Priority | Minor / P3 |
| Linked test / requirement | VoteValidatorTest.emptyCommentIsRejected, набори [1–3]; BL-50 (SRS 1.1, RD-10), F-90 |
| Reporter | Асташов Іван, 11.11.2026 |
| Reviewer | Кінаш Єгор, 11.11.2026 — чек-лист 7/7 |
| Status | New |

**Description.** BL-50 у SRS 1.1 (правка RD-10, узгоджена з F-90 «голос зараховується лише разом із коментарем») вимагає відхиляти null, порожній і пробільний коментар з повідомленням «порожній коментар». Модуль v1 реалізує редакцію SRS 1.0 («порожній коментар дозволено») і повертає порожній перелік порушень. Severity Minor: вебформа вже не пропускає порожній коментар (TC-034 спринта 3 — passed), розбіжність лише у модулі. Priority P3. Примітка: дефект виник через зміну вимоги у SRS 1.1 — під час демо підтвердити із замовником.

**Steps to reproduce.**
1. `new VoteValidator().validate("u2", "diablo", 4, "", List.of())` (також `null` і `"   "`).
2. Або `mvn test -Dtest=VoteValidatorTest#emptyCommentIsRejected`.

**Actual result.** `[]` для всіх трьох значень.

**Expected result.** Перелік містить «порожній коментар» (BL-50, SRS 1.1).

**Environment.** ENV-M1; `mvn test -Dtest=VoteValidatorTest`.

## BUG-016. ModelSearch.search для порожнього запиту повертає весь каталог

| Поле | Значення |
| --- | --- |
| Bug ID | BUG-016 |
| Title | ModelSearch.search повертає весь каталог для порожнього, пробільного і null-запиту |
| Severity / Priority | Normal / P3 |
| Linked test / requirement | ModelSearchTest.blankQueryGivesEmptyResult, набори [1–3]; BL-60 |
| Reporter | Кінаш Єгор, 11.11.2026 |
| Reviewer | Асташов Іван, 11.11.2026 — чек-лист 7/7 |
| Status | New |

**Description.** BL-60: порожній, пробільний або null-запит повертає порожній список. Метод для таких запитів повертає копію всього каталогу. Пошук за непорожнім префіксом працює правильно. Severity Normal: функція працює, але на «порожній» пошук користувач отримує до 1 000 нерелевантних результатів. Priority P3.

**Steps to reproduce.**
1. `List<CarModel> catalog = List.of(new CarModel("Ferrari","F40",4.2,8), new CarModel("Lamborghini","Diablo",4.6,12));`
2. `new ModelSearch().search("", catalog)` (також `null` і `"   "`).
3. Або `mvn test -Dtest=ModelSearchTest#blankQueryGivesEmptyResult`.

**Actual result.** Список з усіма моделями каталогу.

**Expected result.** Порожній список (BL-60).

**Environment.** ENV-M1; `mvn test -Dtest=ModelSearchTest`.

## BUG-017. ModelSearch.sortByRating сортує за зростанням

| Поле | Значення |
| --- | --- |
| Bug ID | BUG-017 |
| Title | ModelSearch.sortByRating впорядковує моделі за зростанням рейтингу замість спадання |
| Severity / Priority | Major / P3 |
| Linked test / requirement | ModelSearchTest.sortByRatingDescending; BL-70 |
| Reporter | Кінаш Єгор, 11.11.2026 |
| Reviewer | Асташов Іван, 11.11.2026 — чек-лист 7/7 |
| Status | New |

**Description.** BL-70: новий список за **спаданням** рейтингу, рівні — за повною назвою. Компаратор не має `reversed()`, тому першою стоїть найгірша модель. Правило для рівних рейтингів і незмінність вхідного списку працюють правильно. Severity Major: сортування «за рейтингом» показує моделі у зворотному порядку. Priority P3.

**Steps to reproduce.**
1. Каталог: Ferrari F40 (4,2), Lamborghini Diablo (4,6), Lamborghini Murcielago (3,9).
2. `new ModelSearch().sortByRating(catalog)`.
3. Або `mvn test -Dtest=ModelSearchTest#sortByRatingDescending`.

**Actual result.** Murcielago (3,9), F40 (4,2), Diablo (4,6).

**Expected result.** Diablo (4,6), F40 (4,2), Murcielago (3,9) (BL-70).

**Environment.** ENV-M1; `mvn test -Dtest=ModelSearchTest`.
