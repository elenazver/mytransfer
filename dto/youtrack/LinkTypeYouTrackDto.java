package org.example.dto.youtrack;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class LinkTypeYouTrackDto {
    /**
     * Name of the connection type.
     * Example of values: "relates to", "is duplicated by".
     */
    @JsonProperty("name")
    private String name;

    /**
     * Localized name of the communication type.
     */
    @JsonProperty("localizedName")
    private String localizedName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinkTypeYouTrackDto that = (LinkTypeYouTrackDto) o;
        return Objects.equals(name, that.name) && Objects.equals(localizedName, that.localizedName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, localizedName);
    }
}
