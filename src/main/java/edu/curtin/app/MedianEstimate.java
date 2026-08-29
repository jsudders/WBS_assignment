package edu.curtin.app;
import java.util.Collections;
import java.util.List;

public class MedianEstimate implements EffortEstimate {
    
    @Override
    public int getEffortEstimate(TaskLeaf leaf, List<Integer> estimates)
    {
        Collections.sort(estimates); // sort estimates

        int midPoint = estimates.size() / 2;

        if(estimates.size() % 2 == 1)
        {
            return estimates.get(midPoint);
        }
        else
        {
            int estimate1 = estimates.get(midPoint - 1);
            int estimate2 = estimates.get(midPoint);
            return (estimate1 + estimate2) / 2;
        }
    }
}
