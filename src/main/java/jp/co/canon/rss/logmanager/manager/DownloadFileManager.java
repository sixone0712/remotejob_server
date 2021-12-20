package jp.co.canon.rss.logmanager.manager;

import jp.co.canon.rss.logmanager.repository.LocalJobFileIdVoRepository;
import jp.co.canon.rss.logmanager.vo.LocalJobFileIdVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@EnableScheduling
@Component
public class DownloadFileManager {
    private static String downloadPath;
    private static String uploadPath;
    private LocalJobFileIdVoRepository localJobFileIdVoRepository;

    @Value("${file.upload-dir}")
    public void setUploadPath(String path) {
        uploadPath = path;
    }

    @Value("${file.download-dir}")
    public void setDownloadPath(String path) {
        downloadPath = path;
    }

    public DownloadFileManager(
            JobManager manager
    ) {
        this.localJobFileIdVoRepository = manager.getLocalJobFileIdVoRepository();
    }

    @Scheduled(cron = "${file.file-delete-scheduled}")
    public void downloadFileDelete() {
        File downloadfolder = new File(downloadPath);
        File uploadfolder = new File(uploadPath);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String currentTime = dateFormat.format(new Date(System.currentTimeMillis()));

        List<LocalJobFileIdVo> localFileList = localJobFileIdVoRepository.findAll();
        LocalJobFileIdVo delLocalFileList = new LocalJobFileIdVo();

        if (uploadfolder.exists()) {
            File[] listFiles = uploadfolder.listFiles();
            List<String> delFileName = new ArrayList<>();
            int idx = 0;

            for (LocalJobFileIdVo localJobFileIdVo : localFileList) {
                String uploadTime = localJobFileIdVo.getUploadDate().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));

                if (currentTime.compareTo(uploadTime) > 0) {
                    delLocalFileList.setId(localJobFileIdVo.getId());
                    delLocalFileList.setFileName(localJobFileIdVo.getFileName());
                    delLocalFileList.setFileOriginalName(localJobFileIdVo.getFileOriginalName());
                    delLocalFileList.setUploadDate(localJobFileIdVo.getUploadDate());

                    localJobFileIdVoRepository.delete(delLocalFileList);

                    delFileName.add(localJobFileIdVo.getFileName());
                }
            }

            if(delFileName.size() != 0) {
                for (File listFile : listFiles) {
                    if (listFile.getName().equals(delFileName.get(idx))) {
                        if (!listFile.delete()) {
                            log.info(String.format("Unable to Upload Delete File : %s", delFileName.get(idx)));
                        }

                        if (idx == delFileName.size() - 1) {
                            break;
                        } else {
                            idx++;
                        }
                    }
                }
            }
        }

        if (downloadfolder.exists()) {
            File[] listFiles = downloadfolder.listFiles();

            if(listFiles.length != 0) {
                for (File listFile : listFiles) {
                    String lastModifiedDate = dateFormat.format(new Date(listFile.lastModified()));

                    if (currentTime.compareTo(lastModifiedDate) > 0) {
                        if (!listFile.delete()) {
                            log.info(String.format("Unable to Download Delete File : %s", listFile.getName()));
                        }
                    }
                }
            }
        }
    }
}
