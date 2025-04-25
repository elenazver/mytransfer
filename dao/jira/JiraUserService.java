package org.example.dao.jira;

import org.example.dto.jira.UserJiraDto;
import org.example.enumeration.ProjectType;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.example.constants.ProjectConstants.JIRA_URL;

public class JiraUserService extends JiraService {
    public List<UserJiraDto> getAllJiraUsers() {
        String url = String.format("%s/users", JIRA_URL);
        return fetchFromJira(url, UserJiraDto.class);
    }

    public List<UserJiraDto> getJiraUsersAssignableToProject(ProjectType project) {
        String projectKey = project.getJiraKey();
        String encodedProjectKey = URLEncoder.encode(projectKey, StandardCharsets.UTF_8);
        String url = String.format("%s/user/assignable/multiProjectSearch?projectKeys=%s", JIRA_URL, encodedProjectKey);
        return fetchFromJira(url, UserJiraDto.class);
    }
}
