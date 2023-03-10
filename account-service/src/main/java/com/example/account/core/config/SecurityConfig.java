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
 
        // ????????? ??????
        http.authorizeRequests()
            // USER, ADMIN?????? ?????? ?????? ????????? ??????
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
            // ????????? ????????? ??? ?????? url, handler ????????? ????????? ??? ???????????? id, password ???????????? ??????
            .formLogin()
            //.authenticationDetailsSource(captchaWebAuthenticationDetailsSource)
            //.loginPage("/login")
            // .defaultSuccessUrl("/")
            .successHandler(authSuccessHandler)
            .failureHandler(authFailureHandler)
        .and()
            // ???????????? ?????? ??????
            .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
            .logoutSuccessUrl("/")
            .invalidateHttpSession(true)
        .and()
            // csrf ???????????? ??????
            // csrf ????????? ???????????? ?????? request??? csrf ?????? ?????? ??????????????????.
            .csrf().ignoringAntMatchers("/api/v1/**")
        .and()
            // ????????? ??????????????? ????????? provider
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
                .clientSecret("test") //???????????? ????????? null?????? ????????? ???????????? ???????????? ??????
                .jwkSetUri("test") //???????????? ????????? null?????? ????????? ???????????? ???????????? ??????
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