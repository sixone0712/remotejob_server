package jp.co.canon.rss.logmanager.dto.auth;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ResTokenServiceDTO extends ResLoginDTO {
	String accessToken;
	String refreshToken;
}
