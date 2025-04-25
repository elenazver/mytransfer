package org.example.dto.jira.issuelink;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.example.dto.jira.LinkTypeJiraDto;
import org.example.dto.jira.RelatedIssueJiraDto;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class IssueLinkJiraResponseDto {

    /**
     * A unique identifier for the relationship between tasks.
     * It is assigned automatically by the Jira system.
     * Example: "10123"
     */
    @JsonProperty("id")
    private Long id;

    /**
     * The type of relationship between tasks (for example, "relates to", "blocks", "duplicates").
     * Represented by an object {@link LinkTypeJiraDto}, which contains information about the type of connection.
     * Example:
     * {
     *   "name": "blocks",
     *   "inward": "is blocked by",
     *   "outward": "blocks"
     * }
     */
    @JsonProperty("type")
    private LinkTypeJiraDto type;

    /**
     * A task that is related to the current task as an incoming one (for example, a blocking task).
     * Used to specify the task that the current one depends on.
     * Can be `null`, if the connection is not incoming.
     * Example:
     * {
     *   "key": "TRNTES-123",
     *   "fields": {
     *     "summary": "Implement feature X"
     *   }
     * }
     */
    @JsonProperty("inwardIssue")
    private RelatedIssueJiraDto inwardIssue;

    /**
     * A task that is associated with the current task as an outgoing one (for example, blocked by the current task).
     * Used to specify the task that is affected by the current one.
     * Can be `null` if the connection is not outgoing.
     * Example:
     * {
     *   "key": "TRNTES-124",
     *   "fields": {
     *     "summary": "Fix bug Y"
     *   }
     * }
     */
    @JsonProperty("outwardIssue")
    private RelatedIssueJiraDto outwardIssue;
}