package jp.co.canon.rss.logmanager.controller;

import jp.co.canon.rss.logmanager.service.HostService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;

@SpringBootTest
public class HostControllerTest {
    HostService hostService;
    HostController hostController = new HostController(hostService);

    @Test
    void getApplicationYml() {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "http://localhost:8080/logmonitor/api/configure/host");
        hostController.getApplicationYml(req);
    }

    @Test
    void getSettingServerIP() {
    }
}
