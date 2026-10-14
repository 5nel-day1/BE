package com.sku.backend.domain.report.dto;

import com.sku.backend.domain.report.entity.Building;
import com.sku.backend.domain.report.entity.Floor;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "제보 등록 요청")
public class ReportCreateRequestDto {

    @NotNull(message = "학생 ID는 필수입니다.")
    private Long studentId;

    @NotNull(message = "건물 선택은 필수입니다.")
    @Schema(description = "건물", example = "YUDAM")
    private Building building;

    @NotNull(message = "층수 선택은 필수입니다.")
    @Schema(description = "층수", example = "F4")
    private Floor floor;

    @NotBlank(message = "상세 공간 입력은 필수입니다.")
    @Schema(description = "상세 공간", example = "화장실 세면대")
    private String spaceDetail;

    @NotBlank(message = "내용은 필수 입력 값입니다.")
    @Schema(description = "문제 내용", example = "세면대 물이 너무 천천히 내려가요.")
    private String content;

    @Schema(description = "첨부 사진 URL", example = "https://...")
    private String photoUrl;
}