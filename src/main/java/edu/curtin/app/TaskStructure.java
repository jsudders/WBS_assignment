package edu.curtin.app;

import java.util.List;

public interface TaskStructure {
    
    void display(String indent);

    int calcEstTime();

    void findTask(List<String> tasks, String search);
    
}
/* Tasks that are not broken down have optional effort estimate. all tasks have an ID and a short description */