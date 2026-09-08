package ru.netology.patterns.generator;

import com.github.javafaker.Faker;
import ru.netology.patterns.data.UserInfo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public class DataGenerator {

    private static final Faker faker = new Faker(new Locale("ru"));

    private DataGenerator() {}

    public static UserInfo generateUserInfo() {
        String city = generateCity();
        String name = generateName();
        String phone = generatePhone();
        String date = generateDate(3);
        return new UserInfo(city, name, phone, date);
    }

    public static UserInfo updateDate(UserInfo originalUser, int daysToAdd) {
        return new UserInfo(
                originalUser.getCity(),
                originalUser.getName(),
                originalUser.getPhone(),
                generateDate(daysToAdd)
        );
    }

    public static String generateDate(int shift) {
        return LocalDate.now().plusDays(shift).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public static String generateCity() {
        String[] cities = {
                "Москва", "Санкт-Петербург", "Челябинск", "Ханты-Мансийск",
                "Калуга", "Уфа", "Якутск", "Псков"
        };
        return cities[ThreadLocalRandom.current().nextInt(cities.length)];
    }

    public static String generateName() {
        return faker.name().firstName() + " " + faker.name().lastName();
    }

    public static String generatePhone() {
        return "+7" + faker.number().digits(10);
    }
}