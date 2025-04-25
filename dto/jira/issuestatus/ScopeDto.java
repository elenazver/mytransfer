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
public class ScopeDto {

    @JsonProperty("type")
    private String type;

    @JsonProperty("project")
    private ProjectDto project;
}
