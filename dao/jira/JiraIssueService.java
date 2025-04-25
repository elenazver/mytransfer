package org.example.dao.jira;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.example.dao.AttachmentMigrationService;
import org.example.dao.youtrack.YouTrackDescriptionService;
import org.example.dao.youtrack.YouTrackIssueService;
import org.example.dao.youtrack.YouTrackPriorityService;
import org.example.dto.jira.JiraIssueDto;
import org.example.dto.jira.JiraPriorityDto;
import org.example.dto.jira.ProjectJiraDto;
import org.example.dto.jira.UserJiraDto;
import org.example.dto.jira.issuetype.JiraIssueTypeDto;
import org.example.dto.youtrack.CommentYouTrackDto;
import org.example.dto.youtrack.CustomFieldYouTrackDto;
import org.example.dto.youtrack.CustomFieldYouTrackValueDto;
import org.example.dto.youtrack.UserYouTrackDto;
import org.example.dto.youtrack.YouTrackIssueDto;
import org.example.enumeration.HttpRequestAgentType;
import org.example.enumeration.ProjectType;
import org.example.service.IssueSynchronizer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.example.constants.ProjectConstants.JIRA_URL;
import static org.example.service.IssueSynchronizer.MAPPER;
import static org.example.service.IssueSynchronizer.jiraPriorities;
import static org.example.service.IssueSynchronizer.jiraProjects;

@Slf4j
@RequiredArgsConstructor
public class JiraIssueService extends JiraService {

    private final JiraAttachmentService jiraAttachmentService;
    private final YouTrackIssueService youTrackIssueService;
    private final JiraPriorityService jiraPriorityService;

    private final static String DESCRIPTION_FIELD = "description";
    private final static String PRIORITY_FIELD = "priority";
    private final static String ASSIGNEE_FIELD = "assignee";
    private final static String PROJECT_FIELD = "project";
    private final static String SUMMARY_FIELD = "summary";

    /**
     * Creates a task in Jira based on data from YouTrack.
     * <p>
     * The method generates JSON-task representation, filling it with the appropriate
     * fields from the passed object {@link YouTrackIssueDto}. after successful completion
     * When creating a task, comments from YouTrack are added to Jira.
     * </p>
     *
     * @param youTrackIssueDto An object containing task data from YouTrack.
     */
    public Long createJiraIssueByYouTrackIssue(YouTrackIssueDto youTrackIssueDto, List<JiraIssueTypeDto> typesByProject) {
        String fieldsName = "fields";
        JSONObject payload = new JSONObject();
        JSONObject fields = new JSONObject();

        addDescriptionField(fields, youTrackIssueDto);
        addAssigneeName(fields, youTrackIssueDto);
        addSummaryField(fields, youTrackIssueDto);
        addIssueTypeField(fields, youTrackIssueDto, typesByProject);
        addProjectField(fields, youTrackIssueDto);
        addPriorityField(fields, youTrackIssueDto);
        payload.put(fieldsName, fields);

        return create(payload);
    }

    public JiraIssueDto getJiraIssueById(Long id) {
        String url = JIRA_URL + "/issue/" + id;
        return getOneObject(url, JiraIssueDto.class);
    }

    public void transitionIssue(Long issueId, String transitionId) {
        String url = JIRA_URL + "/issue/" + issueId + "/transitions";
        JSONObject payload = new JSONObject()
                .put("transition", new JSONObject().put("id", transitionId));
        log.info("PAYLOAD:{}, FOR ISSUE ID={}, TRANSITION_ID={}", payload.toString(), issueId, transitionId);
        httpService.postRequest(url, payload);
        log.info("TRANSITION COMPLETED");
    }

    public JiraIssueDto getJiraIssueByYouTrackIdReadable(String idReadable, List<JiraIssueDto> jiraIssues) {
        return jiraIssues.stream()
                .filter(jiraIssue -> jiraIssue.getFields().getSummary().contains("[" + idReadable + "]"))
                .findFirst().orElse(null);
    }

