package site.nomoreparties.stellarburgers;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;

public class UserDataChangeTest {
    @Test
    @DisplayName("Изменения данных пользователя")
    public void userUpdateData() {
        UserClient userClient = new UserClient();
        User user = UserGenerator.getRandom();
        User userNewData = UserGenerator.getRandom();

        Response createResponse = userClient.create(user);

        String userToken = createResponse.path("accessToken");

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

        userClient.delete(userToken);
    }

    @Test
    @DisplayName("Изменения данных пользователя без авторизации")
    public void userUpdateDataWithoutAuthorization() {
        UserClient userClient = new UserClient();
        User user = UserGenerator.getRandom();
        User userNewData = UserGenerator.getRandom();

        Response createResponse = userClient.create(user);

        Response updateResponse = userClient.userUpdateData(null, userNewData);

        updateResponse
                .then()
                .log().all()
                .assertThat()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));

        String userToken = createResponse.path("accessToken");
        userClient.delete(userToken);
    }
}
