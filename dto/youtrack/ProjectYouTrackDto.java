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
public class ProjectYouTrackDto {
    /**
     * The full name of the project.
     * Example of values: "Mancala Web Project".
     */
    @JsonProperty("name")
    private String name;

    /**
     * The short name of the project.
     * Example values: "MWeb".
     */
    @JsonProperty("shortName")
    private String shortName;

    /**
     * The unique identifier of the project.
     * Example values: "0-12".
     */
    @JsonProperty("ringId")
    private String ringId;

    /**
     * Flag whether the project is archived.
     * Values: true — the project is archived, false — the project is active.
     */
    @JsonProperty("archived")
    private Boolean archived;
}
