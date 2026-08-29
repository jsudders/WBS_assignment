package edu.curtin.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {
    // either create a list of TaskStructure/Parents that don't have any parents or create a TaskStructure or TaskParent to house them all
    TaskParent generalParent = new TaskParent(null, "", "");
    private static final Logger log = Logger.getLogger(App.class.getName());
    int estimationConfiguration = 3;
    int numEstimates = 3;
    Map<Integer, EffortEstimate> estimateOptions = new HashMap<>();

    public static void main(String[] args) {
        // if(args.length != 1) 
        // {
        //     throw new InvalidParameterException("Please only have one argument, or surround the file path in quotes.");
        //  } // Error
        // else
        // {
        //     String filename = args[0];
        //     new App().menu(filename);
        // }
        // this isn't working for now, so i will fix it later
        String filename = "wbs_data.txt";
        new App().menu(filename);
    }

    public void menu(String filename)
    {
        Scanner sc = new Scanner(System.in);
        TaskParent generalParent = null;
        boolean running = true;
        while(true) // while no valid file has been entered
        {
            try
            {
                WBS_FileIO fileIO = new WBS_FileIO();
                generalParent = fileIO.readWBS(filename);
                if(generalParent != null)
                {
                    generalParent.display("");
                    break;
                }
            }
            catch(IOException io)
            {
                System.out.println("Cannot open file. File is invalid. Please try again.");
            }
            catch(WBSException fe)
            {
                System.out.println(fe + ". Quitting program now.");
                running = false;
                break;
            }
            catch(Exception e)
            {
                log.severe(() -> "Unknown exception " + e);
                System.out.println("Exception: " + e);
                running = false;
                break;
            }

        }
        while(running)
        {
            int menuOption = getMenuOption(sc, "mainmenu");
            switch(menuOption)
            {
                case 1 -> {
                    performEstimates(sc);

                 }
                case 2 -> { 
                    numEstimates = enterN("Please enter the number of estimators for this program.", sc);
                    int approachOption = getMenuOption(sc, "approach");
                    switch(approachOption)
                    {
                        case 1 -> {
                            System.out.println("Chosen highest estimate.");
                            this.estimationConfiguration = 1;
                            this.estimateOptions.put(approachOption, new HighestEstimate());
                        }
                        case 2 -> {
                            System.out.println("Chosen median estimate");
                            this.estimationConfiguration = 2;
                            this.estimateOptions.put(approachOption, new MedianEstimate());
                        }
                        case 3 -> {
                            System.out.println("Chosen discussion.");
                            this.estimationConfiguration = 3;
                            this.estimateOptions.put(approachOption, new DiscussionEstimate());
                        }
                    }
                }
                case 0 -> {
                    System.out.println("Goodbye.");
                    running = false;
                    break;
                }
            }
        }
        sc.close();
    }

    public int getMenuOption(Scanner sc, String printOption)
    {
        while(true)
        {
            int option = 9;
            int optionLower = 0;
            int optionHigher = 1; // initialise
            switch(printOption)
            {
                case "mainmenu" -> {
                    printMainMenu();
                    optionLower = 0;
                    optionHigher = 2;
                }
                case "approach" -> {
                    printApproach();
                    optionLower = 1;
                    optionHigher = 3;
                }
                default -> { System.out.println("Internal error.");}
            }
            try
            {
                option = sc.nextInt();
                if((option >= optionLower) && (option <= optionHigher))
                {
                    return option;
                }
                else
                {
                    System.out.println("Please input an integer between " + optionLower + " and " + optionHigher);
                }
            }
            catch(InputMismatchException e)
            {
                System.out.println("Please enter an integer between " + optionLower + " and " + optionHigher);
            }
        }
    }

    public int enterN(String prompt, Scanner sc)
    {
        while(true)
        {
            System.out.println(prompt);
            try
            {
                int N = sc.nextInt();
                log.fine(() -> "User entered " + N);
                if(!(N >= 1))
                {
                    System.out.println("Please enter a positive integer.");
                }
                else
                {
                    System.out.println("Entered: " + N);
                    return N;
                }
                
            }
            catch(InputMismatchException e)
            {
                log.log(Level.INFO, "User entered a bad value for N");
                System.out.println("This format is not valid. Please enter a positive integer.");
                sc.nextLine();
            }
            sc.nextLine();
        }
    }

    public String enterString(String prompt, Scanner sc, boolean continuous)
    {
        do
        {
            System.out.println(prompt);
            try
            {
                String value = sc.nextLine();
                log.fine(() -> "User entered " + value);

                if(value.isEmpty())
                {
                    System.out.println("Please enter a valid string.");
                    if(!continuous)
                    {
                        System.out.println("Exiting function.");
                        return "";
                    }
                }
                else
                {
                    System.out.println("Entered " + value);
                    return value;
                }
            }
            catch (InputMismatchException e)
            {
                log.log(Level.INFO, "User entered a bad value for string");
                System.out.println("This is not a valid string. please try again.");
                sc.nextLine();
                if(!continuous)
                {
                    System.out.println("Exiting function.");
                    return "";
                }
            }
            sc.nextLine();
        } while(continuous);
        return ""; // to avoid an error message, however if it gets to this point then it should fail
    }

    public static void printMainMenu()
    {
        System.out.println("Welcome to WBS helper, please choose a menu option.");
        System.out.println("1 - Estimate effort for task");
        System.out.println("2 - Configure estimation");
        System.out.println("0 - Quit");
    }

    public static void printApproach()
    {
        System.out.println("--Configure Approach--\n");
        System.out.println("Please choose a new reconcilliation approach:");
        System.out.println("1 - Take the highest estimate");
        System.out.println("2 - Take the median estimate");
        System.out.println("3 - Discuss among yourselves, and request a single revised estimate");
    }

    public static void printEffortSummary(int totalEffort, int unknownTasks)
    {
        System.out.println("Total known effort = " + totalEffort);
        System.out.println("Unknown tasks = " + unknownTasks);
    }



    public void performEstimates(Scanner sc)
    {
        
        String exitPrint = "Task ID is either invalid or does not exist. Exiting now.";
        try
        {
            String taskID = enterString("Please enter the taskID (of any task) to set the effort estimates.", sc, false);
            TaskStructure foundTask = generalParent.findTask(taskID);
            log.fine(() -> "User entered: " + taskID);
            if((foundTask == null) || (taskID.isEmpty())) 
            {
                log.fine(() -> "Task not found");
                throw new WBSFormatException(exitPrint);
            }
            List<TaskLeaf> leafList = new ArrayList<>();
            if(foundTask instanceof TaskParent taskParent)
            {
                leafList = taskParent.findRecursiveLeaves(leafList);
            }
            else
            {
                leafList.add((TaskLeaf) foundTask);
            }
            EffortEstimate estimateType = estimateOptions.get(estimationConfiguration);

            for (TaskLeaf leaf : leafList)
            {
                List<Integer> estimates = new ArrayList<>();
                for(int i = 0; i < numEstimates; i++)
                {
                    estimates.add(enterN("Please enter the estimate for estimator " + (i + 1) + ": ", sc));
                }
                
                System.out.println("Estimates for task: " + leaf.ID);
                for(int i = 0; i < numEstimates; i++)
                {
                    System.out.println("\tEstimate " + (i + 1) + ": " + estimates.get(i));
                }
                
                Set<Integer> estimatesDupe = new HashSet<>(estimates); // maybe redo this and change it to something else, it feels slow
                int newEstimate = estimates.get(0); // initialise in case all estimates are the same
                if(estimatesDupe.size() != estimates.size())
                {
                    newEstimate = estimateType.getEffortEstimate(leaf, estimates);
                }
                leaf.setEstTime(newEstimate);
            }

            System.out.println("Estimates for task completed.\n");
        }
        catch(WBSException wbs)
        {
            System.out.println(exitPrint);
        }
    }
}
