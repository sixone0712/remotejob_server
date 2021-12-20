package jp.co.canon.rss.logmanager.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Slf4j
public class ExcelReader {
    private static String uploadPath;
    @Value("${file.upload-dir}")
    public void setUploadPath(String path) {
        uploadPath = path;
    }

    public XSSFWorkbook readExcel(File file) {
        XSSFWorkbook workbook = null;
        try{
            FileInputStream fis = new FileInputStream(file);
            workbook = new XSSFWorkbook(fis);
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return workbook;
    }
}
