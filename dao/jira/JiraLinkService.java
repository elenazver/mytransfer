package org.example.dao.jira;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import static org.example.constants.ProjectConstants.JIRA_URL;

@Slf4j
public class JiraLinkService extends JiraService {
    public void create(JSONObject payload) {
        String url = JIRA_URL + "/issueLink";
        httpService.postRequest(url, payload);
    }
}
