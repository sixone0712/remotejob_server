package jp.co.canon.rss.logmanager.service;

import com.google.gson.Gson;
import jp.co.canon.rss.logmanager.config.ReqURLController;
import jp.co.canon.rss.logmanager.dto.errorlogdownload.*;
import jp.co.canon.rss.logmanager.exception.ConvertException;
import jp.co.canon.rss.logmanager.mapper.errorlogdownload.ErrorLogDLStatusVoResErrorLogDLStatusDTOMapper;
import jp.co.canon.rss.logmanager.mapper.errorlogdownload.ErrorLogDLVoResSettringListDTOMapper;
import jp.co.canon.rss.logmanager.repository.ErrorLogDownloadSettingRepository;
import jp.co.canon.rss.logmanager.repository.ErrorLogDownloadStatusRepository;
import jp.co.canon.rss.logmanager.repository.SiteRepository;
import jp.co.canon.rss.logmanager.service.errorlogdown.ErrorLogFileDownloaderFromCras;
import jp.co.canon.rss.logmanager.util.CallRestAPI;
import jp.co.canon.rss.logmanager.util.FileManageUtils;
import jp.co.canon.rss.logmanager.vo.ErrorLogDownloadSettingVo;
import jp.co.canon.rss.logmanager.vo.ErrorLogDownloadStatusVo;
import jp.co.canon.rss.logmanager.vo.SiteVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service()
public class ErrorLogDownloadService {
    @Value("${file.upload-dir}")
    private String downloadPath;

    SiteRepository siteRepository;
    ErrorLogDownloadSettingRepository errorLogDownloadSettingRepository;
    ErrorLogDownloadStatusRepository errorLogDownloadStatusRepository;
    List<ResErrorLogDownloadStatusDTO> resErrorLogDownloadStatusDTOList = new ArrayList<>();

    public ErrorLogDownloadService(SiteRepository siteRepository,
                                   ErrorLogDownloadSettingRepository errorLogDownloadSettingRepository,
                                   ErrorLogDownloadStatusRepository errorLogDownloadStatusRepository) {
        this.siteRepository = siteRepository;
        this.errorLogDownloadSettingRepository = errorLogDownloadSettingRepository;
        this.errorLogDownloadStatusRepository = errorLogDownloadStatusRepository;
    }

    @PostConstruct
    private void _init() throws InterruptedException {
        List<ErrorLogDownloadStatusVo> errorLogDownloadStatusVoList = errorLogDownloadStatusRepository.findAll();

        for(ErrorLogDownloadStatusVo errorLogDownloadStatusVo : errorLogDownloadStatusVoList) {
            if(errorLogDownloadStatusVo.getStatus().equals("processing")) {
                errorLogDownloadStatusVo.setStatus("failure");
                errorLogDownloadStatusRepository.save(errorLogDownloadStatusVo);
            }
        }
        log.info("All Error Log Download tasks are forced to end.");
    }

