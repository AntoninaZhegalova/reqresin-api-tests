package tests;

import models.lombok.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static specs.BaseSpecs.baseRequestSpec;
import static specs.BaseSpecs.baseResponseSpec;
import static specs.RegistrationSpecs.registerRequestSpec;
import static specs.RegistrationSpecs.registerResponseSpec;
import static specs.UserActionSpecs.userActionRequestSpec;
import static specs.UserActionSpecs.userActionResponseSpec;

public class ReqresApiTests {

    @Test
    @DisplayName("Проверка успешной регистрации")
    void postSuccessfulUserRegistrationTest() {

        UserBodyLombokModel body = new UserBodyLombokModel();
        body.setEmail("eve.holt@reqres.in");
        body.setPassword("pistol");

        UserResponseLombokModel response = given()
                .spec(registerRequestSpec)
                .body(body)
                .when()
                .post()
                .then()
                .spec(registerResponseSpec)
                .statusCode(200)
                .extract()
                .as(UserResponseLombokModel.class);

        assertThat(response.getToken()).isNotBlank();
    }

    @Test
    @DisplayName("Проверка неуспешной регистрации")
    void postUnsuccessfulUserRegistrationTest() {
        UserBodyLombokModel body = new UserBodyLombokModel();
        body.setEmail("eve.holt@reqres.in");

        ErrorResponseLombokModel response = given()
                .spec(registerRequestSpec)
                .body(body)
                .when()
                .post()
                .then()
                .spec(registerResponseSpec)
                .statusCode(400)
                .extract()
                .as(ErrorResponseLombokModel.class);

        assertThat(response.getError()).isEqualTo("Missing password");
    }

    @Test
    @DisplayName("Наличие пагинации")
    void getReturnEntitiesForPage() {
        given()
                .spec(baseRequestSpec)
                .get("/api/users?page=2")
                .then()
                .spec(baseResponseSpec)
                .statusCode(200)
                .body("page", equalTo(2));
    }

    @Test
    @DisplayName("Обновление данных пользователя")
    void patchUserData() {
        UserDataBodyLombokModel body = new UserDataBodyLombokModel();
        body.setName("Vasya");
        body.setJob("leader");

        UserDataResponseLombokModel response = given()
                .spec(userActionRequestSpec)
                .body(body)
                .when()
                .put()
                .then()
                .spec(userActionResponseSpec)
                .statusCode(200)
                .extract()
                .as(UserDataResponseLombokModel.class);

        assertEquals("Vasya", response.getName());
        assertEquals("leader", response.getJob());
        assertThat(response.getUpdatedAt()).isNotBlank();
    }


    @Test
    @DisplayName("Удаление пользователя")
    void deleteUser() {
        given()
                .spec(userActionRequestSpec)
                .when()
                .delete("/api/users/2")
                .then()
                .spec(userActionResponseSpec)
                .statusCode(204);
    }

}
