package edu.curtin.app;
import java.util.List;
public interface EffortEstimate {
    int getEffortEstimate(TaskLeaf task, List<Integer> estimates);
}
