package jp.co.canon.rss.logmanager.service;

import jp.co.canon.rss.logmanager.config.ReqURLController;
import jp.co.canon.rss.logmanager.dto.devlog.ReqLogMonitorDevLogDTO;
import jp.co.canon.rss.logmanager.dto.site.ResSitesDetailDTO;
import jp.co.canon.rss.logmanager.repository.SiteRepository;
import jp.co.canon.rss.logmanager.system.ClientManageService;
import jp.co.canon.rss.logmanager.util.FileManageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.zeroturnaround.zip.ZipUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Slf4j
@Service()
public class DevLogService {
    @Value("${dev-log.cras-server.zip-file-name}")
    private String crasZipFileName;

    @Value("${dev-log.cras-server.cras-server-docker-name}")
    private String crasDockerName;

    @Value("${dev-log.cras-server.cras-server-docker-port}")
    private String crasDockerPort;

    @Value("${dev-log.log-monitor.zip-file-name}")
    private String logMonitorZipFileName;

    @Value("${file.download-dir}")
    private String downloadPath;

    private SiteRepository siteRepository;
    private ClientManageService clientManageService;
    private FileManageUtils fileManageUtils;

    public DevLogService(SiteRepository siteRepository, ClientManageService clientManageService,
                         FileManageUtils fileManageUtils) {
        this.siteRepository = siteRepository;
        this.clientManageService = clientManageService;
        this.fileManageUtils = fileManageUtils;
    }

    public void getLogMonitorDevLog(HttpServletResponse response, ReqLogMonitorDevLogDTO reqLogMonitorDevLogDTO) throws IOException {
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        String fileName = currentTime+"_"+logMonitorZipFileName;

        File resultZip = new File(downloadPath+File.separator+fileName);
        File devLog = new File(reqLogMonitorDevLogDTO.getPath());
        ZipUtil.pack(devLog, resultZip);

        fileManageUtils.fileDownload(response, resultZip, fileName);
        resultZip.delete();
    }

    public void getCrasServerDevLog(HttpServletResponse response, int siteId) throws IOException {

        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        String currentTimeSecond = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String fileName = currentTime+"_"+crasZipFileName;
        String GET_CRAS_LEGACY_DATA_URL = null;

        if(siteId==0) {
            GET_CRAS_LEGACY_DATA_URL = String.format(ReqURLController.API_GET_CRAS_DEV_LOG + currentTimeSecond,
                    crasDockerName, crasDockerPort);
        }
        else {
            Optional<ResSitesDetailDTO> siteVo = siteRepository.findBySiteId(siteId);
            GET_CRAS_LEGACY_DATA_URL = String.format(ReqURLController.API_GET_CRAS_DEV_LOG + currentTimeSecond,
                    siteVo.get().getCrasAddress(), siteVo.get().getCrasPort());
        }
        clientManageService.download(GET_CRAS_LEGACY_DATA_URL, downloadPath + File.separator + fileName);
        File resultZip = new File(downloadPath + File.separator + fileName);
        fileManageUtils.fileDownload(response, resultZip, fileName);
        resultZip.delete();
    }
}
