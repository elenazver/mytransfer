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
public class StatusJiraDto {
    /**
     * A unique status identifier.
     * Assigned automatically by the Jira system.
     * Example: "1"
     */
    @JsonProperty("id")
    private String id;

    /**
     * The name of the task status.
     * Example: "Open", "In Progress", "Done"
     */
    @JsonProperty("name")
    private String name;

    /**
     * Description of the task status.
     * Example: "The issue is open and ready for work."
     */
    @JsonProperty("description")
    private String description;

    /**
     * The issue status category.
     * Includes additional information about the type of status (for example, "To Do", "In Progress", "Done").
     * Represented by an object {@link StatusCategoryDto}.
     * Example:
     * {
     *   "id": "2",
     *   "key": "new",
     *   "name": "To Do"
     * }
     */
    @JsonProperty("statusCategory")
    private StatusCategoryDto statusCategory;
}