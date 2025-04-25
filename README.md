
__mytransfer__ — a Java console application for synchronizing tasks from YouTrack to Jira.

<h3>__Description__</h3>


The application performs the following functions:



* Checks the availability of YouTrack and Jira services.
* Loads YouTrack users from a CSV file.
* Retrieves lists of projects, priorities, statuses and issue types from Jira.
* Exports issues from the selected YouTrack project to a Jira project:
    * Creates new tasks taking into account type, priority and status.
    * Adds attachments and comments.
    * Applies transitions to tasks.
    * Creates links between tasks.
* Supports safe stopping of the synchronization process.

<h3>__Project structure__</h3>


constants/ - constants and settings (URL, tokens, headers)

controller/ - `Application` class for controlling the application menu

dao/ - HTTP client (`HttpService`), migration of attachments, YouTrack/Jira services

dto/ - DTO classes for YouTrack and Jira

enumeration/ - enumerations: `ProjectType`, `LinkType`, `YouTrackLinkType`, etc.

service/ — synchronization business logic (`IssueSynchronizer`)

usermenu/ - display a menu for selecting an action and project (`UserMenu`)

util/ - utilities: CSV reading (`CsvReader`), deserializers

Main.java - entry point to the application

README.md — project documentation

<h3>__Installation and launch__</h3>


Clone the repository: \
git clone https://github.com/elenazver/mytransfer.git



1. cd mytransfer
2. Edit the file `constants/ProjectConstants.java`:

    `YOUTRACK_API_KEY` — your YouTrack token.


    `YOUTRACK_URL` — YouTrack API URL (for example, `https://<your_domain>.youtrack.cloud/api`).


        `JIRA_API_KEY` — your Jira token.


    `JIRA_URL` - Jira API URL (for example, `https://<your_domain>.atlassian.net/rest/api/2`).


    `JIRA_USERNAME` — your login in Jira.

3. Prepare the file `yt_users.csv` in the resources folder (`src/main/resources`):

    Headings and columns must match the class fields `UserYouTrackDto`.


Compile the project and run the application: \
javac -d out src/main/java/org/example/**/*.java



4. java -cp out org.example.Main

<h3>__Usage__</h3>




* After launch, the application will check the availability of services.
* On the main menu:

    Enter `1` to export issues from YouTrack to Jira.


    Enter `0` to exit.

* When exporting, enter the project number (1 to N) to sync.
* The program will show statistics and exit.
