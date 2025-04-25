package org.example.dto.jira;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class JiraPriorityDto {
    /**
     * Unique priority identifier.
     * Assigned by the system automatically.
     * Example: "3" (where "3" may correspond to "High")
     */
    @JsonProperty("id")
    private Long id;

    /**
     * Task priority level name.
     * Example: "High", "Medium", "Low"
     */
    @JsonProperty("name")
    private String name;

    /**
     * Priority description
     */
    @JsonProperty("description")
    private String description;

    public JiraPriorityDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
