package org.example.dto.youtrack;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class LinkYouTrackDto {
    /**
     * The direction of communication.
     * Possible values: "INBOUND" (incoming connection), "OUTBOUND" (outgoing connection).
     * Example of values: "INBOUND".
     */
    @JsonProperty("direction")
    private String direction;

    /**
     * The type of relationship between tasks.
     * Contains information about the type of connection, for example "relates to", "depends on".
     */
    @JsonProperty("linkType")
    private LinkTypeYouTrackDto linkType;

    /**
     * A list of related tasks.
     * Contains a list of related tasks with their IDs and description.
     */
    @JsonProperty("issues")
    private List<IssueLinkYouTrackDto> issues;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinkYouTrackDto that = (LinkYouTrackDto) o;
        return Objects.equals(direction, that.direction) && Objects.equals(linkType, that.linkType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(direction, linkType);
    }
}