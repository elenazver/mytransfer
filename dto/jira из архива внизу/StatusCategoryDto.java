package org.example.dto.jira;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class StatusCategoryDto {
    /**
     * Unique identifier of the status category.
     * Example: "2"
     */
    @JsonProperty("id")
    private String id;

    /**
     * The status category key.
     * Example: "new", "done", "in-progress"
     */
    @JsonProperty("key")
    private String key;

    /**
     * Name of the status category.
     * Example: "To Do", "In Progress", "Done"
     */
    @JsonProperty("name")
    private String name;
}