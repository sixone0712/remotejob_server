package jp.co.canon.rss.logmanager.jwt;

import io.jsonwebtoken.*;
import jp.co.canon.rss.logmanager.util.DateUtils;
import jp.co.canon.rss.logmanager.vo.UserVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
	private String secretKey;
	private TemporalUnit chronoUnit;
	public static final String ACCESS_TOKEN_NAME = "access-token";
	public static final String REFRESH_TOKEN_NAME = "refresh-token";
	public static final String USERID = "id";
	public static final String USERNAME = "username";
	public static final String ROLES = "roles";
	public static final String UUID_KEY = "uuid";
	public static final String JWT_SECOND = "second";
	public static final String JWT_MINUTE = "minute";

	@Value("${auth.jwt.salt}")
	private String SALT;
	@Value("${auth.jwt.access-token-valid-time}")
	private long ACCESS_TOKEN_VALID_TIME;
	@Value("${auth.jwt.refresh-token-valid-time}")
	private long REFRESH_TOKEN_VALID_TIME;
	@Value("${auth.jwt.auto-refresh-time}")
	private long AUTO_REFRESH_TIME;
	@Value("${auth.jwt.token-time-unit}")
	private String TOKEN_TIME_UNIT;

	private final UserDetailsService userDetailsService;

	@PostConstruct
	protected void init() {
		secretKey = Base64.getEncoder().encodeToString(SALT.getBytes());
		chronoUnit = TOKEN_TIME_UNIT.equals(JWT_SECOND) ? ChronoUnit.SECONDS : ChronoUnit.MINUTES;
	}

	public String createJwtAccessToken(Integer id, String username, List<String> roles) {

		LocalDateTime issueDate = LocalDateTime.now();
		LocalDateTime expirationDate = plusExpiredTime(issueDate, ACCESS_TOKEN_VALID_TIME);
		Claims claims = Jwts.claims().setSubject(ACCESS_TOKEN_NAME);
		claims.put(USERID, id);
		claims.put(USERNAME, username);
		claims.put(ROLES, roles);
		claims.put(UUID_KEY, UUID.randomUUID().toString());

		return Jwts.builder()
			.setClaims(claims)
			.setIssuedAt(DateUtils.toDate(issueDate))
			.setExpiration(DateUtils.toDate(expirationDate))
			.signWith(SignatureAlgorithm.HS256, secretKey)
			.compact();
	}

	public String createJwtRefreshToken(Integer id, String username) {
		LocalDateTime issueDate = LocalDateTime.now();
		LocalDateTime expirationDate = plusExpiredTime(issueDate, REFRESH_TOKEN_VALID_TIME);
		Claims claims = Jwts.claims().setSubject(REFRESH_TOKEN_NAME);
		claims.put(USERID, id);
		claims.put(USERNAME, username);
		claims.put(UUID_KEY, UUID.randomUUID().toString());

		return Jwts.builder()
			.setClaims(claims)
			.setIssuedAt(DateUtils.toDate(issueDate))
			.setExpiration(DateUtils.toDate(expirationDate))
			.signWith(SignatureAlgorithm.HS256, secretKey)
			.compact();
	}

	public Jws<Claims> getClaimsFromJwtToken(String jwtToken) throws JwtException {
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
	}

	public JwtTokens resolveJwtToken(HttpServletRequest request) {

		Cookie[] cookies = request.getCookies();
		JwtTokens tokens = new JwtTokens();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (ACCESS_TOKEN_NAME.equals(cookie.getName())) {
					tokens.setAccessToken(cookie.getValue());
				}
				if (REFRESH_TOKEN_NAME.equals(cookie.getName())) {
					tokens.setRefreshToken(cookie.getValue());
				}
				if (!ObjectUtils.isEmpty(tokens.getAccessToken()) && !ObjectUtils.isEmpty(tokens.getRefreshToken()))
					break;
			}
		}

		return tokens;
	}

	public Authentication getAuthentication(String token) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserName(token));
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}

	public Integer getUserId(String token) {
		return (Integer) getClaimsFromJwtToken(token).getBody().get(USERID);
	}

	public String getUserName(String token) {
		return (String) getClaimsFromJwtToken(token).getBody().get(USERNAME);
	}

	public List<String> getUserRoles(String token) {
		return (List<String>) getClaimsFromJwtToken(token).getBody().get(ROLES);
	}

	public UUID getUuid(String token) {
		return UUID.fromString((String) getClaimsFromJwtToken(token).getBody().get(UUID_KEY));
	}

	public JwtAccessTokenInfo decodeAccessToken(String accessToken) {

		try {
			Jws<Claims> claims = Jwts.parser()
				.setSigningKey(secretKey)
				.parseClaimsJws(accessToken);

			JwtAccessTokenInfo decodedInfo = new JwtAccessTokenInfo()
				.setSub(claims.getBody().getSubject())
				.setIat(DateUtils.toLocalDateTime(claims.getBody().getIssuedAt()))
				.setExp(DateUtils.toLocalDateTime(claims.getBody().getExpiration()))
				.setId((Integer) claims.getBody().get(USERID))
				.setUsername((String) claims.getBody().get(USERNAME))
				.setRoles((List<String>) (claims.getBody().get(ROLES)))
				.setUuid(UUID.fromString((String) claims.getBody().get(UUID_KEY)));

			if (decodedInfo.getSub().equals(ACCESS_TOKEN_NAME)) return decodedInfo;
		} catch (ExpiredJwtException e) {
			log.error(e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return null;
	}

	public JwtRefreshTokenInfo decodeRefreshToken(String refreshToken) {

		try {
			Jws<Claims> claims = Jwts.parser()
				.setSigningKey(secretKey)
				.parseClaimsJws(refreshToken);

			JwtRefreshTokenInfo decodedInfo = new JwtRefreshTokenInfo()
				.setSub(claims.getBody().getSubject())
				.setIat(DateUtils.toLocalDateTime(claims.getBody().getIssuedAt()))
				.setExp(DateUtils.toLocalDateTime(claims.getBody().getExpiration()))
				.setId((Integer) claims.getBody().get(USERID))
				.setUsername((String) claims.getBody().get(USERNAME))
				.setUuid(UUID.fromString((String) claims.getBody().get(UUID_KEY)));

			if (decodedInfo.getSub().equals(REFRESH_TOKEN_NAME)) return decodedInfo;
		} catch (ExpiredJwtException e) {
			log.error(e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return null;
	}


	public LocalDateTime getExpired(String token) {
		try {
			LocalDateTime expired = DateUtils.toLocalDateTime(getClaimsFromJwtToken(token).getBody().getExpiration());
			return expired;
		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
		}
	}

	public boolean isTokenValid(String jwtToken) {
		try {
			Jws<Claims> claims = getClaimsFromJwtToken(jwtToken);
			return !claims.getBody().getExpiration().before(new Date());
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isExpired(LocalDateTime expirationDate) {

		if (!ObjectUtils.isEmpty(expirationDate)) {
			long until = LocalDateTime.now().until(expirationDate, chronoUnit);
			if (until <= 0) {
				return true;
			} else {
				return false;
			}
		}
		return true;
	}

	public boolean isBeforeAutoRefresh(LocalDateTime expirationDate) {

		if (!ObjectUtils.isEmpty(expirationDate)) {
			long until = LocalDateTime.now().until(expirationDate, chronoUnit);
			if (until > AUTO_REFRESH_TIME) {
				return true;
			}
		}
		return false;
	}

	public String reissueRefreshToken(UserVo user, String curRefreshToken) {

		if (ObjectUtils.isEmpty(curRefreshToken) || !this.isTokenValid(curRefreshToken)) {
			return this.createJwtRefreshToken(user.getId(), user.getUsername());
		} else {
			LocalDateTime expirationDate = this.getExpired(curRefreshToken);
			if (!isBeforeAutoRefresh(expirationDate)) {
				return this.createJwtRefreshToken(user.getId(), user.getUsername());
			}
		}
		return curRefreshToken;
	}

	public LocalDateTime plusExpiredTime(LocalDateTime time, long plus) {
		return TOKEN_TIME_UNIT.equals(JWT_SECOND)
			? time.plusSeconds(plus)
			: time.plusMinutes(plus);
	}
}