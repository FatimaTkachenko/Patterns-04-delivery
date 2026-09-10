package ru.netology.patterns.tests;

import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import ru.netology.patterns.data.UserInfo;
import ru.netology.patterns.generator.DataGenerator;

import static com.codeborne.selenide.Selenide.*;

public class DeliveryTest {

    private UserInfo userInfo;

    @BeforeEach
    void setUp() {
        userInfo = DataGenerator.generateUserInfo();
        open("http://localhost:9999");
    }

    @Test
    void shouldReplanMeeting() {
        // Заполняем форму первый раз
        $("[data-test-id='city'] input").setValue(userInfo.getCity());
        $("[data-test-id='date'] input").doubleClick().setValue(userInfo.getDate());
        $("[data-test-id='name'] input").setValue(userInfo.getName());
        $("[data-test-id='phone'] input").setValue(userInfo.getPhone());
        $("[data-test-id='agreement']").click();
        $$("button").find(Condition.text("Запланировать")).click();

        // Проверяем полный текст сообщения с датой
        $("[data-test-id='success-notification'] .notification__content")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + userInfo.getDate()));

        // Генерируем новую дату
        UserInfo updatedUserInfo = DataGenerator.updateDate(userInfo, 5);

        // Закрываем уведомление
        $("[data-test-id='success-notification'] .icon-button").click();

        // Очищаем поле даты с помощью Ctrl+A и Backspace
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(updatedUserInfo.getDate());
        $$("button").find(Condition.text("Запланировать")).click();

        // Появляется диалог перепланирования
        $("[data-test-id='replan-notification'] button").click();

        // Проверяем полный текст сообщения с новой датой
        $("[data-test-id='success-notification'] .notification__content")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + updatedUserInfo.getDate()));
    }
}