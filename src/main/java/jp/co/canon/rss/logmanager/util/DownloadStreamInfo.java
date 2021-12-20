package jp.co.canon.rss.logmanager.util;

import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;

public class DownloadStreamInfo {
    @Getter
    @Setter
    long contentLength = 0;

    @Getter @Setter
    InputStream inputStream = null;

    @Getter @Setter
    int errorCode = 0;
}
