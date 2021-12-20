package jp.co.canon.rss.logmanager.dto.host;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Getter
@Setter
@Component
@ConfigurationProperties(prefix="settingsinfo")
public class ReqSettingsDBInfo {
        private String address;
        private String port;
        private String user;
        private String password;
}
