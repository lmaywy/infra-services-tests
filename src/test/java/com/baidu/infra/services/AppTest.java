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
import java.util.concurrent.TimeUnit;

/**
 * Unit test for simple App.
 */
public class AppTest {

    @Test
    public void getACCCCases() throws IOException, InterruptedException {
        String baseUrl = "https://www.accc.gov.au";
        String paraPage = "";
        int count = 0;
        int totalPage = 344;
        int timeInterval=5;

        for (int i = 0; i < totalPage; i++) {
            if (i != 0)
                paraPage = "?page=" + i;
            RestUtil.setBaseURI(baseUrl);
            Response response = RestUtil.getResponse("/media/media-releases" + paraPage);
            Document doc = Jsoup.parse(response.body().asString());
            Elements links = doc.select(".views-row a");
            List<String> titleLinks = links.eachAttr("href");

            for (int j = 0; j < titleLinks.size(); j++) {
                count++;
                Response article = RestUtil.getResponse(titleLinks.get(j));
                String fileName = (i + 1) + "-" + count + "_" + titleLinks.get(j).split("/")[2];
                String content = article.body().asString();
                System.out.println("start to save file:" + fileName);
                saveFile(content, fileName);
            }
            TimeUnit.MINUTES.sleep(timeInterval);
        }
    }


    public void saveFile(String content, String fileName)
            throws IOException {
        String filePath = "c:\\ACCC_Cases\\";
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath + fileName + ".html"));
        writer.write(content);
        writer.close();
    }
}