    /**
     * Adds attachments from YouTrack to a Jira task.
     *
     * @param createdTaskId    ID the created issue in Jira.
     * @param youTrackIssueDto A task object from YouTrack containing attachments.
     * @see JiraAttachmentService#addAttachmentsToIssue(Long, Map)
     */
    public void addAttachmentsToIssue(Long createdTaskId, YouTrackIssueDto youTrackIssueDto) {
        // List of Map attachments <URL, fileName>
        Map<String, String> attachments = AttachmentMigrationService.getAttachmentUrls(youTrackIssueDto);
        if (attachments == null || attachments.isEmpty()) {
            return;
        }

        jiraAttachmentService.addAttachmentsToIssue(createdTaskId, attachments);
    }

    /**
     * Sets the task description in Jira based on data from YouTrack.
     * <p>
     * If the description is missing from YouTrack, the "description" field will not be added.
     * </p>
     *
     * @param fields           JSON-an object with task fields Jira.
     * @param youTrackIssueDto Task object YouTrack.
     */
    private void addDescriptionField(JSONObject fields, YouTrackIssueDto youTrackIssueDto) {
        StringBuilder sb = new StringBuilder();
        String description = youTrackIssueDto.getDescription();
        log.info("DESCRIPTION: {}", description);
        List<CustomFieldYouTrackDto> customDescriptions =
                YouTrackDescriptionService.findIssueCustomTextField(youTrackIssueDto)
                        .orElse(Collections.emptyList());

        if ((description == null || description.isBlank()) && customDescriptions.isEmpty()) {
            log.warn("YouTrack issue description is missing, 'description' field will not be set");
            return;
        }

        if (description != null && !description.isEmpty()) {
            log.info("DESCRIPTION ADDED");
            sb.append(description);
        }

        if (!customDescriptions.isEmpty()) {
            for (CustomFieldYouTrackDto customDescription : customDescriptions) {
                sb.append("\n**").append(customDescription.getName()).append("**\n");
                log.info("CUSTOM DESCRIPTION NAME: {}", customDescription.getName());

                // Processing the field value 'value'
                if (customDescription.getValue() != null) {
                    log.info("VALUE: not null. Value: {}", customDescription.getValue());
                    CustomFieldYouTrackValueDto valueObject = customDescription.getValue();
                    if (valueObject.getText() != null) {
                        log.info("VALUE HAS TEXT: {}", valueObject.getText());
                        sb.append(valueObject.getText()).append("\n");
                    }
                }
            }
        }

        String convertedDescription = convertYouTrackTextToJira(sb.toString());
        log.info("CONVERTED FULL DESCRIPTION: {}", convertedDescription);
        fields.put(DESCRIPTION_FIELD, convertedDescription);
        log.info("Description added for Jira issue");
    }


    /**
     * Sets the task type in Jira based on data from YouTrack.
     * <p>
     * The method determines the task type from YouTrack and finds the corresponding type in Jira.
     * and sets it in the "issuetype" field. If no match is found, the field is not filled in.
     * </p>
     *
     * @param fields           JSON-an object with Jira task fields.
     * @param youTrackIssueDto Task object YouTrack.
     */
    private void addIssueTypeField(JSONObject fields, YouTrackIssueDto youTrackIssueDto, List<JiraIssueTypeDto> typesByProject) {
        String typeName = youTrackIssueService.getTypeByIssue(youTrackIssueDto).equalsIgnoreCase("user story") ? "Story" : youTrackIssueService.getTypeByIssue(youTrackIssueDto);
        log.info("Type name in YouTrack Issue = {}", typeName);
        if (typeName != null) {
            log.info("Jira Types contains type with name {}", typeName);
            for (JiraIssueTypeDto type : typesByProject) {
                if (typeName.equalsIgnoreCase(type.getName())) {
                    fields.put("issuetype", new JSONObject().put("name", type.getName()));
                    System.out.println("ISSUE TYPE ADDED TO CREATING ISSUE:\n" + type.getId() + " name: " + type.getName());
                }
            }
        }
    }

