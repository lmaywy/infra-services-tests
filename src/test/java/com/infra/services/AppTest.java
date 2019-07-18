package com.baidu.infra.services;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import io.restassured.response.Response;
import org.jsoup.select.Elements;
import org.junit.Test;
import utils.RestUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Unit test for simple App.
 */
public class AppTest {

    @Test
    public void getACCCCases() throws Exception {
        String baseUrl = "https://www.accc.gov.au";
        String paraPage = "";
        String ext = ".html";
        List<String[]> data = new ArrayList<String[]>();


        int totalPage = 344;
        data.add(new String[]{"title", "report date", "content", "Release Number", "ACCC Infocentre", "Media enquiries", "AUDIENCE", "Topics"});

        for (int i = 0; i < 1; i++) {
            int count = i * 20;
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
                String title = titleLinks.get(j).split("/")[2];
                String log = (i + 1) + "-" + count + "_" + title;
                String body = article.body().asString();

                String[] item = new String[10];
                // only get the cases content
                Document detail = Jsoup.parse(body);
                Element ele = detail.select(".field-type-text-with-summary").get(0);
                String content =ele.wholeText().replace("“","\'").replace("”","\'").
                        replace('‘','\'').replace('’','\'');

                String date = detail.getElementsByClass("date-display-single").get(0).text();

                //System.out.println(ele.text());
                //System.out.println("html" + ele.html());
                //System.out.println("text" + ele.text());
                System.out.println(content);


                item[0] = title;
                item[1] = date;
                item[2] = content;

                Elements eleLabels = detail.select(".field-label");

                for (int k = 0; k <1 ; k++) {
                    Elements label_values = eleLabels.get(k).nextElementSibling().select(".field-item");
                    item[k + 3] = "";
                    for (int m = 0; m < label_values.size(); m++) {
                        String text = label_values.get(m).text();
                        item[k + 3] += text + ";";
                    }

                }

                System.out.println("start to save file:" + log);
                data.add(item);
                TimeUnit.SECONDS.sleep(1);
            }

        }

        writeCSV(data);
    }


    public void saveAs(String content, String fileName, String ext)
            throws IOException {
        String filePath = "c:\\ACCC_Cases\\";
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath + fileName + ext));
        writer.write(content);
        writer.close();
    }

    public void writeCSV(List<String[]> data) throws Exception {
        String filePath = "c:\\ACCC_Cases\\cases.csv";
        File file = null;
        List<String[]> originData = null;
        if (Files.exists(Paths.get(filePath))) {
            originData = readCSV(filePath);
            data.remove(0);
        }
        file = new File(filePath);
        FileOutputStream fos = new FileOutputStream(file);
        OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
        CSVWriter writer = new CSVWriter(osw);

        if (originData != null)
            writer.writeAll(originData);
        writer.writeAll(data);
        writer.close();
    }

    public List<String[]> readCSV(String file) throws Exception {
        CSVReader reader = new CSVReader(new FileReader(file));
        return reader.readAll();
    }
}
