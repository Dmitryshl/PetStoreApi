package tests;

import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.Pet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestPet {

    private static final String BASE_URL = "http://5.181.109.28:9090/api/v3";

    @Test
    @Feature("Pet")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("Dmitry Shliatski")
    public void testDeleteNonexistentPet() {
        Response response = step("Отправить DELETE запрос на несуществующий PET", () ->
                given()
                        .contentType(ContentType.JSON)
                        .header("Accept", "application/json")
                        .when()
                        .delete(BASE_URL + "/pet/9999"));


        String responseBody = response.getBody().asString();
        step("Проверить что статус код ответа = 200", () ->
                assertEquals(200, response.getStatusCode(),
                        "Код ответа не совпал с ожидаемым. Ответ: " + responseBody));

        step("Проверить что текст ответ 'Pet deleted'", () ->
                assertEquals("Pet deleted", responseBody,
                        "Текст ошибки не совпал с ожидаемым. Получен: " + responseBody));


    }

    @Test
    @Feature("Pet")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("Dmitry Shliatski")
    public void updateNonexistingPet() {
        Pet pet = new Pet();
        pet.setId(9999);
        pet.setName("Non-existent Pet");
        pet.setStatus("available");
        Response response = step("Отправить PUT запрос на обновление несуществующего PET", () ->
                given()
                        .contentType(ContentType.JSON)
                        .header("Accept", "application/json")
                        .body(pet)
                        .when()
                        .put(BASE_URL + "/pet"));

        String responseBody = response.getBody().asString();

        step("Проверить что статус код ответа = 404", () ->
                assertEquals(404, response.getStatusCode(),
                        "Код ответа не совпал с ожидаемым. Ответ: " + responseBody));

        step("Проверить что текст ответ 'Pet not found'", () ->
                assertEquals("Pet not found", responseBody,
                        "Текст ошибки не совпал с ожидаемым. Получен: " + responseBody));
    }

    @Test
    @Feature("Pet")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("Dmitry Shliatski")
    public void GetNonexistingPet() {
        Response response = step("Отправить GET запрос на получение несуществующего PET", () ->
                given()
                        .contentType(ContentType.JSON)
                        .header("Accept", "application/json")
                        .when()
                        .get(BASE_URL + "/pet/9999"));

        String responseBody = response.getBody().asString();

        step("Проверить что статус код ответа = 404", () ->
                assertEquals(404, response.getStatusCode(),
                        "Код ответа не совпал с ожидаемым. Ответ: " + responseBody));

        step("Проверить что текст ответ 'Pet not found'", () ->
                assertEquals("Pet not found", responseBody,
                        "Текст ошибки не совпал с ожидаемым. Получен: " + responseBody));
    }

    @ParameterizedTest(name = "Добавление питомца со статусом: {2}")
    @CsvSource({
            "214, Yasha, available",
            "215, Plusha, pending,",
            "216, Bosha, sold"
    })
    @Feature("Pet")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("Dmitry Shliatski")
    public void testaAddNewPet(int id, String name, String status) {
        Pet pet = new Pet();
        pet.setId(id);
        pet.setName(name);
        pet.setStatus(status);
        Response response = step("Отправить Post запрос на добавление нового Pet", () ->
                given()
                        .contentType(ContentType.JSON)
                        .header("Accept", "application/json")
                        .body(pet)
                        .when()
                        .post(BASE_URL + "/pet"));

        String responseBody = response.getBody().asString();

        step("Проверить что статус код ответа = 200", () ->
                assertEquals(200, response.getStatusCode(),
                        "Код ответа не совпал с ожидаемым. Ответ: " + responseBody)
        );
        step("Проверка параметров созданного питомца", () -> {
                    Pet createdPet = response.as(Pet.class);
                    assertEquals(pet.getId(),createdPet.getId(), "id не совпадает");
                    assertEquals(pet.getName(),createdPet.getName(), "имя не совпадает");
                    assertEquals(pet.getStatus(),createdPet.getStatus(), "статус не совпадает");
                });
    }
}