package api;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class ApiAuthUser {
    @Step("delete запрос на ручку api/auth/user")
    public Response apiAuthUserDelete(String accessToken) {
        return given().auth().oauth2(accessToken).and().when().delete("api/auth/user");
    }

    @Step("patch запрос на ручку api/auth/user с авторизацией под клиентом")
    public Response apiAuthUserPatchWithAuth(String accessToken, Object userAccount) {
        return given().auth().oauth2(accessToken).header("Content-type", "application/json").and().body(userAccount).and().when().
                patch("api/auth/user");
    }

    @Step("patch запрос на ручку api/auth/user без авторизацией под клиентом")
    public Response apiAuthUserPatchWithoutAuth(Object userAccount) {
        return given().header("Content-type", "application/json").and().body(userAccount).and().when().
                patch("api/auth/user");
    }
}
