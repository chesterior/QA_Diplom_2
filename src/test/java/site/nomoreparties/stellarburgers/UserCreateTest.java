package site.nomoreparties.stellarburgers;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import site.nomoreparties.stellarburgers.api.client.UserClient;
import site.nomoreparties.stellarburgers.api.model.User;
import site.nomoreparties.stellarburgers.api.util.UserGenerator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;

public class UserCreateTest {
    private User user;
    private UserClient userClient;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = UserGenerator.getRandom();
    }

    @Test
    @DisplayName("Успешное создание пользователя")
    public void userCanBeCreated() {
        Response createResponse = userClient.create(user);
        String userToken = createResponse.path("accessToken");
        user.setAccessToken(userToken);

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
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    public void userCreateWhoCreated() {
        Response createResponse = userClient.create(user);
        String userToken = createResponse.path("accessToken");
        user.setAccessToken(userToken);

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
    }

    @Test
    @DisplayName("Попытка создать пользователя без указания обязательного поля name")
    public void userCreateWithoutRequiredNameField() {
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

    @After
    @DisplayName("Удаление пользователя")
    public void deleteUser() {
        UserClient userClient = new UserClient();
        String userToken = user.getAccessToken();

        if (userToken != null) {
            Response deleteResponse = userClient.delete(userToken);
            deleteResponse
                    .then()
                    .assertThat()
                    .statusCode(202);
        }
    }
}
