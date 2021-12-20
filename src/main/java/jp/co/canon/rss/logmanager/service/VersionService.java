package jp.co.canon.rss.logmanager.service;

import jp.co.canon.rss.logmanager.dto.version.ResVersionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Service()
public class VersionService {
    public ResVersionDTO checkVersionTXT() {
        try {
            ClassPathResource resource = new ClassPathResource("version.txt");

            Path path = Paths.get(resource.getURI());
            List<String> content = Files.readAllLines(path, StandardCharsets.UTF_8);
            ResVersionDTO resVersionDTO = new ResVersionDTO()
                    .setVersion(content.get(0));
            return resVersionDTO;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
