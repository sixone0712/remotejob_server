package jp.co.canon.rss.logmanager.controller;

import jp.co.canon.rss.logmanager.config.ReqURLController;
import jp.co.canon.rss.logmanager.dto.auth.ResTokenServiceDTO;
import jp.co.canon.rss.logmanager.dto.user.ResUserMeDTO;
import jp.co.canon.rss.logmanager.jwt.JwtTokenProvider;
import jp.co.canon.rss.logmanager.jwt.JwtTokens;
import jp.co.canon.rss.logmanager.mapper.LoginDtoMapper;
import jp.co.canon.rss.logmanager.service.AuthService;
import jp.co.canon.rss.logmanager.util.ErrorMessage;
import jp.co.canon.rss.logmanager.vo.UserVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static jp.co.canon.rss.logmanager.jwt.JwtTokenProvider.ACCESS_TOKEN_NAME;
import static jp.co.canon.rss.logmanager.jwt.JwtTokenProvider.REFRESH_TOKEN_NAME;

@Slf4j
@RestController
@RequestMapping(ReqURLController.API_DEFAULT_AUTH_URL)
public class AuthController {

	private AuthService authService;
	private JwtTokenProvider jwtTokenProvider;

	public AuthController(AuthService authService, JwtTokenProvider jwtTokenProvider) {
		this.authService = authService;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@GetMapping(ReqURLController.API_GET_LOGIN)
	public ResponseEntity<?> login(
		HttpServletRequest request,
		HttpServletResponse response,
		@Valid @RequestParam(name = "username", required = true) @NotNull String username,
		@Valid @RequestParam(name = "password", required = true) @NotNull String password) {

		try {
			ResTokenServiceDTO result = authService.logIn(username, password);
			response.addCookie(authService.createCookie(ACCESS_TOKEN_NAME, result.getAccessToken()));
			response.addCookie(authService.createCookie(REFRESH_TOKEN_NAME, result.getRefreshToken()));

			return ResponseEntity.status(HttpStatus.OK).body(LoginDtoMapper.INSTANCE.ToLoginResDto(result));
		} catch (ResponseStatusException e) {
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(ReqURLController.API_GET_LOGOUT)
	public ResponseEntity<?> logout(
		HttpServletRequest request, HttpServletResponse response) {

		try {
			response.addCookie(authService.deleteCookie(ACCESS_TOKEN_NAME));
			response.addCookie(authService.deleteCookie(REFRESH_TOKEN_NAME));
			return ResponseEntity.status(HttpStatus.OK).body(null);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(ReqURLController.API_POST_TOKEN)
	public ResponseEntity<?> token(HttpServletRequest request, HttpServletResponse response) {

		try {
			JwtTokens tokens = this.authService.reissueTokens(jwtTokenProvider.resolveJwtToken(request).getRefreshToken());
			response.addCookie(authService.createCookie(ACCESS_TOKEN_NAME, tokens.getAccessToken()));
			response.addCookie(authService.createCookie(REFRESH_TOKEN_NAME, tokens.getRefreshToken()));
			return ResponseEntity.status(HttpStatus.OK).body(null);
		} catch (ResponseStatusException e) {
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(ReqURLController.API_GET_ME)
	@ResponseBody
	public ResponseEntity<?> me(HttpServletRequest request, HttpServletResponse response) {

		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			UserVo user = (UserVo) authentication.getPrincipal();
			if (user == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessage.INVALID_USER.getMsg());
			}

			ResUserMeDTO me = new ResUserMeDTO()
				.setId(user.getId())
				.setUsername(user.getUsername())
				.setRoles(user.getRoles());

			return ResponseEntity.status(HttpStatus.OK).body(me);
		} catch (UsernameNotFoundException e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		} catch (ResponseStatusException e) {
			log.error(e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
