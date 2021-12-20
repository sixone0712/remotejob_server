package jp.co.canon.rss.logmanager.controller;

import jp.co.canon.rss.logmanager.exception.StatusResourceNotFoundException;
import jp.co.canon.rss.logmanager.service.AnalysisToolService;
import jp.co.canon.rss.logmanager.service.VersionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

class AnalysisToolApiControllerTest {

    AnalysisToolService analysisToolService;
    private VersionService versionService;
    AnalysisToolApiController analysisToolApiController = new AnalysisToolApiController(analysisToolService, versionService);

    @BeforeEach
    void setUp() {
    }

    @Test
    void getAllMpaList() {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "http://localhost:8080/logmonitor/api/equipments");
        analysisToolApiController.getAllMpaList(req);
    }

    @Test
    void getLogTime() {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "http://localhost:8080/logmonitor/api/date/PLATEAUTOFOCUSCOMPENSATION/BSOT_s2_SBPCN480_G147");
        try {
            analysisToolApiController.getLogTime(req, "PLATEAUTOFOCUSCOMPENSATION", "BSOT_s2_SBPCN480_G147");
        } catch (StatusResourceNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getLogData() {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "http://localhost:8080/logmonitor/api/log/BSOT_s2_SBPCN480_G147/PLATEAUTOFOCUSCOMPENSATION?start=2021-05-10&end=2021-09-06");
        analysisToolApiController.getLogData(req, "BSOT_s2_SBPCN480_G147", "PLATEAUTOFOCUSCOMPENSATION", "2021-05-10", "2021-09-06");
    }
}