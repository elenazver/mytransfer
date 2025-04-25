package org.example.dto.jira.issuetypescheme;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class IssueTypeSchemeResponse {

    private boolean isLast;
    private int maxResults;
    private int startAt;
    private int total;
    private List<IssueTypeScheme> values;

    public List<Long> getIssueTypeIdsBySchemeId(Long schemeId) {
        List<Long> issueTypeIds = new ArrayList<>();
        for (IssueTypeScheme scheme : values) {
            if (scheme.getIssueTypeSchemeId().equals(schemeId)) {
                issueTypeIds.add(scheme.getIssueTypeId());
            }
        }
        return issueTypeIds;
    }


    @Getter
    @Setter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class IssueTypeScheme {
        private Long issueTypeSchemeId;
        private Long issueTypeId;
    }
}
