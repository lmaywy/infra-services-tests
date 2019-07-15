package com.baidu.infra.services;

import static org.junit.Assert.assertEquals;

import io.restassured.response.Response;
import org.junit.Test;
import utils.RestUtil;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void articalDetailShouldReturn200() {
        RestUtil.setBaseURI("https://github.com/");
        Response response = RestUtil.getResponse("");
        assertEquals(200, response.statusCode());
        response.body().prettyPrint();
    }

    @Test
    public void searchRepoShouldReturn200() {
        RestUtil.setBaseURI("https://github.com/");
        Response response = RestUtil.setAuth("lmaywy","Passw0rd13$","search/count?q=specflow&type=Code");
        assertEquals(200, response.statusCode());
        response.body().prettyPrint();
    }
}
