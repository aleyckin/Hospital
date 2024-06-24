package com.example.hospital;

import com.example.hospital.Hospital.controllers.UserController;
import com.example.hospital.Hospital.models.enums.UserRole;
import com.example.hospital.Hospital.services.UserService;
import com.example.hospital.JWT.JwtFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.oauth2.jwt.Jwt;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
        securedEnabled = true
)
public class SecurityConfiguration {
    private final Logger log = LoggerFactory.getLogger(SecurityConfiguration.class);
    private static final String LOGIN_URL = "/login";
    public static final String SPA_URL_MASK = "/{path:[^\\.]*}";

    private final UserService userService;
    private final JwtFilter jwtFilter;

    public SecurityConfiguration(UserService userService)
    {
        this.userService = userService;
        this.jwtFilter = new JwtFilter(userService);
        createAdminOnStartup();
    }

    private void createAdminOnStartup() {
        final String admin = "admin";
        if (userService.findByLogin(admin) == null) {
            log.info("Admin user successfully created");
            userService.addUser(admin, admin, admin, UserRole.ADMIN, admin, admin);
        }
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(c -> c
                        .requestMatchers("/", SPA_URL_MASK).permitAll()
                        .requestMatchers(HttpMethod.POST, UserController.URL_LOGIN).permitAll()
                        .requestMatchers(HttpMethod.POST, UserController.URL_SIGN_UP).permitAll()
                        .requestMatchers(HttpMethod.POST, UserController.URL_WHO_AM_I).permitAll()
                        .requestMatchers("/email/**").permitAll()
                        .anyRequest()
                        .authenticated())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(HttpMethod.OPTIONS, "/**")
                .requestMatchers("/*.js")
                .requestMatchers("/*.html")
                .requestMatchers("/*.css")
                .requestMatchers("/assets/**")
                .requestMatchers("/favicon.ico")
                .requestMatchers("/.js", "/.css")
                .requestMatchers("/swagger-ui/index.html")
                .requestMatchers("/webjars/**")
                .requestMatchers("/swagger-resources/**")
                .requestMatchers("/v3/api-docs/**")
                .requestMatchers("/h2-console/**");
    }

    @Bean
    public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedPercent(true);
        firewall.setAllowUrlEncodedSlash(true);
        firewall.setAllowUrlEncodedPeriod(true);
        firewall.setAllowSemicolon(true);
        firewall.setAllowBackSlash(true);
        firewall.setAllowUrlEncodedDoubleSlash(true);
        return firewall;
    }

    public void configure(WebSecurity web) throws Exception {
        web.httpFirewall(allowUrlEncodedSlashHttpFirewall());
    }

}
