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
public class CustomFieldYouTrackValueDto {
    @JsonProperty("name")
    private String name;

    @JsonProperty("$type")
    private String type;

    @JsonProperty("id")
    private String id;

    @JsonProperty("description")
    private String description;

    @JsonProperty("text")
    private String text;

    @JsonProperty("archived")
    private Boolean archived;
}
