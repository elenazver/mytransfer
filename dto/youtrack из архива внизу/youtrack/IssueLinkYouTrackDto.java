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
            * Brief description of the related task.
            * Example of values: "Error during authorization", "The function is unavailable".
            */
    @JsonProperty("summary")
    private String summary;
}
