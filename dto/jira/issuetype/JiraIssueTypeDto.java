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
public class JiraIssueTypeDto {
    /**
      A unique identifier of the task type.
      It is assigned automatically by the system Jira.
     Example: "10001"
     */
    @JsonProperty("id")
    private Long id;

    /**
      Task type name.
      Example: "Bug", "Task", "Story"
     */
    @JsonProperty("name")
    private String name;

    /**
      Task type description.
      Example: "A problem which impairs or prevents the functions of the product."
     */
    @JsonProperty("description")
    private String description;

    /**
      A flag indicating whether the task type is a subtask.
      Example: true (if this is a subtask), false (if this is the main task)
     */
    @JsonProperty("subtask")
    private Boolean subtask;

    /**
      Scope of the task type (Next-gen project or global).
     */
    @JsonProperty("scope")
    private ScopeDto scope;
}
