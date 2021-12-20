package jp.co.canon.rss.logmanager.manager;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReqConvertRunConfig {
    private String host;
    private int port;
    private String user;
    private String password;
}
