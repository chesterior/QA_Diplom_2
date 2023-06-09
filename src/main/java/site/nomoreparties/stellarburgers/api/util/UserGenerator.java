package site.nomoreparties.stellarburgers.api.util;

import org.apache.commons.lang3.RandomStringUtils;
import site.nomoreparties.stellarburgers.api.model.User;

import java.util.Random;

public class UserGenerator {
    public static User getRandom() {
        final String email = generateRandomEmail();;
        final String password = RandomStringUtils.randomAlphabetic(10);
        final String name = RandomStringUtils.randomAlphabetic(10);
        return new User(email, password, name);
    }
    private static String generateRandomEmail() {
        String[] domains = { "com", "net", "org", "gov", "edu" }; // Допустимые домены
        String email = RandomStringUtils.randomAlphabetic(5, 10); // Случайные буквы для имени пользователя
        email += "@" + RandomStringUtils.randomAlphabetic(5, 10); // Случайные буквы для имени домена
        email += "." + domains[new Random().nextInt(domains.length)]; // Случайный домен из массива
        return email;
    }

}
