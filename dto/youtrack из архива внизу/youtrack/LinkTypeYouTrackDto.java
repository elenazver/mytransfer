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
public class LinkTypeYouTrackDto {
    /**
     * The name of the connection type.
     * Example values: "relates to", "is duplicated by".
     */
    @JsonProperty("name")
    private String name;

    /**
     * The localized name of the connection type.
     */
    @JsonProperty("localizedName")
    private String localizedName;
}
