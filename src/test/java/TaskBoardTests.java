import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import javax.json.Json;
import javax.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TaskBoardTests {

    private static int taskId;

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://taskboard.portnov.com";
    }

//    @Test
//    @Order(1)
//    public void testGetAllTasks() {
//        given()
//                .log().all()
//                .when()
//                .get("/api/Task")
//                .then()
//                .log().all()
//                .statusCode(200)
//                .contentType(ContentType.JSON);
//    }

    @Test
    @Order(1)
    public void testGetAllTasks() {
        Response response = given()
                .log().all()
                .when()
                .get("/api/Task")
                .then()
                .log().all()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("[0].id", equalTo(11))
                .extract()
                .response();

        List<Map<String, Object>> tasks = response.jsonPath().getList("$");
        for (Map<String, Object> task : tasks) {
            System.out.println("Task: " + task);
            System.out.println("Task: " + task.get("id"));
        }
    }

    @Test
    @Order(2)
    public void testCreateTask() {
        String newTask = "{ " +
                "\"id\": 0, " +
                "\"taskName\": \"New Task\", " +
                "\"description\": \"Task description\", " +
                "\"dueDate\": \"2024-12-11T23:13:46.658Z\", " +
                "\"priority\": 0, " +
                "\"status\": \"New\", " +
                "\"author\": \"Student\" " +
                "}";

//        JsonObject jsonTask = Json.createObjectBuilder()
//                .add("id", 0)
//                .add("taskName", "New Task")
//                .add("description", "Task description")
//                .add("dueDate", "2024-12-11T23:13:46.658Z")
//                .add("priority", 0)
//                .add("status", "New")
//                .add("author", "Student")
//                .build();
//        newTask = jsonTask.toString();

        Response response = given()
                .contentType(ContentType.JSON)
                .body(newTask)
                .when()
                .post("/api/Task")
                .then()
                .statusCode(201)
                .body("taskName", equalTo("New Task"))
                .body("description", equalTo("Task description"))
                //.body("id", notNullValue())
                .extract().response();
        taskId = response.path("id");
        System.out.println("Created task ID: " + taskId);
    }

    @Test
    @Order(3)
    public void testGetTaskById() {
        //int taskId = 74; // Replace with a valid task ID

        Response response = given()
                .when()
                .get("/api/Task/" + taskId)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(taskId))
                .extract().response();
        String jsonResponse = response.asString();
        System.out.println("Task JSON Response: " + jsonResponse);
    }

    @Test
    @Order(4)
    public void testUpdateTask() {
        //int taskId = 74; // Replace with a valid task ID
        String updatedTask = "{ " +
                "\"id\":" +taskId+ ", " +
                "\"taskName\": \"Updated Task\", " +
                "\"description\": \"Updated description\", " +
                "\"dueDate\": \"2024-12-11T23:13:46.658Z\", " +
                "\"priority\": 0, " +
                "\"status\": \"New\", " +
                "\"author\": \"Student\" " +
                "}";

        given()
                .contentType(ContentType.JSON)
                .body(updatedTask)
                .when()
                .put("/api/Task/" + taskId)
                .then()
                .statusCode(204);
    }

    @Test
    @Order(5)
    public void testDeleteTask() {
        //int taskId = 74; // Replace with a valid task ID

        given()
                .when()
                .delete("/api/Task/" + taskId)
                .then()
                .statusCode(204);
    }
}
