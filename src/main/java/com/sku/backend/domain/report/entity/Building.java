package com.sku.backend.domain.report.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Building {
    YUDAM("유담관"),
    MAIN("본관"),
    MOONAE("문예관"),
    CHEONGUN("청운관"),
    HEEIN("혜인관"),
    BUKGWAK("북악관"),
    EUNJU1("은주1관"),
    HANLIM("한림관"),
    SUIN("수인관"),
    ETC("기타");

    private final String description;
}