    /**
     * Sets the priority of a task in Jira based on YouTrack data.
     * <p>
     * The method determines the priority of the task from YouTrack, checks its presence in Jira,
     * and if the priority exists, sets it in the "priority" field.
     * Otherwise, the priority is "Normal".
     * </p>
     *
     * @param fields           JSON-an object with Jira task fields.
     * @param youTrackIssueDto Task object YouTrack.
     */
    private void addPriorityField(JSONObject fields, YouTrackIssueDto youTrackIssueDto) {
        // We get the priority name from YouTrack
        String priorityName = YouTrackPriorityService.getPriorityNameByYouTrackIssue(youTrackIssueDto);
        if (priorityName == null) {
            log.warn("YouTrack issue priority is missing, default priority (Normal) will be set");
            priorityName = "Normal";
        }

        // Checking for priority in Jira
        boolean exists = jiraPriorityService.isPriorityExists(priorityName, jiraPriorities);
        log.info("Priority '{}' {} in Jira", priorityName, exists ? "exists" : "does not exist. Normal priority will be set");

        // We get the priority ID (if not found, "Normal" is used)
        String priorityId = Optional.ofNullable(jiraPriorityService.getByName(priorityName))
                .map(priority -> priority.getId().toString())
                .orElseGet(
                        () -> {
                            JiraPriorityDto createdPriority = jiraPriorityService.createJiraPriorityIfNotExists(youTrackIssueDto, jiraPriorities);
                            return createdPriority.getId().toString();
                        });
        // Setting the priority
        fields.put(PRIORITY_FIELD, new JSONObject().put("id", priorityId));
    }

    /**
     * Assigns an (assignee) for a task in Jira based on YouTrack data.
     * <p>
     * The method extracts the artist's name from YouTrack, searches for a match among Jira users
     * and sets it in the "assignee" field. If no match is found, it is assigned
     * the default user.
     *
     * @param fields           JSON-an object with Jira task fields.
     * @param youTrackIssueDto Task object YouTrack.
     */
    private void addAssigneeName(JSONObject fields, YouTrackIssueDto youTrackIssueDto) {
        String defaultUserId = "712020:8f8b6a95-e606-4ee8-b34f-a15ead2f9389";

        String assigneeName = youTrackIssueDto.getCustomFields().stream()
                .filter(field -> "Assignee".equalsIgnoreCase(field.getName()))
                .findFirst()
                .flatMap(field -> {
                    Object value = field.getValue();
                    if (value instanceof List<?> list && !list.isEmpty() && list.get(0) instanceof CustomFieldYouTrackValueDto) {
                        return Optional.of((CustomFieldYouTrackValueDto) list.get(0));
                    }
                    return Optional.empty();
                })
                .map(CustomFieldYouTrackValueDto::getName)
                .orElse(null);
        log.info("Assignee name from YouTrack Issue: {}", assigneeName);
        if (assigneeName == null) {
            log.info("assignee name: null");
            fields.put(ASSIGNEE_FIELD, new JSONObject().put("id", defaultUserId));
        } else {
            Optional<UserJiraDto> jiraUserOpt = IssueSynchronizer.jiraUsersAssignableToProject.stream()
                    .filter(jiraUser -> normalizeName(assigneeName)
                            .equalsIgnoreCase(normalizeName(jiraUser.getDisplayName())))
                    .findFirst();
            jiraUserOpt.ifPresent(userJiraDto -> log.info("Optional<UserJiraDto> {}", userJiraDto));
            // We assign the found user or the default ID
            String assigneeId = jiraUserOpt.map(UserJiraDto::getAccountId)
                    .orElse(defaultUserId);
            log.info("User assigned ID = {}", assigneeId);

            fields.put(ASSIGNEE_FIELD, new JSONObject().put("id", assigneeId));
        }
    }

    /**
     * Assigns a project to a task in Jira based on YouTrack data.
     * <p>
     * The method identifies the YouTrack project and searches for the corresponding project in Jira
     * and sets it in the "project" field. If the project is not found, the field remains empty.
     * </p>
     *
     * @param fields           JSON-an object with Jira task fields.
     * @param youTrackIssueDto Task object YouTrack.
     */
    private void addProjectField(JSONObject fields, YouTrackIssueDto youTrackIssueDto) {
        // Defining the project YouTrack
        ProjectType youTrackProject = ProjectType.getProjectByYouTrackKey(youTrackIssueDto);
        if (youTrackProject == null) {
            return; // If the project is not found, exit
        }

        String jiraKey = youTrackProject.getJiraKey();
        // We are looking for the corresponding project in Jira and adding it to JSON.
        jiraProjects.stream()
                .filter(jiraProject -> jiraProject.getKey().equalsIgnoreCase(jiraKey))
                .findFirst()
                .map(ProjectJiraDto::getId)
                .ifPresent(projectId -> fields.put(PROJECT_FIELD, new JSONObject().put("id", projectId)));
    }

