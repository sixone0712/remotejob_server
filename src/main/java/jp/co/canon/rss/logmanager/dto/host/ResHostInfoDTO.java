package jp.co.canon.rss.logmanager.dto.host;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Schema
@Accessors(chain = true)
public class ResHostInfoDTO {
	public String address;
	public Integer port;
	public String user;
	public String password;
}
