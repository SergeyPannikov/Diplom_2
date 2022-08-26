import api.ApiOrders;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class TestGetUserOrders {
    ApiOrders apiOrders = new ApiOrders();

    @Before
    public void setUp() {
        RestAssured.baseURI = TestRegisterUser.BASEURI;
    }

    @After
    public void deleteUser() {
        TestRegisterUser.deleteUser();
    }

    @DisplayName("Получение заказов авторизированного пользователя")
    @Test
    public void testSuccessfulUserOrdersWithAuth() {
        TestRegisterUser.CreateUser();
        TestRegisterUser.getAccessToken();
        Order order = new Order(new String[]{"61c0c5a71d1f82001bdaaa6d", "609646e4dc916e00276b2870", "61c0c5a71d1f82001bdaaa72"});
        Response createOrderWithAuth = apiOrders.apiOrdersCreateOrderWithAuth(TestRegisterUser.accessToken, order);
        createOrderWithAuth.then().body("success", equalTo(true)).
                and().body("name", equalTo("Spicy флюоресцентный бургер")).
                and().body("order.ingredients", notNullValue()).
                and().body("order.owner.name", equalTo("IvanTest")).
                and().body("order.owner.email", equalTo("testselenium@yandex.ru")).
                and().body("order.status", equalTo("done")).
                and().body("order.name", equalTo("Spicy флюоресцентный бургер"));
        Response getOrderWithAuth = apiOrders.apiGetOrdersWithAuth(TestRegisterUser.accessToken);
        getOrderWithAuth.then().statusCode(200).and().assertThat().body("success", equalTo(true))
                .and().assertThat().body("orders", notNullValue()).
                and().assertThat().body("orders[0].ingredients[0]", equalTo("61c0c5a71d1f82001bdaaa6d")).
                and().assertThat().body("orders[0].ingredients[1]", equalTo("609646e4dc916e00276b2870")).
                and().assertThat().body("orders[0].ingredients[2]", equalTo("61c0c5a71d1f82001bdaaa72")).
                and().assertThat().body("orders[0].status", equalTo("done")).
                and().assertThat().body("orders[0].name", equalTo("Spicy флюоресцентный бургер"));
    }

    @DisplayName("Получение заказов неавторизированного пользователя")
    @Test
    public void testUserOrdersWithoutAuth() {
        Response getOrderWithAuth = apiOrders.apiGetOrdersWithoutAuth();
        getOrderWithAuth.then().statusCode(401).and().assertThat().body("success", equalTo(false))
                .and().assertThat().body("message", equalTo("You should be authorised"));
    }
}
