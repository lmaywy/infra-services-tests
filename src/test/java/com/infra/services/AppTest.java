package com.infra.services;

import io.restassured.response.Response;
import org.jsoup.select.Elements;
import org.junit.Test;
import utils.FileUtil;
import utils.RestUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Unit test for simple App.
 */
public class AppTest {

    private String filePath = "c:\\ACCC_Cases\\case_links.csv";
    private String casesPath = "c:\\ACCC_Cases\\cases.csv";

    @Test
    public void getACCCCases() throws Exception {
        String baseUrl = "https://www.accc.gov.au";
        String paraPage = "";
        List<String[]> caseLinks = new ArrayList<String[]>();
        int totalPage = 344;


        for (int i = 0; i < totalPage; i++) {
            if (i != 0)
                paraPage = "?page=" + i;
            RestUtil.setBaseURI(baseUrl);
            Response response = RestUtil.getResponse("/media/media-releases" + paraPage);
            Document doc = Jsoup.parse(response.body().asString());
            Elements links = doc.select(".views-row a");
            List<String> hrefList = links.eachAttr("href");
            for (String s : hrefList) {
                caseLinks.add(new String[]{s});
            }

            System.out.println("----------finish the page:" + (i + 1) + "------------");
            TimeUnit.MILLISECONDS.sleep(500);
        }
        FileUtil.writeCSV(caseLinks, filePath);
    }

    @Test
    public void getACCC_CaseDetails() throws Exception {
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
                    .replace("<BR>","\r\n")
                    .replace("<BR/>","\r\n");

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

    @Test
    public void getACCCCases_both() throws Exception {
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

            for (int j = 0; j < 1; j++) {
                count++;
                Response article = RestUtil.getResponse(titleLinks.get(j));
                String title = titleLinks.get(j).split("/")[2];
                String log = (i + 1) + "-" + count + "_" + title;
                String body = article.body().asString();

                String[] item = new String[10];
                // only get the cases content
                Document detail = Jsoup.parse(body);
                Element ele = detail.select(".field-type-text-with-summary").get(0);
                String content = ele.wholeText();

                String date = detail.getElementsByClass("date-display-single").get(0).text();

                //System.out.println(ele.text());
                //System.out.println("html" + ele.html());
                //System.out.println("text" + ele.text());
                System.out.println(content);


                item[0] = title;
                item[1] = date;
                item[2] = content;

                Elements eleLabels = detail.select(".field-label");

                for (int k = 0; k < 1; k++) {
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

        FileUtil.writeCSV(data, casesPath);
    }

}