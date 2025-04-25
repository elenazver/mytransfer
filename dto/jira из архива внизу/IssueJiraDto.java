package org.example.dto.jira;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.example.enumeration.ProjectType;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class IssueJiraDto {
    /**
     * The unique identifier of the task in the system.
     * It is assigned automatically by the system when creating a task.
     * Example: "23008"
     */
    @JsonProperty("id")
    private String id;

    /**
     * The task key in Jira, which is used as the task identifier for links.
     * This is usually a combination of the project code and the task sequence number.
     * Example: "TRNTES-76"
     * Matches the issue ID in YouTrack if the issue has been migrated.
     */
    @JsonProperty("key")
    private String key;

    /**
     * A link to the Jira REST API for the current task.
     * Allows you to get additional information about the task.
     * Example: "https://mancalagaming.atlassian.net/rest/api/2/issue/23008"
     */
    @JsonProperty("self")
    private String self;

    /**
     * Task fields, including description, status, priority, reporter, and performer.
     * Contains the {@link FieldsJiraDto} object, which represents
     * all detailed task properties.
     */
    @JsonProperty("fields")
    private FieldsJiraDto fields;
}