    /**
     * Sets the title (summary) of the task in Jira based on data from YouTrack.
     * <p>
     * The title is formed in the format: "[Issue ID in YouTrack] The task title".
     * If there is no header, only the task ID will be used.
     * </p>
     *
     * @param fields           JSON-an object with Jira task fields.
     * @param youTrackIssueDto Task object YouTrack.
     */
    private void addSummaryField(JSONObject fields, YouTrackIssueDto youTrackIssueDto) {
        String summary = youTrackIssueDto.getSummary();
        String issueId = youTrackIssueDto.getIdReadable();

        if (summary == null || summary.isBlank()) {
            log.warn("YouTrack issue title is missing, only the ID will be used");
            fields.put(SUMMARY_FIELD, String.format("[%s]", issueId));
        } else {
            fields.put(SUMMARY_FIELD, String.format("[%s] %s", issueId, summary));
        }
        log.info("Added title for Jira issue: [{}] {}", issueId, summary);
    }

    /**
     * Adds comments from YouTrack to the Jira task.
     * <p>
     * If there are no comments in YouTrack, the method terminates execution,
     * by writing the corresponding message to the log. Otherwise
     * each comment is converted into a JSON-object and sent to Jira.
     * </p>
     *
     * @param jiraIssueId      ID of the Jira issue to add comments to.
     * @param youTrackIssueDto is an object containing information about a task in YouTrack, including comments.
     */
    public void addCommentsToIssue(Long jiraIssueId, YouTrackIssueDto youTrackIssueDto) {
        LinkedList<CommentYouTrackDto> comments = new LinkedList<>(youTrackIssueDto.getComments());
        if (comments.isEmpty()) {
            return;
        }
        String url = JIRA_URL + "/issue/" + jiraIssueId + "/comment";
        int commentCount = 0;
        int maxLength = 32000;

        // We go through all the comments
        for (int i = 0; i < comments.size(); i++) {
            CommentYouTrackDto originalComment = comments.get(i);
            String commentBody = convertYouTrackTextToJira(originalComment.getText());

            if (commentBody.length() > maxLength) {
                log.error("THE COMMENT EXCEEDS {} SYMBOLS. LENGTH {}", maxLength, commentBody.length());
                List<CommentYouTrackDto> parts = new LinkedList<>();

                int partCount = 0;
                // We divide the long comment into parts
                for (int start = 0; start < commentBody.length(); start += maxLength) {
                    ++partCount;
                    int end = Math.min(start + maxLength, commentBody.length());
                    String partCommentBody = commentBody.substring(start, end);

                    // Creating a copy of the original comment
                    CommentYouTrackDto partComment = new CommentYouTrackDto();
                    partComment.setAuthor(originalComment.getAuthor());
                    partComment.setText("Part " + partCount + ": " + partCommentBody);

                    // Adding a part to the list
                    parts.add(partComment);
                }

                log.info("PARTS OF THE COMMENT {}", partCount);

                // We insert the parts immediately after the original comment
                comments.addAll(i + 1, parts);

                // Deleting the original long comment
                comments.remove(i);

                // Shifting the index to skip the inserted parts
                i += parts.size() - 1;
            }
        }
        log.info("All comments after normalization: ");
        for (CommentYouTrackDto comment : comments) {
            if (comment.getText() != null) {
                log.info("COMMENT SIZE: {}", comment.getText().length());
            } else {
                log.error("THE COMMENT HAS NOT BEEN INITIALIZED!");
            }
        }

        // We are sending all comments to Jira
        for (CommentYouTrackDto comment : comments) {
            String authorEmail = findUserEmail(comment.getAuthor().getFullName()).orElse("unknown");
            String formattedBody = String.format(
                    "[%s, email: %s] %s",
                    comment.getAuthor().getFullName(),
                    authorEmail,
                    comment.getText()
            );

            // We check the length before sending
            if (formattedBody.length() > 32767) {
                log.error("THE COMMENT IS TOO LONG AFTER FORMATTING. LENGTH {}", formattedBody.length());
                continue;
            }

            JSONObject commentJson = new JSONObject();
            commentJson.put("body", formattedBody);

            httpService.postRequest(url, commentJson);
            commentCount++;
        }

        // Logging the number of added comments
        log.info("Added {} comments to Jira issue with id={}", commentCount, jiraIssueId);
    }


