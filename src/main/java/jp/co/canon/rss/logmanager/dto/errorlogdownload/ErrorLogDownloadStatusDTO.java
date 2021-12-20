package jp.co.canon.rss.logmanager.dto.errorlogdownload;

import jp.co.canon.rss.logmanager.vo.SiteVo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class ErrorLogDownloadStatusDTO {
    private Integer id;
    private int siteId;
    private String rid;

    private String occurred_date;
    private String equipment_name;
    private String download_url;
    private String download_id;
    private String error_code;
    private String type;
    private String command;
    private String start;
    private String end;
    private String status;
    private String error;
    private SiteVo siteVoList;
}
