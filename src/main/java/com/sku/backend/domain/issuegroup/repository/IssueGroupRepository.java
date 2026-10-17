package com.sku.backend.domain.issuegroup.repository;

import com.sku.backend.domain.issuegroup.entity.IssueGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IssueGroupRepository extends JpaRepository<IssueGroup, Long> {
    Optional<IssueGroup> findByBuildingAndFacilityAndProblemTypeAndStatusNot(
            String building, String facility, String problemType, String status
    );
}