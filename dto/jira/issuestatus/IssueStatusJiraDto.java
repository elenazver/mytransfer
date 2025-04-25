package org.example.dto.jira.issuestatus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;


@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class IssueStatusJiraDto {

    @JsonProperty("isLast")
    private boolean isLast;

    @JsonProperty("maxResults")
    private int maxResults;

    @JsonProperty("nextPage")
    private String nextPage;

    @JsonProperty("self")
    private String self;

    @JsonProperty("startAt")
    private int startAt;

    @JsonProperty("total")
    private int total;

    @JsonProperty("values")
    private List<StatusJiraDto> values;
}
