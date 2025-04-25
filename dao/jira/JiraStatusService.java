package org.example.dao.jira;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.jira.issuestatus.IssueStatusJiraDto;
import org.example.dto.jira.issuestatus.StatusJiraDto;
import org.example.dto.jira.transition.JiraTransitionsResponseDto;

import java.util.ArrayList;
import java.util.List;

import static org.example.constants.ProjectConstants.JIRA_URL;

@Slf4j
public class JiraStatusService extends JiraService {

    public List<StatusJiraDto> getAllJiraStatuses() {
        String url = String.format("%s/statuses/search", JIRA_URL);
        return new ArrayList<>(fetchObjectFromJira(url, IssueStatusJiraDto.class).getValues());
    }

    public JiraTransitionsResponseDto getJiraTransitionsByJiraIssueId(Long jiraIssueId) {
        String url = String.format("%s/issue/%d/transitions", JIRA_URL, jiraIssueId);
        return fetchObjectFromJira(url, JiraTransitionsResponseDto.class);
    }
}
