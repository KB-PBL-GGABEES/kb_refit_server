package org.refit.spring.security;

import org.refit.spring.mapper.UserMapper;
import org.refit.spring.security.filter.JwtAuthenticationFilter;
import org.refit.spring.security.filter.JwtLoginFilter;
import org.refit.spring.security.jwt.JwtTokenProvider;
import org.refit.spring.security.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private CustomUserDetailsService userDetailsService;
    @Autowired
    private UserMapper userMapper;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        JwtLoginFilter jwtLoginFilter = new JwtLoginFilter(authenticationManagerBean(), jwtTokenProvider, userMapper);
        jwtLoginFilter.setFilterProcessesUrl("/api/auth/login"); // 경로 설정

        http
                .httpBasic().disable()
                .csrf().disable()
                .cors()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) //JWT 기반
                .and()
                .authorizeRequests()
                    .antMatchers("/api/auth/login",
                            "/api/auth/refresh",
                            "/swagger-ui.html",
                            "/swagger-ui/**",
                            "/swagger-resources/**",
                            "/v2/api-docs",
                            "/api/test",
                            "/webjars/**").permitAll()
                    .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .anyRequest().authenticated()
                .and()
                .addFilter(jwtLoginFilter)
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //추후에 CustomUserDetailService 연결
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.addAllowedOriginPattern("*"); // 모든 origin 허용
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*"); // GET, POST, PUT, DELETE 등 모두 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
