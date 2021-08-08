package com.gwendolinanna.ws.auth.app.restassuredtest;

import com.gwendolinanna.ws.auth.app.restassuredtest.utils.TestConstants;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * @author Johnkegd
 */
//@FixMethodOrder to be implemented
@SpringBootTest
class UserWebServiceEndpointTest {

    private static String authorizationHeader;
    private static String userId;
    private static List<Map<String, String>> posts;

    @BeforeEach
    public void setUp() throws Exception {
        RestAssured.baseURI = TestConstants.BASE_URI;
        RestAssured.port = TestConstants.PORT;
    }

    @Test
    final void testUserLogin() {
        Map<String, String> loginDetails = new HashMap<>();
        loginDetails.put("email", TestConstants.EMAIL_ADDRESS);
        loginDetails.put("password", TestConstants.PASSWORD);
        Response response = given()
                .contentType(TestConstants.CONTENT_TYPE_JSON)
                .accept(TestConstants.CONTENT_TYPE_JSON)
                .body(loginDetails)
                .when()
                .post(TestConstants.APP_CONTEXT.concat(TestConstants.USERS_PATH).concat("/login"))
                .then()
                .statusCode(200)
                .extract()
                .response();

        authorizationHeader = response.header("Authorization");
        userId = response.header("UserId");

        assertNotNull(authorizationHeader);
        assertNotNull(userId);
    }

    @Test
    final void testGetUserDetails() {
        Response response = given()
                .param("id", userId)
                .header("Authorization", authorizationHeader)
                .accept(TestConstants.CONTENT_TYPE_JSON)
                .when()
                .get(TestConstants.APP_CONTEXT.concat(TestConstants.USERS_PATH).concat("/{id}"))
                .then()
                .statusCode(200)
                .contentType(TestConstants.CONTENT_TYPE_JSON)
                .extract()
                .response();

        String userPublicId = response.jsonPath().getString("userId");
        String userEmail = response.jsonPath().getString("email");
        String firstName = response.jsonPath().getString("firstName");
        String lastName = response.jsonPath().getString("lastName");
        posts = response.jsonPath().getList("posts");
        String postsId = response.jsonPath().getString("postsId");

        assertNotNull(userPublicId);
        assertNotNull(userEmail);
        assertNotNull(firstName);
        assertNotNull(lastName);
        assertEquals(TestConstants.EMAIL_ADDRESS, userEmail);

        assertTrue(posts.size() == 1);
        assertTrue(postsId.length() == 30);
    }


    @Test
    final void testUpdateUserDetails() {

        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("firstName", "John");
        userDetails.put("lastName", "Garcia");


        Response response = given()
                .contentType(TestConstants.CONTENT_TYPE_JSON)
                .accept(TestConstants.CONTENT_TYPE_JSON)
                .header("Authorization", authorizationHeader)
                .pathParam("id", userId)
                .body(userDetails)
                .when()
                .put(TestConstants.APP_CONTEXT.concat(TestConstants.USERS_PATH.concat("/{id}")))
                .then()
                .statusCode(200)
                .contentType(TestConstants.CONTENT_TYPE_JSON)
                .extract().response();

        String firstName = response.jsonPath().getString("firstName");
        String lastName = response.jsonPath().getString("lastName");
        List<Map<String, String>> storedPosts = response.jsonPath().getList("posts");

        assertEquals("John", firstName);
        assertEquals("Garcia", lastName);
        assertNotNull(storedPosts);
        assertTrue(posts.size() == storedPosts.size());
        assertEquals(posts.get(0).get("category"), storedPosts.get(0).get("category"));
    }

    @Test
    final void testDeleteUserDetails() {
        Response response = given()
                .header("Authorization", authorizationHeader)
                .accept(ContentType.JSON)
                .pathParam("id", userId)
                .when()
                .delete(TestConstants.APP_CONTEXT.concat(TestConstants.USERS_PATH).concat("/{id}"))
                .then()
                .statusCode(200)
                .contentType(TestConstants.CONTENT_TYPE_JSON)
                .extract()
                .response();

        String result = response.jsonPath().getString("operationResult");
        assertEquals("SUCCESS", result);
    }

}