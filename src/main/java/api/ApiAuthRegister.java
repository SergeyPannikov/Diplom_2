package api;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class ApiAuthRegister {
    @Step("post запрос на ручку api/auth/register")
    public Response apiAuthRegister(Object body) {
        return given().header("Content-type", "application/json").
                and().body(body).when().post("api/auth/register");
    }
}
