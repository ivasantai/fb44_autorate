# AutoRate QA — автотести продукту «АвтоРейт»

Дисципліна «Технології забезпечення якості програмних засобів», командний формат, ФБ-44.
Продукт: П5 «АвтоРейт» (slug `autorate`). Demo-застосунок: https://buggy.justtestit.org/.
Досьє якості: [docs/](docs/) — спринти 1–3 (моделі, вимоги, тест-кейси), спринт 4 — [docs/sprint4/](docs/sprint4/README.md).

## Склад команди і ролі у спринтах 4–5

| ПІБ | GitHub | Роль С4 | Роль С5 |
|-----|--------|---------|---------|
| Кінаш Єгор | @Shelt00n | Керівник тестування | Тест-аналітик |
| Рушай Євген | @yevh-rush | Тест-аналітик | Інженер з тестування |
| Поломарьова Дарина | @lidnech0k | Інженер з тестування | Скрам-майстер / рецензент |
| Асташов Іван | @ivasantai | Скрам-майстер / рецензент | Керівник тестування |

## Вимоги до середовища

- JDK 17 (Temurin), Maven 3.9 — перевірка: `mvn -v` показує `Java version: 17.…`
- Chrome, Firefox або Edge; драйвер встановлювати не потрібно — його (і за відсутності Chrome — Chrome for Testing) завантажує Selenium Manager
- JUnit 5 (Jupiter 5.10.2), Selenium 4 (selenium-java 4.21.0) — підтягуються з `pom.xml`
- Інтернет: для першого збирання (залежності Maven) і для UI-тестів (публічний demo)

## Як зібрати і запустити

```bash
git clone https://github.com/ivasantai/fb44_autorate.git
cd fb44_autorate
mvn clean test                                    # лише юніт-тести модуля (UI-тести вимкнені тегом ui)
mvn test -Dtest=VoteValidatorTest                 # один тест-клас
mvn test -DexcludedGroups=none -Dtest='*UiTest'   # лише WebDriver-сценарії
mvn test -DexcludedGroups=none                    # усі тести
mvn test -DexcludedGroups=none -Dtest=ModelPageUiTest -Dheadless=false   # UI-тест із видимим вікном
mvn test -DexcludedGroups=none -Dbrowser=firefox  # інший браузер: chrome (за замовчуванням), firefox, edge
```

Windows: якщо `mvn -v` показує не 17 — у PowerShell `$env:JAVA_HOME = "<шлях до jdk-17>"`; для кирилиці в консолі — `chcp 65001`.

Звіти Surefire: `target/surefire-reports/`. Підсумок прогону — [docs/sprint4/surefire-summary.md](docs/sprint4/surefire-summary.md).
Модуль бізнес-логіки: версія v1 (спринт 4) / v2 (спринт 5) — див. розділ «Версії».

**Очікуваний результат на v1:** `mvn clean test` → `Tests run: 75, Failures: 11` і `BUILD FAILURE`. Це не помилка збірки:
11 «червоних» запусків у 7 тестах фіксують 7 дефектів модуля v1 (BUG-011…BUG-017) і лишаються у наборі, щоб у спринті 5 перевірити виправлення.
UI-набір (`-DexcludedGroups=none -Dtest='*UiTest'`) — `Tests run: 8, Failures: 0`.

## Структура

```
pom.xml                                       # JDK 17, JUnit 5.10, Selenium 4.21, Surefire; тег ui вимкнено за замовчуванням
src/main/java/ua/kpi/tzyapz/autorate/         # модуль бізнес-логіки v1: RatingAggregator, VoteValidator, ModelSearch, Vote, CarModel
src/test/java/ua/kpi/tzyapz/autorate/         # юніт-тести JUnit 5: <Клас>Test.java
src/test/java/ua/kpi/tzyapz/autorate/ui/      # WebDriver-сценарії: <Сторінка>UiTest.java, спільний UiTestBase
selenium-ide/autorate.side                    # проєкт Selenium IDE (2 сценарії); export/ — Java-чернетки (JUnit 4, не збираються)
docs/sprint4/                                 # матриця покриття, UI-сценарії, протокол Surefire, дефекти модуля
docs/sprint1 … docs/sprint3/                  # досьє якості попередніх спринтів
```

## Що покрито

| Тест | Вимога SRS | Тест-кейс | Автор |
|------|-----------|-----------|-------|
| `RatingAggregatorTest` (11 методів) | BL-10, BL-20 | — | @yevh-rush |
| `VoteValidatorTest` (7 методів) | BL-30, BL-40, BL-50 | — | @ivasantai |
| `VoteValidatorStopWordsTest` (5 методів) | BL-80 | — | @lidnech0k |
| `ModelSearchTest` (8 методів) | BL-60, BL-70 | — | @Shelt00n |
| `HomePageUiTest` (2) | F-10, GUI-10 | TC-001, TC-013 | @lidnech0k |
| `RegistrationUiTest` (3) | F-20, F-30, GUI-20, GUI-40 | TC-003, TC-004, TC-012 | @lidnech0k |
| `OverallRatingUiTest` (2) | F-70, F-140, GUI-60 | TC-015, TC-022 | @lidnech0k |
| `ModelPageUiTest` (1) | F-90, S-10 | TC-019 | @lidnech0k |

Детально (вимога → правило → тест → рецензент): [docs/sprint4/coverage-matrix.md](docs/sprint4/coverage-matrix.md); UI-сценарії з локаторами: [docs/sprint4/ui-scenarios.md](docs/sprint4/ui-scenarios.md).

## Дефекти

Дефекти demo (спринт 3, BUG-001…BUG-010): [docs/sprint3/bug-reports.md](docs/sprint3/bug-reports.md).
Дефекти модуля v1 (спринт 4, мітка `module`, BUG-011…BUG-017): [docs/sprint4/bug-reports.md](docs/sprint4/bug-reports.md).
Кожен дефект модуля має тест, що його фіксує; ID звіту — у коментарі тесту.

## Версії

- v1 — спринт 4: тег `v1-sprint4`, результат `mvn clean test`: 64 passed / 11 failed (75 запусків; 7 дефектів модуля); UI: 8 passed / 0 failed
- v2 — спринт 5 (заплановано): заміна `src/main/java` на модуль v2, тег `v2-sprint5`, журнал регресії
