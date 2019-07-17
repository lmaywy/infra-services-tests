package utils;

import io.restassured.*;
import io.restassured.http.*;
import io.restassured.response.*;
import io.restassured.path.json.*;

import java.net.URL;

import static io.restassured.RestAssured.*;

public class RestUtil {
    //Global Setup Variables
    public static String path; //Rest request path

    /*
    ***Sets Base URI***
    Before starting the test, we should set the RestAssured.baseURI
    */
    public static void setBaseURI(String baseURI) {
        RestAssured.baseURI = baseURI;
    }

    /*
    ***Sets base path***
    Before starting the test, we should set the RestAssured.basePath
    */
    public static void setBasePath(String basePathTerm) {
        RestAssured.basePath = basePathTerm;
    }

    /*
    ***Reset Base URI (after test)***
    After the test, we should reset the RestAssured.baseURI
    */
    public static void resetBaseURI() {
        RestAssured.baseURI = "";
    }

    /*
    ***Reset base path (after test)***
    After the test, we should reset the RestAssured.basePath
    */
    public static void resetBasePath() {
        RestAssured.basePath = "";
    }

    /*
    ***Sets ContentType***
    We should set content type as JSON or XML before starting the test
    */
    public static void setContentType(ContentType Type) {
        RestAssured.given().contentType(Type);
    }

    /*
    ***Returns response***
    We send "path" as a parameter to the Rest Assured'a "get" method
    and "get" method returns response of API
    */
    public static Response getResponse(String params) {
        //System.out.print("path: " + path +"\n");
        return RestAssured.given().urlEncodingEnabled(false).get(params);
    }

    public static Response getResponseWithToken(String userName, String password, String path) {
        return RestAssured.given().auth().preemptive().basic(userName, password).get(path);
    }

    /*
     ***Returns JsonPath object***
     * First convert the API's response to String type with "asString()" method.
     * Then, send this String formatted json response to the JsonPath class and return the JsonPath
     */
    public static JsonPath getJsonPath(Response res) {
        String resp = res.asString();
        return new JsonPath(resp);
    }

    public static Response sendpostWithHttp(String surl, String str) throws Exception {
        String msg = null;
        URL url = new URL(surl);
        Response response = given().log().all().
                header("accept", "application/json").
                contentType("application/json").
                body(str).
                then().
                when().
                post(url);
        response.getBody().prettyPrint();

        return response;
    }

    public static ValidatableResponse sendgetWithHttp(String surl, String str) throws Exception {
        URL url = new URL(surl);
        ValidatableResponse response = given()
                .log().all()
                .queryParam(str)
                .when()
                .get(surl)
                .then()
                .log().all();
        return response;
    }

    public static Response sendpostWithHttps(String surl, String str) throws Exception {
        URL url = new URL(surl);
        useRelaxedHTTPSValidation();
        Response response = given().log().all().
                header("accept", "application/json").
                contentType("application/json").
                body(str).
                then().
                statusCode(200).
                when().
                post(url);
        response.getBody().prettyPrint();
        return response;
    }

    public static ValidatableResponse sendgetWithHttps(String surl, String str) throws Exception {
        URL url = new URL(surl);
        useRelaxedHTTPSValidation();
        ValidatableResponse response = given()
                .log().all()
                .queryParam(str)
                .when()
                .get(surl)
                .then()
                .log().all()
                .statusCode(200);
        return response;
    }
}