package com.sku.backend.domain.auth.dto;

import com.sku.backend.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "인증 성공 응답")
public class AuthResponseDto {

    @Schema(description = "사용자 ID", example = "1")
    private Long id;

    @Schema(description = "이름", example = "홍길동")
    private String name;

    @Schema(description = "권한", example = "STUDENT")
    private String role;

    @Schema(description = "Access Token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    public static AuthResponseDto from(User user, String accessToken) {
        return AuthResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .role(user.getRole().name())
                .accessToken(accessToken)
                .build();
    }
}