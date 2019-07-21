package com.infra.services;

import io.restassured.response.Response;
import org.jsoup.select.Elements;
import org.junit.Test;
import utils.FileUtil;
import utils.RestUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Unit test for simple App.
 */
public class AppTest {

    private String filePath = "c:\\ACCC_Cases\\case_links.csv";
    private String casesPath = "c:\\ACCC_Cases\\cases_%s.csv";
    private String[] columns = new String[]{"title", "report date", "content", "Sub Links", "Release number", "ACCC Infocentre", "Media enquiries", "Additional contacts", "Audience", "Topics"};
    private String baseUrl = "https://www.accc.gov.au";
    private String rawHTMLPath = "c:\\ACCC_Cases\\Raw_HTML\\";

    @Test
    public void getACCCCases() throws Exception {
        String paraPage;
        List<String[]> caseLinks = new ArrayList<String[]>();
        int totalPage = 344;

        for (int i = 0; i < totalPage; i++) {
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
        RestUtil.setBaseURI(baseUrl);
        caseLinks.size();

        for (int j = 0; j < caseLinks.size(); j++) {
            String path = caseLinks.get(j)[0];
            Response article = RestUtil.getResponse(path);

            String body = article.body().asString();
            Document detail = Jsoup.parse(body);
            String fileName = rawHTMLPath + (j + 1) + "-" + path.split("/")[2];
            // download to local
            FileUtil.saveAs(detail.html(), fileName, ".html");
            System.out.println("start to save the ACCC news:" + fileName);
            TimeUnit.MILLISECONDS.sleep(500);
        }
    }

    private String[] getItem(Document detail) throws IOException {
        Element ele = detail.select(".field-type-text-with-summary").get(0);
        String content = ele.wholeText();
        String date = detail.getElementsByClass("date-display-single").get(0).text();
        String title = detail.getElementById("page-title").text();
        String subLinks = String.join("\n",ele.select("a").eachAttr("href"));

        String[] item = new String[15];
        item[0] = title;
        item[1] = date;
        item[2] = content.replaceAll("(?m)^\\s*$(\\n|\\r\\n)", "").trim().replaceAll("  +", "");
        item[3] = subLinks;
        Elements eleLabels = detail.select(".field-label");

        for (int k = 0; k < eleLabels.size(); k++) {
            Elements label_values = eleLabels.get(k).nextElementSibling().select(".field-item");

            //System.out.println("column:"+eleLabels.get(k).text());
            int index = Arrays.asList(columns).indexOf(eleLabels.get(k).text().replace(":", ""));
            item[index] = "";
            for (int m = 0; m < label_values.size(); m++) {
                String text = label_values.get(m).text();
                item[index] += text + ";";
            }
        }
        return item;
    }

    @Test
    public void crawlFromLocal() throws Exception {
        File[] list = new File(rawHTMLPath).listFiles();
        Arrays.sort(list, Comparator.comparingLong(File::lastModified));
        int countPerFile = 500;

        List<String[]> data = new ArrayList<>();
        data.add(columns);

        for (int i = 0; i < list.length; i++) {
            if (list[i].isFile()) {
                Document doc = Jsoup.parse(list[i], "UTF-8");
                String[] item = getItem(doc);
                data.add(item);
                if (i != 0 && (i + 1) % countPerFile == 0) {
                    FileUtil.writeCSV(data, String.format(casesPath, i + 1));
                    data.clear();
                    data.add(columns);
                } else if (i == list.length - 1) {
                    FileUtil.writeCSV(data, String.format(casesPath, i + 1));
                }
            }
        }

        // save all items into one file
           /* for (File file : list) {
                if (file.isFile()) {
                    if (file.getName().endsWith(".html")) {
                        Document doc = Jsoup.parse(file, "UTF-8");
                        String[] item = getItem(doc);
                        data.add(item);
                    }
                }
            }
*/
        //FileUtil.writeCSV(data, casesPath);
    }
}