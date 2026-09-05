package edu.curtin.app;

import java.util.ArrayList;
import java.util.List;

public abstract class TaskStructure {
    protected String ID;
    protected String description;

    abstract void display(String indent);

    abstract int calcEstTime();

    abstract TaskStructure findTask(String search);

    abstract String printTask();

    public static int calcUnknownTasks(TaskParent generalParent)
    {
        int unknownTasks = 0; // preset unknown tasks to 0
        List<TaskLeaf> leafList = new ArrayList<>(); // initialise array list for the recursive leaf list to go into
        try // findRecursiveLeaves can throw a WBSException
        {
            leafList = generalParent.findRecursiveLeaves(leafList); // attempt to find all the leaves of the WBS structure
            for(TaskLeaf leaf : leafList)
            {
                if(leaf.estTime == null) // for each leaf, if there is no estimated time for the task, add to the unknown tasks
                {
                    unknownTasks += 1;
                }
            }
        }
        catch(WBSException e) // if the recursiveleaves fail, then there is a catastrophic error in the task structure. print this error and set unknown tasks to -1 for this error to be handled elsewhere
        {
            System.out.println(e);
            unknownTasks = -1;
        }
        return unknownTasks;
    }
}
/* Tasks that are not broken down have optional effort estimate. all tasks have an ID and a short description */
