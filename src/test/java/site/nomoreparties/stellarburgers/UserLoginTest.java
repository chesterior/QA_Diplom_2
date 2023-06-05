package site.nomoreparties.stellarburgers;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;

public class UserLoginTest {
    @Test
    @DisplayName("Успешная авторизация пользователя")
    public void userCanBeLogin() {
        UserClient userClient = new UserClient();
        User user = UserGenerator.getRandom();

        Response createResponse = userClient.create(user);

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


        String userToken = createResponse.path("accessToken");
        userClient.delete(userToken);
    }

    @Test
    @DisplayName("Авторизация пользователя с неверным email")
    public void userLoginWithWrongEmail() {
        UserClient userClient = new UserClient();
        User user = UserGenerator.getRandom();

        Response createResponse = userClient.create(user);

        User userLogin = new User(user.getEmail() + "ru", user.getPassword());

        Response loginResponse = userClient.login(userLogin);

        loginResponse
                .then()
                .log().all()
                .assertThat()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));


        String userToken = createResponse.path("accessToken");
        userClient.delete(userToken);
    }

    @Test
    @DisplayName("Авторизация пользователя с неверным паролем")
    public void userLoginWithWrongPassword() {
        UserClient userClient = new UserClient();
        User user = UserGenerator.getRandom();

        Response createResponse = userClient.create(user);

        User userLogin = new User(user.getEmail(), user.getPassword() + "jfnD");

        Response loginResponse = userClient.login(userLogin);

        loginResponse
                .then()
                .log().all()
                .assertThat()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));


        String userToken = createResponse.path("accessToken");
        userClient.delete(userToken);
    }
}
