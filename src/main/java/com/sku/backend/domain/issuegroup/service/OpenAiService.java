package com.sku.backend.domain.issuegroup.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sku.backend.global.exception.CustomException;
import com.sku.backend.global.exception.GlobalErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class OpenAiService {

    @Value("${app.ai.api-key}")
    private String apiKey;

    @Value("${app.ai.base-url}")
    private String baseUrl;

    @Value("${app.ai.model}")
    private String modelName;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> analyzeReportText(String buildingCode, String floorCode, String spaceDetail, String reportContent) {
        if (!StringUtils.hasText(apiKey)) {
            throw new CustomException(GlobalErrorCode.AI_NOT_CONFIGURED);
        }

        log.info("OpenAi 분석 시작");

        String trimmedBaseUrl = trimTrailingSlash(baseUrl);
        String url = trimmedBaseUrl + "/models/" + modelName + ":generateContent?key=" + apiKey;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String prompt = """
                다음 캠퍼스 불편 제보 내용과 사용자가 선택한 위치 정보를 분석하여 반드시 아래 키를 가진 JSON 형식으로만 응답해줘. 
                다른 설명 텍스트나 마크다운 블록(```json 등) 없이 순수 JSON 문자열만 반환해야 해.

                [사용자가 입력한 위치 정보]
                - 선택된 건물 코드: %s (YUDAM->유담관, MAIN->본관, MOONAE->문예관, CHEONGUN->청운관, HEEIN->혜인관, BUKGWAK->북악관, EUNJU1->은주1관, HANLIM->한림관, SUIN->수인관, ETC->기타)
                - 선택된 층수 코드: %s (B1->B1층, F1->1층, F2->2층, F3->3층, F4->4층, F5->5층, F6->6층, F7->7층, F8->8층, ETC->기타)
                - 상세 공간/시설 입력값: %s

                [분석 및 명명 규칙]
                - building: 위 건물 코드를 참고하여 올바른 한글 건물명 ("유담관", "본관", "문예관", "청운관", "혜인관", "대일관", "북악관", "은주1관", "한림관", "수인관" 등)으로 반환할 것.
                - floor: 위 층수 코드를 참고하여 올바른 층수 명칭 ("B1층", "1층", "2층", "3층", "4층", "5층", "6층", "7층", "8층" 등)으로 반환할 것.
                - facility: 구체적 시설명 (예: 화장실 세면대, 복도 조명 등 상세 공간 정보 적극 반영)
                - problemType: 문제유형 (배수 관련, 시설 고장, 조명 고장, 냉난방, 청결 위생 등)
                - severity: 심각도 (낮음, 보통, 심각 중 택1)
                - summaryTitle: 반드시 "[건물명] [층수] [시설명]" 형식으로 작성할 것. (예: "유담관 4층 화장실 세면대" 등)
                - aiSummary: 관리자용 상세 요약 텍스트 (2~3문장 작성)
                - urgencyScore: 점검 필요도 점수 (1부터 100 사이의 정수. 누수나 안전 등 심각 사안은 70~100점 부여)

                제보 내용: %s
                """.formatted(buildingCode, floorCode, spaceDetail, reportContent);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(
                Map.of("parts", List.of(Map.of("text", prompt)))
        ));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        int maxRetries = 3;
        int retryCount = 0;

        while (retryCount < maxRetries) {
            try {
                ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
                Map responseBody = response.getBody();

                if (responseBody != null) {
                    List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseBody.get("candidates");
                    if (candidates != null && !candidates.isEmpty()) {
                        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                        if (parts != null && !parts.isEmpty()) {
                            String rawText = (String) parts.get(0).get("text");
                            String cleanedJson = stripCodeFence(rawText);

                            log.info("분석 완료");
                            return objectMapper.readValue(cleanedJson, Map.class);
                        }
                    }
                }
            } catch (Exception e) {
                retryCount++;
                log.warn("서버 과부하/통신 실패 (시도 횟수 {}/{}): {}", retryCount, maxRetries, e.getMessage());

                if (retryCount >= maxRetries) {
                    log.warn("Fallback 데이터를 반환합니다.");
                    return createFallbackResponse(buildingCode, floorCode, spaceDetail, reportContent);
                }

                try {
                    Thread.sleep(1500);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        return createFallbackResponse(buildingCode, floorCode, spaceDetail, reportContent);
    }

    private String trimTrailingSlash(String url) {
        if (url != null && url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        }
        return url;
    }

    private String stripCodeFence(String content) {
        String trimmed = content.trim();
        if (trimmed.startsWith("```")) {
            int firstNewline = trimmed.indexOf('\n');
            if (firstNewline > 0) {
                trimmed = trimmed.substring(firstNewline + 1);
            }
            if (trimmed.endsWith("```")) {
                trimmed = trimmed.substring(0, trimmed.length() - 3);
            }
        }
        return trimmed.trim();
    }

    private Map<String, Object> createFallbackResponse(String buildingCode, String floorCode, String spaceDetail, String reportContent) {
        String buildingName = convertBuildingName(buildingCode);
        String floorName = convertFloorName(floorCode);
        String facilityName = StringUtils.hasText(spaceDetail) ? spaceDetail : "일반 시설";

        Map<String, Object> fallback = new HashMap<>();
        fallback.put("building", buildingName);
        fallback.put("floor", floorName);
        fallback.put("facility", facilityName);
        fallback.put("problemType", "기타 시설 문제");
        fallback.put("severity", "낮음");
        fallback.put("summaryTitle", String.format("%s %s %s", buildingName, floorName, facilityName));
        fallback.put("aiSummary", String.format("[%s %s] 내용: %s",
                buildingName, floorName,
                StringUtils.hasText(reportContent) ? (reportContent.length() > 30 ? reportContent.substring(0, 30) + "..." : reportContent) : "내용 없음"));
        fallback.put("urgencyScore", 20);

        return fallback;
    }

    private String convertBuildingName(String code) {
        if (code == null) return "기타";
        return switch (code) {
            case "YUDAM" -> "유담관";
            case "MAIN" -> "본관";
            case "MOONAE" -> "문예관";
            case "CHEONGUN" -> "청운관";
            case "HEEIN" -> "혜인관";
            case "BUKGWAK" -> "북악관";
            case "EUNJU1" -> "은주1관";
            case "HANLIM" -> "한림관";
            case "SUIN" -> "수인관";
            default -> "기타";
        };
    }

    private String convertFloorName(String code) {
        if (code == null) return "기타";
        return switch (code) {
            case "B1" -> "B1층";
            case "F1" -> "1층";
            case "F2" -> "2층";
            case "F3" -> "3층";
            case "F4" -> "4층";
            case "F5" -> "5층";
            case "F6" -> "6층";
            case "F7" -> "7층";
            case "F8" -> "8층";
            default -> "기타";
        };
    }
}