package jp.co.canon.rss.logmanager.system;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileUploadProperties {

    @Value("${file.upload-dir}")
    @Getter
    private String uploadDir;

    @Value("${file.download-dir}")
    @Getter
    private String downloadPath;

/*    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }*/
}
