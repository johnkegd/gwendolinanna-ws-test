package com.gwendolinanna.ws.auth.app.restassuredtest;

import com.gwendolinanna.ws.auth.app.restassuredtest.utils.TestConstants;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * @author Johnkegd
 */
@SpringBootTest
class UserWebServiceEndpointTest {

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

        String authorizationHeader = response.header("Authorization");
        String userId = response.header("UserId");

        assertNotNull(authorizationHeader);
        assertNotNull(userId);
    }

}