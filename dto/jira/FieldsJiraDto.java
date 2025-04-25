package org.example.dto.jira;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.example.dto.jira.issuelink.IssueLinkJiraResponseDto;
import org.example.dto.jira.issuestatus.IssueStatusJiraDto;

import java.util.List;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class FieldsJiraDto {
    /**
     * Brief description of the task (topic).
     * Example: "[TRN-1] Need to translate a phrase"
     */
    @JsonProperty("summary")
    private String summary;

    /**
     * Detailed task description.
     * Example:
     * "IN RU: Ссылка будет отправлена вам по почте, когда ваш отчет будет готов. Обычно это занимает до 15 минут.
     *  IN EN: A link will be mailed to you when your report is ready. Usually, it takes up to 15 minutes."
     */
    @JsonProperty("description")
    private String description;

    /**
     * Task status (for example, "Open", "In Progress", "Done").
     * Contains information about the current issue status.
     * Example: "Open"
     */
    @JsonProperty("status")
    private IssueStatusJiraDto status;

    /**
     * The data of the task author (reporter).
     * Example:
     * {
     *  "accountId": "712020:fa31713f-b47e-4fc9-8ff0-fa8ea04486b4",
     *  "displayName": "Elena Prokofyeva",
     *  "emailAddress": "prokofyeva.e@mancala.games",
     *  "active": true
     * }
     */
    @JsonProperty("reporter")
    private UserJiraDto reporter;

    /**
     * Data of the task performer (if assigned).
     * Example:
     * {
     *  "accountId": "512345:abcd1234-ef56-gh78-ijkl-1234567890ab",
     *  "displayName": "John Doe",
     *  "emailAddress": "johndoe@example.com",
     *  "active": true
     * }
     */
    @JsonProperty("assignee")
    private UserJiraDto assignee;

    /**
     * Task priority (for example, "High", "Medium", "Low").
     * Example: "High"
     */
    @JsonProperty("priority")
    private JiraPriorityDto priority;

    /**
     * A list of links to related tasks.
     * Example:
     * [
     *   {
     *     "linkType": "Relates",
     *     "targetIssueKey": "TRNTES-123",
     *     "targetIssueId": "10002"
     *   }
     * ]
     */
    @JsonProperty("issuelinks")
    private List<IssueLinkJiraResponseDto> links;
}