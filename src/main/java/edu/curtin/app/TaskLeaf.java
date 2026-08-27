package edu.curtin.app;

import java.util.List;

public class TaskLeaf implements TaskStructure {
    String ID;
    String description;
    TaskParent parent;
    int estTime;

    @Override
    public void display(String indent)
    {

    }

    @Override
    public int calcEstTime()
    {
        return 9;
    }

    @Override
    public void findTask(List<String> tasks, String search)
    {

    }
}
