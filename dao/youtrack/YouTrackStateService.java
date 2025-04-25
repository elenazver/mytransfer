package org.example.dao.youtrack;

import org.example.constants.ProjectConstants;
import org.example.dto.youtrack.CustomFieldYouTrackDto;
import org.example.dto.youtrack.CustomFieldYouTrackValueDto;
import org.example.dto.youtrack.YouTrackIssueDto;

public class YouTrackStateService {
    public static String getIssueStatusNameByYouTrackIssue(YouTrackIssueDto youTrackIssueDto) {
        CustomFieldYouTrackDto customFieldYouTrackDto = youTrackIssueDto.getCustomFields().stream()
                .filter(field -> field.getName().toLowerCase().endsWith(ProjectConstants.STATUS_FIELD_NAME.toLowerCase()) || field.getName().equalsIgnoreCase("status"))
                .findFirst().orElse(null);
        CustomFieldYouTrackValueDto value = null;
        if (customFieldYouTrackDto != null) {
            value = customFieldYouTrackDto.getValue();
        }
        return value != null ? value.getName() : null;
    }
}
