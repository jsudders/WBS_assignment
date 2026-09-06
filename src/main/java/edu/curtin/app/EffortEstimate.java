package edu.curtin.app;
import java.util.List;
import java.util.Scanner;
public interface EffortEstimate {
    int getEffortEstimate(Scanner sc, TaskLeaf task, List<Integer> estimates);

}
