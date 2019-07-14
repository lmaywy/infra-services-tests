package utils;

import io.restassured.*;
import io.restassured.http.*;
import io.restassured.response.*;
import io.restassured.path.json.*;


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
    ***search query path of first example***
    It is  equal to "barack obama/videos.json?num_of_videos=4"
    */
    public static void createDetailsPath(double detailId) {
        path = RestAssured.basePath + "/" + detailId;
    }

    /*
    ***Returns response***
    We send "path" as a parameter to the Rest Assured'a "get" method
    and "get" method returns response of API
    */
    public static Response getResponse() {
        //System.out.print("path: " + path +"\n");
        return RestAssured.get(path);
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
}