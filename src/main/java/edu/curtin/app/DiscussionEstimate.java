package edu.curtin.app;
import java.util.List;
import java.util.Scanner;
import java.util.InputMismatchException;

public class DiscussionEstimate implements EffortEstimate {
    @Override
    public int getEffortEstimate(Scanner sc, TaskLeaf task, List<Integer> estimates)
    {
        System.out.println("---Discussion Estimate---");
        int finalEstimate;
        while(true) // loop while trying to get valid input from user
        {
            try
            {
                System.out.println("Please discuss among yourselves for the effort estimate of the task: " + task.ID + " and enter an integer for the new effort estimate.");
                System.out.print("Estimate for " + task.ID +": ");
                finalEstimate = sc.nextInt();
                // prompt user for an agreed-upon estimation
                if(finalEstimate <= 0)
                {
                    throw new IllegalArgumentException();
                    // if the finalestimate is not greater than 0, throw an IllegalArgumentException
                }
                else
                {
                    System.out.println("Effort estimate chosen: " + finalEstimate);
                    return finalEstimate;
                    // otherwise inform the user of the decision and return the estimate
                }
            }
            catch(IllegalArgumentException num)
            {
                sc.nextLine();
                System.out.println("You have not entered a valid positive integer. Please try again.");
                // if the user enters bad input, clear the buffer and inform the user
            }
            catch(InputMismatchException mismatch)
            {
                sc.nextLine(); // clear buffer
                System.out.println("No valid integer was entered for " + task.ID + " please try again.\n");
                // if the user enters bad input, clear the buffer and inform the user
            }
        }
        
    }
    
}
