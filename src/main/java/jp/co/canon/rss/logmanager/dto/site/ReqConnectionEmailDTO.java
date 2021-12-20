package jp.co.canon.rss.logmanager.dto.site;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqConnectionEmailDTO {
    String emailAddress;
    int emailPort;
    String emailUserName;
    String emailPassword;
}
