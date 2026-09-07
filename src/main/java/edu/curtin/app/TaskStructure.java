package edu.curtin.app;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public abstract class TaskStructure {
    protected String ID;
    protected String description;
    protected TaskParent parent;

    abstract void display(String indent);

    abstract int calcEstTime();

    abstract TaskStructure findTask(String search);

    abstract String printTask();

    public boolean hasParent() // default function for taskLeaf and taskParent
    {
        return (this.parent != null);
    }

    public static int calcTotalTime(HashMap<String, TaskStructure> baseTasks) // static function for calculating the totalTime in all of the tasks, given the baseTask list
    {
        int totalTime = 0;
        for(TaskStructure task : baseTasks.values())
        {
            totalTime += task.calcEstTime();
        }
        return totalTime;
    }
    public static int calcUnknownTasks(HashMap<String, TaskStructure> baseTasks) // static function for calculating all unknown tasks, given the base task list
    {
        int unknownTasks = 0; // preset unknown tasks to 0
        List<TaskLeaf> leafList = new ArrayList<>(); // initialise array list for the recursive leaf list to go into
        try // findRecursiveLeaves can throw a WBSException
        {
            for(TaskStructure task : baseTasks.values())
            {
                switch(task)
                {
                    case TaskParent parentTask ->
                    {
                        leafList = parentTask.findRecursiveLeaves(leafList); // attempt to find all the leaves of the WBS structure
                    }
                    case TaskLeaf leafTask ->
                    {
                        leafList.add(leafTask);
                    }
                    default -> {} // not possible
                }
            }
            
            for(TaskLeaf leaf : leafList)
            {
                if(leaf.getEstTime() == null) // for each leaf, if there is no estimated time for the task, add to the unknown tasks
                {
                    unknownTasks += 1;
                }
            }
        }
        catch(WBSException e) // if the recursiveleaves fail, then there is a catastrophic error in the task structure. print this error and set unknown tasks to -1 for this error to be handled elsewhere
        {
            System.out.println(e.getMessage());
            unknownTasks = -1;
        }
        return unknownTasks;
    }

    public static TaskStructure findTask(HashMap<String, TaskStructure> baseTasks, String searchID) // static method to search through all tasks in the structure to find the searched task
    {
        TaskStructure foundTask;
        foundTask = baseTasks.get(searchID); // see if the foundTask is already in the baseTasks map
        if(foundTask == null)
        {
            for (TaskStructure task : baseTasks.values()) // search through all of the baseTasks and subtasks for the search term
            {
                if (foundTask == null)
                {
                    foundTask = task.findTask(searchID);
                }
            }
        }
        return foundTask;
    }
}
/* Tasks that are not broken down have optional effort estimate. all tasks have an ID and a short description */
