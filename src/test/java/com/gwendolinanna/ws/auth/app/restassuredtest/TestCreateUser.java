package com.gwendolinanna.ws.auth.app.restassuredtest;

import com.gwendolinanna.ws.auth.app.restassuredtest.utils.TestConstants;

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

    @BeforeEach
    public void setUp() throws Exception {
        RestAssured.baseURI = TestConstants.BASE_URI;
        RestAssured.port = TestConstants.PORT;
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
        userDetails.put("email", TestConstants.EMAIL_ADDRESS);
        userDetails.put("password", TestConstants.PASSWORD);
        userDetails.put("posts", userPosts);


        Response response = given()
                .contentType(TestConstants.CONTENT_TYPE_JSON)
                .accept(TestConstants.CONTENT_TYPE_JSON)
                .body(userDetails)
                .when()
                .post(TestConstants.APP_CONTEXT + TestConstants.USERS_PATH)
                .then()
                .statusCode(200)
                .contentType(TestConstants.CONTENT_TYPE_JSON)
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