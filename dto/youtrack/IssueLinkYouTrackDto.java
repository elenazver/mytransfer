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
public class IssueLinkYouTrackDto {
    /**
     *  ID related tasks.
     * Example of values: "MWeb-128", "BUG-42".
     */
    @JsonProperty("idReadable")
    private String idReadable;
    /**
            * A brief description of the related task.
            * Example values: "Authorization error", "Function unavailable".
            */
    @JsonProperty("summary")
    private String summary;
}
