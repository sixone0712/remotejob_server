package jp.co.canon.rss.logmanager.jwt;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class JwtTokens {
	String accessToken;
	String refreshToken;
}
