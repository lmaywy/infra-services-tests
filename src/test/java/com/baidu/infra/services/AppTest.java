package com.baidu.infra.services;

import static org.junit.Assert.assertEquals;

import io.restassured.response.Response;
import org.jsoup.select.Elements;
import org.junit.Test;
import utils.RestUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;

import java.io.*;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void articalDetailShouldReturn200() throws FileNotFoundException, UnsupportedEncodingException {
        String baseUrl = "https://www.accc.gov.au";
        /*for (int i = 0; i < 344; i++) {
            if (i != 0)
                baseUrl += "?page=" + i;
        }*/
        RestUtil.setBaseURI(baseUrl);
        Response response = RestUtil.getResponse("/media/media-releases");
        Document doc = Jsoup.parse(response.body().asString());
        assertEquals(200, response.statusCode());

        Elements links = doc.select(".views-row a");
        List<String> titleLinks = links.eachAttr("href");

        // for (int j = 0; j < titleLinks.size(); j++) {
        Response article = RestUtil.getResponse(titleLinks.get(0));
        System.out.println(article.body().asString());
        String fileName = titleLinks.get(0).split("/")[2];
        String content = article.body().asString();
        try {
            saveFile(content, fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void searchRepoShouldReturn200() {
        RestUtil.setBaseURI("https://github.com/");
        Response response = RestUtil.setAuth("lmaywy", "Passw0rd13$", "search/count?q=specflow&type=Code");
        assertEquals(200, response.statusCode());
        response.body().prettyPrint();
    }

    public void saveFile(String content, String fileName)
            throws IOException {
        String filePath="c:\\htmls\\";
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath+fileName + ".html"));
        writer.write(content);
        writer.close();
    }
}
