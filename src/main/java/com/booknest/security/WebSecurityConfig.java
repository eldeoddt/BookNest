package com.booknest.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private OAuthUserServiceImpl oAuthUserService;

    @Autowired
    private OAuthSuccessHandler oAuthSuccessHandler;

    @Autowired
    private RedirectUrlCookieFilter redirectUrlFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .sessionManagement(
                    session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(
                        auth -> auth.requestMatchers("/", "/auth/**", "/oauth2/**",
                                        "/v3/api-docs/**",
                                        "/swagger-ui/**",
                                        "/swagger-ui.html",
                                        "/swagger-resources/**").permitAll()
            .anyRequest().authenticated())
            //.oauth2Login(Customizer.withDefaults())
            //    /*
                .oauth2Login(oauth2 -> oauth2
                        // 서버 인증(로그인) 후 리다이렉트할 URL을 지정함
                       .redirectionEndpoint(redir -> redir
                                .baseUri("/oauth2/callback/*")
                        )
                        // 7.3절에서: 소셜 로그인 흐름의 시작점을 변경함
                        // 기본 엔드포인트인 "http://localhost:8080/oauth2/authorization/github" 대신에
                        // 새로운 엔드포인트인 "http://localhost:8080/auth/authorize/github"으로 변경함
                        .authorizationEndpoint(auth -> auth
                                .baseUri("/auth/authorize")
                        )
                        // 서버 인증(로그인) 후 사용자 정보를 처리하는 객체를 지정함함
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuthUserService)
                        )
                        // 로그인 성공 후 처리할 핸들러를 지정함
                        .successHandler(oAuthSuccessHandler)
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new Http403ForbiddenEntryPoint()))

            //     */
                ;

   
            

        http.addFilterAfter(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.addFilterAfter(
                redirectUrlFilter,
                OAuth2AuthorizationRequestRedirectFilter.class
        );

        return http.build();
    }
}

















