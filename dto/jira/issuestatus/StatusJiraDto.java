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
public class StatusJiraDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("statusCategory")
    private String statusCategory;

    @JsonProperty("scope")
    private ScopeDto scope;

    @JsonProperty("usages")
    private List<UsageDto> usages;

    @JsonProperty("workflowUsages")
    private List<WorkflowUsageDto> workflowUsages;
}
