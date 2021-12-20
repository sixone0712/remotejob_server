package jp.co.canon.rss.logmanager.dto.errorlogdownload;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ResSettingListDTO {
    public String error_code_range;
    public String type;
    public String command;
    public String before;
    public String after;
}
