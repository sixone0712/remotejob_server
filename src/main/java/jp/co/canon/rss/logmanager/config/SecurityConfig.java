package jp.co.canon.rss.logmanager.config;


import jp.co.canon.rss.logmanager.filter.CustomUriLogFilter;
import jp.co.canon.rss.logmanager.jwt.JwtAccessDeniedHandler;
import jp.co.canon.rss.logmanager.jwt.JwtAuthenticationEntryPoint;
import jp.co.canon.rss.logmanager.jwt.JwtAuthenticationFilter;
import jp.co.canon.rss.logmanager.jwt.JwtTokenProvider;
import jp.co.canon.rss.logmanager.util.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


/*
   Security Filter Chain
	 1. ChannelProcessingFilter
	 2. SecurityContextPersistenceFilter		( auto-config default )
	 3. ConcurrentSessionFilter
	 4. LogoutFilter							( auto-config default )
	 5. UsernamePasswordAuthenticationFilter	( auto-config default )
	 6. DefaultLoginPageGeneratingFilter		( auto-config default )
	 7. CasAuthenticationFilter
	 8. BasicAuthenticationFilter			  	( auto-config default )
	 9. RequestCacheAwareFilter					( auto-config default )
	10. SecurityContextHolderAwareRequestFilter	( auto-config default )
	11. JaasApiIntegrationFilter
	12. RememberMeAuthenticationFilter
	13. AnonymousAuthenticationFilter			( auto-config default )
	14. SessionManagementFilter					( auto-config default )
	15. ExceptionTranslationFilter				( auto-config default )
	16. FilterSecurityInterceptor				( auto-config default )
*/

// TODO : 스프링 시큐리트 비활성화함.
@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final JwtTokenProvider jwtTokenProvider;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		// [[Springboot] addCorsMappings 관련 에러] https://code-delivery.me/posts/2021-03-16/
		// configuration.addAllowedOrigin("*");
		configuration.addAllowedOriginPattern("*");
		configuration.addAllowedMethod("*");
		configuration.addAllowedHeader("*");
		configuration.setAllowCredentials(true);
		//configuration.setMaxAge(3600L);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}



	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
			.requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
			.and()
			.cors()
			.and()
			.csrf().disable() // csrf 보안 토큰 disable처리.
			.httpBasic().disable() // rest api 만을 고려하여 기본 설정은 해제하겠습니다.
			.formLogin().disable()    // Form 기반 인증 비활성화
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 토큰 기반 인증이므로 세션 안함.
			.and()
			.headers().frameOptions().sameOrigin()

			.and()
			// custom filter
			.addFilterBefore(new CustomUriLogFilter(), WebAsyncManagerIntegrationFilter.class)
			// exception handling Custom Class
			.exceptionHandling()
			.authenticationEntryPoint(jwtAuthenticationEntryPoint)
			.accessDeniedHandler(jwtAccessDeniedHandler)

			.and()
			.authorizeRequests()
			.regexMatchers("^((?!\\/api).)*$").permitAll()
			.antMatchers("/api/v1/auth/login", "/api/v1/auth/reissue", "/api/v1/auth/logout").permitAll()
			.antMatchers("/api/v1/status/**").authenticated()
			.antMatchers(HttpMethod.GET, "/api/v1/version").permitAll()
			.antMatchers("/api/v1/upload/**").authenticated()	// 추후 확인 후 authenticated()로 변경
			.antMatchers("/api/v1/draft/**").permitAll()
			.antMatchers("/api/v1/analysis/**").permitAll()
			.antMatchers("/api/v1/backup/**").permitAll()	// 추후 확인 후 authenticated()로 변경
			.antMatchers("/api/v1/history/**").authenticated()
			.and()

			.authorizeRequests()
			.antMatchers("/api/v1/job/**").hasRole(UserRole.JOB.name())
			.antMatchers("/api/v1/site/**", "/api/v1/host/**").hasRole(UserRole.CONFIGURE.name())
			.antMatchers("/api/v1/user/**").hasRole(UserRole.ACCOUNT.name())
			.antMatchers("/api/v1/rule/**").hasRole(UserRole.RULES.name())
			.antMatchers("/api/v1/errorlog/**").hasRole(UserRole.JOB.name())

			.and()
			.authorizeRequests()
			.anyRequest().authenticated()

			.and()
			.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
				UsernamePasswordAuthenticationFilter.class);
	}
}
