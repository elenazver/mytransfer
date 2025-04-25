package org.example.dao.youtrack;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.example.constants.ProjectConstants;
import org.example.dao.HttpService;
import org.example.dto.youtrack.CustomFieldYouTrackDto;
import org.example.dto.youtrack.CustomFieldYouTrackValueDto;
import org.example.dto.youtrack.YouTrackIssueDto;
import org.example.enumeration.HttpRequestAgentType;
import org.example.enumeration.ProjectType;
import org.json.JSONArray;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.example.constants.ProjectConstants.YOUTRACK_URL;
import static org.example.service.IssueSynchronizer.MAPPER;

@Slf4j
public class YouTrackIssueService extends YouTrackService {

    /**
     * The method receives all tasks for all projects from YOUTRACK, including:<br>
     * <ul>
     *     <li>connections;</li>
     *     <li>designated persons;</li>
     *     <li>attachments.</li>
     * </ul>
     * To receive tasks, use the url generated in accordance with <a href="https://www.jetbrains.com/help/youtrack/devportal/api-query-syntax.html#samples">Query Syntax YouTrack</a><br>
     * To add/exclude new, modified or redundant fields, you must:
     * <ol>
     *     <li>Make changes to String url (fields=...) the current method</li>
     *     <li>Make changes to the corresponding dto in the package dto.youtrack</li>
     *     <li>If necessary, write a custom mapper</li>
     * </ol>
     *
     * @see HttpService#getRequest(String, HttpRequestAgentType)
     */
    public List<YouTrackIssueDto> getAllIssuesFromYourTrack() {
        List<YouTrackIssueDto> allIssues = new ArrayList<>();
        int skip = 0;
        int top = 50;

        while (true) {
            String url = YOUTRACK_URL + "/issues?fields=id,idReadable,summary,description,created," +
                    "customFields(name,value(name,id,text)),comments(text,author(login,fullName,email))," +
                    "attachments(name,url),links(direction,linkType(name,localizedName),issues(idReadable,summary))," +
                    "project(name,shortName,ringId,archived)&$skip=" + skip + "&$top=" + top;

            System.out.print(".");
            if (allIssues.size() > 1 && allIssues.size() % 5000 == 0) {
                System.out.println();
                System.out.print(".");
            }

            try (CloseableHttpResponse response = httpService.getRequest(url, HttpRequestAgentType.YOUR_TRACK_AGENT)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    log.error("Error HTTP {} when requesting to YouTrack", statusCode);
                    throw new RuntimeException("Error HTTP " + statusCode + " when loading tasks from YouTrack");
                }

                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                JSONArray issues = new JSONArray(responseBody);

                if (issues.isEmpty()) {
                    break; // If there is no more data, exit the loop
                }

                List<YouTrackIssueDto> issueDTOYouTrackList = MAPPER.readValue(
                        issues.toString(), MAPPER.getTypeFactory().constructCollectionType(List.class, YouTrackIssueDto.class)
                );

                allIssues.addAll(issueDTOYouTrackList);
                log.debug("loaded {} issues, total: {}", issueDTOYouTrackList.size(), allIssues.size());

                skip += top; // Skip to the next page

            } catch (IOException e) {
                log.error("Request reading error: {}", e.getMessage(), e);
                break;
            } catch (Exception e) {
                log.error("Error parsing the JSON-response: {}", e.getMessage(), e);
                break;
            }
        }
        System.out.println();
        return allIssues;
    }

    /**
     * Filters YouTrack issues by project
     *
     * @param projectUserChoice    It takes as a parameter {@link ProjectType#getSequence()}
     * @param issueDTOYouTrackList list of all {@link YouTrackIssueDto}
     * @return a filtered list of all {@link YouTrackIssueDto}
     */
    public List<YouTrackIssueDto> getAllYouTrackIssuesByProject(int projectUserChoice, List<YouTrackIssueDto> issueDTOYouTrackList) {
        return issueDTOYouTrackList.stream()
                .filter(issueYouTrackDto -> issueYouTrackDto.getProject().getShortName().equalsIgnoreCase(ProjectType.getProjectBySequence(projectUserChoice).getYouTrackKey()))
                .toList();
    }

    public String getTypeByIssue(YouTrackIssueDto youTrackIssueDto) {
        CustomFieldYouTrackDto customFieldYouTrackDto = youTrackIssueDto.getCustomFields().stream()
                .filter(field -> field.getName().startsWith(ProjectConstants.TYPE_FIELD_NAME))
                .findFirst().orElse(null);
        if (customFieldYouTrackDto == null) {
            return "Task";
        }

        CustomFieldYouTrackValueDto value = customFieldYouTrackDto.getValue();
        return value.getName();
    }
}
