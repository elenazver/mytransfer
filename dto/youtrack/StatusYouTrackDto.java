package org.example.dto.youtrack;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class StatusYouTrackDto {

    /**
     * Unique status identifier.
     */
    @JsonProperty("id")
    private String id;

    /**
     * The name of the status.
     * Example: "Open", "Closed".
     */
    @JsonProperty("name")
    private String name;

    /**
     * A flag indicating whether the status is completed.
     * true — the status is completed, false — the status is in progress.
     */
    @JsonProperty("isResolved")
    private Boolean isResolved;


    /**The localized name of the status.
     * Example: "Open", "Closed".
     */
    @JsonProperty("localizedName")
    private String localizedName;

    /**
     * A nested class for representing colors.
     * Contains the color of the text and the background of the status.
     */

}
