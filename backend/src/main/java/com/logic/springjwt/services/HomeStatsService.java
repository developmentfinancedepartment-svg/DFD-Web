package com.logic.springjwt.services;

import com.logic.springjwt.models.HomeStats;
import com.logic.springjwt.repository.HomeStatsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class HomeStatsService {

    private static final Logger logger = LoggerFactory.getLogger(HomeStatsService.class);

    private final HomeStatsRepository homeStatsRepository;

    public HomeStatsService(HomeStatsRepository homeStatsRepository) {
        this.homeStatsRepository = homeStatsRepository;
    }

    public HomeStats getHomeStats() {
        return homeStatsRepository.findFirstByOrderByIdAsc().orElseGet(this::initDefaultStats);
    }

    public HomeStats initDefaultStats() {
        HomeStats defaultStats = new HomeStats(
                "25,400+",
                "18,250+",
                "14+",
                "8+",
                "Rs. 4,850M+"
        );
        HomeStats saved = homeStatsRepository.save(defaultStats);
        logger.info("Initialized default HomeStats: ID={}", saved.getId());
        return saved;
    }

    public HomeStats updateHomeStats(HomeStats updated) {
        HomeStats stats = homeStatsRepository.findFirstByOrderByIdAsc().orElseGet(this::initDefaultStats);

        if (updated.getRefinanceBeneficiaries() != null && !updated.getRefinanceBeneficiaries().trim().isEmpty()) {
            stats.setRefinanceBeneficiaries(updated.getRefinanceBeneficiaries().trim());
        }
        if (updated.getInterestSubsidies() != null && !updated.getInterestSubsidies().trim().isEmpty()) {
            stats.setInterestSubsidies(updated.getInterestSubsidies().trim());
        }
        if (updated.getPartnerBanks() != null && !updated.getPartnerBanks().trim().isEmpty()) {
            stats.setPartnerBanks(updated.getPartnerBanks().trim());
        }
        if (updated.getActiveLoanSchemes() != null && !updated.getActiveLoanSchemes().trim().isEmpty()) {
            stats.setActiveLoanSchemes(updated.getActiveLoanSchemes().trim());
        }
        String funds = updated.getTotalFundsDisbursed() != null ? updated.getTotalFundsDisbursed() : updated.getTotalFundDisbursed();
        if (funds != null && !funds.trim().isEmpty()) {
            stats.setTotalFundsDisbursed(funds.trim());
        }

        stats.setUpdatedAt(LocalDateTime.now());
        return homeStatsRepository.save(stats);
    }
}
