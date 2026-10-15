package com.sku.backend.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NotificationPatchRequestDto {
    private boolean statusAlarm;
    private boolean commentAlarm;
    private boolean solvedAlarm;
}