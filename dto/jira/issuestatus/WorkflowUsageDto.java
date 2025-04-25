package org.example.dto.jira.issuestatus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkflowUsageDto {

    @JsonProperty("workflowId")
    private String workflowId;

    @JsonProperty("workflowName")
    private String workflowName;
}
