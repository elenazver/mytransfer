package org.example.dao.youtrack;

import org.example.dto.youtrack.LinkYouTrackDto;
import org.example.dto.youtrack.YouTrackIssueDto;
import org.example.enumeration.YouTrackLinkType;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class YouTrackLinkService extends YouTrackService {

    public static List<LinkYouTrackDto> getLinksByYouTrackIssueAndType(YouTrackIssueDto youTrackIssueDto, YouTrackLinkType youTrackLinkType) {
        if (youTrackIssueDto == null || youTrackLinkType == null || youTrackIssueDto.getLinks() == null) {
            return Collections.emptyList();
        }

        return youTrackIssueDto.getLinks().stream()
                .filter(link -> youTrackLinkType.name().equals(link.getDirection()))
                .filter(link -> link.getIssues() != null && !link.getIssues().isEmpty())
                .collect(Collectors.toList());
    }
}
