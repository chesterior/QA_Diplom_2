package site.nomoreparties.stellarburgers;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;

public class UserCreateTest {

    @Test
    @DisplayName("Успешное создание пользователя")
    public void userCanBeCreated() {
        UserClient userClient = new UserClient();
        User user = UserGenerator.getRandom();

        Response createResponse = userClient.create(user);

        createResponse
                .then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalToIgnoringCase(user.getEmail()))
                .body("user.name", equalToIgnoringCase(user.getName()))
                .body("accessToken", is(notNullValue()))
                .body("refreshToken", is(notNullValue()));

        String userToken = createResponse.path("accessToken");
        Response deleteResponse = userClient.delete(userToken);
        deleteResponse
                .then()
                .log().all()
                .assertThat()
                .statusCode(202);
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    public void userCreateWhoCreated() {
        UserClient userClient = new UserClient();
        User user = UserGenerator.getRandom();

        Response createResponse = userClient.create(user);

        createResponse
                .then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalToIgnoringCase(user.getEmail()))
                .body("user.name", equalToIgnoringCase(user.getName()))
                .body("accessToken", is(notNullValue()))
                .body("refreshToken", is(notNullValue()));

        User userSecond = new User(user.getEmail(), user.getName(), user.getName());
        Response createSecondResponse = userClient.create(userSecond);

        createSecondResponse
                .then()
                .log().all()
                .assertThat()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));


        String userToken = createResponse.path("accessToken");
        userClient.delete(userToken);
    }

    @Test
    @DisplayName("Попытка создать пользователя без указания обязательного поля name")
    public void userCreateWithoutRequiredNameField() {
        UserClient userClient = new UserClient();
        User user = UserGenerator.getRandom();
        User userWithoutName = new User(user.getEmail(), user.getPassword());

        Response createResponse = userClient.create(userWithoutName);

        createResponse
                .then()
                .log().all()
                .assertThat()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}
