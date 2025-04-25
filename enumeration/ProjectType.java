package org.example.enumeration;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.youtrack.ProjectYouTrackDto;
import org.example.dto.youtrack.YouTrackIssueDto;

@Getter
@RequiredArgsConstructor
@Slf4j
/*
  The system directory of projects available for synchronization operations.
  If the list is expanded, it should be supplemented with new values.
 */
public enum ProjectType {

    MWTES("MW", "MWTES", 1), //MWTES
    HDK("HELPDESK", "HDK", 2), //HDK
    MBO("BO", "YTJ4", 3), //MBT
    MDOT("MDO", "YTJ", 4),//MDOT
    MITT("MI", "MITT", 5), //MITT
    MWB("MWeb", "YTJ3", 6), //MWB
    TRNTES("TRN", "YTJ2", 7), //TRNTES in YouTrack 117 project objectives TRN
    TEST_LINK("MAS", "YTJMS", 8);

    private final String youTrackKey;
    private final String jiraKey;
    private final int sequence;

    public String getName() {
        return name();
    }


    public static ProjectType getProjectBySequence(int sequence) {
        for (ProjectType project : values()) {
            if (project.getSequence() == sequence) {
                return project;
            }
        }
        throw new IllegalArgumentException("No ProjectType with sequence " + sequence + " found.");
    }

    /**
     * For a task from YouTrack, it gets ProjectJiraDto if it matches, or null if there is no match.
     *
     * @param youTrackIssueDto specific task YouTrack
     * @return value {@link ProjectType} or 'null'
     */
    public static ProjectType getProjectByYouTrackKey(@NonNull YouTrackIssueDto youTrackIssueDto) {
        ProjectYouTrackDto project = youTrackIssueDto.getProject();
        if (project != null) {
            String projectName = project.getShortName();
            for (ProjectType projectType : values()) {
                if (projectType.getYouTrackKey().equalsIgnoreCase(projectName)) {
                    return projectType;
                }
            }
            log.error("No ProjectType with key {} found", projectName);
        } else {
            log.error("YouTrack Issue not contains project");
        }
        return null;
    }
}
