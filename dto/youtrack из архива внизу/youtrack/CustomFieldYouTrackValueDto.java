package org.example.dto.youtrack;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
 /* It is used to represent the values of fields of the type enum,  "Priority" or "State".

        */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomFieldYouTrackValueDto {
    @JsonProperty("name")
    private String name;
}
