package site.nomoreparties.stellarburgers;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;

public class CreateOrderTest {
    @Test
    @DisplayName("Создание заказа с авторизацией")
    public void createOrderWithAuthorization() {
        UserClient userClient = new UserClient();
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

        Response createOrderResponse = createOrderClient.createOrder(userToken, createOrder);
        createOrderResponse
                .then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("name", is(notNullValue()))
                .body("order.ingredients", is(notNullValue()))
                .body("order._id", is(notNullValue()))
                .body("order.owner", is(notNullValue()))
                .body("order.status", is("done"))
                .body("order.name", is(notNullValue()))
                .body("order.createdAt", is(notNullValue()))
                .body("order.updatedAt", is(notNullValue()))
                .body("order.number", is(notNullValue()))
                .body("order.price", is(notNullValue()));

        userClient.delete(userToken);
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    public void createOrderWithoutAuthorization() {

        CreateOrderClient createOrderClient = new CreateOrderClient();
        IngredientsClient ingredientsClient = new IngredientsClient();
        Response ingredientsResponse = ingredientsClient.getIngredients();

        String firstIngredient = ingredientsResponse.path("data[0]._id");
        String secondIngredient = ingredientsResponse.path("data[1]._id");
        String[] ingredients = {firstIngredient, secondIngredient};

        CreateOrder createOrder = new CreateOrder(ingredients);

        Response createOrderResponse = createOrderClient.createOrder(null, createOrder);
        createOrderResponse
                .then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("name", is(notNullValue()))
                .body("order.number", is(notNullValue()));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void createOrderWithoutIngredients() {
        CreateOrderClient createOrderClient = new CreateOrderClient();
        String[] ingredients = {};
        CreateOrder emptyOrder = new CreateOrder(ingredients);

        Response createOrderResponse = createOrderClient.createOrder(null, emptyOrder);
        createOrderResponse
                .then()
                .log().all()
                .assertThat()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", is("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    public void createOrderWithIncorrectHashIngredients() {

        CreateOrderClient createOrderClient = new CreateOrderClient();
        IngredientsClient ingredientsClient = new IngredientsClient();
        Response ingredientsResponse = ingredientsClient.getIngredients();

        String firstIngredient = ingredientsResponse.path("data[0]._id");
        String secondIngredient = ingredientsResponse.path("data[1]._id");
        String[] ingredients = {firstIngredient + "de2", secondIngredient};

        CreateOrder createOrder = new CreateOrder(ingredients);

        Response createOrderResponse = createOrderClient.createOrder(null, createOrder);
        createOrderResponse
                .then()
                .log().all()
                .assertThat()
                .statusCode(500);
    }
}
