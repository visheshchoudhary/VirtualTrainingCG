package com.founderlink.team.repository;

import com.founderlink.team.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TeamRepository extends JpaRepository<TeamMember, Long> {
    List<TeamMember> findByStartupId(Long startupId);
    List<TeamMember> findByUserId(Long userId);
}