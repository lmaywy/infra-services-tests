package utils;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FileUtil {
    public static void saveAs(String content, String fileName, String ext)
            throws IOException {
        String filePath = "c:\\ACCC_Cases\\";
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath + fileName + ext));
        writer.write(content);
        writer.close();
    }

    public static void writeCSV(List<String[]> data, String filePath) throws Exception {

        File file = null;
        List<String[]> originData = null;
        if (Files.exists(Paths.get(filePath))) {
            originData = readCSV(filePath);
            data.remove(0);
        }
        file = new File(filePath);
        FileOutputStream fos = new FileOutputStream(file);
        OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
        osw.write('\ufeff');
        CSVWriter writer = new CSVWriter(osw);

        if (originData != null)
            writer.writeAll(originData);
        writer.writeAll(data);
        writer.close();
    }

    public static List<String[]> readCSV(String file) throws Exception {
        CSVReader reader = new CSVReader(new FileReader(file));
        return reader.readAll();
    }
}
