package site.nomoreparties.stellarburgers;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class OrderUserClient extends BaseClient {
    private static final String ORDER_USER_PATH = "/api/orders";
    @Step("Получение заказов конкретного пользователя")
    public Response getOrderUser(String tokenUser) {
        RequestSpecification requestSpec = getRequestSpec(tokenUser)
                .log().all();
        return requestSpec
                .when()
                .get(ORDER_USER_PATH);
    }
}
