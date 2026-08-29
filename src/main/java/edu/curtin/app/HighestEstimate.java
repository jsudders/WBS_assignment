package edu.curtin.app;
import java.util.Collections;
import java.util.List;
public class HighestEstimate implements EffortEstimate {
    
    @Override
    public int getEffortEstimate(TaskLeaf task, List<Integer> estimates)
    {
        System.out.println("---Getting max estimate---");
        return Collections.max(estimates);
    }
}
