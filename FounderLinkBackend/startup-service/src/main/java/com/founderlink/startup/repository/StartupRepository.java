package com.founderlink.startup.repository;

import com.founderlink.startup.entity.Startup;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StartupRepository extends JpaRepository<Startup, Long> {
    List<Startup> findByIndustry(String industry);
    List<Startup> findByStage(String stage);
    List<Startup> findByFounderId(Long founderId);
}