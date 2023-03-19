package ru.netology.delivery.test;

import com.codeborne.selenide.Condition;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import org.openqa.selenium.Keys;

import ru.netology.delivery.data.DataGenerator;

import static com.codeborne.selenide.Selenide.*;

class DeliveryTest {
    String planningDate = DataGenerator.generateDate(3);

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }
    @BeforeEach
    void setup() {
        open("http://localhost:9999");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
    }

    @Test
    @DisplayName("Replan meeting")
    void ReplanMeeting() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 3;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        var daysToAddForSecondMeeting = 7;
        var secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);
        $("[data-test-id=date] input").setValue(firstMeetingDate);
        $("[data-test-id=city] input").setValue(validUser.getCity());
        $("[data-test-id=name] input").setValue(validUser.getName());
        $("[data-test-id=phone] input").setValue(validUser.getPhone());
        $("[data-test-id=agreement] span").click();
        $x("//*[@class='button__content']").click();
        $("[data-test-id='success-notification']")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + firstMeetingDate));
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(secondMeetingDate);
        $x("//*[@class='button__content']").click();
        $("[data-test-id='replan-notification']").shouldBe(Condition.visible);
        $x("//span[contains(text(), 'Перепланировать')]").click();
        $("[data-test-id='success-notification']")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + secondMeetingDate));
    }

    @Test
    @DisplayName("Should submit form with valid phone number")
    void shouldSubmitFormWithValidPhoneNumber() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var validPhoneNumber = DataGenerator.generatePhone("ru");
        $("[data-test-id=date] input").setValue(planningDate);
        $("[data-test-id=city] input").setValue(validUser.getCity());
        $("[data-test-id=name] input").setValue(validUser.getName());
        $("[data-test-id=phone] input").setValue(validPhoneNumber);
        $("[data-test-id=agreement] span").click();
        $x("//*[@class='button__content']").click();
        $("[data-test-id='success-notification']").shouldBe(Condition.visible);
    }

    @Test
    @DisplayName("Should not submit form with invalid phone number")
    void shouldNotSubmitFormWithInvalidPhoneNumber() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var invalidPhoneNumber = DataGenerator.generateInvalidPhone("ru");
        $("[data-test-id=date] input").setValue(planningDate);
        $("[data-test-id=city] input").setValue(validUser.getCity());
        $("[data-test-id=name] input").setValue(validUser.getName());
        $("[data-test-id=phone] input").setValue(invalidPhoneNumber);
        $("[data-test-id=agreement] span").click();
        $x("//*[@class='button__content']").click();
        $x("//*[contains(text(), 'Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.')]")
                .shouldBe(Condition.visible);
    }

    @Test
    public void testUncheckedCheckbox() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        $("[data-test-id=date] input").setValue(planningDate);
        $("[data-test-id=city] input").setValue(validUser.getCity());
        $("[data-test-id=name] input").setValue(validUser.getName());
        $("[data-test-id=phone] input").setValue(validUser.getPhone());
        $x("//*[@class='button__content']").click();
        $("[data-test-id=agreement].input_invalid .checkbox__text")
                .shouldHave(Condition.text("Я соглашаюсь с условиями обработки и использования моих персональных данных"));
    }

    @Test
    public void testEmptyCityField() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        $("[data-test-id=date] input").setValue(planningDate);
        $("[data-test-id=name] input").setValue(validUser.getName());
        $("[data-test-id=phone] input").setValue(validUser.getPhone());
        $("[data-test-id=agreement] span").click();
        $x("//*[@class='button__content']").click();
        $("[data-test-id=city].input_invalid .input__sub")
                .shouldHave(Condition.text("Поле обязательно для заполнения"));
    }

    @Test
    public void testEmptyNameField() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        $("[data-test-id=date] input").setValue(planningDate);
        $("[data-test-id=city] input").setValue(validUser.getCity());
        $("[data-test-id=phone] input").setValue(validUser.getPhone());
        $("[data-test-id=agreement] span").click();
        $x("//*[@class='button__content']").click();
        $("[data-test-id=name].input_invalid .input__sub").shouldHave(Condition.text("Поле обязательно для заполнения"));

    }

    @Test
    public void testEmptyPhoneField() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        $("[data-test-id=date] input").setValue(planningDate);
        $("[data-test-id=city] input").setValue(validUser.getCity());
        $("[data-test-id=name] input").setValue(validUser.getName());
        $("[data-test-id=agreement] span").click();
        $x("//*[@class='button__content']").click();
        $("[data-test-id=phone].input_invalid .input__sub")
                .shouldHave(Condition.text("Поле обязательно для заполнения"));
    }
}
