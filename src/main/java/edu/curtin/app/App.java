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
    private static final Logger log = Logger.getLogger(App.class.getName());
    private int estimationConfiguration = 3;
    private int numEstimates = 3;
    private Map<Integer, EffortEstimate> estimateOptions = new HashMap<>();
    private Scanner sc;
    private HashMap<String, TaskStructure> baseTasks;

    public static void main(String[] args) {
        try
        {
            if(args.length != 1) 
            {
                throw new IllegalArgumentException("Please only have one argument, or surround the file path in quotes.");
            } // if there are more arguments than just the file name given (or no arguments at all), throw an IllegalArgumentException to gracefully end the program
            else
            {
                String filename = args[0];
                new App().menu(filename); // create a new menu object with the filename given as args[0]
            }
        }
        catch(IllegalArgumentException e)
        {
            System.out.println(e); // if the IllegalArgumentException is thrown, print out the exception message and end the program
        }
    }

    public void menu(String filename)
    {
        this.sc = new Scanner(System.in);
        boolean running = true;
        this.estimateOptions.put(1, new HighestEstimate());
        this.estimateOptions.put(2, new MedianEstimate());
        this.estimateOptions.put(3, new DiscussionEstimate());
        // pre-set the options for estimates for strategy method
        try
        {
            this.baseTasks = WBSFileIO.readWBS(filename); // reading file gives a map of all basetasks that don't have a parent
        
            if((baseTasks != null) && !(baseTasks.isEmpty())) // as long as reading the file worked, display all tasks
            {
                for(TaskStructure task : baseTasks.values())
                {
                    task.display("");
                }
                
                int totalTime = TaskStructure.calcTotalTime(baseTasks); // calculate estimated time for the whole WBS structure
                int unknownTasks = TaskStructure.calcUnknownTasks(baseTasks); // calculate how many leaves there are without estimated time

                if(unknownTasks == -1)
                {
                    System.out.println("Unable to calculate the number of unknown tasks due to catastrophic error. Please review your WBS file and try again.");
                    // if the recursive leaf function fails, unknownTasks is set to -1
                    running = false; // the program will close because this is a catastrophic error. it is likely that this error in the task structure will be caught before this.
                }
                else
                {
                    printEffortSummary(totalTime, unknownTasks); // if unknownTasks was successfully calculated, print the effort summary
                }
            }
            else
            { // if the basetasks map is null, then the program has not read/saved the data from the file correctly. throw a WBSFormatException
                System.out.println("BaseTasks empty or null.");
                throw new WBSFormatException("Unknown error occured whilst trying to read file. Make sure the file is in the correct format.");
            }
        }
        catch(IOException io) // if the file doesn't exist, end the program
        {
            System.out.println("Cannot open file. File is invalid. Please try again.");
            running = false;
        }
        catch(WBSFormatException fe) // if any WBSException is thrown (from the WBSFormatException of not saving data correctly, or something that is otherwise thrown in the file reading), print the error and quit the program
        {
            System.out.println("WBSFormatException " + fe.getMessage() + ".. Quitting program now.");
            running = false;
        }
        catch(WBSLogicException L)
        {
            System.out.println("WBSLogicException " + L.getMessage() + " The WBS does not make logical sense... Quitting program now.");
            running = false;
        }
        catch(Exception e) // catch-all at the end.
        {
            log.severe(() -> "Other exception " + e);
            System.out.println("Exception: " + e);
            running = false;
        }
        promptContinue();
        
        while(running) // as long as none of the exceptions from above were thrown, start a loop for the menu
        {
            int menuOption = getMenuOption("mainmenu"); // send to get menu option with prompt
            
            switch(menuOption)
            {
                case 1 -> {
                    performEstimates();
                }
                case 2 -> {
                    numEstimates = enterN("Please enter the number of estimators for this program.", "Estimators: "); // get the number of estimators and set it to local estimates variable
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
                case 0 -> { // case of quitting, write to the file
                    System.out.println("Writing to file...");
                    WBSFileIO.writeToWBS(filename, baseTasks);
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
            int option; 
            int optionLower = 0;
            int optionHigher = 1; // initialise menu options 
            switch(printOption) // print different menus depending on the menu option and also set the option limits per menu
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
                default -> { System.out.println("Internal error.");} // not possible in current iteration
            } // this will loop the prompt until the user enters a valid menu option
            try
            {
                System.out.print("Option: ");
                option = sc.nextInt();
                sc.nextLine(); // get the user input and clear the buffer
                log.log(Level.FINE, "User entered {0} for menu option", option); // logging the menu option chosen
                if((option >= optionLower) && (option <= optionHigher))
                {
                    return option; // if the user input is a valid menu option, return the option
                }
                else
                {
                    System.out.println(option + " is not a valid menu option. Please input an integer between " + optionLower + " and " + optionHigher);
                    // if the user input is not within the option ranges, inform the user and loop the prompt again
                }
            }
            catch(InputMismatchException e)
            {
                System.out.println("Input was not an integer. Please enter an integer between " + optionLower + " and " + optionHigher);
                promptContinue();
                // if the user input is not an integer,inform the user
                // and  clear the buffer with promptContinue (as the initial buffer clearing wouldn't run) before looping the menu options again
            }
        }
    }

    public int enterN(String prompt, String enterLine) // Scanner logic for entering N (number of estimators)
    {
        while(true)
        {
            System.out.println(prompt);
            System.out.print(enterLine);
            try
            {
                int N = sc.nextInt();
                sc.nextLine(); // get the entry for N and clear the buffer
                log.log(Level.FINE, "User entered {0} for enterN.", N); // log the user's menu option
                if(!(N >= 1)) // guard in case user enters a non-positive or 0
                {
                    System.out.println("Please enter a positive integer.");
                }
                else
                {
                    return N;
                }
                
            }
            catch(InputMismatchException e)
            {
                log.log(Level.FINE, "User entered a bad value for N"); // log that the input was invalid
                System.out.println("This format is not valid. Please enter a positive integer.");
                promptContinue();
                // inform user and clear buffer (executed in promptContinue)
            }
        }
    }

    public String enterString(String prompt, String enterLine) // Scanner logic for entering strings (such as task ID)
    {
        while(true)
        {
            System.out.println(prompt);
            System.out.print(enterLine);

            String value = sc.next();
            log.log(Level.FINE, "User entered {0} for value", value);
            sc.nextLine(); // clear the buffer
            if(value.isEmpty()) // guard in case the value is empty
            {
                System.out.println("Please enter a valid string.");
            }
            else
            {
                return value; // if the value is not empty, then the value is valid and it is returned
            }
            // the only exceptions thrown by .next() and .nextLine() are NoSuchElementException or IllegalStateException, neither of which should appear in this block of code
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
        System.out.println("\nTotal known effort = " + totalEffort);
        System.out.println("Unknown tasks = " + unknownTasks);
    }

    public void promptContinue()
    {
        System.out.println("[Press enter to continue]");
        sc.nextLine(); // clears buffer and allows a break in the code for users to read the output
    }


    public void performEstimates()
    {
        try
        {
            String taskID = enterString("Please enter the taskID (of any task) to set the effort estimates.", "TaskID: ");
            TaskStructure foundTask = TaskStructure.findTask(baseTasks, (taskID).strip());
            // get the taskID for the estimating task and get the taskStructure for it.

            log.info(() -> "User entered: " + taskID + " for perform estimates"); 
            // log user input and the performing of estimate


            if((foundTask == null) || (taskID.isEmpty())) // if the task cannot be found within the structure or the taskID is empty, throw an error
            {
                log.info(() -> "Task not found");
                throw new WBSException("Task ID is either invalid or doesn't exist.");
            }

            List<TaskLeaf> leafList = new ArrayList<>(); // initialise a leafList for the instance that task entered was a parent and estimates are needed for each subtask.
            
            if(foundTask instanceof TaskParent taskParent) // if the task is a parent, set the leaves to each of the subtasks
            {
                leafList = taskParent.findRecursiveLeaves(leafList);
            }
            else // otherwise, the leafList is just the initial task
            {
                leafList.add((TaskLeaf) foundTask);
            }
            
            for (TaskLeaf leaf : leafList) // looping through each task in the leafList
            {
                System.out.println("\nPlease enter the estimates for task " + leaf.ID + " below.");
                List<Integer> estimates = new ArrayList<>(); // initialise ArrayList to keep all of the estimates in
                for(int i = 0; i < numEstimates; i++) // get the estimates for each estimator
                {
                    estimates.add(enterN("", "Please enter the estimate for estimator " + (i + 1) + ": "));
                    
                }
                
                System.out.println("Estimates for task: " + leaf.ID); // display the estimates from each estimator
                for(int i = 0; i < numEstimates; i++)
                {
                    System.out.println("\tEstimate " + (i + 1) + ": " + estimates.get(i));
                }
                
                Set<Integer> estimatesDupe = new HashSet<>(estimates);  // duplicate the task estimates into a hashset (which can't have duplicates)
                int newEstimate = estimates.get(0); // initialise in case all estimates are the same
                if(estimatesDupe.size() != 1) // if the hashSet's size is not 1 (i.e. the estimates from all the estimators are not the same), then get the effort estimate with the configuration
                {
                    newEstimate = (estimateOptions.get(estimationConfiguration)).getEffortEstimate(this.sc, leaf, estimates);
                }
                log.log(Level.INFO, "Estimate for {0} set to be {1}", new Object[] {leaf.ID, newEstimate});
                leaf.setEstTime(newEstimate);
                // set the new estimated time for the leaf.
            }

            System.out.println("\nEstimates for task " + foundTask.ID + " (and subtask(s) if applicable) completed.\n");
            promptContinue();
        }
        catch(WBSException wbs)
        {
            System.out.println("WBSException: " + wbs.getMessage() + " Exiting now."); // graceful quit
        }
    }
}
