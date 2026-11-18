package com.logic.springjwt.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "schemes")
public class Scheme {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String category;

    @Column(length = 50)
    private String schemeType = "LOAN"; // "LOAN" or "SERVICE"

    private String maxAmount;

    private String interestRate;

    private String natureOfLoan; // e.g. "Working Capital", "Investment & Combined"

    private String subsidyDetails; // e.g. "4% Govt. Subsidy (12% Total)"

    @Column(columnDefinition = "TEXT")
    private String description;

    private String icon;

    @Column(nullable = false)
    private boolean active = true;

    @Column(length = 20)
    private String status = "ACTIVE"; // "ACTIVE", "COMING_SOON", "DRAFT"

    private String repaymentPeriod;

    private String gracePeriod; // Optional/nullable

    @Column(columnDefinition = "TEXT")
    private String purpose;

    @Column(columnDefinition = "TEXT")
    private String eligibility;

    @Column(columnDefinition = "TEXT")
    private String participatingBanks;

    @Column(columnDefinition = "TEXT")
    private String beneficiaries;

    @Column(columnDefinition = "TEXT")
    private String requirements;

    @Column(columnDefinition = "TEXT")
    private String locations; // JSON or text

    @Column(columnDefinition = "TEXT")
    private String servicesProvided; // JSON or text

    @Column(columnDefinition = "TEXT")
    private String centerLocationsTable; // JSON array or text

    @Column(columnDefinition = "TEXT")
    private String operationalArrangements; // TEXT

    @Column(columnDefinition = "TEXT")
    private String implementationArrangements;

    @Column(columnDefinition = "TEXT")
    private String otherInitiatives;

    @Column(columnDefinition = "TEXT")
    private String contactInfo;

    @Column(columnDefinition = "TEXT")
    private String operationalGuidelineUrl;

    private String operationalGuidelineFileName;

    public Scheme() {
    }

