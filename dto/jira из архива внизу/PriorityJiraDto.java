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
public class PriorityJiraDto {
    /**
     * The unique priority identifier.
     * It is assigned automatically by the system.
     * Example: "3" (where  "3" may correspond to "High")
     */
    @JsonProperty("id")
    private String id;

    /**
     * Task priority level name.
     * Example: "High", "Medium", "Low"
     */
    @JsonProperty("name")
    private String name;
}
