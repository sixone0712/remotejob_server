package jp.co.canon.rss.logmanager.controller;

import jp.co.canon.rss.logmanager.service.UploadService;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;

@SpringBootTest
public class UploadControllerTest {
    UploadService uploadService;
    UploadController uploadController = new UploadController(uploadService);

    @Test
    void uploadFile() {
        MockHttpServletRequest req = new MockHttpServletRequest("POST", "http://localhost:8080/logmonitor/api/status/local/upload");
        MockMultipartFile multipartFile = new MockMultipartFile("file", "filename.txt","text/plain", "abcdef".getBytes());
        uploadController.uploadFile(req, multipartFile);
    }
}
