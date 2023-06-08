package site.nomoreparties.stellarburgers;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import site.nomoreparties.stellarburgers.api.client.OrderUserClient;
import site.nomoreparties.stellarburgers.api.client.UserClient;
import site.nomoreparties.stellarburgers.api.model.User;
import site.nomoreparties.stellarburgers.api.util.UserGenerator;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;

public class UserDataChangeTest {
    private User user;
    private UserClient userClient;
    private User userNewData;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = UserGenerator.getRandom();
        userNewData = UserGenerator.getRandom();
    }

    @Test
    @DisplayName("Изменения данных пользователя")
    public void userUpdateData() {
        Response createResponse = userClient.create(user);
        String userToken = createResponse.path("accessToken");
        user.setAccessToken(userToken);

        Response updateResponse = userClient.userUpdateData(userToken, userNewData);

        updateResponse
                .then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalToIgnoringCase(userNewData.getEmail()))
                .body("user.name", equalToIgnoringCase(userNewData.getName()));

        User userLogin = new User(userNewData.getEmail(), userNewData.getPassword());

        Response loginResponse = userClient.login(userLogin);
        loginResponse
                .then()
                .log().all()
                .assertThat()
                .statusCode(200);
    }

    @Test
    @DisplayName("Изменения данных пользователя без авторизации")
    public void userUpdateDataWithoutAuthorization() {
        Response createResponse = userClient.create(user);
        String userToken = createResponse.path("accessToken");
        user.setAccessToken(userToken);

        Response updateResponse = userClient.userUpdateData(null, userNewData);

        updateResponse
                .then()
                .log().all()
                .assertThat()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
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
