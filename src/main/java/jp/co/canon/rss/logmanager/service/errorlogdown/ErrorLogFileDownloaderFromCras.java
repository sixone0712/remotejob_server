package jp.co.canon.rss.logmanager.service.errorlogdown;

import jp.co.canon.rss.logmanager.config.ReqURLController;
import jp.co.canon.rss.logmanager.dto.errorlogdownload.ResErrorLogDownloadStatusCrasDTO;
import jp.co.canon.rss.logmanager.dto.errorlogdownload.ResErrorLogDownloadStatusDTO;
import jp.co.canon.rss.logmanager.repository.ErrorLogDownloadStatusRepository;
import jp.co.canon.rss.logmanager.util.CallRestAPI;
import jp.co.canon.rss.logmanager.util.DownloadStreamInfo;
import jp.co.canon.rss.logmanager.vo.ErrorLogDownloadStatusVo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ErrorLogFileDownloaderFromCras implements Runnable {
    String rid;
    String downloadPath;
    ErrorLogDownloadStatusRepository errorLogDownloadStatusRepository;

    List<ResErrorLogDownloadStatusDTO> resErrorLogDownloadStatusDTOList = new ArrayList<>();

    public ErrorLogFileDownloaderFromCras(String rid, String downloadPath, ErrorLogDownloadStatusRepository errorLogDownloadStatusRepository) {
        this.rid = rid;
        this.downloadPath = downloadPath;
        this.errorLogDownloadStatusRepository = errorLogDownloadStatusRepository;
    }

    @SneakyThrows
    @Override
    public void run() {
        String status = null;
        CallRestAPI callRestAPI = new CallRestAPI();

        ErrorLogDownloadStatusVo errorLogDownloadStatusVo = errorLogDownloadStatusRepository.findByRid(rid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        String crasServer = String.format(ReqURLController.API_DEFAULT_CRAS_SERVER_ERRORLOG,
                errorLogDownloadStatusVo.getSiteVoList().getCrasAddress(), errorLogDownloadStatusVo.getSiteVoList().getCrasPort());

        String occurredDate = errorLogDownloadStatusVo.getOccurred_date().replaceAll("[- :]", "");
        HttpHeaders headers = new HttpHeaders();
        headers.set(ReqURLController.JOB_CONTENT_TYPE, ReqURLController.JOB_APPLICATION_JSON);
        headers.set(ReqURLController.JOB_CLIENT_ID, occurredDate + "-" + errorLogDownloadStatusVo.getEquipment_name());
        HttpEntity reqHeaders = new HttpEntity<>(headers);

        // Check the download status
        try {
            long startTime = System.currentTimeMillis();
            do {
                ResponseEntity<?> resErrorLogStatus = callRestAPI.getWithCustomHeaderRestAPI(
                        crasServer + ReqURLController.API_GET_ERRORLOG_CRAS_DOWNLOAD + errorLogDownloadStatusVo.getRid(),
                        reqHeaders, ResErrorLogDownloadStatusCrasDTO.class);

                ResErrorLogDownloadStatusCrasDTO resErrorLogDownloadStatusCrasDTO = (ResErrorLogDownloadStatusCrasDTO) resErrorLogStatus.getBody();

                if (resErrorLogDownloadStatusCrasDTO.getStatus().equals("success"))
                    status = "cras_success";
                else if (resErrorLogDownloadStatusCrasDTO.getStatus().equals("nodata"))
                    status = "cras_nodata";
                else if (resErrorLogDownloadStatusCrasDTO.getStatus().equals("error"))
                    status = "cras_error";
                else
                    status = "cras_processing";
                log.info("Download ErrorLog file to CRAS server :: " + status);

                errorLogDownloadStatusRepository.updateStatus(resErrorLogDownloadStatusCrasDTO.getId(), "processing");
                errorLogDownloadStatusRepository.updateDownloadUrl(resErrorLogDownloadStatusCrasDTO.getId(),
                        resErrorLogDownloadStatusCrasDTO.getDownload_url() != null ? resErrorLogDownloadStatusCrasDTO.getDownload_url()[0] : null);
                errorLogDownloadStatusRepository.updateError(resErrorLogDownloadStatusCrasDTO.getId(),
                        resErrorLogDownloadStatusCrasDTO.getError() != null ? resErrorLogDownloadStatusCrasDTO.getError()[0] : null);

                try {
                    Thread.sleep(5 * 1000);
                    if((System.currentTimeMillis()-startTime) > (1 * 60 * 60 * 1000)) {
                        status = "cras_error";
                        log.info("Download ended due to timeout occurrence");
                    }

                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            } while (status.equals("cras_processing"));
            log.info("Complete Download to CRAS server!");
        }
        catch (Exception e) {
            e.printStackTrace();
            status = "cras_error";
        }

        // File Download CRAS -> Log Monitor
        if(status.equals("cras_success")) {
            errorLogDownloadStatusVo = errorLogDownloadStatusRepository.findByRid(rid)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            String getDowonloadUrlSplit[] = errorLogDownloadStatusVo.getDownload_url().split("/");
            String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSS"))
                    + "_"
                    + getDowonloadUrlSplit[getDowonloadUrlSplit.length - 1];

            String crasServerDL = String.format(ReqURLController.API_DEFAULT_CRAS_SERVER_ERRORLOG_DOWNLOAD,
                    errorLogDownloadStatusVo.getSiteVoList().getCrasAddress(), errorLogDownloadStatusVo.getSiteVoList().getCrasPort());

            try {
                DownloadStreamInfo res = callRestAPI.getWithCustomHeaderDownloadFileRestAPI(
                        crasServerDL + errorLogDownloadStatusVo.getDownload_url(), reqHeaders, downloadPath, fileName);

                errorLogDownloadStatusRepository.updateSavedFileName(errorLogDownloadStatusVo.getRid(), fileName);
                errorLogDownloadStatusRepository.updateStatus(errorLogDownloadStatusVo.getRid(), "success");
            }
            catch (Exception e) {
                e.printStackTrace();
                errorLogDownloadStatusRepository.updateStatus(errorLogDownloadStatusVo.getRid(), "failure");
            }
        }
        else if(status.equals("cras_nodata"))
            errorLogDownloadStatusRepository.updateStatus(errorLogDownloadStatusVo.getRid(), "nodata");
        else
            errorLogDownloadStatusRepository.updateStatus(errorLogDownloadStatusVo.getRid(), "failure");
    }

    public List<ResErrorLogDownloadStatusDTO> getDownloadList(int siteId) {
        List<ResErrorLogDownloadStatusDTO> downloadList = new ArrayList<>();

        for(ResErrorLogDownloadStatusDTO resErrorLogDownloadStatusDTO : resErrorLogDownloadStatusDTOList) {
            if(resErrorLogDownloadStatusDTO.getSiteId() == siteId)
                downloadList.add(resErrorLogDownloadStatusDTO);
        }

        return downloadList;
    }
}
