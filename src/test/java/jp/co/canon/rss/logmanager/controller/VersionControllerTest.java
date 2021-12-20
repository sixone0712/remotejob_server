package jp.co.canon.rss.logmanager.controller;

import jp.co.canon.rss.logmanager.service.VersionService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;

@SpringBootTest
public class VersionControllerTest {
    VersionService versionService;
    VersionController versionController = new VersionController(versionService);

    @Test
    void getVersion() {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "http://localhost:8080/logmonitor/api/configure/version");
        versionController.getVersion(req);
    }
}
