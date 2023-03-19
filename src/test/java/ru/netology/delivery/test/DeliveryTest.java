package ru.netology.delivery.test;

import com.codeborne.selenide.Condition;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import ru.netology.delivery.data.DataGenerator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DeliveryTest {
    String planningDate = generateDate(3);

    public String generateDate(int days) {

        return LocalDate.now().plusDays(days).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
    }

    @Test
    @DisplayName("Should plan meeting")
    void shouldPlanMeeting() {
        var validUser = DataGenerator.Registration.generateUser("ru");

// Fill in the form to plan the first meeting
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(planningDate);
        $("[data-test-id=city] input").setValue(DataGenerator.generateCity("ru"));
        $("[data-test-id=name] input").setValue(validUser.getName());
        $("[data-test-id=phone] input").setValue(validUser.getPhone());
        $("[data-test-id=agreement] span").click();

        $x("//*[@class='button__content']").click();
// check that the first meeting is planned
        $x("//*[contains(text(), 'Встреча успешно запланирована')]")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + planningDate));
    }


    @Test
    @DisplayName("Replan meeting")
    void ReplanMeeting() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 3;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        var daysToAddForSecondMeeting = 7;
        var secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);

        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(firstMeetingDate);
        $("[data-test-id=city] input").setValue(validUser.getCity());
        $("[data-test-id=name] input").setValue(validUser.getName());
        $("[data-test-id=phone] input").setValue(validUser.getPhone());
        $("[data-test-id=agreement] span").click();
        $x("//*[@class='button__content']").click();
        $x("//*[contains(text(), 'Встреча успешно запланирована')]")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + firstMeetingDate));
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(secondMeetingDate);
        $x("//*[@class='button__content']").click();

        $x("//*[contains(text(), 'У вас уже запланирована встреча на другую дату. Перепланировать?')]").shouldBe(Condition.visible);
        $x("//span[contains(text(), 'Перепланировать')]").click();
        $x("//*[contains(text(), 'Встреча успешно запланирована')]")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + secondMeetingDate));
    }

    @Test
    @DisplayName("Should submit form with valid phone number")
    void shouldSubmitFormWithValidPhoneNumber() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var validPhoneNumber = "+7 9231233321";
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(planningDate);
        $("[data-test-id=city] input").setValue(validUser.getCity());
        $("[data-test-id=name] input").setValue(validUser.getName());
        $("[data-test-id=phone] input").setValue(validPhoneNumber);
        $("[data-test-id=agreement] span").click();
        $x("//*[@class='button__content']").click();
        $x("//*[contains(text(), 'Встреча успешно запланирована')]").shouldBe(Condition.visible);
    }

    @Test
    @DisplayName("Should not submit form with invalid phone number")
    void shouldNotSubmitFormWithInvalidPhoneNumber() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var invalidPhoneNumber = "123456";
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(planningDate);
        $("[data-test-id=city] input").setValue(validUser.getCity());
        $("[data-test-id=name] input").setValue(validUser.getName());
        $("[data-test-id=phone] input").setValue(invalidPhoneNumber);
        $("[data-test-id=agreement] span").click();
        $x("//*[@class='button__content']").click();
        $x("//*[contains(text(), 'Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.')]").shouldBe(Condition.visible);
    }

    @Test
    public void testUncheckedCheckbox() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(planningDate);
        $("[data-test-id=city] input").setValue(validUser.getCity());
        $("[data-test-id=name] input").setValue(validUser.getName());
        $("[data-test-id=phone] input").setValue(validUser.getPhone());
        $x("//*[@class='button__content']").click();
        String errorMessage = $("[data-test-id=agreement].input_invalid .checkbox__text").getText().trim();
        assertEquals("Я соглашаюсь с условиями обработки и использования моих персональных данных", errorMessage);
    }

    @Test
    public void testEmptyCityField() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(planningDate);
        $("[data-test-id=name] input").setValue(validUser.getName());
        $("[data-test-id=phone] input").setValue(validUser.getPhone());
        $("[data-test-id=agreement] span").click();
        $x("//*[@class='button__content']").click();
        String errorMessage = $("[data-test-id=city].input_invalid .input__sub").getText().trim();
        assertEquals("Поле обязательно для заполнения", errorMessage);
    }

    @Test
    public void testEmptyNameField() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(planningDate);
        $("[data-test-id=city] input").setValue(validUser.getCity());
        $("[data-test-id=phone] input").setValue(validUser.getPhone());
        $("[data-test-id=agreement] span").click();
        $x("//*[@class='button__content']").click();
        String errorMessage = $("[data-test-id='name'].input_invalid .input__sub").getText().trim();
        assertEquals("Поле обязательно для заполнения", errorMessage);
    }

    @Test
    public void testEmptyPhoneField() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(planningDate);
        $("[data-test-id=city] input").setValue(validUser.getCity());
        $("[data-test-id=name] input").setValue(validUser.getName());
        $("[data-test-id=agreement] span").click();
        $x("//*[@class='button__content']").click();
        String errorMessage = $("[data-test-id=phone].input_invalid .input__sub").getText().trim();
        assertEquals("Поле обязательно для заполнения", errorMessage);
    }


}