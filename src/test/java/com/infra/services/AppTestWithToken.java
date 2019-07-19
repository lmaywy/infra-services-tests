package com.infra.services;

import io.restassured.response.Response;
import org.junit.Test;
import utils.RestUtil;

import static org.junit.Assert.assertEquals;

public class AppTestWithToken {
    @Test
    public void searchRepoShouldReturn200() {
        RestUtil.setBaseURI("https://github.com/");
        //Response response = RestUtil.getResponseWithToken("lmaywy", "Passw0rd13$", "search/count?q=specflow&type=Code");
        //assertEquals(200, response.statusCode());
        //response.body().prettyPrint();
    }
}
