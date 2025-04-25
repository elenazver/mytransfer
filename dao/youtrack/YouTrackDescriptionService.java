package org.example.dao.youtrack;

import org.example.dto.youtrack.CustomFieldYouTrackDto;
import org.example.dto.youtrack.YouTrackIssueDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class YouTrackDescriptionService extends YouTrackService {
    public static Optional<List<CustomFieldYouTrackDto>> findIssueCustomTextField(YouTrackIssueDto youTrackIssueDto) {
        List<CustomFieldYouTrackDto> result = youTrackIssueDto.getCustomFields().stream()
                .filter(field -> field.getType().equalsIgnoreCase("TextIssueCustomField"))
                .collect(Collectors.toCollection(ArrayList::new));

        return result.isEmpty() ? Optional.empty() : Optional.of(result);
    }
}
