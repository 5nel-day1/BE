package com.sku.backend.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "학생 로그인 요청")
public class StudentLoginRequestDto {

    @NotBlank(message = "학번을 입력해주세요.")
    @Schema(description = "학번", example = "20201234")
    private String studentId;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Schema(description = "비밀번호", example = "password123")
    private String password;
}