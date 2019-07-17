package com.infra.services;

import io.restassured.response.Response;
import org.jsoup.select.Elements;
import org.junit.Test;
import utils.RestUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        String ext = ".html";


        int totalPage = 344;
        int timeInterval = 3;

        for (int i = 1; i < 2; i++) {
            int count = i * 20;
            if (i != 0)
                paraPage = "?page=" + i;
            RestUtil.setBaseURI(baseUrl);
            String path = String.format("/media/media-releases%s", paraPage);
            Response response = RestUtil.getResponse(path);
            Document doc = Jsoup.parse(response.body().asString());
            Elements links = doc.select(".views-row a");
            List<String> titleLinks = links.eachAttr("href");

            for (int j = 18; j < titleLinks.size(); j++) {
                count++;
                Response article = RestUtil.getResponse(titleLinks.get(j));
                String fileName = (i + 1) + "-" + count + "_" + titleLinks.get(j).split("/")[2];
                String body = article.body().asString();
                String content = body;

                // only get the cases content
                Document detail = Jsoup.parse(body);
                Element ele = detail.getElementById("readspeaker-process");
                content = ele.outerHtml();
                //content = ele.text();
                System.out.println("response code:" + response.statusCode());
                System.out.println("start to save file:" + fileName);
                saveAs(content, fileName, ext);

            }
            //TimeUnit.MINUTES.sleep(timeInterval);
        }
    }


    public void saveAs(String content, String fileName, String ext)
            throws IOException {
        String filePath = "target/accc_cases/";
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath + fileName + ext));
        writer.write(content);
        writer.close();
    }
}
