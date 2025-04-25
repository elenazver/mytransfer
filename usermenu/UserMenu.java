package org.example.usermenu;

import org.example.enumeration.ProjectType;

public class UserMenu {

    /**
     * User's menu with a list of projects available for synchronization
     * @see ProjectType
     */
    public static void printProjectForChoice() {
        System.out.printf("Select a project for further import (1-%d):%n", ProjectType.values().length);
        for (ProjectType project : ProjectType.values()) {
            System.out.printf("%s - %s \n", project.getSequence(), project.getYouTrackKey());
        }
        System.out.println("0 - Log out of the system");
    }

    /**
     * Main User menu
     */
    public static void printMainMenu() {
        System.out.println("1 - Exporting tasks from YouTrack to Jira by project");
        System.out.println("0 - Log out of the system");
    }
}
