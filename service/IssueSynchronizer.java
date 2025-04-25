package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.example.dao.jira.JiraAttachmentService;
import org.example.dao.jira.JiraIssueService;
import org.example.dao.jira.JiraIssueTypeService;
import org.example.dao.jira.JiraLinkService;
import org.example.dao.jira.JiraPriorityService;
import org.example.dao.jira.JiraProjectService;
import org.example.dao.jira.JiraService;
import org.example.dao.jira.JiraStatusService;
import org.example.dao.jira.JiraUserService;
import org.example.dao.youtrack.YouTrackIssueService;
import org.example.dao.youtrack.YouTrackLinkService;
import org.example.dao.youtrack.YouTrackService;
import org.example.dao.youtrack.YouTrackStateService;
import org.example.dto.jira.JiraIssueDto;
import org.example.dto.jira.JiraPriorityDto;
import org.example.dto.jira.ProjectJiraDto;
import org.example.dto.jira.UserJiraDto;
import org.example.dto.jira.issuestatus.StatusJiraDto;
import org.example.dto.jira.issuetype.JiraIssueTypeDto;
import org.example.dto.jira.transition.JiraTransitionDto;
import org.example.dto.jira.transition.JiraTransitionsResponseDto;
import org.example.dto.youtrack.IssueLinkYouTrackDto;
import org.example.dto.youtrack.LinkYouTrackDto;
import org.example.dto.youtrack.UserYouTrackDto;
import org.example.dto.youtrack.YouTrackIssueDto;
import org.example.enumeration.LinkType;
import org.example.enumeration.ProjectType;
import org.example.enumeration.YouTrackLinkType;
import org.example.util.CsvReader;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * A service for synchronizing tasks between YouTrack and Jira.
 * Provides methods for uploading and filtering tasks, as well as retrieving priorities, statuses, and projects from Jira.
 */
@Slf4j
@NoArgsConstructor
public class IssueSynchronizer {
    private final YouTrackIssueService youTrackIssueService = new YouTrackIssueService();
    private final JiraService jiraService = new JiraService();
    private final YouTrackService youTrackService = new YouTrackService();
    private final JiraUserService jiraUserService = new JiraUserService();
    private final JiraIssueTypeService jiraTypeService = new JiraIssueTypeService();
    private final JiraPriorityService jiraPriorityService = new JiraPriorityService();
    private final JiraProjectService jiraProjectService = new JiraProjectService();
    private final JiraStatusService jiraStatusService = new JiraStatusService();
    private final JiraAttachmentService jiraAttachmentService = new JiraAttachmentService();
    private final JiraLinkService jiraLinkService = new JiraLinkService();
    private final JiraIssueService jiraIssueService = new JiraIssueService(jiraAttachmentService, youTrackIssueService, jiraPriorityService);
    private final JiraIssueTypeService jiraIssueTypeService = new JiraIssueTypeService();
    public static List<YouTrackIssueDto> issueDTOYouTrackByProject = new ArrayList<>();
    public static List<YouTrackIssueDto> issueDTOYouTrackList = new ArrayList<>();
    public static List<JiraIssueDto> issueDTOJiraByProject = new ArrayList<>();
    public static List<JiraPriorityDto> jiraPriorities = new ArrayList<>();
    public static List<ProjectJiraDto> jiraProjects = new ArrayList<>();
    public static List<JiraIssueDto> jiraIssues = new ArrayList<>();
    public static List<StatusJiraDto> jiraStatuses = new ArrayList<>();
    public static List<UserYouTrackDto> youTrackUsers = new ArrayList<>();
    public static List<UserJiraDto> jiraUsers = new ArrayList<>();
    public static List<UserJiraDto> jiraUsersAssignableToProject = new ArrayList<>();
    public static List<JiraIssueTypeDto> jiraTypes = new ArrayList<>();
    public static final ObjectMapper MAPPER = new ObjectMapper();
    private volatile boolean stopRequested = false;
    private final CountDownLatch shutdownLatch = new CountDownLatch(1);

    static {
        MAPPER.registerModule(new JavaTimeModule());
    }

    public void preloadData() {
        getAllYouTrackUsers();
        getAllJiraUsers();
        getAllJiraPriorities();
        getAllJiraStatuses();
        getJiraProjects();
        getAllJiraIssueTypes();
        getAllIssues();
    }

