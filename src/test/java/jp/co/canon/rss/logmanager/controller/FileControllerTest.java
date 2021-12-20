package jp.co.canon.rss.logmanager.controller;

import jp.co.canon.rss.logmanager.system.FileUploadResponse;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FileControllerTest {
    @Autowired
    private FileController controller;

    @Test
    void uploadFile() {
        MockMultipartFile multipartFile = new MockMultipartFile("file", "filename.txt",
                "text/plain", "abcdef".getBytes());
        try {
            FileUploadResponse response = controller.uploadFile(multipartFile);
            assertNotNull(response);
        } catch (FileUploadException e) {
            e.printStackTrace();
        }
    }
}