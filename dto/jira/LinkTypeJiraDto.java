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
public class LinkTypeJiraDto {

    /**
     * Unique identifier of the type of connection.
     * Assigned automatically.
     * Example: "10012"
     */
    @JsonProperty("id")
    private Long id;

    /**
     * Name of the connection type.
     * Description of how the tasks are related.
     * Example: "blocks", "duplicates", "relates to"
     */
    @JsonProperty("name")
    private String name;

    /**
     * Description of the incoming connection.
     * Shows how the current task depends on another one.
     * Example: "is blocked by", "is duplicated by"
     */
    @JsonProperty("inward")
    private String inward;

    /**
     * Description of the outgoing connection.
     * Shows how the current task affects another one.
     * Example: "blocks", "duplicates"
     */
    @JsonProperty("outward")
    private String outward;
}