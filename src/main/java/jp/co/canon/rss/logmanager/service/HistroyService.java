package jp.co.canon.rss.logmanager.service;

import com.vladmihalcea.hibernate.type.util.StringUtils;
import jp.co.canon.rss.logmanager.config.ReqURLController;
import jp.co.canon.rss.logmanager.dto.history.ResHistoryDTO;
import jp.co.canon.rss.logmanager.exception.ConvertException;
import jp.co.canon.rss.logmanager.mapper.history.HistoryVoDTOMapper;
import jp.co.canon.rss.logmanager.repository.HistoryRepository;
import jp.co.canon.rss.logmanager.repository.LocalJobRepository;
import jp.co.canon.rss.logmanager.repository.RemoteJobRepository;
import jp.co.canon.rss.logmanager.util.CallRestAPI;
import jp.co.canon.rss.logmanager.vo.HistoryVo;
import jp.co.canon.rss.logmanager.vo.LocalJobVo;
import jp.co.canon.rss.logmanager.vo.RemoteJobVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service()
public class HistroyService {
    @Value("${logmonitor.logging.root}")
    private String loggingPath;

    HistoryRepository historyRepository;
    RemoteJobRepository remoteJobRepository;
    LocalJobRepository localJobRepository;

    public HistroyService(RemoteJobRepository remoteJobRepository,
                      LocalJobRepository localJobRepository, HistoryRepository historyRepository) {
        this.remoteJobRepository = remoteJobRepository;
        this.localJobRepository = localJobRepository;
        this.historyRepository = historyRepository;
    }

    public List<ResHistoryDTO>  getBuildLogList(String jobTypeFlag, int jobId, String flag) throws ConvertException {
        List<ResHistoryDTO> result = new ArrayList<>();

        List<HistoryVo> resHistoryDTOList = historyRepository.findByRequestId(jobTypeFlag + "-" + flag + "-" + jobId,
                Sort.by(Sort.Direction.DESC, "runningTime"));
        List<HistoryVo> resHistoryDTOListManual = historyRepository.findByRequestId(ReqURLController.JOB_TYPE_MANUALJOB + "-" + flag + "-" + jobId,
                Sort.by(Sort.Direction.DESC, "runningTime"));
        resHistoryDTOList.addAll(resHistoryDTOListManual);

        Collections.sort(resHistoryDTOList, new Comparator<HistoryVo>() {
            @Override
            public  int compare(HistoryVo o1, HistoryVo o2) {
                return o2.getRunningTime().compareTo(o1.getRunningTime());
            }
        });

        for(HistoryVo historyVo : resHistoryDTOList) {
            if(historyVo.getStatus().equals(ReqURLController.JOB_STATUS_SUCCESS)
                    || historyVo.getStatus().equals(ReqURLController.JOB_STATUS_FAILURE)
                    || historyVo.getStatus().equals(ReqURLController.JOB_STATUS_NODATA)) {
                ResHistoryDTO resHistoryDTO = HistoryVoDTOMapper.INSTANCE.mapResHistoryDTO(historyVo);
                if(resHistoryDTO.getId()!=null)
                    result.add(resHistoryDTO);
            }
        }

        return result;
    }

    public String getBuildLogTextCras(String jobTypeFlag, int jobId, String flag, String buildLogId) throws ConvertException {
        RemoteJobVo getRemoteJobInfo;
        LocalJobVo getLocalJobInfo;
        String crasserver = null;

        switch (jobTypeFlag) {
            case ReqURLController.JOB_TYPE_REMOTEJOB :
                getRemoteJobInfo = remoteJobRepository.findByJobId(jobId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
                crasserver = String.format("%s:%s", getRemoteJobInfo.getSiteVoList().getCrasAddress(), getRemoteJobInfo.getSiteVoList().getCrasPort());
                break;
            case ReqURLController.JOB_TYPE_LOCALJOB :
                getLocalJobInfo = localJobRepository.findByJobId(jobId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
                crasserver = String.format("%s:%s", getLocalJobInfo.getSiteVoListLocal().getCrasAddress(), getLocalJobInfo.getSiteVoListLocal().getCrasPort());
                break;
        }

        if(flag.equals(ReqURLController.JOB_STEP_ERROR))
            flag = ReqURLController.JOB_STEP_SUMMARY_CRAS;

        String GET_BUILD_LOG_TEXT_URL = String.format(ReqURLController.API_GET_BUILD_LOG_LIST_DETAIL, crasserver, flag, buildLogId);

        CallRestAPI callRestAPI = new CallRestAPI();
        HttpHeaders headers = new HttpHeaders();
        headers.set(ReqURLController.JOB_CONTENT_TYPE, ReqURLController.JOB_APPLICATION_JSON);
        headers.set(ReqURLController.JOB_CLIENT_ID, jobTypeFlag + "_" + String.format("%06d", jobId));
        HttpEntity reqHeaders = new HttpEntity<>(headers);

        ResponseEntity<?> response = callRestAPI.getWithCustomHeaderRestAPI(GET_BUILD_LOG_TEXT_URL, reqHeaders, String [].class);
        String [] responseBodyArray = (String[]) response.getBody();
        String result = "";

        for(String responseBody : responseBodyArray) {
            result += responseBody + "\n";
        }

        return result;
    }

    public String getBuildLogTextLogMonitor(String jobTypeFlag, int jobId, String flag, String buildLogId) throws ConvertException, IOException {
        HistoryVo resHistoryDTOList = historyRepository.findByRequestIdCras(buildLogId);

        List<String> readFile = Files.readAllLines(Paths.get(loggingPath + "/" +
                resHistoryDTOList.getHistoryName() + "/" + resHistoryDTOList.getHistoryName() + ".log"));

        String result = "";
        for(String readFileLine : readFile)
            result += readFileLine + "\n";

        return result;
    }
}
