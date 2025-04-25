package org.example.dto.youtrack;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class YouTrackIssueDto {
    /**
     * Unique issue ID.
     */
    @JsonProperty("id")
    private String id;

    /**
     * Issue ID.
     * Example of values: "MWeb-128", "BUG-42".
     */
    @JsonProperty("idReadable")
    private String idReadable;

    /**
     * A brief description of the task (title).
     * Example: "Login error".
     */
    @JsonProperty("summary")
    private String summary;

    /**
     * Detailed task description.
     */
    @JsonProperty("description")
    private String description;

    /**
     * Date and time of issue creation.
     *
     */
    @JsonProperty("created")
    private Instant created;

    /**
     * A list of custom task fields.
     * Data type: List<CustomFieldYouTrackDto>.
     * Example of values: "Priority", "Assignee".
     */
    @JsonProperty("customFields")
    private List<CustomFieldYouTrackDto> customFields;

    /**
     * List of comments on the issue.
     * Data type: List<CommentYouTrackDto>.
     */
    @JsonProperty("comments")
    private List<CommentYouTrackDto> comments;

    /**
     * List of task attachments.
     * Data type: List<AttachmentYouTrackDto>.
     */
    @JsonProperty("attachments")
    private List<AttachmentYouTrackDto> attachments;

    /**
     * List of related tasks (links to other tasks).
     * Data type: List<LinkYouTrackDto>.
     */
    @JsonProperty("links")
    private List<LinkYouTrackDto> links;

    /**
     * Information about the project to which the task belongs.
     * Data type: ProjectYouTrackDto.
     */
    @JsonProperty("project")
    private ProjectYouTrackDto project;
}