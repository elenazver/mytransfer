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
public class ProjectJiraDto {

    /**
     * Id project. It is assigned automatically by the system when creating a project
     */
    @JsonProperty("id")
    private Long id;

    /**
     * The project key. In fact, the short name of the project is
     */
    @JsonProperty("key")
    private String key;

    /**
     * Full name of the project
     */
    @JsonProperty("name")
    private String name;

    @JsonProperty("projectTypeKey")
    private String projectTypeKey;

    /**
     * If simplified: true, this indicates Team-managed (next-gen) project.
     * If simplified: false, this indicates Company-managed (classic) project.
     */
    @JsonProperty("simplified")
    private Boolean simplified;
}
