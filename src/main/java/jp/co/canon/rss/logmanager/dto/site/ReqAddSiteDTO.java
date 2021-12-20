package jp.co.canon.rss.logmanager.dto.site;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Schema
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class ReqAddSiteDTO {
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
}
