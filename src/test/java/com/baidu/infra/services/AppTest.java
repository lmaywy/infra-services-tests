package com.baidu.infra.services;

import static org.junit.Assert.assertEquals;
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
        RestUtil.setBaseURI("https://blog.csdn.net");
        RestUtil.setBasePath("article/details");
        RestUtil.createDetailsPath(95635775);
        int statusCode = RestUtil.getResponse().statusCode();
        assertEquals(200, statusCode);
    }
}
