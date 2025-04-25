package org.example.dto.youtrack;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.example.util.CustomFieldValueDeserializer;

import java.util.List;

/**
 * DTO-class for representing a custom task field in YouTrack.
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomFieldYouTrackDto {
    /**
     * Name of the custom field.
     * Example of values: "Priority", "Assignee", "State".
     */
    @JsonProperty("name")
    private String name;

    /**
     * Custom field values.
     * Example of values: User: {"login": "tereshin.a", "fullName": "Alexey Tereshin"}
     */
    @JsonProperty("value")
    @JsonDeserialize(using = CustomFieldValueDeserializer.class)
    private CustomFieldYouTrackValueDto value;

    @JsonProperty("$type")
    private String type;
}
