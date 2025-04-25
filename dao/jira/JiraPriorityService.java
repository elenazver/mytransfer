package org.example.dao.jira;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.example.constants.ProjectConstants;
import org.example.dao.youtrack.YouTrackPriorityService;
import org.example.dto.jira.JiraPriorityDto;
import org.example.dto.youtrack.CustomFieldYouTrackDto;
import org.example.dto.youtrack.CustomFieldYouTrackValueDto;
import org.example.dto.youtrack.YouTrackIssueDto;
import org.example.service.IssueSynchronizer;
import org.json.JSONObject;

import java.util.List;

import static org.example.constants.ProjectConstants.JIRA_URL;

@Slf4j
public class JiraPriorityService extends JiraService {
    private static final String BASE_SERVICE_URL = String.format("%s/priority", JIRA_URL);

    /**
     * Creating a priority in Jira based on a task from YouTrack, if it does not exist.
     *
     * @param youTrackIssueDto a task from YouTrack
     * @param jiraPriorityDtos priority list in Jira
     */
    public JiraPriorityDto createJiraPriorityIfNotExists(YouTrackIssueDto youTrackIssueDto, List<JiraPriorityDto> jiraPriorityDtos) {
        return YouTrackPriorityService.findPriorityField(youTrackIssueDto)
                .map(field -> processPriorityField(field, jiraPriorityDtos))
                .orElseGet(() -> {
                    log.error("The field with the name '{}' not found in the issue YouTrack", ProjectConstants.PRIORITY_FIELD_NAME);
                    return null;
                });
    }


    /**
     * Processes the field "Priority" and creates a priority in Jira, if it doesn't exist.
     *
     * @param field          the "Priority" field from YouTrack
     * @param jiraPriorities priority list in Jira
     */
    private JiraPriorityDto processPriorityField(CustomFieldYouTrackDto field, List<JiraPriorityDto> jiraPriorities) {
        CustomFieldYouTrackValueDto value = field.getValue();
        String priorityName = value.getName();
        String priorityDescription = value.getDescription();

        if (!isPriorityExists(priorityName, jiraPriorities)) {
            return createPriority(priorityName, priorityDescription);
        }
        return null;
    }

    /**
     * Creates a priority in Jira. In addition to the name and status description, the color is set: HEX #ff0000 by default
     * due to the lack of requirements. The field is required
     *
     * @param name        priority name (required field)
     * @param description priority description
     */
    private JiraPriorityDto createPriority(String name, String description) {
        JSONObject payload = new JSONObject();
        payload.put("name", name);
        payload.put("description", description);
        payload.put("statusColor", "#ff0000");
        payload.put("iconUrl", "https://play-lh.googleusercontent.com/ix1Fwe9SdugCTd2Y8aYS1A6UY9t45Z7D9Kch-vrTVtw3Vk5pgt1JPIR3BNTyfxNhpg");
        JSONObject response = httpService.postRequest(BASE_SERVICE_URL, payload);

        if (response != null && response.has("id")) {
            Long createdId = Long.valueOf(response.get("id").toString());
            JiraPriorityDto createdPriority = getById(createdId);
            IssueSynchronizer.jiraPriorities.add(createdPriority); //To remove such a terrible connectedness.
            return createdPriority;
        } else {
            log.error("Error when creating a priority: the response from Jira is empty or does not contain 'id'");
            return null;
        }
    }

    /**
     * Checks if there is a priority in Jira by name.
     *
     * @param priorityName   priority name
     * @param jiraPriorities priority list Jira
     * @return true, if priority exists, otherwise false
     */
    public boolean isPriorityExists(String priorityName, @NonNull List<JiraPriorityDto> jiraPriorities) {
        return jiraPriorities.stream()
                .anyMatch(priority -> priority.getName().equalsIgnoreCase(priorityName));
    }

    /**
     * Gets all priorities from Jira.
     *
     * @return priority list
     */
    public List<JiraPriorityDto> getAllJiraPriorities() {
        return fetchFromJira(BASE_SERVICE_URL, JiraPriorityDto.class);
    }

    /**
     * Gets Jira priority by name.
     * <p>
     * The method searches for priority in the list of `jiraPriorities` by comparing the name of each element with the passed
     * parameter `name` case-insensitive. If a priority with that name is found, the first one is returned.
     * A suitable element. If a priority with this name is not found, `null` is returned.
     *
     * @param name The name of the priority to be found.
     * @return Object of type {@link JiraPriorityDto}, representing the found Jira priority, or `null`,
     *  if the priority with the specified name is not found.
     * <p>
     * Example:
     * <pre>
     * String name = "High";
     * JiraPriorityDto priority = getByName(name);
     * // Result: priority will contain a priority named "High" or null if no such priority is found.
     * </pre>
     */
    public JiraPriorityDto getByName(String name) {
        return IssueSynchronizer.jiraPriorities.stream()
                .filter(jiraPriorityDto -> jiraPriorityDto.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);  // Returns null, if the element is not found.
    }

    public JiraPriorityDto getById(Long id) {
        if (id == null) {
            log.error("An attempt to request Priority from id = null");
            return null;
        }
        String url = BASE_SERVICE_URL + "/" + id;
        return fetchObjectFromJira(url, JiraPriorityDto.class);
    }
}
