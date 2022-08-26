import api.ApiAuthLogin;
import api.ApiAuthUser;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@RunWith(Parameterized.class)
public class TestUserDataChangeParam {
    public UserAccount userAccount;
    private String email;
    private String password;
    private String name;

    public TestUserDataChangeParam(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    @Parameterized.Parameters(name = "Тестовые данные: {0}, {1}, {2}")
    public static Object[][] getUserDataFields() {
        return new Object[][]{
                {"testselenium@yandex.ru123", "password123", "IvanTest"},
                {"testselenium@yandex.ru", "password123123", "IvanTest"},
                {"testselenium@yandex.ru", "password123", "IvanTest123"}
        };
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = TestRegisterUser.BASEURI;
    }

    @After
    public void deleteUser() {
        Response authLogin = TestRegisterUser.apiAuthLogin.apiAuthLoginPost(userAccount);
        if (authLogin.then().extract().statusCode() == 200) {
            Response authDelete = TestRegisterUser.apiAuthUser.apiAuthUserDelete(TestRegisterUser.accessToken);
            authDelete.then().statusCode(202).and().assertThat().body("success", equalTo(true))
                    .and().assertThat().body("message", equalTo("User successfully removed"));
        }
    }

    @DisplayName("Изменение у пользователя емейл,пароля или имени с авторизацией")
    @Test
    public void testSuccessfulChangeUserFieldsWithAuth() {
        TestRegisterUser.CreateUser();
        TestRegisterUser.getAccessToken();
        userAccount = new UserAccount(email, password, name);
        Response changeUserFieldsWithAuth = TestRegisterUser.apiAuthUser.apiAuthUserPatchWithAuth(TestRegisterUser.accessToken, userAccount);
        changeUserFieldsWithAuth.then().statusCode(200).and().assertThat().body("success", equalTo(true))
                .and().assertThat().body("user.email", equalTo(email)).
                and().assertThat().body("user.name", equalTo(name));
    }

    @DisplayName("Изменение у пользователя емейла,пароля или имени без авторизации")
    @Test
    public void testErrorChangeUserFieldsWithoutAuth() {
        userAccount = new UserAccount(email, password, name);
        Response changeUserFieldsWithoutAuth = TestRegisterUser.apiAuthUser.apiAuthUserPatchWithoutAuth(userAccount);
        changeUserFieldsWithoutAuth.then().statusCode(401).and().assertThat().body("success", equalTo(false))
                .and().assertThat().body("message", equalTo("You should be authorised"));
    }
}