    /**
     * Converts the text of a comment or description (hereinafter referred to as the text) from YouTrack to a format suitable for Jira, with the replacement of links to images.
     * <p>
     * The method searches the YouTrack text for patterns of the form `![](file){width=**%}`, extracts the file name
     * and replaces it with the syntax used in Jira: `[^filename]`.
     *
     * @param youTrackText text in YouTrack format containing links to images.
     * @return Text in Jira format, where links to images are replaced with the format `[^filename]`.
     * <p>
     * Example:
     * <pre>
     * String youTrackText = "Description with image ![](file.jpg){width=100%}";<br>
     * String jiraText = convertYouTrackToJira(youTrackComment);<br>
     * // Result: "Description with image [^file.jpg]"<br>
     * </pre>
     */
    private static String convertYouTrackTextToJira(String youTrackText) {
        if (youTrackText == null || youTrackText.isBlank()) {
            return "";
        }

        // A regular expression for searching ![](file){width=**%}
        Pattern pattern = Pattern.compile("!\\[\\]\\(([^)]+)\\)(\\{[^}]+\\})?");
        Matcher matcher = pattern.matcher(youTrackText);

        if (!matcher.find()) {
            return youTrackText;
        }

        // Replacing it with [^filename.***]
        StringBuilder jiraText = new StringBuilder();
        while (matcher.find()) {
            String fileName = matcher.group(1);
            matcher.appendReplacement(jiraText, "[^" + fileName + "]");
        }
        matcher.appendTail(jiraText);

        return jiraText.toString();
    }

    /**
     * Finds the email address of the comment author in the list of YouTrack users.
     * If the user is not found, returns null.
     *
     * @param userFullName is the full name of the comment author.
     * @return Optional Email of the author.
     */
    private Optional<String> findUserEmail(String userFullName) {
        return IssueSynchronizer.youTrackUsers.stream()
                .filter(user -> user.getFullName().equalsIgnoreCase(userFullName))
                .map(UserYouTrackDto::getEmail)
                .findFirst();
    }

