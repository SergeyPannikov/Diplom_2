package api;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class ApiOrders {
    @Step("post запрос на ручку api/orders для создания заказа под авторизированным пользователем")
    public Response apiOrdersCreateOrderWithAuth(String accessToken, Object order) {
        return given().auth().oauth2(accessToken).header("Content-type", "application/json").and().body(order).and().when().
                post("api/orders");
    }

    @Step("post запрос на ручку api/orders для создания заказа под неавторизированным пользователем")
    public Response apiOrdersCreateOrderWithoutAuth(Object order) {
        return given().header("Content-type", "application/json").and().body(order).and().when().
                post("api/orders");
    }

    @Step("post запрос на ручку api/orders для создания заказа под авторизированным пользователем")
    public Response apiGetOrdersWithAuth(String accessToken) {
        return given().auth().oauth2(accessToken).header("Content-type", "application/json").and().when().
                get("api/orders");
    }

    @Step("post запрос на ручку api/orders для создания заказа под неавторизированным пользователем")
    public Response apiGetOrdersWithoutAuth() {
        return given().header("Content-type", "application/json").and().when().
                get("api/orders");
    }
}
