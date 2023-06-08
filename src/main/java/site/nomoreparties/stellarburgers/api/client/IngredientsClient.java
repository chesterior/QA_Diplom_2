package site.nomoreparties.stellarburgers.api.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class IngredientsClient extends RestClient {
    private static final String GET_INGREDIENTS_PATH = "/api/ingredients";
    @Step("Получение данных об ингредиентах")
    public Response getIngredients() {
        return given()
                .spec(getBaseSpec())
                .log().all()
                .when()
                .get(GET_INGREDIENTS_PATH);
    }
}
