package com.example.backend.config;


import com.example.backend.sys.jwt.JwtUtils;
import com.example.backend.sys.secure.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Configuration
@EnableWebFluxSecurity
public class AppConfig implements WebFluxConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:8080")
                .allowedHeaders("token")
                .exposedHeaders("token")
                .allowCredentials(true).maxAge(3600);

    }

    @Autowired
    private UserService userService;

    @Autowired
    private Environment env;

    /**
     * 读取配置文件入静态类
     * @return
     */
    @Bean
    @Lazy(false)
    public int readConf() {
        JwtUtils.setSECRECT(env.getProperty("JWT.secret"));
        JwtUtils.setEXPIRE(env.getProperty("JWT.expire",Long.class, 600000L));
        return 1;
    }


    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * spring security配置
     * @param http
     * @return
     */
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.csrf().disable()
                .authorizeExchange()
                .pathMatchers("/api/signIn").authenticated()
                .pathMatchers("/api/signUp").permitAll()
                .and()
                .addFilterAt(loginFilter(), SecurityWebFiltersOrder.HTTP_BASIC)
                .authorizeExchange()
                .pathMatchers("/api/**").authenticated()
                .and().addFilterAt(AuthFilter(),SecurityWebFiltersOrder.AUTHENTICATION);
        return http.build();
    }

    /**
     *token验证
     * @return
     */
    public WebFilter AuthFilter() {
        //创建filter，创建authenticateMananger：返回
        AuthenticationWebFilter authFilter = new AuthenticationWebFilter(Mono::just);
        authFilter.setServerAuthenticationConverter(JwtUtils::extract);
        authFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/api/**"));
        authFilter.setAuthenticationFailureHandler((webFilterExchange, exception) -> Mono.fromRunnable(()->webFilterExchange.getExchange().getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)));
        //验证成功更新jwt日期
        authFilter.setAuthenticationSuccessHandler((webFilterExchange, authentication) -> {
            ServerWebExchange exchange = webFilterExchange.getExchange();
            exchange.getResponse()
                    .getHeaders()
                    .add(HttpHeaders.AUTHORIZATION, JwtUtils.generateJWT(authentication.getName(),authentication.getAuthorities()));
            return webFilterExchange.getChain().filter(exchange);
        });
        return authFilter;
    }


    /**
     * 登录验证，生成token
     *
     * @return
     */
    public WebFilter loginFilter() {
        //创建filter，创建authenticateMananger：用于验证结果
        UserDetailsRepositoryReactiveAuthenticationManager manager = new UserDetailsRepositoryReactiveAuthenticationManager(userService);
        manager.setPasswordEncoder(bCryptPasswordEncoder());
        AuthenticationWebFilter loginFilter =
                new AuthenticationWebFilter(manager);


        loginFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/api/signIn"));

        //设置authenticate对象转换器，将请求中的内容转换成token对象
        loginFilter.setServerAuthenticationConverter(exchange -> exchange.getRequest().getBody().map(dataBuffer -> {
            try {
                com.example.backend.sys.secure.User user = new ObjectMapper().readValue(dataBuffer.asInputStream(), com.example.backend.sys.secure.User.class);

                return (Authentication) new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }).last());

        //登录成功的处理
        loginFilter.setAuthenticationSuccessHandler((webFilterExchange, authentication) -> {
            ServerWebExchange exchange = webFilterExchange.getExchange();
            exchange.getResponse()
                    .getHeaders()
                    .add(HttpHeaders.AUTHORIZATION, JwtUtils.generateJWT(authentication.getName(),authentication.getAuthorities()));

            return webFilterExchange.getChain().filter(exchange);
        });

        loginFilter.setAuthenticationFailureHandler((webFilterExchange, exception) -> Mono.fromRunnable(()->webFilterExchange.getExchange().getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)));
        return loginFilter;
    }




}