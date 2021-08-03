package com.gwendolinanna.ws.auth.app.restassuredtest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;


/**
 * @author Johnkegd
 */
@SpringBootTest
class TestCreateUser {

    private final String CONTEXT_PATH = "/auth";
    private final String CONTEN_TYPE = "application/json";

    @BeforeEach
    public void setUp() throws Exception {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    @Test
    final void testCreateUser() {

        List<Map<String, Object>> userPosts = new ArrayList<>();

        Map<String, Object> post = new HashMap<String, Object>();
        post.put("postId", "51d612");
        post.put("title", "Moon");
        post.put("description", "The beautiful moon");
        post.put("image", "moon.png");
        post.put("icon", "faa-moon");
        post.put("category", "space");
        post.put("icon", "faa-moon");

        userPosts.add(post);

        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("firstName", "John");
        userDetails.put("lastName", "Garcia");
        userDetails.put("email", "john@gwendolinanna.com");
        userDetails.put("password", "vawe12");
        userDetails.put("posts", userPosts);


        Response response = given()
                .contentType(CONTEN_TYPE)
                .accept(CONTEN_TYPE)
                .body(userDetails)
                .when()
                .post(CONTEXT_PATH + "/users")
                .then()
                .statusCode(200)
                .contentType(CONTEN_TYPE)
                .extract()
                .response();

        String userId = response.jsonPath().getString("userId");
        assertNotNull(userId);

        String bodyString = response.body().asString();
        try {
            JSONObject responseBodyJson = new JSONObject(bodyString);
            JSONArray posts = responseBodyJson.getJSONArray("posts");

            assertNotNull(posts);
            assertTrue(posts.length() == 1);

            String postsId = posts.getJSONObject(0).getString("postsId");
            assertNotNull(postsId);
            assertTrue(postsId.length() == 30);
        } catch (JSONException e) {
            fail(e.getMessage());
        }
    }

}