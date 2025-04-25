package org.example.dto.jira.issuetype;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScopeDto {
    /**
      Information about the project, if the task type is linked to the Next-gen project.
     */
    @JsonProperty("project")
    private ProjectDto project;
}