    public void exportIssues(int userChoiceProjectInt) {
        ProjectJiraDto jiraProject = jiraProjectService.getJiraProjectByProjectTypeSequence(userChoiceProjectInt, jiraProjects);
        Long jiraProjectId = jiraProject.getId();
        String jiraProjectKey = jiraProject.getKey();
        log.info("EXPORT YOUTRACK ISSUES TO JIRA PROJECT WITH ID = {}, PROJECT KEY = {}", jiraProjectId, jiraProjectKey);

        jiraUsersAssignableToProject = jiraUserService.getJiraUsersAssignableToProject(ProjectType.getProjectBySequence(userChoiceProjectInt));
        getAllIssuesByProject(userChoiceProjectInt);

        //We get the types associated with the project
        List<JiraIssueTypeDto> jiraIssueTypesForProject = jiraTypeService.getAllJiraIssueTypesByProjectId(jiraProjectId);
        //We go through all the tasks of the You Track project and check if there is a corresponding one issueType in jira
        for (YouTrackIssueDto youTrackIssueDto : issueDTOYouTrackByProject) {
            String typeName = youTrackIssueService.getTypeByIssue(youTrackIssueDto).equalsIgnoreCase("user story") ? "Story" : youTrackIssueService.getTypeByIssue(youTrackIssueDto);
            JiraIssueTypeDto jiraType = jiraTypeService.getIssueTypeByName(typeName, jiraIssueTypesForProject);
            //If there is no type in jiraIssueTypesForProject, creating it.
            if (jiraType == null) {
                log.info("IssueType with name = {}, not exists into the jiraIssueTypesForProject list", typeName);
                JiraIssueTypeDto createdType = jiraTypeService.createIssueType(typeName, jiraProjectId);
                log.info("IssueType created:\n{}", createdType);
                //Adding the type to jiraIssueTypesForProject for further work
                jiraIssueTypesForProject.add(createdType);
                log.info("IssueType added to jiraIssueTypesForProject list");
            }
        }
        syncIssuesByProject(jiraIssueTypesForProject);
    }

