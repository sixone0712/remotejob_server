package jp.co.canon.rss.logmanager.service;

import jp.co.canon.rss.logmanager.dto.auth.ResTokenServiceDTO;
import jp.co.canon.rss.logmanager.jwt.JwtRefreshTokenInfo;
import jp.co.canon.rss.logmanager.jwt.JwtTokenProvider;
import jp.co.canon.rss.logmanager.jwt.JwtTokens;
import jp.co.canon.rss.logmanager.repository.BlockedTokenRepository;
import jp.co.canon.rss.logmanager.repository.UserRepository;
import jp.co.canon.rss.logmanager.util.ErrorMessage;
import jp.co.canon.rss.logmanager.vo.BlockedTokenVo;
import jp.co.canon.rss.logmanager.vo.UserVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.Cookie;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service()
public class AuthService {
	UserRepository userRepository;
	BlockedTokenRepository blockedTokenRepository;
	JwtTokenProvider jwtTokenProvider;

	public AuthService(UserRepository userRepository, BlockedTokenRepository blockedTokenRepository, JwtTokenProvider jwtTokenProvider) {
		this.userRepository = userRepository;
		this.blockedTokenRepository = blockedTokenRepository;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	public ResTokenServiceDTO logIn(String username, String password) throws Exception {

		try {
			UserVo user = userRepository.findByUsername(username);
			if (user == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessage.INVALID_USERNAME.getMsg());
			if (!user.getPassword().equals(password)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessage.INVALID_PASSWORD.getMsg());

			String userRefreshToken = user.getRefreshToken();
			String newAccessToken = jwtTokenProvider.createJwtAccessToken(user.getId(), user.getUsername(), user.getRoles());
			String newRefreshToken = jwtTokenProvider.reissueRefreshToken(user, userRefreshToken);
			user.setAccessAt(LocalDateTime.now());

			if (ObjectUtils.isEmpty(userRefreshToken)) {
				// refresh token is empty from user repository
				log.info("[loginUser] issue new refresh token");
				userRepository.save(user.setRefreshToken(newRefreshToken));
			} else if (!userRefreshToken.equals(newRefreshToken)) {
				// reissue refresh token
				log.info("[loginUser] reissue new refresh token for expiration");
				userRepository.save(user.setRefreshToken(newRefreshToken));

				// add old refresh token to blocked token db if the expiration date is left
				addBlockedToken(userRefreshToken);
			}
			return (ResTokenServiceDTO) new ResTokenServiceDTO()
				.setAccessToken(newAccessToken)
				.setRefreshToken(newRefreshToken)
				.setId(user.getId())
				.setUsername(user.getUsername())
				.setRoles(user.getRoles());
		} catch (ResponseStatusException e) {
			log.error(e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public JwtTokens reissueTokens(String userRefreshToken) throws Exception {

		try {
			if (!jwtTokenProvider.isTokenValid(userRefreshToken)) {
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ErrorMessage.INVALID_REFRESH_TOKEN.getMsg());
			}

			JwtRefreshTokenInfo refreshTokenInfo = jwtTokenProvider.decodeRefreshToken(userRefreshToken);

			// Check Refresh Token Black List
			BlockedTokenVo blockedToken = blockedTokenRepository.findByUuid(refreshTokenInfo.getUuid());
			if (blockedToken != null) {
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ErrorMessage.BLOCKED_TOKEN.getMsg());
			}

			Optional<UserVo> optionalUser = userRepository.findById(refreshTokenInfo.getId());
			if (!optionalUser.isPresent()) {
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ErrorMessage.INVALID_USER.getMsg());
			}

			UserVo user = optionalUser.get();
			if (!userRefreshToken.equals(user.getRefreshToken())) {
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ErrorMessage.INVALID_REFRESH_TOKEN.getMsg());
			}

			String newAccessToken = jwtTokenProvider.createJwtAccessToken(user.getId(), user.getUsername(), user.getRoles());
			String newRefreshToken = jwtTokenProvider.reissueRefreshToken(user, userRefreshToken);

			if (!userRefreshToken.equals(newRefreshToken)) {
				// reissue refresh token
				log.info("[reissueTokens] reissue new refresh token for expiration");
				userRepository.save(user.setRefreshToken(newRefreshToken));

				// add old refresh token to blocked token db if the expiration date is left
				addBlockedToken(userRefreshToken);
			}

			return new JwtTokens().setAccessToken(newAccessToken).setRefreshToken(newRefreshToken);
		} catch (ResponseStatusException e) {
			log.error(e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public Cookie createCookie(String key, String value) {

		Cookie cookie = new Cookie(key, value);
		//cookie.setHttpOnly(true);
		cookie.setPath("/");
		return cookie;
	}

	public Cookie deleteCookie(String key) {

		Cookie cookie = new Cookie(key, null);
		cookie.setMaxAge(0);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		cookie.setDomain("localhost.org");
		return cookie;
	}

	public void addBlockedToken(String token) throws Exception {

		try {
			LocalDateTime expiration = jwtTokenProvider.getExpired(token);

			if (!jwtTokenProvider.isExpired(expiration)) {
				UUID uuid = jwtTokenProvider.getUuid(token);
				blockedTokenRepository.save(
					new BlockedTokenVo()
						.setToken(token)
						.setExpired(expiration)
						.setUuid(uuid)
				);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			if (!e.getCause().toString().contains("ConstraintViolationException")) {
				throw e;
			}
		}
	}
}
