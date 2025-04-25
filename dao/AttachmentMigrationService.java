package org.example.dao;

import org.example.dto.youtrack.AttachmentYouTrackDto;
import org.example.dto.youtrack.YouTrackIssueDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.example.constants.ProjectConstants.YOUTRACK_URL;

public class AttachmentMigrationService {

    public static Map<String, String> getAttachmentUrls(YouTrackIssueDto youTrackIssueDto) {
        List<AttachmentYouTrackDto> attachments = youTrackIssueDto.getAttachments();
        Map<String, String> attachmentsMap = new HashMap<>();
        if (attachments.isEmpty()) {
            return null;
        } else {
            attachments.forEach(attachment -> attachmentsMap.put(YOUTRACK_URL + attachment.getUrl().replace("/api", ""), attachment.getName()));
            return attachmentsMap;
        }
    }
}
