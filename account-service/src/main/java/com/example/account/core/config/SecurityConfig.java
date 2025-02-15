package com.example.account.core.config;

import java.util.Arrays;
import java.util.Collections;

import com.example.account.core.security.*;
import com.example.account.core.service.CustomUserDetailsService;

import static org.springframework.web.cors.CorsConfiguration.ALL;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Autowired
    CustomUserDetailsService service;

    @Autowired
    JwtTokenProvider jwtTokenProvider;
 
    @Autowired
    private AuthSuccessHandler authSuccessHandler;
    
    @Autowired
    CustomAccessDeniedHandler customAccessDeniedHandler;

    static final String[] resourceArray = {"/webjars/**",  "/wro4j/**", "/css/**", "/image/**", "/js/**"}; 

    @Override
    protected void configure(HttpSecurity http) throws Exception {
 
        // 로그인 설정
        http.authorizeRequests()
            .antMatchers(resourceArray).permitAll()
            .antMatchers("/**/csrf/").permitAll()
            .antMatchers("/**/sso/refresh/").permitAll()
            .anyRequest().authenticated()
        .and()
            // 로그인 페이지 및 성공 url, handler 그리고 로그인 시 사용되는 id, password 파라미터 정의
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
            .formLogin()
            //.loginPage("/login")
            // .defaultSuccessUrl("/")
            .successHandler(authSuccessHandler)
            // .failureHandler(authFailureHandler)
        .and()
            // 로그아웃 관련 설정
            .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
            .logoutSuccessUrl("/")
            .invalidateHttpSession(true)
        .and()
            // csrf 사용유무 설정
            // csrf 설정을 사용하면 모든 request에 csrf 값을 함께 전달해야한다.
            .csrf().disable()
            // 로그인 프로세스가 진행될 provider
            .authenticationProvider(authenticationProvider())
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
        config.setAllowedOrigins(Arrays.asList("http://localhost:19006", "http://localhost:9000", "https://blacktokki.github.io"));
        config.setMaxAge(1800L);
        config.setExposedHeaders(Arrays.asList("Authorization"));
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new AuthProvider(service, passwordEncoder());
    }
}