    /**
     * Syncs tasks between YouTrack and Jira for the selected project.
     * <p>
     * The method goes through all the tasks from YouTrack, checks their availability in Jira and, if the task is not already in Jira,
     * creates a new task, adds attachments and comments. The synchronization process can be safely stopped,
     * wait for the current iteration to complete and then terminate the program correctly.
     * </p>
     * <p>
     * When the method is started, a shutdown hook is added, which allows you to wait for the current task to complete when the process is completed
     * and safely stop the program. If an error occurs when creating a task in Jira, the task will be canceled and the synchronization process will be completed
     * will be interrupted.
     *
     * <p>
     * Important: The synchronization process can only be interrupted between iterations when a task is created for a new record.
     * After the current iteration is completed, the process will be completed correctly.
     * </p>
     *
     * @throws RuntimeException if an error occurs when creating an issue, adding attachments, or adding comments.
     */
    private void syncIssuesByProject(List<JiraIssueTypeDto> typesByProject) {
        Set<String> jiraSummaries = issueDTOJiraByProject.stream()
                .map(issue -> issue.getFields().getSummary())
                .collect(Collectors.toSet());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.warn("Shutdown requested! Waiting for the current iteration to finish...");
            stopRequested = true;
            try {
                shutdownLatch.await(); // Waiting for the completion of the main stream
            } catch (InterruptedException ignored) {
            }
            log.warn("Sync process stopped safely.");
        }));

        try {
            for (YouTrackIssueDto youTrackIssueDto : issueDTOYouTrackByProject) {
                if (stopRequested) {
                    log.info("Stopping sync after completing the current issue.");
                    break; // Interrupting before processing a new task
                }

                String issueIdReadable = youTrackIssueDto.getIdReadable();

                if (jiraSummaries.stream().anyMatch(summary -> summary.contains("[" + issueIdReadable + "]"))) {
                    log.info("YouTrack Issue with idReadable = {} already exists in Jira", issueIdReadable);
                    continue;
                }

                Long id = null;
                try {
                    id = createTask(youTrackIssueDto, typesByProject);
                    addAttachmentsToIssue(id, youTrackIssueDto);
                    addCommentsToIssue(id, youTrackIssueDto);
                    addTransitionToIssue(id, youTrackIssueDto);
                    JiraIssueDto issue = jiraIssueService.getJiraIssueById(id);
                    issueDTOJiraByProject.add(issue);
                    jiraSummaries.add(issue.getFields().getSummary());
                    log.info("Jira issue created successfully with ID: {}\n", id);
                } catch (Exception e) {
                    log.error("Failed to create Jira issue for YouTrack issue: {}. Rolling back...", issueIdReadable, e);
                    if (id != null) {
                        jiraIssueService.rollbackJiraIssue(id);
                    }
                    throw e;
                }
            }
            addLinksToIssues(issueDTOJiraByProject);
        } finally {
            shutdownLatch.countDown(); // Allowing completion shutdown hook
        }
    }

    private void addLinksToIssues(List<JiraIssueDto> jiraProjectIssues) {
        Map<String, List<LinkYouTrackDto>> outwardLinksForProject = new HashMap<>();
        Map<String, List<LinkYouTrackDto>> bothLinksForProject = new HashMap<>();
        for (YouTrackIssueDto youTrackIssueDto : issueDTOYouTrackByProject) {
            String idReadable = youTrackIssueDto.getIdReadable();
            List<LinkYouTrackDto> outwardLinksForIssue = YouTrackLinkService.getLinksByYouTrackIssueAndType(youTrackIssueDto, YouTrackLinkType.OUTWARD);
            if (!outwardLinksForIssue.isEmpty()) {
                outwardLinksForProject.put(idReadable, new ArrayList<>(outwardLinksForIssue));
            }
            List<LinkYouTrackDto> bothLinksForIssue = YouTrackLinkService.getLinksByYouTrackIssueAndType(youTrackIssueDto, YouTrackLinkType.BOTH);
            if (!bothLinksForIssue.isEmpty()) {
                bothLinksForProject.put(idReadable, new ArrayList<>(bothLinksForIssue));
            }

        }
        generateLinks(jiraProjectIssues, outwardLinksForProject);
        generateLinks(jiraProjectIssues, bothLinksForProject);

    }

    private void generateLinks(List<JiraIssueDto> jiraProjectIssues, Map<String, List<LinkYouTrackDto>> linksForProjectWithDirection) {
        for (String s : linksForProjectWithDirection.keySet()) {
            List<LinkYouTrackDto> links = linksForProjectWithDirection.get(s);
            JiraIssueDto issueFrom = jiraIssueService.getJiraIssueByYouTrackIdReadable(s, jiraProjectIssues);
            for (LinkYouTrackDto link : links) {
                for (IssueLinkYouTrackDto issue : link.getIssues()) {
                    JiraIssueDto issueTo = jiraIssueService.getJiraIssueByYouTrackIdReadable(issue.getIdReadable(), issueDTOJiraByProject);
                    if (issueTo != null) {
                        String issueToKey = issueTo.getKey();
                        LinkType linkType = LinkType.getLinkTypeByYouTrackLinkTypeName(link.getLinkType().getName());
                        if (linkType != null) {
                            JSONObject payload = new JSONObject()
                                    .put("type", new JSONObject().put("name", linkType.getJiraLinkType()))
                                    .put("inwardIssue", new JSONObject().put("key", issueFrom.getKey()))
                                    .put("outwardIssue", new JSONObject().put("key", issueToKey));

                            jiraLinkService.create(payload);
                        }
                    }
                }
            }
        }
    }


    private void addTransitionToIssue(Long id, YouTrackIssueDto youTrackIssueDto) {
        log.info("Add transition to Jira issue with id = {}", id);
        JiraTransitionsResponseDto jiraTransitionDto = jiraStatusService.getJiraTransitionsByJiraIssueId(id);
        List<JiraTransitionDto> transitions = jiraTransitionDto.getTransitions();
        String statusName = YouTrackStateService.getIssueStatusNameByYouTrackIssue(youTrackIssueDto);
        log.info("Status name for Jira issue with id = {} from YouTrack issue = {}", id, statusName);
        String targetStatusName = statusName == null ? "Backlog" : statusName;
        log.info("Set status name = {} for Jira issue with id = {}", targetStatusName, id);
        JiraTransitionDto targetTransition = transitions.stream()
                .filter(transition -> targetStatusName.equalsIgnoreCase(transition.getTo().getName()))
                .findFirst()
                .orElse(null);
        log.info("Target transition found in Jira: {}", targetTransition);
        if (targetTransition != null) {
            String transitionId = targetTransition.getId();
            jiraIssueService.transitionIssue(id, transitionId);
            log.info("Transition added to Jira issue with id = {}\n", id);
        }
    }

    /**
     * Creates a task in Jira based on the task YouTrack
     *
     * @param youTrackDto task YouTrack
     * @see YouTrackIssueDto
     */
    private Long createTask(YouTrackIssueDto youTrackDto, List<JiraIssueTypeDto> typesByProject) {
        log.info("Creating Jira issue for YouTrack issue with idReadable = {}", youTrackDto.getIdReadable());
        return jiraIssueService.createJiraIssueByYouTrackIssue(youTrackDto, typesByProject);
    }

    /**
     * Adds attachments to issue
     *
     * @param jiraIssueId      identifier issue
     * @param youTrackIssueDto task YouTrack which you need to get attachments from
     *                         and copy them to the created task Jira
     */
    private void addAttachmentsToIssue(Long jiraIssueId, YouTrackIssueDto youTrackIssueDto) {
        jiraIssueService.addAttachmentsToIssue(jiraIssueId, youTrackIssueDto);
        log.info("Attachments added to Jira issue");
    }

    private void addCommentsToIssue(Long id, YouTrackIssueDto youTrackIssueDto) {
        jiraIssueService.addCommentsToIssue(id, youTrackIssueDto);
        log.info("Comments added");
    }

    /**
     * Filters uploaded tasks from YouTrack and Jira by the specified project.
     *
     * @param projectUserChoice the project ID, corresponds to {@link ProjectType#getSequence()}.
     */
    private void getAllIssuesByProject(int projectUserChoice) {
        issueDTOYouTrackByProject = youTrackIssueService.getAllYouTrackIssuesByProject(projectUserChoice, issueDTOYouTrackList);
        System.out.printf("В YouTrack %d project objectives %s\n",
                issueDTOYouTrackByProject.size(),
                ProjectType.getProjectBySequence(projectUserChoice).getYouTrackKey()
        );

        issueDTOJiraByProject = jiraIssueService.getAllJiraIssuesByProject(projectUserChoice, jiraIssues);
        System.out.printf("В Jira %d project objectives %s\n",
                issueDTOJiraByProject.size(),
                ProjectType.getProjectBySequence(projectUserChoice).getJiraKey()
        );
    }

    private void getAllYouTrackUsers() {
        System.out.println("We read users YouTrack from csv");
        youTrackUsers = CsvReader.readUsersFromCsv("yt_users");
        System.out.printf("Read by users YouTrack from csv: %d\n%n", youTrackUsers.size());
    }

    private void getAllJiraUsers() {
        System.out.println("We read users from Jira");
        jiraUsers = jiraUserService.getAllJiraUsers();
        System.out.printf("Read users from Jira: %d\n\n", jiraUsers.size());
    }

    /**
     * Downloads all task statuses from Jira and saves them in a local list.
     */
    private void getAllJiraStatuses() {
        System.out.println("Starting to read the statuses Jira");
        jiraStatuses = jiraStatusService.getAllJiraStatuses();
        System.out.printf("Read statuses Jira: %d\n\n", jiraStatuses.size());
    }

    /**
     * Downloads all task priorities from Jira and saves them in a local list.
     */
    private void getAllJiraPriorities() {
        System.out.println("Starting to read the priorities Jira");
        jiraPriorities = jiraPriorityService.getAllJiraPriorities();
        System.out.printf("Priorities read Jira: %d\n\n", jiraPriorities.size());
    }

    private void getAllJiraIssueTypes() {
        System.out.println("Starting to read the task types Jira");
        jiraTypes = jiraIssueTypeService.getAllJiraIssueTypes();
        System.out.printf("Task types read Jira: %d\n\n", jiraTypes.size());
    }

    /**
     * Downloads all projects from Jira and saves them in a local list.
     */
    public void getJiraProjects() {
        System.out.println("Starting to read the projects Jira");
        jiraProjects = jiraProjectService.getAllJiraProjects();
        System.out.printf("Projects read Jira: %d\n\n", jiraProjects.size());
    }

    /**
     * Loads all tasks from YouTrack and Jira.
     */
    public void getAllIssues() {
        System.out.println("Starting to read the issues from YouTrack");
        issueDTOYouTrackList = youTrackIssueService.getAllIssuesFromYourTrack();
        System.out.printf("Total issues uploaded from YouTrack: %d\n\n", issueDTOYouTrackList.size());

        System.out.println("Starting to read the issues from Jira");
        jiraIssues = jiraIssueService.getAllIssuesFromJira();
        System.out.printf("Total issues uploaded from Jira: %d\n\n", jiraIssues.size());
    }


    public void checkServices() {
        checkServiceAvailability("Jira", jiraService::checkService);
        checkServiceAvailability("YouTrack", youTrackService::checkService);
    }

    private void checkServiceAvailability(String serviceName, Supplier<Integer> statusChecker) {
        System.out.println("Checking the availability of services " + serviceName);
        int attempt = 1;
        long delay = 5;
        final long maxDelay = 300;
        final long delayIncrement = 10;

        while (statusChecker.get() != HttpStatus.SC_OK) {
            System.out.println("Checking the availability of services " + serviceName +
                    ". Attempt: " + attempt + ". Next attempt in " + delay + " sec.");

            attempt++;
            delay = Math.min(delay + delayIncrement, maxDelay);

            try {
                TimeUnit.SECONDS.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interruption while waiting for verification " + serviceName, e);
            }
        }
        System.out.println("Services " + serviceName + " available");
    }
}
