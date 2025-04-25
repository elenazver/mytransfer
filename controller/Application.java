package org.example.controller;


import org.example.service.IssueSynchronizer;
import org.example.usermenu.UserMenu;

import java.util.Scanner;

/**
 * Class the logic of switching between the user's menu items with a call to the main functionality
 */
public class Application {
    public void start() {
        IssueSynchronizer synchronizer = new IssueSynchronizer();
        Scanner scanner = new Scanner(System.in);

        synchronizer.checkServices();

        while (true) {
            UserMenu.printMainMenu();
            String mainChoice = scanner.nextLine();
            int mainChoiceInt;
            try {
                mainChoiceInt = Integer.parseInt(mainChoice);
            } catch (NumberFormatException e) {
                System.out.println("Error: Enter a numeric value.");
                continue;
            }

            if (mainChoiceInt == 0) {
                System.out.println("The system is shutting down");
                System.exit(0);
            } else if (mainChoiceInt == 1) {
                synchronizer.preloadData();
                UserMenu.printProjectForChoice();
                String userChoiceProject = scanner.nextLine();
                int userChoiceProjectInt;
                try {
                    userChoiceProjectInt = Integer.parseInt(userChoiceProject);
                } catch (NumberFormatException e) {
                    System.out.println("Error: Enter a numeric value.");
                    continue;
                }
                if (userChoiceProjectInt == 0) {
                    System.exit(0);
                } else {
                    synchronizer.exportIssues(userChoiceProjectInt);
                    System.exit(0);
                }
            } else {
                System.out.println("Wrong choice. Try again.");
            }
        }
    }
}
