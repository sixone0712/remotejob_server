package jp.co.canon.rss.logmanager.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class CustomUriLogFilter extends OncePerRequestFilter {

	@Override
	public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

		log.info(getCurrentUriFromRequest(request));
		filterChain.doFilter(request, response);
	}

	public static String getCurrentUriFromRequest(HttpServletRequest request) {

		String requestURI = request.getRequestURI();
		String method = request.getMethod();
		String queryString = request.getQueryString();

		if (queryString == null)
			return "[" + method + "] " + requestURI;

		return "[" + method + "] " + requestURI + '?' + queryString;
	}
}
