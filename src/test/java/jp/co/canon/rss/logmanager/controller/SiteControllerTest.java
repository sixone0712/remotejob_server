package jp.co.canon.rss.logmanager.controller;

import jp.co.canon.rss.logmanager.dto.site.ReqConnectionCrasDTO;
import jp.co.canon.rss.logmanager.dto.site.ReqConnectionEmailDTO;
import jp.co.canon.rss.logmanager.dto.site.ReqConnectionRssDTO;
import jp.co.canon.rss.logmanager.service.SiteService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;

@SpringBootTest
public class SiteControllerTest {
    private SiteService siteService;
    SiteController siteController = new SiteController(siteService);

    @Test
    void getPlanInfo() {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "http://localhost:8080/logmonitor/api/configure/sites/1/plans");
        siteController.getPlanInfo(req, 1);
    }

    @Test
    void getSitesNamesList() {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "http://localhost:8080/logmonitor/api/configure/sites/names");
        siteController.getSitesNamesList(req, true);
    }

    @Test
    void getAllSites() {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "http://localhost:8080/logmonitor/api/configure/sites/");
        siteController.getAllSites(req);
    }

    @Test
    void getSitesDetail() {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "http://localhost:8080/logmonitor/api/configure/sites/1");
        siteController.getSitesDetail(req, 1);
    }

    @Test
    void addSites() {
    }

    @Test
    void updateSites() {
    }

    @Test
    void getSiteJobStatus() {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "http://localhost:8080/logmonitor/api/configure/sites/1/jobstatus");
        siteController.getSiteJobStatus(req, 1);
    }

    @Test
    void deletesSite() {
    }

    @Test
    void crasConnection() {
        ReqConnectionCrasDTO reqConnectionCrasDTO = new ReqConnectionCrasDTO();
        reqConnectionCrasDTO.setCrasAddress("10.1.31.195");
        reqConnectionCrasDTO.setCrasPort(5000);
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "http://localhost:8080/logmonitor/api/configure/sites/connection/cras");
        siteController.crasConnection(req, reqConnectionCrasDTO);
    }

    @Test
    void rssConnection() {
        ReqConnectionRssDTO reqConnectionRssDTO = new ReqConnectionRssDTO();
        reqConnectionRssDTO.setRssAddress("10.1.31.195");
        reqConnectionRssDTO.setRssPort(3000);
        reqConnectionRssDTO.setRssPassword("696d29e0940a4957748fe3fc9efd22a3");
        reqConnectionRssDTO.setRssUserName("Administrator");
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "http://localhost:8080/logmonitor/api/configure/sites/connection/rss");
        siteController.rssConnection(req, reqConnectionRssDTO);
    }

    @Test
    void emailConnection() {
        ReqConnectionEmailDTO reqConnectionEmailDTO = new ReqConnectionEmailDTO();
        reqConnectionEmailDTO.setEmailAddress("10.1.2.70");
        reqConnectionEmailDTO.setEmailPort(587);
        reqConnectionEmailDTO.setEmailUserName("hkkim7@canon-bs.co.kr");
        reqConnectionEmailDTO.setEmailPassword("ckbsnrd2!@");
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "http://localhost:8080/logmonitor/api/configure/sites/connection/email");
        siteController.emailConnection(req, reqConnectionEmailDTO);
    }
}
