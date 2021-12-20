package jp.co.canon.rss.logmanager.dto.site;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResSitesDetailDTO {
    private Integer siteId;
    private String crasCompanyName;
    private String crasFabName;
    private String crasAddress;
    private int crasPort;
    private String rssAddress;
    private int rssPort;
    private String rssUserName;
    private String rssPassword;
    private String emailAddress;
    private int emailPort;
    private String emailUserName;
    private String emailPassword;
    private String emailFrom;

    public ResSitesDetailDTO(Integer siteId, String crasCompanyName, String crasFabName, String crasAddress, int crasPort,
                             String rssAddress, int rssPort, String rssUserName, String rssPassword,
                             String emailAddress, int emailPort, String emailUserName, String emailPassword, String emailFrom) {
        this.siteId = siteId;
        this.crasCompanyName = crasCompanyName;
        this.crasFabName = crasFabName;
        this.crasAddress = crasAddress;
        this.crasPort = crasPort;
        this.rssAddress = rssAddress;
        this.rssPort = rssPort;
        this.rssUserName = rssUserName;
        this.rssPassword = rssPassword;
        this.emailAddress = emailAddress;
        this.emailPort = emailPort;
        this.emailUserName = emailUserName;
        this.emailPassword = emailPassword;
        this.emailFrom = emailFrom;
    }
}
