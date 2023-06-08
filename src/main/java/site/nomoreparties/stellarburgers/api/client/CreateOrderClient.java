package site.nomoreparties.stellarburgers.api.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import site.nomoreparties.stellarburgers.api.model.CreateOrder;


public class CreateOrderClient extends BaseClient {
    private static final String CREATE_ORDER_PATH = "/api/orders";

    @Step("Создание заказа")
    public Response createOrder(String tokenUser, CreateOrder createOrder) {
        RequestSpecification requestSpec = getRequestSpec(tokenUser)
                .body(createOrder);
        return requestSpec
                .when()
                .post(CREATE_ORDER_PATH);
    }
}
