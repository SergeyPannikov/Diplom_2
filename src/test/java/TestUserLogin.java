import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class TestUserLogin extends TestRegisterUser {
    public UserAccount userLogin;

    @DisplayName("Успешный вход под созданным пользователем")
    @Test
    public void testSuccessfulLoginUser() {
        CreateUser();
        userLogin = new UserAccount("testselenium@yandex.ru", "password123");
        Response loginUser = TestRegisterUser.apiAuthLogin.apiAuthLoginPost(userLogin);
        loginUser.then().statusCode(200).
                and().assertThat().body("success", equalTo(true)).
                and().assertThat().body("user.email", equalTo("testselenium@yandex.ru")).
                and().assertThat().body("user.name", equalTo("IvanTest"));
    }

    @DisplayName("Проверка ошибка при некорректном вводе емейла при логине")
    @Test
    public void testSErrorWhenInvalidEmail() {
        CreateUser();
        userLogin = new UserAccount("testselenium@yandex.ru111", "password123");
        Response invalidEmail = TestRegisterUser.apiAuthLogin.apiAuthLoginPost(userLogin);
        invalidEmail.then().statusCode(401).
                and().assertThat().body("success", equalTo(false)).
                and().assertThat().body("message", equalTo("email or password are incorrect"));
    }

    @DisplayName("Проверка ошибка при некорректном вводе емейла при логине")
    @Test
    public void testSErrorWhenInvalidPasword() {
        CreateUser();
        userLogin = new UserAccount("testselenium@yandex.ru", "password123456");
        Response invalidPassword = TestRegisterUser.apiAuthLogin.apiAuthLoginPost(userLogin);
        invalidPassword.then().statusCode(401).
                and().assertThat().body("success", equalTo(false)).
                and().assertThat().body("message", equalTo("email or password are incorrect"));
    }
}
