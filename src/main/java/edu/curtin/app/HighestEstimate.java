package edu.curtin.app;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
public class HighestEstimate implements EffortEstimate {
    
    @Override
    public int getEffortEstimate(Scanner sc, TaskLeaf task, List<Integer> estimates)
    {
        System.out.println("---Getting max estimate---");
        return Collections.max(estimates); // return the maximum from the list
    }

}
