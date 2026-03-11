package com.example.user.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.user.dao.UserRepository;
import com.example.user.domain.dto.UserRequestDTO;
import com.example.user.domain.dto.UserResponseDTO;
import com.example.user.domain.entity.UserEntity;
import com.example.user.provider.JwtProvider;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    //redis
    @Qualifier("tokenRedis")
    private final RedisTemplate<String, Object> redisTemplate;
    private static final long REFRESH_TOKEN_TTL = 60 * 60 * 24 * 7; // 만료일
                                                //60초 * 60분 * 24시간 * 7일

    public Map<String, Object> singIn(UserRequestDTO userRequestDTO){
        System.out.println(">>>> userService signIn");
        Map<String, Object> map = new HashMap<>();

        System.out.println(">>>> 1. userRepository login 사용자 조회");
        /*
        // 평문버전 암호화 
        UserEntity userEntity = userRepository.findByEmailAndPassword(userRequestDTO.getEmail(), userRequestDTO.getPassword())
                                            .orElseThrow(
                                                () -> new RuntimeException(">>>> UserService login 실패!!!!")
                                            );
        */

        // 해싱버전 암호화
        UserEntity userEntity = userRepository.findById(userRequestDTO.getEmail())
                                            .orElseThrow(()-> new RuntimeException("Not Found!"));
        // 평문과 인코드 비교
        if(!passwordEncoder.matches(userRequestDTO.getPassword(), userEntity.getPassword())){
            throw new RuntimeException("Password Not Matched!");
        }



        System.out.println(">>>> 2. userRepository login 토큰 설정");
        String at = jwtProvider.createAT(userEntity.getEmail());
        String rt = jwtProvider.createRT(userEntity.getEmail());
        


        System.out.println(">>>> 3. userRepository RT 토큰 redis 저장");
        redisTemplate.opsForValue()
                    .set("RT : " + userEntity.getEmail(), rt, REFRESH_TOKEN_TTL, TimeUnit.SECONDS);

        map.put("response", UserResponseDTO.fromEntity(userEntity));
        //map.put("access", "jwt-access-token");
        map.put("access", at);
        //map.put("refresh", "jwt-refresh-token");
        map.put("refresh", rt);
        
        return map;
    }




}
