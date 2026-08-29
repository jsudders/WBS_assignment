package edu.curtin.app;
import java.util.List;
import java.util.Scanner;

public class DiscussionEstimate implements EffortEstimate {
    @Override
    public int getEffortEstimate(TaskLeaf task, List<Integer> estimates)
    {
        int finalEstimate;
        try(Scanner sc = new Scanner(System.in))
        {
            while(true)
            {
                try
                {
                    System.out.println("Please discuss among yourselves for the effort estimate of the task: " + task.ID);
                    System.out.println("When you are ready. Please enter an integer for the new effort estimate.");
                    finalEstimate = sc.nextInt();
                    if(finalEstimate <= 0)
                    {
                        throw new IllegalArgumentException();
                    }
                    else
                    {
                        System.out.println("Effort estimate chosen: " + finalEstimate);
                        return finalEstimate;
                    }
                }
                catch(IllegalArgumentException num)
                {
                    sc.nextLine(); // clear buffer
                    System.out.println("You have not entered a valid positive integer. Please try again.");
                }
            }
        }
    }
}
