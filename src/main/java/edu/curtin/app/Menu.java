package edu.curtin.app;
import java.util.InputMismatchException;
import java.util.Scanner;

import edu.curtin.app.Hello;



public class Menu {
    Scanner sc = new Scanner(System.in);
    private static final Logger log = Logger.getLogger(Hello.class.getName());

    public int enterN(String prompt)
    {
        int N;
        while(true)
        {
            System.out.println(prompt);
            try
            {
                N = sc.nextInt();
                log.fine(() -> "User entered " + N);
                if(!(N >= 1))
                {
                    System.out.println("Please enter a positive integer.");
                }
                else
                {
                    System.out.println("Entered: " + N);
                    break;
                }
                
            }
            catch(InputMismatchException e)
            {
                logger.log("User entered a bad value for N");
                System.out.println("This format is not valid. Please enter a positive integer.");
                sc.nextLine();
            }
            sc.nextLine();
        }
        return N;
    }

    public String enterString(String prompt)
    {
        String value;
        while(true)
        {
            System.out.println(prompt);
            try
            {
                value = sc.nextLine();
                log.fine(() -> "User entered " + value);

                if(value.isEmpty())
                {
                    System.out.println("Please enter a valid string.");
                }
                else
                {
                    System.out.println("Entered " + value);
                    
                    break;
                }
            }
            catch (InputMismatchException e)
            {
                logger.log("User entered a bad value for string");
                System.out.println("This is not a valid string. please try again.")
                sc.nextLine();
            }
            sc.nextLine();
        }
    }

    public void printMainMenu()
    {
        System.out.println("Welcome to WBS helper, please choose a menu option.");
        System.out.println("1 - Estimate effort for task");
        System.out.println("2 - Configure estimation");
        System.out.println("0 - Quit");
    }

    public void printApproach()
    {
        System.out.println("--Configure Approach--\n");
        System.out.println("Please choose a new reconcilliation approach:");
        System.out.println("1 - Take the highest estimate");
        System.out.println("2 - Take the median estimate");
        System.out.println("3 - Discuss among yourselves, and request a single revised estimate");
    }

    public void printEffortSummary(int totalEffort, int unknownTasks)
    {
        System.out.println("Total known effort = " + totalEffort);
        System.out.println("Unknown tasks = " + unknownTasks);
    }

}
