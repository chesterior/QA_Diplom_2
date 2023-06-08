package site.nomoreparties.stellarburgers;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import site.nomoreparties.stellarburgers.api.client.CreateOrderClient;
import site.nomoreparties.stellarburgers.api.client.IngredientsClient;
import site.nomoreparties.stellarburgers.api.client.OrderUserClient;
import site.nomoreparties.stellarburgers.api.client.UserClient;
import site.nomoreparties.stellarburgers.api.model.CreateOrder;
import site.nomoreparties.stellarburgers.api.model.User;
import site.nomoreparties.stellarburgers.api.util.UserGenerator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;

public class OrderUserTest {
    private User user;
    private UserClient userClient;
    private OrderUserClient orderUserClient;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = UserGenerator.getRandom();
        orderUserClient = new OrderUserClient();
    }

    @Test
    @DisplayName("Получение заказов конкретного пользователя с авторизацией")
    public void createOrderWithAuthorization() {
        Response createResponse = userClient.create(user);

        String userToken = createResponse.path("accessToken");
        user.setAccessToken(userToken);

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
    }

    @Test
    @DisplayName("Получение заказов без авторизации")
    public void createOrderWithoutAuthorization() {

        Response createOrderResponse = orderUserClient.getOrderUser(null);
        createOrderResponse
                .then()
                .log().all()
                .assertThat()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", is(notNullValue()));
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
