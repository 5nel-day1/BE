package com.sku.backend.domain.issuegroup.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class IssueGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String building;
    private String facility;
    private String problemType;
    private String status;

    public void updateStatus(String status) {
        this.status = status;
    }
}