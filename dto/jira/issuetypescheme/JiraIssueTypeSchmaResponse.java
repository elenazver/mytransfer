package org.example.dto.jira.issuetypescheme;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true) // Ignoring unknown fields in JSON
@Slf4j // Logging
public class JiraIssueTypeSchmaResponse {

    @JsonProperty("isLast")
    private boolean isLast;

    @JsonProperty("maxResults")
    private int maxResults;

    @JsonProperty("startAt")
    private int startAt;

    @JsonProperty("total")
    private int total;

    @JsonProperty("values")
    private List<IssueTypeSchemeWrapper> values;

    // Method for searching for issueType by name
    public IssueType findIssueTypeByName(String issueTypeName) {
        if (values == null || values.isEmpty()) {
            log.warn("There are no task type schemes available.");
            return null;
        }

        for (IssueTypeSchemeWrapper wrapper : values) {
            IssueTypeScheme scheme = wrapper.getIssueTypeScheme();
            if (scheme != null && scheme.getIssueTypes() != null && scheme.getIssueTypes().getValues() != null) {
                for (IssueType issueType : scheme.getIssueTypes().getValues()) {
                    if (issueTypeName.equals(issueType.getName())) {
                        log.info("The issue type was found with name={}", issueTypeName);
                        return issueType;
                    }
                }
            }
        }

        log.warn("The issue type was not found with name={}", issueTypeName);
        return null;
    }

    @Getter
    @Setter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true) // Ignoring unknown fields in JSON
    public static class IssueTypeSchemeWrapper {

        @JsonProperty("issueTypeScheme")
        private IssueTypeScheme issueTypeScheme;

        @JsonProperty("projectIds")
        private List<String> projectIds;
    }

    @Getter
    @Setter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true) // Ignoring unknown fields in JSON
    public static class IssueTypeScheme {

        @JsonProperty("id")
        private Long id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("description")
        private String description;

        @JsonProperty("defaultIssueTypeId")
        private String defaultIssueTypeId;

        @JsonProperty("isDefault")
        private Boolean isDefault;

        @JsonProperty("issueTypes")
        private IssueTypes issueTypes;
    }

    @Getter
    @Setter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true) // Ignoring unknown fields in JSON
    public static class IssueTypes {

        @JsonProperty("isLast")
        private boolean isLast;

        @JsonProperty("maxResults")
        private int maxResults;

        @JsonProperty("startAt")
        private int startAt;

        @JsonProperty("total")
        private int total;

        @JsonProperty("values")
        private List<IssueType> values;
    }

    @Getter
    @Setter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true) // Ignoring unknown fields in JSON
    public static class IssueType {

        @JsonProperty("description")
        private String description;

        @JsonProperty("hierarchyLevel")
        private int hierarchyLevel;

        @JsonProperty("iconUrl")
        private String iconUrl;

        @JsonProperty("id")
        private Long id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("subtask")
        private boolean subtask;
    }
}
