package org.example.dao.jira;

import org.example.dto.jira.ProjectJiraDto;
import org.example.enumeration.ProjectType;

import java.util.List;

import static org.example.constants.ProjectConstants.JIRA_URL;

public class JiraProjectService extends JiraService {

    /**
     * Collects all projects {@link ProjectJiraDto} from Jira
     *
     * @return list {@link ProjectJiraDto}
     */
    public List<ProjectJiraDto> getAllJiraProjects() {
        String url = String.format("%s/project", JIRA_URL);
        return fetchFromJira(url, ProjectJiraDto.class);
    }

    /**
     * Gets the DTO project's {@link ProjectJiraDto} by sequence in {@link ProjectType}
     * Important! The first found project is returned with the corresponding key
     *
     * @param sequence {@link ProjectType} the project ID in the enum
     * @param projects list of projects {@link ProjectJiraDto}
     * @return {@link ProjectJiraDto}. If the project is missing from projects, returns 'null'
     */
    public ProjectJiraDto getJiraProjectByProjectTypeSequence(int sequence, List<ProjectJiraDto> projects) {
        String jiraKey = ProjectType.getProjectBySequence(sequence).getJiraKey();
        return projects.stream()
                   .filter(project -> jiraKey.equalsIgnoreCase(project.getKey()))
                   .findFirst()
                   .orElse(null);
    }
}
