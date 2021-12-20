package jp.co.canon.rss.logmanager.system;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileUploadDownloadService {
    private final Path fileLocation;

    @Autowired
    public FileUploadDownloadService(FileUploadProperties prop) throws FileUploadException {
        this.fileLocation = Paths.get(prop.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileLocation);
        }catch(Exception e) {
            throw new FileUploadException("Could not created directory(File Upload).", e);
        }
    }


    public String storeFile(MultipartFile file) throws FileUploadException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            if(fileName.contains(".."))
                throw new FileUploadException("The file name contains illegal characters. " + fileName);

            Path targetLocation = this.fileLocation.resolve(fileName);

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        }catch(Exception e) {
            throw new FileUploadException("["+fileName+"] File upload failed. Please try again.",e);
        }
    }

    public Resource loadFileAsResource(String fileName) throws FileDownloadException {

        try {
            Path filePath = this.fileLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            }else {
                throw new FileDownloadException(fileName + " The file could not be found.");
            }
        }catch(MalformedURLException e) {
            throw new FileDownloadException(fileName + " The file could not be found.", e);
        }
    }

    // 로컬 파일 저장(파일이름 변경)
    public String storeLocalFile(MultipartFile file, String fileName) throws FileUploadException {
        try {
            if(fileName.contains(".."))
                throw new FileUploadException("The file name contains illegal characters. " + fileName);

            Path targetLocation = this.fileLocation.resolve(fileName);

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        }catch(Exception e) {
            throw new FileUploadException("["+fileName+"] File upload failed. Please try again.",e);
        }
    }

}
