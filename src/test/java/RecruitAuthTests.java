import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RecruitAuthTests {

    private static String token;

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://recruit-stage.portnov.com/recruit/api/v1";
    }

    @Test
    @Order(1)
    public void testLogin() {
        String credentials = "{ \"email\": \"student@example.com\", \"password\": \"welcome\" }";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(credentials)
                .when()
                .post("/login")
                .then()
                .statusCode(200)
                .extract().response();

        token = response.path("token");
        System.out.println("Token: " + token);
    }

    @Test
    @Order(2)
    public void testVerifyRequestWithToken() {
        given()
                .config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig().defaultContentCharset("UTF-8")))
                .header("Authorization", "Bearer " + token)
                .when()
                .post("/verify")
                .then()
                .log().all()
                .statusCode(200);
    }
}
