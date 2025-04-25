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
public class UserJiraDto {
    /**
     * The unique ID of the author's account in Jira.
     * Example: "712020:fa31713f-b47e-4fc9-8ff0-fa8ea04486b4"
     */
    @JsonProperty("accountId")
    private String accountId;

    /**
     * The displayed name of the author.
     * Example: "Elena Prokofyeva"
     */
    @JsonProperty("displayName")
    private String displayName;

    /**
     * The author's email address.
     * Example: "prokofyeva.e@mancala.games"
     */
    @JsonProperty("emailAddress")
    private String emailAddress;

    /**
     * Account activity status.
     * true — active, false — disabled.
     * Example: true
     */
    @JsonProperty("active")
    private boolean active;
}