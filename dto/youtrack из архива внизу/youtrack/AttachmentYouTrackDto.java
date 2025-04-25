package org.example.dto.youtrack;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO-class for embedding an issue in YouTrack.
 * Contains the file name and URL for accessing the attachment.
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class AttachmentYouTrackDto {

    /**
     * The name of the attachment file.
     * For example: "screenshot.png".
     */
    @JsonProperty("name")
    private String name;

    /**
     * URL to download or display an attachment.
     * For example: "/attachments/8-43382/screenshot.png".
     */
    @JsonProperty("url")
    private String url;
}
