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

public class UserLoginTest {
    private User user;
    private UserClient userClient;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = UserGenerator.getRandom();
    }

    @Test
    @DisplayName("Успешная авторизация пользователя")
    public void userCanBeLogin() {
        Response createResponse = userClient.create(user);
        String userToken = createResponse.path("accessToken");
        user.setAccessToken(userToken);

        User userLogin = new User(user.getEmail(), user.getPassword());

        Response loginResponse = userClient.login(userLogin);

        loginResponse
                .then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("accessToken", is(notNullValue()))
                .body("refreshToken", is(notNullValue()))
                .body("user.email", equalToIgnoringCase(user.getEmail()))
                .body("user.name", equalToIgnoringCase(user.getName()));
    }

    @Test
    @DisplayName("Авторизация пользователя с неверным email")
    public void userLoginWithWrongEmail() {
        Response createResponse = userClient.create(user);
        String userToken = createResponse.path("accessToken");
        user.setAccessToken(userToken);

        User userLogin = new User(user.getEmail() + "ru", user.getPassword());

        Response loginResponse = userClient.login(userLogin);

        loginResponse
                .then()
                .log().all()
                .assertThat()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Авторизация пользователя с неверным паролем")
    public void userLoginWithWrongPassword() {
        Response createResponse = userClient.create(user);
        String userToken = createResponse.path("accessToken");
        user.setAccessToken(userToken);

        User userLogin = new User(user.getEmail(), user.getPassword() + "jfnD");

        Response loginResponse = userClient.login(userLogin);

        loginResponse
                .then()
                .log().all()
                .assertThat()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @After
    @DisplayName("Удаление пользователя")
    public void deleteUser() {
        userClient = new UserClient();
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
