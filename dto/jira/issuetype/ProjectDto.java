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
public class ProjectDto {
    /**
      The unique identifier of the project.
     */
    @JsonProperty("id")
    private Long id;
}

