package jp.co.canon.rss.logmanager.util;

import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;

@Component
public class FileManageUtils {
    public void fileDownload(HttpServletResponse response, File resultZip, String zipFileName) throws IOException {
        response.setContentType("application/download");
        response.setHeader("Content-Disposition",
                String.format("attachment; filename=\"%s\"", URLEncoder.encode(zipFileName,"UTF-8")));

        ServletOutputStream sos = null;
        sos = response.getOutputStream();
        FileInputStream fio = null;
        byte[] buf = new byte[1024];

        fio = new FileInputStream(resultZip.getPath());
        int n = 0;

        while((n=fio.read(buf, 0, buf.length))!=-1) {
            sos.write(buf, 0, n);
            sos.flush();
        }
        sos.close();
    }

    public boolean deleteResultLogFolder(File rootFile) {
        File[] allFiles = rootFile.listFiles();
        if (allFiles != null) {
            for (File file : allFiles) {
                deleteResultLogFolder(file);
            }
        }
        return rootFile.delete();
    }
}
