package com.logic.springjwt.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "home_stats")
public class HomeStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String refinanceBeneficiaries = "25,400+";

    @Column(nullable = false)
    private String interestSubsidies = "18,250+";

    @Column(nullable = false)
    private String partnerBanks = "14+";

    @Column(nullable = false)
    private String activeLoanSchemes = "8+";

    @Column(nullable = false)
    private String totalFundsDisbursed = "Rs. 4,850M+";

    private LocalDateTime updatedAt;

    public HomeStats() {
    }

    public HomeStats(String refinanceBeneficiaries, String interestSubsidies, String partnerBanks, String activeLoanSchemes, String totalFundsDisbursed) {
        this.refinanceBeneficiaries = refinanceBeneficiaries;
        this.interestSubsidies = interestSubsidies;
        this.partnerBanks = partnerBanks;
        this.activeLoanSchemes = activeLoanSchemes;
        this.totalFundsDisbursed = totalFundsDisbursed;
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRefinanceBeneficiaries() {
        return refinanceBeneficiaries;
    }

    public void setRefinanceBeneficiaries(String refinanceBeneficiaries) {
        this.refinanceBeneficiaries = refinanceBeneficiaries;
    }

    public String getInterestSubsidies() {
        return interestSubsidies;
    }

    public void setInterestSubsidies(String interestSubsidies) {
        this.interestSubsidies = interestSubsidies;
    }

    public String getPartnerBanks() {
        return partnerBanks;
    }

    public void setPartnerBanks(String partnerBanks) {
        this.partnerBanks = partnerBanks;
    }

    public String getActiveLoanSchemes() {
        return activeLoanSchemes;
    }

    public void setActiveLoanSchemes(String activeLoanSchemes) {
        this.activeLoanSchemes = activeLoanSchemes;
    }

    @JsonProperty("totalFundsDisbursed")
    public String getTotalFundsDisbursed() {
        return totalFundsDisbursed;
    }

    @JsonProperty("totalFundsDisbursed")
    public void setTotalFundsDisbursed(String totalFundsDisbursed) {
        if (totalFundsDisbursed != null && !totalFundsDisbursed.trim().isEmpty()) {
            this.totalFundsDisbursed = totalFundsDisbursed.trim();
        }
    }

    @JsonProperty("totalFundDisbursed")
    public String getTotalFundDisbursed() {
        return totalFundsDisbursed;
    }

    @JsonProperty("totalFundDisbursed")
    public void setTotalFundDisbursed(String totalFundDisbursed) {
        if (totalFundDisbursed != null && !totalFundDisbursed.trim().isEmpty()) {
            this.totalFundsDisbursed = totalFundDisbursed.trim();
        }
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