    public Object getErrorLogDownloadList(int siteId) throws ConvertException {
        SiteVo siteVo = siteRepository.findById(siteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        CallRestAPI callRestAPI = new CallRestAPI();

        HttpHeaders headers = new HttpHeaders();
        headers.set(ReqURLController.JOB_CONTENT_TYPE, ReqURLController.JOB_APPLICATION_JSON);
        HttpEntity reqHeaders = new HttpEntity<>(headers);

        String crasServer = String.format(ReqURLController.API_DEFAULT_CRAS_SERVER_ERRORLOG,
                siteVo.getCrasAddress(), siteVo.getCrasPort());

        ResponseEntity<?> resErrorLogList = callRestAPI.getWithCustomHeaderRestAPI(
                crasServer + ReqURLController.API_GET_ERRORLOG_LIST, reqHeaders, ResErrorLogDownloadListDTO.class);

        ResErrorLogDownloadListDTO resErrorLogDownloadListDTO = (ResErrorLogDownloadListDTO) resErrorLogList.getBody();

        return resErrorLogDownloadListDTO.getData();
    }

    public List<ResSettingListDTO> getSettingList(int siteId) throws ConvertException {
        List<ErrorLogDownloadSettingVo> errorLogDownloadSettingVoList = Optional
                .ofNullable(errorLogDownloadSettingRepository.findBySiteId(siteId, Sort.by(Sort.Direction.DESC, "id")))
                .orElse(Collections.emptyList());

        List<ResSettingListDTO> resSettingListDTOList = new ArrayList<>();
        for(ErrorLogDownloadSettingVo errorLogDownloadSettingVo : errorLogDownloadSettingVoList) {
            ResSettingListDTO resSettingListDTO =
                    ErrorLogDLVoResSettringListDTOMapper.INSTANCE.mapErrorLogDlVoToDto(errorLogDownloadSettingVo);
            resSettingListDTOList.add(resSettingListDTO);
        }
        return resSettingListDTOList;
    }

    public void reqDownload(int siteId, ReqErrorLogDownloadDTO reqErrorLogDownloadDTO) throws ConvertException {
        SiteVo siteVo = siteRepository.findById(siteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        CallRestAPI callRestAPI = new CallRestAPI();

        String occurredDate = reqErrorLogDownloadDTO.getOccurred_date().replaceAll("[- :]", "");

        String [] machineList = {reqErrorLogDownloadDTO.getEquipment_name()};
        String [] category = {reqErrorLogDownloadDTO.getCommand()};

        String ftpType = null;
        switch (reqErrorLogDownloadDTO.getType()) {
            case (ReqURLController.DOWNLOAD_FTP) :
                ftpType = ReqURLController.DOWNLOAD_FTP_CRAS;
                break;
            case (ReqURLController.DOWNLOAD_VFTP_COMPAT) :
                ftpType = ReqURLController.DOWNLOAD_VFTP_COMPAT_CRAS;
                break;
            case (ReqURLController.DOWNLOAD_VFTP_SSS) :
                ftpType = ReqURLController.DOWNLOAD_VFTP_SSS_CRAS;
                break;
        }

        ReqErrorLogDownloadCrasDTO reqErrorLogDownloadCrasDTO = new ReqErrorLogDownloadCrasDTO()
                .setFtp_type(ftpType)
                .setStart_date(reqErrorLogDownloadDTO.getStart().replaceAll("[- :]", ""))
                .setEnd_date(reqErrorLogDownloadDTO.getEnd().replaceAll("[- :]", ""))
                .setMachine(machineList)
                .setCommand(category)
                .setDevice(reqErrorLogDownloadDTO.getDevice())
                .setProcess(reqErrorLogDownloadDTO.getProcess());

        HttpHeaders headers = new HttpHeaders();
        headers.set(ReqURLController.JOB_CONTENT_TYPE, ReqURLController.JOB_APPLICATION_JSON);
        headers.set(ReqURLController.JOB_CLIENT_ID, occurredDate + "-" + reqErrorLogDownloadDTO.getEquipment_name());
        log.info("client ID :" + occurredDate + "-" + reqErrorLogDownloadDTO.getEquipment_name());

        HttpEntity<Object> requestEntity = new HttpEntity<>(reqErrorLogDownloadCrasDTO, headers);

        String crasServer = String.format(ReqURLController.API_DEFAULT_CRAS_SERVER_ERRORLOG,
                siteVo.getCrasAddress(), siteVo.getCrasPort());

        Object response = callRestAPI.postRestAPI(crasServer+ReqURLController.API_POST_ERRORLOG_CRAS_DOWNLOAD,
                requestEntity,
                Object.class,
                ReqURLController.API_POST_ERRORLOG_CRAS_DOWNLOAD);

        Gson gson = new Gson();
        ResRidDTO resRidDTO = gson.fromJson(response.toString(), ResRidDTO.class);

        ErrorLogDownloadStatusVo errorLogDownloadStatusVo = new ErrorLogDownloadStatusVo()
                .setSiteId(siteId)
                .setRid(resRidDTO.getRid())
                .setOccurred_date(reqErrorLogDownloadDTO.getOccurred_date())
                .setEquipment_name(reqErrorLogDownloadDTO.getEquipment_name())
                .setDownload_id(resRidDTO.getRid())
                .setError_code(reqErrorLogDownloadDTO.getError_code())
                .setType(reqErrorLogDownloadDTO.getType())
                .setCommand(reqErrorLogDownloadDTO.getCommand())
                .setStart(reqErrorLogDownloadDTO.getStart())
                .setEnd(reqErrorLogDownloadDTO.getEnd())
                .setDevice(reqErrorLogDownloadDTO.getDevice())
                .setProcess(reqErrorLogDownloadDTO.getProcess())
                .setSiteVoList(siteVo);
        errorLogDownloadStatusRepository.save(errorLogDownloadStatusVo);

        Thread thread = new Thread(new ErrorLogFileDownloaderFromCras(resRidDTO.getRid(), downloadPath, errorLogDownloadStatusRepository));
        thread.setName("ErrorLog_"+resRidDTO.getRid());
        thread.start();
    }

    public List<ResErrorLogDownloadStatusDTO> getDownloadList(int siteId) throws ConvertException {
        List<ErrorLogDownloadStatusVo> errorLogDownloadStatusVoList =
                errorLogDownloadStatusRepository.findBySiteId(siteId, Sort.by(Sort.Direction.DESC, "id"));

        List<ResErrorLogDownloadStatusDTO> resErrorLogDownloadStatusDTOList =
                ErrorLogDLStatusVoResErrorLogDLStatusDTOMapper.INSTANCE.toDto(errorLogDownloadStatusVoList);
        return resErrorLogDownloadStatusDTOList;
    }

    public void getDownloadFile(String rid, HttpServletResponse response) throws ConvertException, IOException {
        ErrorLogDownloadStatusVo errorLogDownloadStatusVo = errorLogDownloadStatusRepository.findByRid(rid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        String getDowonloadUrlSplit[] = errorLogDownloadStatusVo.getDownload_url().split("/");
        String fileName = getDowonloadUrlSplit[getDowonloadUrlSplit.length - 1];

        FileManageUtils fileManageUtils = new FileManageUtils();
        File file = new File(downloadPath, errorLogDownloadStatusVo.getSaved_file_name());
        fileManageUtils.fileDownload(response, file, fileName);
    }
}
