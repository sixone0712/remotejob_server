package jp.co.canon.rss.logmanager.controller;

import com.google.gson.Gson;
import jp.co.canon.rss.logmanager.dto.job.ResLocalJobListDTO;
import jp.co.canon.rss.logmanager.dto.job.ResRemoteJobDetailDTO;
import jp.co.canon.rss.logmanager.exception.StatusResourceNotFoundException;
import jp.co.canon.rss.logmanager.service.JobService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;

@SpringBootTest
public class JobControllerTest {
    private JobService jobService;
    JobController jobController = new JobController(jobService);

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getRemoteJobList() {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "http://localhost:8080/logmonitor/api/status/remote");
        jobController.getRemoteJobList(req);
    }

    @Test
    void getRemoteJobDetail() {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "http://localhost:8080/logmonitor/api/status/remote/1");
        jobController.getRemoteJobDetail(req, 1);
    }

    @Test
    void addRemoteJob() {
        MockHttpServletRequest req = new MockHttpServletRequest("POST", "http://localhost:8080/logmonitor/api/status/remote");
        String jsonStr = "{\"siteId\": 1,\"planIds\": [2,4,6,8],\"sendingTimes\": [\"21:00\",\"13:00\"],\"isErrorSummary\": true,\"isCrasData\": true,\"isMpaVersion\": true,\"errorSummary\": {\"recipients\": [\"hmpark2@canon.bs.co.kr\", \"hmpark2@canon.bs.co.kr\"],\"subject\": \"hello? errorSummaryEmail_1\",\"body\": \"this is body_2\",\"before\": 8500 },\"crasData\": {\"recipients\": [\"hmpark2@canon.bs.co.kr\"],\"subject\": \"hello? crasDataEmail_3\",\"body\": \"this is body_4\",\"before\": 8500 },\"mpaVersion\": {\"recipients\": [\"hmpark2@canon.bs.co.kr\",\"hmpark2@canon.bs.co.kr\",\"hmpark2@canon.bs.co.kr\"],\"subject\": \"hello? version_email_5\",\"body\": \"this is body_6\",\"before\": 8500 } }";

        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(jsonStr);
            JSONObject jsonObj = (JSONObject) obj;
            Gson gson = new Gson();
            ResRemoteJobDetailDTO resRemoteJobDTO = gson.fromJson(jsonObj.toString(), ResRemoteJobDetailDTO.class);
            jobController.addRemoteJob(req, resRemoteJobDTO);
        } catch ( Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void mappingRemoteJob() {
    }

    @Test
    void deleteRemoteJob() {
    }

    @Test
    void updateJob() {
    }

    @Test
    void mappingRemoteJobEdit() {
    }

    @Test
    void getStatusRemoteJob() {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "http://localhost:8080/logmonitor/api/status/remote/1/status");
        jobController.getStatusRemoteJob(req,1);
    }

    @Test
    void runRemoteJob() {
        MockHttpServletRequest req = new MockHttpServletRequest("PATCH", "http://localhost:8080/logmonitor/api/status/remote/1/run");
        jobController.runRemoteJob(req,1);
    }

    @Test
    void stopRemoteJob() {
        MockHttpServletRequest req = new MockHttpServletRequest("PATCH", "http://localhost:8080/logmonitor/api/status/remote/1/stop");
        jobController.stopRemoteJob(req,1);
    }

    @Test
    void getLocalJobList() {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "http://localhost:8080/logmonitor/api/status/local");
        jobController.getLocalJobList(req);
    }

    @Test
    void setLocalJob() {
        MockHttpServletRequest req = new MockHttpServletRequest("POST", "http://localhost:8080/logmonitor/api/status/local");
        String jsonStr = "{\"siteId\": 1,\"fileIndices\": [1] }";
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(jsonStr);
            JSONObject jsonObj = (JSONObject) obj;
            Gson gson = new Gson();
            ResLocalJobListDTO resLocalJobListDTO = gson.fromJson(jsonObj.toString(), ResLocalJobListDTO.class);
            jobController.setLocalJob(req, resLocalJobListDTO);
        } catch ( Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void runLocalJob() {
    }

    @Test
    void deleteLocalJob() {
        MockHttpServletRequest req = new MockHttpServletRequest("DELETE", "http://localhost:8080/logmonitor/api/status/local/1");
        try {
            jobController.deleteLocalJob(req,1);
        } catch (StatusResourceNotFoundException e) {
            e.printStackTrace();
        }
    }
}
