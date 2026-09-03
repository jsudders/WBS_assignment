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
    private TaskParent generalParent;
    private static final Logger log = Logger.getLogger(App.class.getName());
    int estimationConfiguration = 3;
    int numEstimates = 3;
    Map<Integer, EffortEstimate> estimateOptions = new HashMap<>();
    private Scanner sc;
    private WBS_FileIO fileIO;

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
        this.sc = new Scanner(System.in);
        boolean running = true;
        this.estimateOptions.put(1, new HighestEstimate());
        this.estimateOptions.put(2, new MedianEstimate());
        this.estimateOptions.put(3, new DiscussionEstimate());
        // pre-set the options for estimates for strategy method
        while(true) // while no valid file has been entered
        {
            try
            {
                this.fileIO = new WBS_FileIO(); // generate a new fileIO instance to read and write to the file
                this.generalParent = fileIO.readWBS(filename); // reading file gives a "generalParent"
                if(generalParent != null) // as long as reading the file worked, display all tasks
                {
                    generalParent.display("");
                    break;
                }
                else
                {
                    throw new WBSFormatException("Unknown error occured whilst trying to read file. Make sure the file is in the correct format.");
                }
            }
            catch(IOException io)
            {
                System.out.println("Cannot open file. File is invalid. Please try again.");
                running = false;
                break;
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
        promptContinue();
        
        while(running)
        {
            int menuOption = getMenuOption("mainmenu"); // send to get menu option with prompt
            promptContinue();
            switch(menuOption)
            {
                case 1 -> {
                    performEstimates(); // if option 1, perform estimates.
                }
                case 2 -> { 
                    numEstimates = enterN("Please enter the number of estimators for this program."); // get the number of estimators and set it to local estimates variable
                    int approachOption = getMenuOption("approach"); // print approach options and get the choice
                    switch(approachOption)
                    {
                        case 1 -> {
                            System.out.println("Chosen highest estimate.");
                            this.estimationConfiguration = 1;
                        }
                        case 2 -> {
                            System.out.println("Chosen median estimate");
                            this.estimationConfiguration = 2;
                            
                        }
                        case 3 -> {
                            System.out.println("Chosen discussion.");
                            this.estimationConfiguration = 3;
                        } 
                    }// set the configuration for estimation which is used in performEstimates
                }
                case 0 -> {
                    System.out.println("Writing to file...");
                    fileIO.writeToWBS(filename, generalParent);
                    System.out.println("Goodbye.");
                    running = false;
                    break;
                }
            }
        }
        sc.close();
    }

    public int getMenuOption(String printOption)
    {
        while(true)
        {
            int option = 9; // option 9 isn't possible. failsafe in case bad scanner doesn't set initial option
            int optionLower = 0;
            int optionHigher = 1; // initialise menu options 
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
            } // initialise option ranges to allow prompt to loop until user enters a valid option
            try
            {
                System.out.print("Option: ");
                option = sc.nextInt();
                log.log(Level.FINE, "User entered {0} for menu option", option);
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

    public int enterN(String prompt) // Scanner logic for entering N (number of estimators)
    {
        while(true)
        {
            System.out.println(prompt);
            try
            {
                int N = sc.nextInt();
                sc.nextLine();
                log.log(Level.FINE, "User entered {0} for enterN.", N);
                if(!(N >= 1)) // guard in case user enters a non-positive or 0
                {
                    System.out.println("Please enter a positive integer.");
                }
                else
                {
                    System.out.println("Entered: " + N + "\n");
                    return N;
                }
                
            }
            catch(InputMismatchException e)
            {
                log.log(Level.FINE, "User entered a bad value for N");
                System.out.println("This format is not valid. Please enter a positive integer.");
                sc.nextLine();
            }
            sc.nextLine();
        }
    }

    public String enterString(String prompt) // Scanner logic for entering strings (such as task ID)
    {
        while(true)
        {
            System.out.println(prompt);
            try
            {
                String value = sc.next();
                log.log(Level.FINE, "User entered {0} for value", value);

                if(value.isEmpty())
                {
                    System.out.println("Please enter a valid string.");
                }
                else
                {
                    System.out.println("Entered " + value + "\n");
                    return value;
                }
            }
            catch (InputMismatchException e)
            {
                log.log(Level.FINE, "User entered a bad value for string");
                System.out.println("This is not a valid string. please try again.");
                sc.nextLine();
            }
            promptContinue();
        }
    }

    public static void printMainMenu() // Organise printing 
    {
        System.out.println("\nWelcome to WBS helper, please choose a menu option.");
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

    public void promptContinue()
    {
        System.out.println("[Press enter to continue]\n");
        sc.nextLine();
    }


    public void performEstimates()
    {
        
        String exitPrint = "Task ID is either invalid or does not exist. Exiting now.";
        try
        {
            String taskID = enterString("Please enter the taskID (of any task) to set the effort estimates.");
            TaskStructure foundTask = generalParent.findTask((taskID).strip());
            // get the taskID for the estimating task and get the taskStructure for it.
            log.info(() -> "User entered: " + taskID);
            if((foundTask == null) || (taskID.isEmpty())) 
            {
                log.info(() -> "Task not found");
                throw new WBSFormatException("Task is null or empty");
                // if the task cannot be found within the structure or the taskID is empty, throw an error
            }
            List<TaskLeaf> leafList = new ArrayList<>(); // initialise a leafList for the instance that task entered was a parent and estimates are needed for each subtask.
            if(foundTask instanceof TaskParent taskParent)
            {
                leafList = taskParent.findRecursiveLeaves(leafList);
            }// if the task is a parent, set the leaves to each of the subtasks
            else
            {
                leafList.add((TaskLeaf) foundTask);
            } // otherwise, the leafList is just the initial task
            EffortEstimate estimateType = estimateOptions.get(estimationConfiguration);
            // get the estimationType based on configuration
            for (TaskLeaf leaf : leafList) // looping through each task in the leafList
            {
                System.out.println("\nPlease enter the estimates for task " + leaf.ID + " below.");
                List<Integer> estimates = new ArrayList<>(); // initialise ArrayList to keep all of the estimates in
                for(int i = 0; i < numEstimates; i++)
                {
                    estimates.add(enterN("Please enter the estimate for estimator " + (i + 1) + ": "));
                    // get the estimates for each estimator
                }
                
                System.out.println("Estimates for task: " + leaf.ID); // display the estimates from each estimator
                for(int i = 0; i < numEstimates; i++)
                {
                    System.out.println("\tEstimate " + (i + 1) + ": " + estimates.get(i));
                }
                
                Set<Integer> estimatesDupe = new HashSet<>(estimates);  // duplicate the task estimates into a hashset (which can't have duplicates)
                // maybe redo this and change it to something else, it feels slow
                int newEstimate = estimates.get(0); // initialise in case all estimates are the same
                if(estimatesDupe.size() != 1) // if the hashSet's size is not 1 (i.e. the estimates from all the estimators are not the same), then get the effort estimate with the configuration
                {
                    newEstimate = estimateType.getEffortEstimate(this.sc, leaf, estimates);
                }
                leaf.setEstTime(newEstimate);
                // set the new estimated time for the leaf.
            }

            System.out.println("\nEstimates for task " + foundTask.ID + " (and subtask(s) if applicable) completed.\n");
        }
        catch(WBSException wbs)
        {
            System.out.println(exitPrint + "FAIL");
        }
    }
}
