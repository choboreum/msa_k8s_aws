package com.example.user.ctrl;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.user.domain.dto.UserRequestDTO;
import com.example.user.domain.dto.UserResponseDTO;
import com.example.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;

    @PostMapping("/signIn") 
    public ResponseEntity<?> singIn(@RequestBody UserRequestDTO userRequestDTO){ //json을 dto로 변환
        System.out.println(">>>> UserController signIn");
        Map<String, Object> map = userService.singIn(userRequestDTO);

        System.out.println(">>>> BlogController login() \n    body : " + (UserResponseDTO)(map.get("response")));
        System.out.println(">>>> BlogController login() \n    at : " + (String)(map.get("access")));
        System.out.println(">>>> BlogController login() \n    rt : " + (String)(map.get("refresh")));
        
        HttpHeaders headers = new HttpHeaders();
        
        headers.add("Authorization" , "Bearer " + (String)(map.get("access")) );
        headers.add("Refresh-Token", (String)(map.get("access")) );
        headers.add("Access-Control-Expose-Headers", "Authorization, Refresh-Token");
        
        return ResponseEntity.status(HttpStatus.OK)
                            .headers(headers)
                            .body((String)(map.get("access")));
    }
}
