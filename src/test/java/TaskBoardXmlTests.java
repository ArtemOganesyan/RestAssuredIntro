import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TaskBoardXmlTests {

    private static int taskId;

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://taskboard.portnov.com";
    }

    @Test
    @Order(1)
    public void testGetAllTasks() {
        given()
                .log().all()
                .accept(ContentType.XML)
                .when()
                .get("/api/TaskXml")
                .then()
                .log().all()
                .statusCode(200)
                .contentType(ContentType.XML);
    }

    @Test
    @Order(2)
    public void testCreateTask() {
        String newTask = "<TaskItem xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
                "<TaskName>New Task</TaskName>" +
                "<Description>Task description</Description>" +
                "<DueDate>2024-12-11T23:13:46</DueDate>" +
                "<Priority>0</Priority>" +
                "<Status>New</Status>" +
                "<Author>Student</Author>" +
                "</TaskItem>";

        Response response = given()
                .config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig().defaultContentCharset("UTF-8")))
                .log().all()
                .header("Content-Type", "application/xml")
                .body(newTask)
                .when()
                .post("/api/TaskXml")
                .then()
                .log().all()
                .statusCode(201)
                .body("TaskItem.TaskName", equalTo("New Task"))
                .body("TaskItem.Description", equalTo("Task description"))
                .extract().response();
        taskId = Integer.parseInt(response.path("TaskItem.Id"));
        System.out.println("Created task ID: " + taskId);
    }

    @Test
    @Order(3)
    public void testGetTaskById() {
        Response response = given()
                .log().all()
                .accept(ContentType.XML)
                .when()
                .get("/api/TaskXml/" + taskId)
                .then()
                .log().all()
                .statusCode(200)
                .contentType(ContentType.XML)
                .body("Task.Id", equalTo(String.valueOf(taskId)))
                .extract().response();
        String xmlResponse = response.asString();
        System.out.println("Task XML Response: " + xmlResponse);
    }

    @Test
    @Order(4)
    public void testUpdateTask() {
        String updatedTask = "<TaskItem>" +
                "<Id>" + taskId + "</Id>" +
                "<TaskName>Updated Task</TaskName>" +
                "<Description>Updated description</Description>" +
                "<DueDate>2024-12-11T23:13:45</DueDate>" +
                "<Priority>0</Priority>" +
                "<Status>New</Status>" +
                "<Author>Student</Author>" +
                "</TaskItem>";

        given()
                .config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig().defaultContentCharset("UTF-8")))
                .log().all()
                .contentType(ContentType.XML)
                .body(updatedTask)
                .when()
                .put("/api/TaskXml/" + taskId)
                .then()
                .log().all()
                .statusCode(204);
    }

    @Test
    @Order(5)
    public void testDeleteTask() {
        given()
                .log().all()
                .when()
                .delete("/api/TaskXml/" + taskId)
                .then()
                .log().all()
                .statusCode(204);
    }
}
