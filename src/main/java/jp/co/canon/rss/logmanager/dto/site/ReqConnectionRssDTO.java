package jp.co.canon.rss.logmanager.dto.site;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqConnectionRssDTO {
    String crasAddress;
    int crasPort;
    String rssAddress;
    int rssPort;
    String rssUserName;
    String rssPassword;
}
