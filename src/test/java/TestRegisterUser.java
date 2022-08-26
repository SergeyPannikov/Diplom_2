import api.ApiAuthLogin;
import api.ApiAuthRegister;
import api.ApiAuthUser;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

public class TestRegisterUser {
    public static String accessToken;
    public static ApiAuthRegister apiAuthRegister = new ApiAuthRegister();
    public static ApiAuthUser apiAuthUser = new ApiAuthUser();
    public static ApiAuthLogin apiAuthLogin = new ApiAuthLogin();
    public final static String BASEURI = "https://stellarburgers.nomoreparties.site";
    public static UserAccount userAccount = new UserAccount("testselenium@yandex.ru", "password123", "IvanTest");

    @Before
    public void setUp() {
        RestAssured.baseURI = BASEURI;
    }

    @After
    public void deleteUserAfterTest() {
        deleteUser();
    }

    @Step("Удаление пользователя")
    public static void deleteUser() {
        Response authLogin = apiAuthLogin.apiAuthLoginPost(userAccount);
        if (authLogin.then().extract().statusCode() == 200) {
            getAccessToken();
            Response authDelete = apiAuthUser.apiAuthUserDelete(accessToken);
            authDelete.then().statusCode(202).and().assertThat().body("success", equalTo(true))
                    .and().assertThat().body("message", equalTo("User successfully removed"));
        }
    }

    @Step("Получение токена при логине клиента")
    public static void getAccessToken() {
        Response authLogin = apiAuthLogin.apiAuthLoginPost(userAccount);
        if (authLogin.then().extract().statusCode() == 200) {
            String accessTokenString = authLogin.then().statusCode(200).and().extract().body().path("accessToken");
            accessToken = accessTokenString.substring(accessTokenString.indexOf(' ') + 1);
        }
    }

    @DisplayName("Успешное создание пользователя, проверка кода успешного ответа и тела ответа об успешном создании пользователя")
    @Test
    public void testCreateUser() {
        CreateUser();
    }

    @Step("Создание пользователя")
    public static void CreateUser() {
        Response createUser = apiAuthRegister.apiAuthRegister(userAccount);
        createUser.then().statusCode(200).and().assertThat().body("success", equalTo(true))
                .and().assertThat().body("user.email", equalTo("testselenium@yandex.ru"))
                .and().assertThat().body("user.name", equalTo("IvanTest"));
    }

    @DisplayName("Проверка создания пользователя, который уже зарегестрирован")
    @Test
    public void testErrorWhenCreateExistingUser() {
        CreateUser();
        Response errorWhenCreateExistingUser = apiAuthRegister.apiAuthRegister(userAccount);
        errorWhenCreateExistingUser.then().statusCode(403)
                .and().assertThat().body("success", equalTo(false))
                .and().assertThat().body("message", equalTo("User already exists"));
    }

    @DisplayName("Проверка создания пользователя без обязательного поля емейла")
    @Test
    public void testErrorWhenCreateUserWithoutEmail() {
        UserAccount userAccountWithoutEmail = new UserAccount(null, "password123", "IvanTest");
        Response errorWhenCreateUserWithoutEmail = apiAuthRegister.apiAuthRegister(userAccountWithoutEmail);
        errorWhenCreateUserWithoutEmail.then().statusCode(403)
                .and().assertThat().body("success", equalTo(false))
                .and().assertThat().body("message", equalTo("Email, password and name are required fields"));
    }

    @DisplayName("Проверка создания пользователя без обязательного поля пароль")
    @Test
    public void testErrorWhenCreateUserWithoutPassword() {
        UserAccount userAccountWithoutPassword = new UserAccount("testselenium@yandex.ru", null, "IvanTest");
        Response errorWhenCreateUserWithoutPassword = apiAuthRegister.apiAuthRegister(userAccountWithoutPassword);
        errorWhenCreateUserWithoutPassword.then().statusCode(403)
                .and().assertThat().body("success", equalTo(false))
                .and().assertThat().body("message", equalTo("Email, password and name are required fields"));
    }

    @DisplayName("Проверка создания пользователя без обязательного поля имя")
    @Test
    public void testErrorWhenCreateUserWithoutName() {
        UserAccount userAccountWithoutName = new UserAccount("testselenium@yandex.ru", "password123", null);
        Response errorWhenCreateUserWithoutName = apiAuthRegister.apiAuthRegister(userAccountWithoutName);
        errorWhenCreateUserWithoutName.then().statusCode(403)
                .and().assertThat().body("success", equalTo(false))
                .and().assertThat().body("message", equalTo("Email, password and name are required fields"));
    }
}
