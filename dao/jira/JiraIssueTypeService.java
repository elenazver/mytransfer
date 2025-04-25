package org.example.dao.jira;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.jira.issuetype.JiraIssueTypeDto;
import org.example.dto.jira.issuetype.ProjectDto;
import org.example.dto.jira.issuetype.ScopeDto;
import org.json.JSONObject;

import java.util.List;

import static org.example.constants.ProjectConstants.JIRA_URL;

@Slf4j
public class JiraIssueTypeService extends JiraService {
    private static final String BASE_SERVICE_URL = String.format("%s/issuetype", JIRA_URL);

    /**
     * Gets all types of tasks from the Jira API.
     *
     * @return A list of all types of tasks in Jira as a list of objects JiraIssueTypeDto
     */
    public List<JiraIssueTypeDto> getAllJiraIssueTypes() {
        return fetchFromJira(BASE_SERVICE_URL, JiraIssueTypeDto.class);
    }

    /**
     * Gets the Jira Issue type by name from the list of types
     *
     * @param name  name IssueType
     * @param types list types
     * @return {@link JiraIssueTypeDto}
     */
    public JiraIssueTypeDto getIssueTypeByName(String name, List<JiraIssueTypeDto> types) {
        if (name == null || types == null) {
            return null;
        }

        return types.stream()
                .filter(type -> name.equalsIgnoreCase(type.getName()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Gets all schemas of types linked to Projects
     *
     * @return {@code List<JiraIssueTypeSchemaDto>}
     */
    public List<JiraIssueTypeDto> getAllJiraIssueTypesByProjectId(Long projectId) {
        return fetchFromJira(String.format("%s/project?projectId=%d", BASE_SERVICE_URL, projectId), JiraIssueTypeDto.class);
    }

    /**
     * Creates IssueType
     *
     * @param typeName  name of the one being created {@link JiraIssueTypeDto}
     * @param projectId  the project ID to put it in the scope id to bind the issueType to a specific project
     * @return {@link JiraIssueTypeDto}
     */
    public JiraIssueTypeDto createIssueType(String typeName, Long projectId) {
        String url = JIRA_URL + "/issuetype";
        JSONObject payload = new JSONObject();
        payload.put("description", "Issue Type created by YouTrack Jira migration");
        payload.put("name", typeName);

        if (projectId != null) {
            JSONObject scope = new JSONObject();
            JSONObject project = new JSONObject();
            project.put("id", projectId);
            scope.put("project", project);
            payload.put("scope", scope);
        }
        log.info("Payload: {}", payload);

        JSONObject response = httpService.postRequest(url, payload);
        log.info("ISSUE TYPE CREATED: {}", response.toString());

        JiraIssueTypeDto jiraIssueTypeDto = new JiraIssueTypeDto();
        jiraIssueTypeDto.setId(Long.parseLong(response.get("id").toString()));
        jiraIssueTypeDto.setName(response.get("name").toString());

        if (response.has("scope")) {
            JSONObject scopeJson = response.getJSONObject("scope");
            ScopeDto scopeDto = new ScopeDto();

            if (scopeJson.has("project")) {
                JSONObject projectJson = scopeJson.getJSONObject("project");
                ProjectDto projectDto = new ProjectDto();
                projectDto.setId(projectJson.getLong("id"));
                scopeDto.setProject(projectDto);
            }

            jiraIssueTypeDto.setScope(scopeDto);
        }
        return jiraIssueTypeDto;
    }
}
