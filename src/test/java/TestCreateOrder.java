import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class TestCreateOrder {

    private String bun;
    private String ingredientSauce;
    private String ingredientFilling;

    public TestCreateOrder(String bun, String ingredientSauce, String ingredientFilling){
        this.bun = bun;
        this.ingredientSauce = ingredientSauce;
        this.ingredientFilling = ingredientFilling;
    }

    @Parameterized.Parameters
    public static Object[][] getOrderFields() {
        return new Object[][] {
                {"61c0c5a71d1f82001bdaaa6d","609646e4dc916e00276b2870","61c0c5a71d1f82001bdaaa72"},
                {null,null,null },
                {"61c0c5a71d1f8200144444","609646e4dc916e00276b2870","61c0c5a71d1f82001bdaaa72" },
        };
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = TestRegisterUser.BASEURI;
    }

    @After
    public void deleteUser(){
        TestRegisterUser.deleteUser();
    }


    @DisplayName("Cоздание заказа под авторизованным пользователем")
    @Test
    public void testCreateOrderWithAuth(){
        TestRegisterUser.CreateUser();
        TestRegisterUser.getAccessToken();
        Order order = new Order(new String[]{bun,ingredientSauce,ingredientFilling});
        int statusCode = given().auth().oauth2(TestRegisterUser.accessToken).header("Content-type", "application/json").and().body(order).and().when().
                post("api/orders").then().extract().statusCode();
        if (statusCode == 200) {
            given().auth().oauth2(TestRegisterUser.accessToken).header("Content-type", "application/json").and().body(order).and().when().
                    post("api/orders").then().body("success",equalTo(true)).
                    and().body("name",equalTo("Spicy флюоресцентный бургер")).
                    and().body("order.ingredients",notNullValue()).
                    and().body("order.owner.name",equalTo("IvanTest")).
                    and().body("order.owner.email",equalTo("testselenium@yandex.ru")).
                    and().body("order.status",equalTo("done")).
                    and().body("order.name",equalTo("Spicy флюоресцентный бургер")) ;
        } else if (statusCode == 400) {
            given().auth().oauth2(TestRegisterUser.accessToken).header("Content-type", "application/json").and().body(order).and().when().
                    post("api/orders").then().body("success",equalTo(false)).
                    and().body("message",equalTo("One or more ids provided are incorrect"));
        } else if (statusCode == 500){
            System.out.println("Internal Server Error");
        } else {
            System.out.println("Такой код ответа не найден "+ statusCode);
        }
        TestRegisterUser.deleteUser();
    }

    @DisplayName("Создание заказа без авторизации клиента")
    @Test
    public void testCreateOrderWithoutAuth(){
        Order order = new Order(new String[]{bun,ingredientSauce,ingredientFilling});
        int statusCode = given().header("Content-type", "application/json").and().body(order).and().when().
                post("api/orders").then().extract().statusCode();
        if (statusCode == 200) {
            given().header("Content-type", "application/json").and().body(order).and().when().
                    post("api/orders").then().body("success",equalTo(true)).
                    and().body("name",equalTo("Spicy флюоресцентный бургер"));
        } else if (statusCode == 400) {
            given().header("Content-type", "application/json").and().body(order).and().when().
                    post("api/orders").then().body("success",equalTo(false)).
                    and().body("message",equalTo("One or more ids provided are incorrect"));
        } else if (statusCode == 500){
            System.out.println("Internal Server Error");
        } else {
            System.out.println("Такой код ответа не найден "+ statusCode);
        }
    }


}
