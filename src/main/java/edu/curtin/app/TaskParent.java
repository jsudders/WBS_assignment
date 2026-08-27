package edu.curtin.app;

import java.util.HashMap;
import java.util.List;

public class TaskParent implements TaskStructure {
    TaskParent parent;
    String ID;
    String description;
    HashMap<String, TaskParent> subtasks;

    public TaskParent(String ID, String description)
    {

    }

    public TaskParent(String parent, String ID, String description)
    {

    }

    @Override
    public void display(String indent)
    {

    }

    @Override
    public int calcEstTime()
    {
        return 6;
    }

    @Override
    public void findTask(List<String> tasks, String search)
    {

    }
}