    public Scheme(Long id, String title, String category, String maxAmount, String interestRate, String description, String icon, boolean active) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.maxAmount = maxAmount;
        this.interestRate = interestRate;
        this.description = description;
        this.icon = icon;
        this.active = active;
        this.schemeType = "LOAN";
        this.status = active ? "ACTIVE" : "DRAFT";
    }

    public Scheme(String title, String category, String maxAmount, String interestRate, String description, String icon, boolean active) {
        this.title = title;
        this.category = category;
        this.maxAmount = maxAmount;
        this.interestRate = interestRate;
        this.description = description;
        this.icon = icon;
        this.active = active;
        this.schemeType = "LOAN";
        this.status = active ? "ACTIVE" : "DRAFT";
    }

    public Scheme(String title, String category, String maxAmount, String interestRate, String description, String icon, boolean active,
                  String repaymentPeriod, String gracePeriod, String purpose, String eligibility, String participatingBanks) {
        this.title = title;
        this.category = category;
        this.maxAmount = maxAmount;
        this.interestRate = interestRate;
        this.description = description;
        this.icon = icon;
        this.active = active;
        this.repaymentPeriod = repaymentPeriod;
        this.gracePeriod = gracePeriod;
        this.purpose = purpose;
        this.eligibility = eligibility;
        this.participatingBanks = participatingBanks;
        this.schemeType = "LOAN";
        this.status = active ? "ACTIVE" : "DRAFT";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSchemeType() {
        return schemeType != null && !schemeType.trim().isEmpty() ? schemeType : "LOAN";
    }

    public void setSchemeType(String schemeType) {
        this.schemeType = schemeType != null && !schemeType.trim().isEmpty() ? schemeType : "LOAN";
    }

    public String getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(String maxAmount) {
        this.maxAmount = maxAmount;
    }

    public String getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(String interestRate) {
        this.interestRate = interestRate;
    }

    public String getNatureOfLoan() {
        return natureOfLoan;
    }

    public void setNatureOfLoan(String natureOfLoan) {
        this.natureOfLoan = natureOfLoan;
    }

    public String getSubsidyDetails() {
        return subsidyDetails;
    }

    public void setSubsidyDetails(String subsidyDetails) {
        this.subsidyDetails = subsidyDetails;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public boolean isActive() {
        if (this.status != null && !this.status.trim().isEmpty()) {
            return !"DRAFT".equalsIgnoreCase(this.status.trim());
        }
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
        if (!active) {
            this.status = "DRAFT";
        } else if (this.status == null || "DRAFT".equalsIgnoreCase(this.status.trim())) {
            this.status = "ACTIVE";
        }
    }

    public String getStatus() {
        if (this.status != null && !this.status.trim().isEmpty()) {
            return this.status.trim().toUpperCase();
        }
        return this.active ? "ACTIVE" : "DRAFT";
    }

    public void setStatus(String status) {
        if (status != null && !status.trim().isEmpty()) {
            this.status = status.trim().toUpperCase();
            this.active = !"DRAFT".equalsIgnoreCase(this.status);
        } else {
            this.status = this.active ? "ACTIVE" : "DRAFT";
        }
    }

    public String getRepaymentPeriod() {
        return repaymentPeriod;
    }

    public void setRepaymentPeriod(String repaymentPeriod) {
        this.repaymentPeriod = repaymentPeriod;
    }

    public String getGracePeriod() {
        return gracePeriod;
    }

    public void setGracePeriod(String gracePeriod) {
        this.gracePeriod = gracePeriod;
    }

    public String getPurpose() {
        return purpose;
    }

    @JsonProperty("purpose")
    public void setPurpose(JsonNode node) {
        if (node == null || node.isNull()) {
            this.purpose = null;
        } else if (node.isArray()) {
            List<String> list = new ArrayList<>();
            for (JsonNode item : node) {
                if (item != null && !item.isNull()) {
                    String s = item.asText().trim();
                    if (!s.isEmpty()) list.add(s);
                }
            }
            this.purpose = String.join("\n", list);
        } else {
            this.purpose = node.asText();
        }
    }

    @JsonIgnore
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getEligibility() {
        return eligibility;
    }

    @JsonProperty("eligibility")
    public void setEligibility(JsonNode node) {
        if (node == null || node.isNull()) {
            this.eligibility = null;
        } else if (node.isArray()) {
            List<String> list = new ArrayList<>();
            for (JsonNode item : node) {
                if (item != null && !item.isNull()) {
                    String s = item.asText().trim();
                    if (!s.isEmpty()) list.add(s);
                }
            }
            this.eligibility = String.join("\n", list);
        } else {
            this.eligibility = node.asText();
        }
    }

    @JsonIgnore
    public void setEligibility(String eligibility) {
        this.eligibility = eligibility;
    }

    public String getParticipatingBanks() {
        return participatingBanks;
    }

    public void setParticipatingBanks(String participatingBanks) {
        this.participatingBanks = participatingBanks;
    }

    public String getBeneficiaries() {
        return beneficiaries;
    }

    @JsonProperty("beneficiaries")
    public void setBeneficiaries(JsonNode node) {
        if (node == null || node.isNull()) {
            this.beneficiaries = null;
        } else if (node.isArray()) {
            List<String> list = new ArrayList<>();
            for (JsonNode item : node) {
                if (item != null && !item.isNull()) {
                    String s = item.asText().trim();
                    if (!s.isEmpty()) list.add(s);
                }
            }
            this.beneficiaries = String.join("\n", list);
        } else {
            this.beneficiaries = node.asText();
        }
    }

    @JsonIgnore
    public void setBeneficiaries(String beneficiaries) {
        this.beneficiaries = beneficiaries;
    }

    public String getRequirements() {
        return requirements;
    }

    @JsonProperty("requirements")
    public void setRequirements(JsonNode node) {
        if (node == null || node.isNull()) {
            this.requirements = null;
        } else if (node.isArray()) {
            List<String> list = new ArrayList<>();
            for (JsonNode item : node) {
                if (item != null && !item.isNull()) {
                    String s = item.asText().trim();
                    if (!s.isEmpty()) list.add(s);
                }
            }
            this.requirements = String.join("\n", list);
        } else {
            this.requirements = node.asText();
        }
    }

    @JsonIgnore
    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    @JsonProperty("locations")
    public Object getLocations() {
        if (this.locations == null) return null;
        String trimmed = this.locations.trim();
        if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
            try {
                return OBJECT_MAPPER.readTree(trimmed);
            } catch (Exception ignored) {}
        }
        return this.locations;
    }

    @JsonProperty("locations")
    public void setLocations(JsonNode node) {
        if (node == null || node.isNull()) {
            this.locations = null;
        } else if (node.isTextual()) {
            this.locations = node.asText();
        } else {
            this.locations = node.toString();
        }
    }

    @JsonIgnore
    public String getLocationsRaw() {
        return this.locations;
    }

    @JsonIgnore
    public void setLocationsRaw(String locations) {
        this.locations = locations;
    }

    @JsonProperty("servicesProvided")
    public Object getServicesProvided() {
        if (this.servicesProvided == null) return null;
        String trimmed = this.servicesProvided.trim();
        if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
            try {
                return OBJECT_MAPPER.readTree(trimmed);
            } catch (Exception ignored) {}
        }
        return this.servicesProvided;
    }

    @JsonProperty("servicesProvided")
    public void setServicesProvided(JsonNode node) {
        if (node == null || node.isNull()) {
            this.servicesProvided = null;
        } else if (node.isTextual()) {
            this.servicesProvided = node.asText();
        } else {
            this.servicesProvided = node.toString();
        }
    }

    @JsonIgnore
    public String getServicesProvidedRaw() {
        return this.servicesProvided;
    }

    @JsonIgnore
    public void setServicesProvidedRaw(String servicesProvided) {
        this.servicesProvided = servicesProvided;
    }

    @JsonProperty("center_locations_table")
    public Object getCenterLocationsTable() {
        if (this.centerLocationsTable == null) return null;
        String trimmed = this.centerLocationsTable.trim();
        if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
            try {
                return OBJECT_MAPPER.readTree(trimmed);
            } catch (Exception ignored) {}
        }
        return this.centerLocationsTable;
    }

    @JsonProperty("center_locations_table")
    @JsonAlias({"centerLocationsTable", "center_locations_table"})
    public void setCenterLocationsTable(JsonNode node) {
        if (node == null || node.isNull()) {
            this.centerLocationsTable = null;
        } else if (node.isTextual()) {
            this.centerLocationsTable = node.asText();
        } else {
            this.centerLocationsTable = node.toString();
        }
    }

    @JsonProperty("centerLocationsTable")
    public Object getCenterLocationsTableCamel() {
        return getCenterLocationsTable();
    }

    @JsonProperty("centerLocationsTable")
    public void setCenterLocationsTableCamel(JsonNode node) {
        setCenterLocationsTable(node);
    }

    @JsonIgnore
    public String getCenterLocationsTableRaw() {
        return this.centerLocationsTable;
    }

    @JsonIgnore
    public void setCenterLocationsTableRaw(String centerLocationsTable) {
        this.centerLocationsTable = centerLocationsTable;
    }

    @JsonProperty("operational_arrangements")
    public String getOperationalArrangements() {
        return operationalArrangements;
    }

    @JsonProperty("operational_arrangements")
    @JsonAlias({"operationalArrangements", "operational_arrangements"})
    public void setOperationalArrangements(String operationalArrangements) {
        this.operationalArrangements = operationalArrangements;
    }

    @JsonProperty("operationalArrangements")
    public String getOperationalArrangementsCamel() {
        return this.operationalArrangements;
    }

    @JsonProperty("operationalArrangements")
    public void setOperationalArrangementsCamel(String operationalArrangements) {
        if (operationalArrangements != null && !operationalArrangements.trim().isEmpty()) {
            this.operationalArrangements = operationalArrangements;
        }
    }

    public String getImplementationArrangements() {
        return implementationArrangements;
    }

    public void setImplementationArrangements(String implementationArrangements) {
        this.implementationArrangements = implementationArrangements;
    }

    public String getOtherInitiatives() {
        return otherInitiatives;
    }

    public void setOtherInitiatives(String otherInitiatives) {
        this.otherInitiatives = otherInitiatives;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getOperationalGuidelineUrl() {
        return operationalGuidelineUrl;
    }

    public void setOperationalGuidelineUrl(String operationalGuidelineUrl) {
        this.operationalGuidelineUrl = operationalGuidelineUrl;
    }

    public String getOperationalGuidelineFileName() {
        return operationalGuidelineFileName;
    }

    public void setOperationalGuidelineFileName(String operationalGuidelineFileName) {
        this.operationalGuidelineFileName = operationalGuidelineFileName;
    }

    @JsonProperty("guidelineFileUrl")
    public String getGuidelineFileUrl() {
        return this.operationalGuidelineUrl;
    }

    @JsonProperty("guidelineFileUrl")
    public void setGuidelineFileUrl(String guidelineFileUrl) {
        if (guidelineFileUrl != null && !guidelineFileUrl.trim().isEmpty()) {
            this.operationalGuidelineUrl = guidelineFileUrl;
        }
    }

    @JsonProperty("guidelineFileName")
    public String getGuidelineFileName() {
        return this.operationalGuidelineFileName;
    }

    @JsonProperty("guidelineFileName")
    public void setGuidelineFileName(String guidelineFileName) {
        if (guidelineFileName != null && !guidelineFileName.trim().isEmpty()) {
            this.operationalGuidelineFileName = guidelineFileName;
        }
    }
}
