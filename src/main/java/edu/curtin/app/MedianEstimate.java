package edu.curtin.app;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class MedianEstimate implements EffortEstimate {
    
    @Override
    public int getEffortEstimate(Scanner sc, TaskLeaf leaf, List<Integer> estimates)
    {
        System.out.println("---Getting median estimate---");
        Collections.sort(estimates); // sort estimates

        int midPoint = estimates.size() / 2; // get the midPoint from the list

        if(estimates.size() % 2 == 1) // if the list is odd (i.e there is one midpoint), then return the value at that midpoint
        {
            return estimates.get(midPoint);
        }
        else
        {
            int estimate1 = estimates.get(midPoint - 1);
            int estimate2 = estimates.get(midPoint);
            return (estimate1 + estimate2) / 2;
            // if the list is even, then get the values for each midpoint and average them before returning
        }
    }

}
