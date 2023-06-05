package site.nomoreparties.stellarburgers;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;

public class OrderUserTest {
    @Test
    @DisplayName("Получение заказов конкретного пользователя с авторизацией")
    public void createOrderWithAuthorization() {
        UserClient userClient = new UserClient();
        OrderUserClient orderUserClient = new OrderUserClient();
        User user = UserGenerator.getRandom();
        Response createResponse = userClient.create(user);

        String userToken = createResponse.path("accessToken");

        CreateOrderClient createOrderClient = new CreateOrderClient();
        IngredientsClient ingredientsClient = new IngredientsClient();
        Response ingredientsResponse = ingredientsClient.getIngredients();

        String firstIngredient = ingredientsResponse.path("data[0]._id");
        String secondIngredient = ingredientsResponse.path("data[1]._id");
        String[] ingredients = {firstIngredient, secondIngredient};

        CreateOrder createOrder = new CreateOrder(ingredients);

        createOrderClient.createOrder(userToken, createOrder);

        Response createOrderResponse = orderUserClient.getOrderUser(userToken);
        createOrderResponse
                .then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("orders", is(notNullValue()))
                .body("total", is(notNullValue()))
                .body("totalToday", is(notNullValue()));

        userClient.delete(userToken);
    }

    @Test
    @DisplayName("Получение заказов без авторизации")
    public void createOrderWithoutAuthorization() {
        OrderUserClient orderUserClient = new OrderUserClient();

        Response createOrderResponse = orderUserClient.getOrderUser(null);
        createOrderResponse
                .then()
                .log().all()
                .assertThat()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", is(notNullValue()));
    }
}
