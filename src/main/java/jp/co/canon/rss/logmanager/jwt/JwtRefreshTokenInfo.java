package jp.co.canon.rss.logmanager.jwt;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class JwtRefreshTokenInfo {
	private String sub;
	private LocalDateTime exp;
	private LocalDateTime iat;
	private int id;
	private String username;
	private UUID uuid;
}

