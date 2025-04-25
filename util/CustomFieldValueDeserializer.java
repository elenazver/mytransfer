package org.example.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.youtrack.CustomFieldYouTrackDto;
import org.example.dto.youtrack.CustomFieldYouTrackValueDto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CustomFieldValueDeserializer extends JsonDeserializer<CustomFieldYouTrackValueDto> {
    @Override
    public CustomFieldYouTrackValueDto deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        if (node.isArray()) {
            List<CustomFieldYouTrackValueDto> values = new ArrayList<>();
            node.forEach(element -> {
                try {
                    values.add(jsonParser.getCodec().treeToValue(element, CustomFieldYouTrackValueDto.class));
                } catch (JsonProcessingException e) {
                    log.error("Error reading Custom fields from a task YouTrack: {}", e.getMessage());
                }
            });
            return values.isEmpty() ? null : values.get(0); // We return the first element if it is a list
        } else if (node.isObject()) {
            CustomFieldYouTrackValueDto c = jsonParser.getCodec().treeToValue(node, CustomFieldYouTrackValueDto.class); // Return the object
            return c;
        }
        return null;
    }
}
