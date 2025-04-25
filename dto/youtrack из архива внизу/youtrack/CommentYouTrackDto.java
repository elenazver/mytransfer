package org.example.dto.youtrack;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO-class for commenting on an issue in YouTrack.
 * Fields:
 * - text: The text of the comment (it is assumed that it contains the content of the comment).
 * - author: nformation about the comment's author (the AuthorYouTrackDto object).

 * Important: The "text" field and the comment block are missing in the current JSON-file.

 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommentYouTrackDto {

    /**
     * The text of the comment.
     * Intended use: storing the content of the task comment.
     * There is no field in the current JSON.
     */
    @JsonProperty("text")
    private String text;

    /**
     * Information about the author of the comment.
     * Uses the AuthorYouTrackDto class containing the "fullName" field.
     * Example value: AuthorYouTrackDto named "Dmitriy Shulga".
     */
    @JsonProperty("author")
    private AuthorYouTrackDto author;
}
