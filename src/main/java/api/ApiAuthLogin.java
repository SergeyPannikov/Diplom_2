package api;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class ApiAuthLogin {
    @Step("post запрос на ручку api/auth/login")
    public Response apiAuthLoginPost(Object body) {
        return given().header("Content-type", "application/json").
                and().body(body).when().post("api/auth/login");
    }
}
