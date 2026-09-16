package ru.netology.patterns.tests;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Allure;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import ru.netology.patterns.data.UserInfo;
import ru.netology.patterns.generator.DataGenerator;

import java.io.ByteArrayInputStream;

import static com.codeborne.selenide.Selenide.*;

public class DeliveryTest {

    private UserInfo userInfo;

    @BeforeEach
    void setUp() {
        SelenideLogger.addListener("AllureSelenide",
                new AllureSelenide()
                        .screenshots(true)
                        .savePageSource(true));

        userInfo = DataGenerator.generateUserInfo();
        open("http://localhost:9999");
    }

    @AfterEach
    void tearDown() {
        SelenideLogger.removeListener("AllureSelenide");
    }

    @Test
    void shouldReplanMeeting() {
        // Заполняем форму заказа карты
        $("[data-test-id='city'] input").setValue(userInfo.getCity());
        $("[data-test-id='date'] input").doubleClick().setValue(userInfo.getDate());
        $("[data-test-id='name'] input").setValue(userInfo.getName());
        $("[data-test-id='phone'] input").setValue(userInfo.getPhone());
        $("[data-test-id='agreement']").click();
        $$("button").find(Condition.text("Запланировать")).click();

        // Проверяем, что заявка успешно создана
        $("[data-test-id='success-notification'] .notification__title")
                .shouldHave(Condition.text("Успешно!"));
        $("[data-test-id='success-notification'] .notification__content")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + userInfo.getDate()));

        // Меняем дату на +5 дней
        UserInfo updatedUserInfo = DataGenerator.updateDate(userInfo, 5);

        // Закрываем уведомление
        $("[data-test-id='success-notification'] .icon-button").click();

        // Меняем дату
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(updatedUserInfo.getDate());
        $$("button").find(Condition.text("Запланировать")).click();

        // Подтверждаем перепланирование
        $("[data-test-id='replan-notification'] button").click();

        // Проверяем уведомление с новой датой
        $("[data-test-id='success-notification'] .notification__title")
                .shouldHave(Condition.text("Успешно!"));
        $("[data-test-id='success-notification'] .notification__content")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + updatedUserInfo.getDate()));

        // Прикрепляем финальный скриншот в отчёт Allure
        Allure.addAttachment(
                "Финальный скриншот",
                "image/png",
                new ByteArrayInputStream(
                        ((TakesScreenshot) WebDriverRunner.getWebDriver())
                                .getScreenshotAs(OutputType.BYTES)
                ),
                "png"
        );
    }
}