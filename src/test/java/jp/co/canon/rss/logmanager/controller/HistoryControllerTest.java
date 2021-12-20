package jp.co.canon.rss.logmanager.controller;

import jp.co.canon.rss.logmanager.service.HistroyService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;

@SpringBootTest
public class HistoryControllerTest {
    private HistroyService histroyService;
    HistoryController historyController = new HistoryController(histroyService);

    @Test
    void getBuildLogList() {
        //convert
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "http://localhost:8080/logmonitor/api/status/remote/1/histories/convert");
        historyController.getBuildLogList(req, "remote", 1, "convert");

        req = new MockHttpServletRequest("GET", "http://localhost:8080/logmonitor/api/status/local/1/histories/convert");
        historyController.getBuildLogList(req, "local", 1, "convert");

        //error
        req = new MockHttpServletRequest("GET", "http://localhost:8080/logmonitor/api/status/remote/1/histories/error");
        historyController.getBuildLogList(req, "remote", 1, "error");

        //cras
        req = new MockHttpServletRequest("GET", "http://localhost:8080/logmonitor/api/status/remote/1/histories/cras");
        historyController.getBuildLogList(req, "remote", 1, "cras");

        //version
        req = new MockHttpServletRequest("GET", "http://localhost:8080/logmonitor/api/status/remote/1/histories/version");
        historyController.getBuildLogList(req, "remote", 1, "version");
    }

    @Test
    void testGetBuildLogList() {
    }

    @Test
    void getBuildLogTextList() {
        //convert
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "http://localhost:8080/logmonitor/api/status/remote/1/histories/convert/1");
        historyController.getBuildLogTextList(req, "remote", 1, "convert", "1");

        //error
        req = new MockHttpServletRequest("GET", "http://localhost:8080/logmonitor/api/status/remote/1/histories/error/1");
        historyController.getBuildLogTextList(req, "remote", 1, "error", "1");

        //cras
        req = new MockHttpServletRequest("GET", "http://localhost:8080/logmonitor/api/status/remote/1/histories/cras/1");
        historyController.getBuildLogTextList(req, "remote", 1, "cras","1");

        //version
        req = new MockHttpServletRequest("GET", "http://localhost:8080/logmonitor/api/status/remote/1/histories/version/1");
        historyController.getBuildLogTextList(req, "remote", 1, "version","1");
    }

    @Test
    void getBuildLogText() {
    }
}
