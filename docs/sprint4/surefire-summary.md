# Протокол запуску (Surefire) — спринт 4

Модуль: autorate-module **v1** (1.0.0). Середовище: Eclipse Temurin JDK 17.0.21, Apache Maven 3.9.9, JUnit 5.10.2, Selenium 4.21.0, Chrome for Testing 157 (завантажено Selenium Manager), Windows 11.
Дата прогону: 11.11.2026 (після виправлень за code review). Повні звіти — `target/surefire-reports/` (`*.txt`, `TEST-*.xml`) після запуску.

Surefire рахує кожен запуск параметризованого методу окремо, тому «Tests run» більше за кількість тест-методів.

## Прогін 1 — юніт-тести: `mvn clean test`

```
[INFO]  Tests run: 5,  Failures: 0, Errors: 0, Skipped: 0 -- in ua.kpi.tzyapz.autorate.ExampleTest
[ERROR] Tests run: 15, Failures: 4, Errors: 0, Skipped: 0 -- in ua.kpi.tzyapz.autorate.ModelSearchTest
[ERROR] Tests run: 25, Failures: 2, Errors: 0, Skipped: 0 -- in ua.kpi.tzyapz.autorate.RatingAggregatorTest
[INFO]  Tests run: 13, Failures: 0, Errors: 0, Skipped: 0 -- in ua.kpi.tzyapz.autorate.VoteValidatorStopWordsTest
[ERROR] Tests run: 17, Failures: 5, Errors: 0, Skipped: 0 -- in ua.kpi.tzyapz.autorate.VoteValidatorTest
[ERROR] Tests run: 75, Failures: 11, Errors: 0, Skipped: 0
[INFO]  BUILD FAILURE
```

## Прогін 2 — юніт- і UI-тести: `mvn test -DexcludedGroups=none`

```
[INFO]  Tests run: 2,  Failures: 0, Errors: 0, Skipped: 0 -- in ua.kpi.tzyapz.autorate.ui.HomePageUiTest
[INFO]  Tests run: 1,  Failures: 0, Errors: 0, Skipped: 0 -- in ua.kpi.tzyapz.autorate.ui.ModelPageUiTest
[INFO]  Tests run: 2,  Failures: 0, Errors: 0, Skipped: 0 -- in ua.kpi.tzyapz.autorate.ui.OverallRatingUiTest
[INFO]  Tests run: 3,  Failures: 0, Errors: 0, Skipped: 0 -- in ua.kpi.tzyapz.autorate.ui.RegistrationUiTest
[ERROR] Tests run: 83, Failures: 11, Errors: 0, Skipped: 0
```

UI-набір: 8 з 8 passed; стабільний — пройшов у трьох прогонах поспіль 11.11.2026.

## Failed — усі відповідають зареєстрованим дефектам модуля

| Тест | Запусків failed | Фактично (Surefire) | Дефект |
| --- | --- | --- | --- |
| RatingAggregatorTest.averageRoundsHalfUp | 1 | `expected: <4.3> but was: <4.2>` | BUG-011 |
| RatingAggregatorTest.topNRequiresAtLeastThreeVotes [2] | 1 | `expected: <true> but was: <false>` (3 голоси) | BUG-012 |
| VoteValidatorTest.sameUserOtherModelIsNotRepeat | 1 | `Порушення: [повторний голос]` | BUG-013 |
| VoteValidatorTest.ratingMustBeFromOneToFive [5] | 1 | `Порушення: [оцінка поза межами 1..5]` для 5 | BUG-014 |
| VoteValidatorTest.emptyCommentIsRejected [1–3] | 3 | `Порушення: []` для null, "", "   " | BUG-015 |
| ModelSearchTest.blankQueryGivesEmptyResult [1–3] | 3 | `expected: <true> but was: <false>` | BUG-016 |
| ModelSearchTest.sortByRatingDescending | 1 | перший — Murcielago (3,9) замість Diablo (4,6) | BUG-017 |

Разом 11 failed-запусків у 7 тест-методах = 7 дефектів; passed — 64 з 75. Інших падінь, помилок (Errors) і пропущених тестів немає.
`BUILD FAILURE` очікуваний: «червоні» тести лишаються у наборі для перевірки виправлень у v2 (спринт 5).

## Selenium IDE: відтворення проєкту

`selenium-ide/autorate.side` відтворено засобом Selenium `selenium-side-runner` 4.0.13 (Chrome, headless), 11.11.2026:

```
√ Running test TC-019 Guest cannot vote (8485 ms)
√ Running test TC-012 Password mismatch (4104 ms)
Tests: 2 passed, 2 total
```
