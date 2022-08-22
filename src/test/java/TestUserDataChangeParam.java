import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
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

    public TestUserDataChangeParam(String email, String password, String name){
        this.email = email;
        this.password = password;
        this.name = name;
    }

    @Parameterized.Parameters
    public static Object[][] getUserDataFields() {
        return new Object[][] {
                {"testselenium@yandex.ru123","password123","IvanTest"},
                {"testselenium@yandex.ru","password123123","IvanTest"},
                {"testselenium@yandex.ru","password123","IvanTest123"}
        };
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = TestRegisterUser.BASEURI;
    }

    @After
    public void deleteUser(){
        if (given().header("Content-type", "application/json").
                and().body(userAccount).when().post("api/auth/login").then().extract().statusCode() == 200) {
            given().auth().oauth2(TestRegisterUser.accessToken).and().when().delete("api/auth/user").then().statusCode(202).and().assertThat().body("success", equalTo(true))
                    .and().assertThat().body("message",equalTo("User successfully removed"));
        }
    }


    @DisplayName("Изменение у пользователя емейл,пароля или имени с авторизацией")
    @Test
    public void testSuccessfulChangeUserFieldsWithAuth(){
        TestRegisterUser.CreateUser();
        TestRegisterUser.getAccessToken();
        userAccount = new UserAccount(email,password,name);

        given().auth().oauth2(TestRegisterUser.accessToken).header("Content-type", "application/json").and().body(userAccount).and().when().
                patch("api/auth/user").then().statusCode(200).and().assertThat().body("success", equalTo(true))
                .and().assertThat().body("user.email",equalTo(email)).
                and().assertThat().body("user.name",equalTo(name));
    }

    @DisplayName("Изменение у пользователя емейла,пароля или имени без авторизации")
    @Test
    public void testErrorChangeUserFieldsWithoutAuth(){
        userAccount = new UserAccount(email,password,name);
        given().header("Content-type", "application/json").and().body(userAccount).and().when().
                patch("api/auth/user").then().statusCode(401).and().assertThat().body("success", equalTo(false))
                .and().assertThat().body("message",equalTo("You should be authorised"));
    }
}
