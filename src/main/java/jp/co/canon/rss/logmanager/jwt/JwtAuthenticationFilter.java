package jp.co.canon.rss.logmanager.jwt;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

		try {
			if (!request.getServletPath().contains("/api/v1/auth/login")) {
				String token = jwtTokenProvider.resolveJwtToken(request).getAccessToken();
				if (token != null && jwtTokenProvider.isTokenValid(token)) {
					Authentication authentication = jwtTokenProvider.getAuthentication(token);
					SecurityContextHolder.getContext().setAuthentication(authentication);

				}
			}
			filterChain.doFilter(request, response);
		} catch (UsernameNotFoundException e) {
			log.error(e.getMessage());
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String jsonString = gson.toJson(
				new jwtExceptionResponse()
					.setTimestamp(LocalDateTime.now().toString())
					.setStatus(HttpStatus.UNAUTHORIZED.value())
					.setError(HttpStatus.UNAUTHORIZED.getReasonPhrase())
					.setMessage("invalid username in access token")
					.setPath(request.getServletPath())
			);

			response.setContentType("application/json");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().write(jsonString);
		} catch (Exception e) {
			log.error(e.getMessage());
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String jsonString = gson.toJson(
				new jwtExceptionResponse()
					.setTimestamp(LocalDateTime.now().toString())
					.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
					.setError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
					.setPath(request.getServletPath())
			);

			response.setContentType("application/json");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().write(jsonString);
		}
	}
}