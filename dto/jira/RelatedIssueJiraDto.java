package org.example.dto.jira;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class RelatedIssueJiraDto {

    /**
     * The unique ID of the issue.
     * Assigned by the system automatically.
     * Example: "23006"
     */
    @JsonProperty("id")
    private Long id; // ID related tasks

    /**
     * The task key used for links.
     * Example: "PROJECT-123"
     */
    @JsonProperty("key")
    private String key; // Key related tasks (for example, "PROJECT-123")

    /**
     * URL tasks in API Jira.
     * Example: "https://mancalagaming.atlassian.net/rest/api/2/issue/23006"
     */
    @JsonProperty("self")
    private String self; // URL tasks

    /**
     * The main task fields, including a brief description, priority, and status.
     * Represented as an object {@link FieldsJiraDto}.
     * Example:
     * {
     *   "summary": "[TRN-13] need to translate not found page",
     *   "priority": {
     *     "name": "High"
     *   },
     *   "status": {
     *     "name": "Open"
     *   }
     * }
     */
    @JsonProperty("fields")
    private FieldsJiraDto fields; // Fields tasks (summary, priority, status)
}