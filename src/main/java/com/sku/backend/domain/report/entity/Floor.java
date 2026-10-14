package com.sku.backend.domain.report.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Floor {
    B1("B1층"),
    F1("1층"),
    F2("2층"),
    F3("3층"),
    F4("4층"),
    F5("5층"),
    F6("6층"),
    F7("7층"),
    F8("8층"),
    ETC("기타");

    private final String description;
}