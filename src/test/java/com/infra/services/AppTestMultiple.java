package com.infra.services;

import io.restassured.response.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import utils.FileUtil;
import utils.RestUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Unit test for simple App.
 */
public class AppTestMultiple {

    @Test
    public void getACCC_CaseDetails_MultiThread() throws Exception {
        String filePath = "c:\\ACCC_Cases\\case_links.csv";
        List<String[]> caseLinks = FileUtil.readCSV(filePath);
        List<String[]> data = new ArrayList<>();
        data.add(new String[]{"title", "report date", "content", "Release Number", "ACCC Infocentre", "Media enquiries", "AUDIENCE", "Topics"});
        String baseUrl = "https://www.accc.gov.au";
        RestUtil.setBaseURI(baseUrl);

        for (int j = 0; j < caseLinks.size(); j++) {
            String path = caseLinks.get(j)[0];
            Response article = RestUtil.getResponse(path);

            String body = article.body().asString()
                    .replace("<BR>", "\r\n")
                    .replace("<BR/>", "\r\n");

            Document detail = Jsoup.parse(body);
            Element ele = detail.select(".field-type-text-with-summary").get(0);
            String content = ele.wholeText();
            String date = detail.getElementsByClass("date-display-single").get(0).text();
            String title = detail.getElementById("page-title").text();
            String log = (j + 1) + "_" + title;

            String[] item = new String[15];
            item[0] = title;
            item[1] = date;
            item[2] = content;
            Elements eleLabels = detail.select(".field-label");

            for (int k = 0; k < eleLabels.size(); k++) {
                Elements label_values = eleLabels.get(k).nextElementSibling().select(".field-item");
                item[k + 3] = "";
                for (int m = 0; m < label_values.size(); m++) {
                    String text = label_values.get(m).text();
                    item[k + 3] += text + ";";
                }
            }
            System.out.println("start to save file:" + log);
            data.add(item);
            TimeUnit.MILLISECONDS.sleep(1000);

            if (j % 200 == 0 && j > 0 && data.size() > 2) {
                FileUtil.writeCSV(data, "c:\\ACCC_Cases\\" + (j / 200) + "_cases.csv");
                data.clear();
                data.add(new String[]{"title", "report date", "content", "Release Number", "ACCC Infocentre", "Media enquiries", "AUDIENCE", "Topics"});
            }

            if (j == caseLinks.size() - 1) {
                FileUtil.writeCSV(data, "c:\\ACCC_Cases\\" + (j / 200) + 1 + "_cases.csv");
            }
        }
    }

    public class CrawlThread implements Runnable{

        @Override
        public void run() {
            // sending request;
        }
    }
}