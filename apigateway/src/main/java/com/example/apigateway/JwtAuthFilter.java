package com.example.apigateway;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthFilter implements GlobalFilter {
    @Value("${jwt.secret}")
    private String secret;
    private Key key;

    // token 검증없이 통과하는 endpoint 등록
    private static final List<String> WHITE_LIST_PATHS = List.of(
        "/users/signIn",
        "/health/alive",
        "/product/list"
    );


    @PostConstruct
    private void init(){
        System.out.println(">>>> JwtAuthenticationFilter init secret : " + secret);
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }   
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain){
        System.out.println(">>>> JwtAuthenticationFilter token valiation : " + chain);
        
        String bearerToken = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        System.out.println(">>>> JwtAuthenticationFilter filter bearerToken : " + bearerToken);
        
        String endPoint = exchange.getRequest().getURI().getRawPath();
        System.out.println(">>>> JwtAuthenticationFilter filter endPoint : " + endPoint);

        String method = exchange.getRequest().getMethod().name();
        System.out.println(">>>> JwtAuthenticationFilter filter method : " + method);
        
        if(WHITE_LIST_PATHS.contains(endPoint)){
            System.out.println(">>>> JwtAuthenticationFilter filter WHITE_LIST_PATHS PASS : ");
            return chain.filter(exchange);
        }

        try{
            System.out.println(">>>> JwtAuthenticationFilter bearerToken : " + bearerToken);
            if(bearerToken == null || !bearerToken.startsWith("Bearer ")){
                System.out.println(">>>> JwtAuthenticationFilter Not bearerToken : ");
                
                throw new RuntimeException("JwtAuthenticationFilter token exception");
            }

            String token = bearerToken.substring(7);
            System.out.println("token" + token);

            Claims claims = Jwts.parserBuilder()
                                .setSigningKey(key)
                                .build()
                                .parseClaimsJws(token)
                                .getBody();
            String email = claims.getSubject(); //기본키인 email에 subject를 담음(subject는 provider에서 사용자의 기본키 계정을 담고 있다)
            System.out.println(">>>> JwtAuthenticationFilter claims get email : " + email);
            
            // JWTProvider에 role이 입력된 경우에만 해당
            String role = claims.get("role", String.class);
            System.out.println(">>>> JwtAuthenticationFilter claims get role : " + role);

            // X-User-Id 변수로 email값과 Role추가
            // X custom header라는 것을 의미하는 관례
            ServerWebExchange modifyExchange = exchange.mutate()
                                                        .request(builder -> builder.header("X-User-Email", email)
                                                                                    .header("X-User-Role", role))
                                                        .build();

            return chain.filter(modifyExchange);
        }catch(Exception e){
            e.printStackTrace();
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
}
