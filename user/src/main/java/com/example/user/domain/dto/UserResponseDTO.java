package com.example.user.domain.dto;

import com.example.user.domain.entity.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private String email, name, role;

    public static UserResponseDTO fromEntity(UserEntity userEntity){
        return UserResponseDTO.builder()
                                .email(userEntity.getEmail())
                                .name(userEntity.getName())
                                .role(userEntity.getRole())
                                .build();
    }
}
