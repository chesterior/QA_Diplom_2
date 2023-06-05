package site.nomoreparties.stellarburgers;

import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class BaseClient extends RestClient {
    protected RequestSpecification getRequestSpec(String tokenUser) {
        RequestSpecification requestSpec = given()
                .spec(getBaseSpec());
        if (tokenUser != null) {
            requestSpec.header("Authorization", tokenUser);
        }
        return requestSpec;
    }
}
