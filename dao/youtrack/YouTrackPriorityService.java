package org.example.dao.youtrack;

import org.example.constants.ProjectConstants;
import org.example.dto.youtrack.CustomFieldYouTrackDto;
import org.example.dto.youtrack.CustomFieldYouTrackValueDto;
import org.example.dto.youtrack.YouTrackIssueDto;

import java.util.Optional;

public class YouTrackPriorityService {


    /**
     * Searches for the "Priority" field in the YouTrack issue.
     *
     * @param youTrackIssueDto task from YouTrack
     * @return Optional with the found field, if it exists
     */
    public static Optional<CustomFieldYouTrackDto> findPriorityField(YouTrackIssueDto youTrackIssueDto) {
        return youTrackIssueDto.getCustomFields().stream()
                .filter(field -> ProjectConstants.PRIORITY_FIELD_NAME.equals(field.getName()))
                .findFirst();
    }

    public static String getPriorityNameByYouTrackIssue(YouTrackIssueDto youTrackIssueDto) {
        CustomFieldYouTrackDto customFieldYouTrackDto = youTrackIssueDto.getCustomFields().stream()
                .filter(field -> ProjectConstants.PRIORITY_FIELD_NAME.equalsIgnoreCase(field.getName()))
                .findFirst().orElse(null);
        if (customFieldYouTrackDto != null) {
            CustomFieldYouTrackValueDto value = customFieldYouTrackDto.getValue();
            return value.getName();
        }
        return null;
    }
}
