package site.nomoreparties.stellarburgers;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class UserClient extends RestClient {
    private static final String USER_CREATE_PATH = "/api/auth/register";
    private static final String USER_LOGIN_PATH = "/api/auth/login";
    private static final String USER_PATH = "/api/auth/user";
    @Step("Создание пользователя")
    public Response create(User user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .log().all()
                .when()
                .post(USER_CREATE_PATH);
    }

    @Step("Удаление пользователя")
    public Response delete(String tokenUser) {
        return given()
                .header("Authorization", tokenUser)
                .spec(getBaseSpec())
                .log().all()
                .when()
                .delete(USER_PATH);
    }
    @Step("Авторизация пользователя")
    public Response login(User user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .log().all()
                .when()
                .post(USER_LOGIN_PATH);
    }

    @Step("Обновление информации о пользователе")
    public Response userUpdateData(String tokenUser, User user) {
        RequestSpecification requestSpec = given()
                .spec(getBaseSpec())
                .body(user)
                .log().all();
        if (tokenUser != null) {
            requestSpec.header("Authorization", tokenUser);
        }
        return requestSpec
                .when()
                .patch(USER_PATH);
    }
}
