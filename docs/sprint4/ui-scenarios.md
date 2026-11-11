# UI-сценарії WebDriver — спринт 4

Demo: https://buggy.justtestit.org/ (Buggy Cars Rating). Пакет тестів: `src/test/java/ua/kpi/tzyapz/autorate/ui/`. Тег `ui`.
Автоматизовано кейси спринта 3 з пріоритетом High/Medium, що пройшли у Run 1 і **не потребують входу чи голосування**:
так тести можна запускати повторно, вони не змінюють спільні дані demo і не зберігають паролі в коді.
Кейси з власним обліковим записом (TC-024–TC-036) лишаються ручними: голосувати можна один раз на модель, тож повторний автоматичний прогін неможливий.

Спільне: новий браузер на кожен тест (`@BeforeEach` / `@AfterEach driver.quit()`), headless, вікно 1280×900, `WebDriverWait` 15 с, без `Thread.sleep` і неявних очікувань.
Запуск: `mvn test -DexcludedGroups=none -Dtest='*UiTest'`; браузер — `-Dbrowser=chrome|firefox|edge`; показати вікно — `-Dheadless=false`.

| TC | Пріоритет | Тест-метод | Кроки | Локатори | Очікування | Перевірки |
| --- | --- | --- | --- | --- | --- | --- |
| TC-001 | High | HomePageUiTest.homeBlocksOpenMakeModelAndOverall | головна → клік Popular Make → головна → Popular Model → головна → Overall Rating | `a[href^='/make/'] img`, `a[href^='/model/'] img`, `a[href='/overall'] img` | видимість зображення блоку; `urlContains`, `urlMatches("/overall$")` | м'яко: 4 тексти блоків; жорстко: адреси переходів |
| TC-013 | High | HomePageUiTest.headerLoginFormMasksPassword | відкрити /register, оглянути верхню панель | `input[name='login']`, `input[name='password']`, XPath кнопок за текстом і класом | видимість поля Login | м'яко: placeholder «Login», type=password, кнопки Login і Register |
| TC-003 | High | RegistrationUiTest.emptyFormHasAllFieldsAndDisabledRegister | відкрити /register | `id=username`, `firstName`, `lastName`, `password`, `confirmPassword`; `my-register button[type='submit']` | видимість поля Login | м'яко: 5 порожніх полів, Cancel, Register неактивна |
| TC-004 | Medium | RegistrationUiTest.clearedFirstNameShowsRequiredError | ввести й очистити First Name, фокус на Last Name, знову ввести | `id=firstName`; XPath `div.alert-danger` з текстом | видимість / зникнення повідомлення | «First Name is required», ліва межа rgba(169, 68, 66), Register неактивна; після введення — зникає |
| TC-012 | High | RegistrationUiTest.passwordMismatchShowsErrorUntilFixed | заповнити форму, Confirm = Auto5678, потім Auto1234; форму не надсилати | ids полів; XPath повідомлення | видимість / зникнення «Passwords do not match»; `elementToBeClickable` | Register неактивна при розбіжності й активна після виправлення |
| TC-015 | High | OverallRatingUiTest.tableHasHeadersAndMakeLinkOpensMake | /overall → клік марки в рядку Diablo | `table.cars thead th`; XPath рядка за назвою моделі | рядки таблиці; `elementToBeClickable`; `urlContains("/make/")` | м'яко: 6 заголовків, View more; жорстко: сторінка Lamborghini |
| TC-022 | Medium | OverallRatingUiTest.nextButtonOpensSecondPage | /overall → » | `my-pager` (« / » за текстом), `table.cars tbody tr` | `textToBePresentInElementLocated("page 2 of")`; лямбда — змінився перший рядок | 5 рядків і « неактивна на 1-й сторінці; ≤ 5 рядків і інший набір на 2-й |
| TC-019 | High | ModelPageUiTest.guestCannotVote | відкрити сторінку Diablo без входу | `p.card-text`, `id=comment`, XPath кнопки Vote! | видимість повідомлення | м'яко: «You need to be logged in to vote.», немає поля Comment і кнопки Vote! |

Стабільність: повний UI-набір пройшов у трьох прогонах поспіль 11.11.2026 (8/8, Chrome for Testing 157, headless).

## Selenium IDE

Проєкт [`selenium-ide/autorate.side`](../../selenium-ide/autorate.side), базовий URL `https://buggy.justtestit.org`:
- **TC-019 Guest cannot vote** — `open`, `waitForElementVisible`, `assertText` (жорстка), `verifyElementNotPresent` ×2 (м'які);
- **TC-012 Password mismatch** — `type` ×6, `waitForElementVisible` / `waitForElementNotVisible`, `assertElementPresent`, `verifyNotEditable` / `verifyEditable`; форма не надсилається.

Проєкт відтворено засобом `selenium-side-runner` 4.0.13: 2 passed. Експорт «Java JUnit» виконано офіційним модулем експорту Selenium IDE (`@seleniumhq/code-export-java-junit`) — `selenium-ide/export/TC019GuestcannotvoteTest.java`, `TC012PasswordmismatchTest.java`. Це чернетки у стилі JUnit 4 (Hamcrest, `new ChromeDriver()` без headless) — вони лежать поза `src/` і не збираються; адаптовані за таблицею README стартового проєкту JUnit 5-версії тих самих сценаріїв — `ModelPageUiTest.guestCannotVote` і `RegistrationUiTest.passwordMismatchShowsErrorUntilFixed`.
