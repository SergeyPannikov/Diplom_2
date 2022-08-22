import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class TestRegisterUser {

    public static String accessToken;
    public final static String BASEURI = "https://stellarburgers.nomoreparties.site";
    public static UserAccount userAccount = new UserAccount("testselenium@yandex.ru","password123","IvanTest");

    @Before
    public void setUp() {
        RestAssured.baseURI = BASEURI;
    }

    @After
    public void deleteUserAfterTest(){
        deleteUser();
    }


    public static void deleteUser(){
        if (given().header("Content-type", "application/json").
                and().body(userAccount).when().post("api/auth/login").then().extract().statusCode() == 200) {
            getAccessToken();
            given().auth().oauth2(accessToken).and().when().delete("api/auth/user").then().statusCode(202).and().assertThat().body("success", equalTo(true))
                    .and().assertThat().body("message",equalTo("User successfully removed"));
        }
    }

    public static void getAccessToken(){
        if (given().header("Content-type", "application/json").
                and().body(userAccount).when().post("api/auth/login").then().extract().statusCode() == 200) {
        String accessTokenString = given().header("Content-type", "application/json").
                and().body(userAccount).when().post("api/auth/login").then().statusCode(200).and().extract().body().path("accessToken");
        accessToken = accessTokenString.substring(accessTokenString.indexOf(' ')+1);
        }
    }

    @DisplayName("Успешное создание пользователя, проверка кода успешного ответа и тела ответа об успешном создании пользователя")
    @Test
    public void testCreateUser(){
        CreateUser();
    }


    public static void CreateUser(){
        given().header("Content-type", "application/json").
                and().body(userAccount).when().post("api/auth/register").then().statusCode(200).and().assertThat().body("success",equalTo(true))
                .and().assertThat().body("user.email",equalTo("testselenium@yandex.ru"))
                .and().assertThat().body("user.name",equalTo("IvanTest"));
    }

    @DisplayName("Проверка создания пользователя, который уже зарегестрирован")
    @Test
    public void testErrorWhenCreateExistingUser(){
        CreateUser();
        given().header("Content-type", "application/json").
                and().body(userAccount).when().post("api/auth/register").then().statusCode(403)
                .and().assertThat().body("success",equalTo(false))
                .and().assertThat().body("message",equalTo("User already exists"));
    }

    @DisplayName("Проверка создания пользователя без обязательного поля емейла")
    @Test
    public void testErrorWhenCreateUserWithoutEmail(){
        UserAccount userAccountWithoutEmail = new UserAccount(null,"password123","IvanTest");
        given().header("Content-type", "application/json").
                and().body(userAccountWithoutEmail).when().post("api/auth/register").then().statusCode(403)
                .and().assertThat().body("success",equalTo(false))
                .and().assertThat().body("message",equalTo("Email, password and name are required fields"));
    }

    @DisplayName("Проверка создания пользователя без обязательного поля пароль")
    @Test
    public void testErrorWhenCreateUserWithoutPassword(){
        UserAccount userAccountWithoutPassword = new UserAccount("testselenium@yandex.ru",null,"IvanTest");
        given().header("Content-type", "application/json").
                and().body(userAccountWithoutPassword).when().post("api/auth/register").then().statusCode(403)
                .and().assertThat().body("success",equalTo(false))
                .and().assertThat().body("message",equalTo("Email, password and name are required fields"));
    }

    @DisplayName("Проверка создания пользователя без обязательного поля имя")
    @Test
    public void testErrorWhenCreateUserWithoutName(){
        UserAccount userAccountWithoutName = new UserAccount("testselenium@yandex.ru","password123",null);
        given().header("Content-type", "application/json").
                and().body(userAccountWithoutName).when().post("api/auth/register").then().statusCode(403)
                .and().assertThat().body("success",equalTo(false))
                .and().assertThat().body("message",equalTo("Email, password and name are required fields"));
    }
}
