package org.example.enumeration;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@RequiredArgsConstructor
@Slf4j
public enum LinkType {
    RELATES_TO("Relates","Relates"),
    REQUIRED_FOR("Is required for","is required for"),
    DEPEND("Depend", "Depend"),
    DUPLICATED_BY("Is duplicated by","duplicates of"),
    DUPLICATES("Duplicate","Duplicate"),
    PARENT_FOR("Parent for","parent for"),
    CHILD_OF("Subtask","Subtask");

    private final String youTrackLinkType;
    private final String jiraLinkType;

    public String getName() {
        return name();
    }

    public static LinkType getLinkTypeByYouTrackLinkTypeName(@NonNull String youTrackLinkType) {
        for (LinkType linkType : values()) {
            if (youTrackLinkType.equalsIgnoreCase(linkType.getYouTrackLinkType())) {
                return linkType;
            }
        }
        log.error("No Link type with You Track type {} found", youTrackLinkType);
        return null;
    }

    public static LinkType getLinkTypeByJiraTypeName(@NonNull String jiraLinkType) {
        for (LinkType linkType : values()) {
            if (jiraLinkType.equalsIgnoreCase(linkType.getYouTrackLinkType())) {
                return linkType;
            }
        }
        log.error("No Link type with Jira type {} found", jiraLinkType);
        return null;
    }
}
