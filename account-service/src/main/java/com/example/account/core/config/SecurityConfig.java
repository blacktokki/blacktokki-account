package com.example.account.core.config;

import java.util.Arrays;
import java.util.Collections;

import com.example.account.core.security.*;

// import java.util.List;
// import java.util.Objects;
// import java.util.stream.Collectors;

import static org.springframework.web.cors.CorsConfiguration.ALL;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
// import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
// import org.springframework.security.oauth2.client.registration.ClientRegistration;
// import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
// import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Autowired
    AuthProvider authProvider;

    @Autowired
    JwtTokenProvider jwtTokenProvider;
    
    // @Autowired
    // private CaptchaAuthenticationDetailsSource captchaWebAuthenticationDetailsSource;
    
    @Autowired
    private AuthFailureHandler authFailureHandler;
 
    @Autowired
    private AuthSuccessHandler authSuccessHandler;
    
    // @Autowired
    // OAuth2SuccessHandler oAuth2SuccessHandler;
    
    @Autowired
    CustomAccessDeniedHandler customAccessDeniedHandler;

    static final String[] resourceArray = {"/webjars/**",  "/wro4j/**", "/css/**", "/image/**", "/js/**"}; 

    @Override
    protected void configure(HttpSecurity http) throws Exception {
 
        // 로그인 설정
        http.authorizeRequests()
            // USER, ADMIN으로 권한 분리 유알엘 정의
            // .antMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
            // .antMatchers("/user/password/change/**").not().hasAuthority("ROLE_SOCIAL")
            // .antMatchers(HttpMethod.POST, "/resume/content/**").hasAuthority("ROLE_USER")
            // .antMatchers(HttpMethod.PUT, "/resume/content/**").hasAuthority("ROLE_USER")
            // .antMatchers("").hasAuthority("ROLE_USER")
            // .antMatchers("/user/join/social/**").authenticated()
            .antMatchers(resourceArray).permitAll()
            .antMatchers("/**/csrf/").permitAll()
            .antMatchers("/**/sso/refresh/").permitAll()
            .anyRequest().authenticated()
            //.antMatchers("/**").permitAll()
        // .and()
        //     .oauth2Login()
        //     .loginPage("/user/login")
        //     .defaultSuccessUrl("/")
        //     .successHandler(oAuth2SuccessHandler)
        //     .authorizationEndpoint()
        //     .baseUri("/user/oauth2/authorization")
        //     .and()
        .and()
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
            // 로그인 페이지 및 성공 url, handler 그리고 로그인 시 사용되는 id, password 파라미터 정의
            .formLogin()
            //.authenticationDetailsSource(captchaWebAuthenticationDetailsSource)
            //.loginPage("/login")
            // .defaultSuccessUrl("/")
            .successHandler(authSuccessHandler)
            .failureHandler(authFailureHandler)
        .and()
            // 로그아웃 관련 설정
            .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
            .logoutSuccessUrl("/")
            .invalidateHttpSession(true)
        .and()
            // csrf 사용유무 설정
            // csrf 설정을 사용하면 모든 request에 csrf 값을 함께 전달해야한다.
            .csrf().ignoringAntMatchers("/api/v1/**")
        .and()
            // 로그인 프로세스가 진행될 provider
            .authenticationProvider(authProvider)
            .exceptionHandling().accessDeniedHandler(customAccessDeniedHandler)
        .and()
            .cors(withDefaults());
    }
    
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedMethods(Collections.singletonList(ALL));
        config.setAllowedHeaders(Collections.singletonList(ALL));
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList("http://localhost:19006", "http://localhost:9000"));
        config.setMaxAge(1800L);
        source.registerCorsConfiguration("/**", config);
        return source;
    }
    /*
    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(OAuth2ClientProperties oAuth2ClientProperties, @Value("${custom.oauth2.kakao.client-id}") String kakaoClientId) {
        List<ClientRegistration> registrations = oAuth2ClientProperties.getRegistration().keySet().stream()
                .map(client -> getRegistration(oAuth2ClientProperties, client))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        /*
        registrations.add(CustomOAuth2Provider.KAKAO.getBuilder("kakao")
                .clientId(kakaoClientId)
                .clientSecret("test") //필요없는 값인데 null이면 실행이 안되도록 설정되어 있음
                .jwkSetUri("test") //필요없는 값인데 null이면 실행이 안되도록 설정되어 있음
                .build());
        *//*
        return new InMemoryClientRegistrationRepository(registrations);
    }
    
    private ClientRegistration getRegistration(OAuth2ClientProperties clientProperties, String client) {
        if ("google".equals(client)) {
            OAuth2ClientProperties.Registration registration = clientProperties.getRegistration().get("google");
            return CommonOAuth2Provider.GOOGLE.getBuilder(client)
                    .clientId(registration.getClientId())
                    .clientSecret(registration.getClientSecret())
                    .scope("email", "profile")
                    .build();
        }

        if ("facebook".equals(client)) {
            OAuth2ClientProperties.Registration registration = clientProperties.getRegistration().get("facebook");
            return CommonOAuth2Provider.FACEBOOK.getBuilder(client)
                    .clientId(registration.getClientId())
                    .clientSecret(registration.getClientSecret())
                    .userInfoUri("https://graph.facebook.com/me?fields=id,name,email,link")
                    .scope("email")
                    .build();
        }
        return null;
    }
    */
}