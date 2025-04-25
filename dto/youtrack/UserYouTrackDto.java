package org.example.dto.youtrack;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO-class for representing the author of a task in YouTrack.
 * <p>
 * Fields:
 * - fullName: The full name of the user (for example, "Alexey Tereshin").
 * Used to display the name of the author of the task or comment.
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserYouTrackDto {

    @JsonProperty("id")
    private String id;

    /**
     * Full name of the author of the issue.
     * Example of a value: "Dmitriy Shulga".
     */
    @JsonProperty("fullName")
    private String fullName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("login")
    private String login;
}