    /**
     * Normalizes the user's name by sorting and combining words in alphabetical order.
     * <p>
     * The method performs the following steps:
     * <ul>
     *     <li>Separates a string by spaces</li>
     *     <li>Removes extra spaces (trim)</li>
     *     <li>Sorts words alphabetically, case-insensitive</li>
     *     <li>Combines words back into a string separated by a space</li>
     * </ul>
     * This allows you to compare names without taking into account the word order and unnecessary spaces.
     * </p>
     *
     * @param fullName is the full name of the user.
     * @return A normalized name or an empty string if the input data is incorrect.
     */
    private String normalizeName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return "";
        }

        return Arrays.stream(fullName.trim().split("\\s+"))
                .sorted(Comparator.comparing(String::toLowerCase))
                .collect(Collectors.joining(" "));
    }

    /**
     * Sends an HTTP-request to create a task in Jira with the transmitted JSON payload.
     * <p>
     * The method performs the following steps:
     * <ul>
     *     <li>Sends a POST-request to Jira with data about the new task.</li>
     *     <li>Processes the response and checks for the presence of the "id" and "key" fields.</li>
     *     <li>Logs the successful creation of a task or an error if the fields are missing.</li>
     * </ul>
     * If the request is completed successfully, returns the ID of the created task.
     * Returns {@code null} in case of an error.
     * </p>
     *
     * @param payload is a JSON-object containing data for creating a task in Jira.
     * @return ID of the created task or {@code null} if creation failed.
     */
    private Long create(JSONObject payload) {
        String url = JIRA_URL + "/issue";
        JSONObject response = httpService.postRequest(url, payload);
        if (response == null) {
            log.error("Empty response from Jira. The issue was not created");
            return null;
        }

        if (!response.has("id") || !response.has("key")) {
            log.error("Error creating issue in Jira: missing field '{}'",
                    response.has("id") ? "key" : "id");
            return null;
        }

        try {
            return Long.parseLong(response.getString("id"));
        } catch (NumberFormatException e) {
            log.error("Error converting Jira issue ID: {}", response.getString("id"), e);
            return null;
        }
    }


    /**
     * Gets all tasks from Jira with page-by-page navigation.
     * <p>
     * The method executes GET requests to the Jira API, requesting tasks in chunks of 100.
     * Requests continue until all tasks are received.
     * In case of an error in the request or response parsing, the method logs the problem and completes execution.
     * </p>
     *
     * @return A list of all tasks in Jira as {@link List} from {@link JiraIssueDto}.
     * @see JiraIssueDto
     */
    public List<JiraIssueDto> getAllIssuesFromJira() {
        List<JiraIssueDto> jiraIssues = new ArrayList<>();
        String urlTemplate = JIRA_URL + "/search?startAt=%d&limit=%d&fields=%s";
        String fields = "id,key,summary,description,issuetype,priority,status,reporter,assignee,created,updated,attachment,issuelinks,project";
        int offset = 0;
        int limit = 100;

        try {
            while (true) {

                System.out.print(".");
                if (jiraIssues.size() > 1 && jiraIssues.size() % 5000 == 0) {
                    System.out.println();
                    System.out.print(".");
                }

                String url = String.format(urlTemplate, offset, limit, fields);
                HttpResponse response = httpService.getRequest(url, HttpRequestAgentType.JIRA_AGENT);

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    log.error("Jira request error: HTTP {}", statusCode);
                    break;
                }

                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                JSONObject jsonResponse = new JSONObject(responseBody);
                JSONArray issues = jsonResponse.optJSONArray("issues");

                if (issues == null || issues.isEmpty()) {
                    log.debug("All issues retrieved, current offset: {}", offset);
                    break;
                }

                List<JiraIssueDto> currentBatch = MAPPER.readValue(
                        issues.toString(),
                        MAPPER.getTypeFactory().constructCollectionType(List.class, JiraIssueDto.class)
                );

                jiraIssues.addAll(currentBatch);
                log.debug("Retrieved {} issues, new offset: {}", currentBatch.size(), offset + issues.length());

                offset += issues.length();
            }
        } catch (IOException e) {
            log.error("Error reading Jira response: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error processing issues from Jira: {}", e.getMessage(), e);
        }
        System.out.println();
        return jiraIssues;
    }

    /**
     * Filters the Jira task list by the project selected by the user.
     * <p>
     * The method selects tasks for which the key (ID) starts with the Jira key of the corresponding project.
     * In case of incorrect project selection, an empty list is returned.
     *
     * @param projectUserChoice The project number entered by the user.
     * @param issueDTOJiraList  Complete list of tasks in JiraIssueDto list format.
     * @return A filtered task list for the specified project, or an empty list if the project is not found.
     * @throws IllegalArgumentException if the task list is {@code null}.
     */
    public List<JiraIssueDto> getAllJiraIssuesByProject(int projectUserChoice, List<JiraIssueDto> issueDTOJiraList) {
        if (issueDTOJiraList == null) {
            throw new IllegalArgumentException("Task list issueDTOJiraList it can't be null");
        }

        ProjectType project = ProjectType.getProjectBySequence(projectUserChoice);

        String projectKey = project.getJiraKey();

        return issueDTOJiraList.stream()
                .filter(issue -> {
                    if (issue.getKey() == null) {
                        log.warn("Skipped issue with null key: {}", issue);
                        return false;
                    }
                    return issue.getKey().startsWith(projectKey);
                })
                .collect(Collectors.toList());
    }

    public void rollbackJiraIssue(Long id) {
        String url = JIRA_URL + "/issue/" + id;
        httpService.delete(url, HttpRequestAgentType.JIRA_AGENT);
    }
}
