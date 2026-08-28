package edu.curtin.app;

import java.util.HashMap;
import java.util.logging.Logger;

public class TaskParent extends TaskStructure{
    TaskParent parent;
    HashMap<String, TaskStructure> subtasks;
    private static final Logger log = Logger.getLogger(TaskParent.class.getName());
    // hard to decide whether to make the hashMap be TaskParent, TaskStructure or TaskLeaf

    public TaskParent(String ID, String description)
    {
        this.ID = ID;
        this.description = description;
        subtasks = new HashMap<>();
        log.finest(() -> "Task parent created with ID: " + ID);
    }

    public TaskParent(TaskParent parent, String ID, String description)
    {
        this.parent = parent;
        this.ID = ID;
        this.description = description;
        subtasks = new HashMap<>();
        log.finest(() -> "Task parent (with parent) created with ID: " + ID);
    }

    public void addSubtask(TaskStructure task, TaskParent generalParent) throws WBSException
    {
        if((task != null) && (generalParent.findTask(task.ID) == null))
        {
            subtasks.put(task.ID, task);
            log.finer(() -> "Subtask ID:" + task.ID + "added to Parent: " + this.ID);
        }
        else
        {
            throw new WBSFormatException("Cannot add subtask. Subtask either already exists or is null.");
        }
    }

    @Override
    public void display(String indent)
    {
        String nextIndent = indent + "    "; // next indent for sub tasks
        System.out.println(String.format("%s%s: %s", indent, this.ID, this.description));
        if(subtasks != null)
        {
            for(TaskStructure task : subtasks.values())
            {
                task.display(nextIndent);
            }
        }
    }

    @Override
    public String printTask()
    {
        String printString;

        printString = String.format("; %s ; %s", this.ID, this.description);

        if(this.parent != null)
        {
            printString = String.format("%s %s", this.parent.ID, printString);
        }
        return printString;
    }

    @Override
    public int calcEstTime()
    {
        int totalTime = 0;
        
        if(subtasks != null)
        {
            log.fine(() -> "Calculating EstTime for subtasks for Parent" + this.ID);
            for(TaskStructure task : subtasks.values())
            {
                totalTime += task.calcEstTime();
            }
        }
        return totalTime;
    }

    @Override
    public TaskStructure findTask(String searchID)
    {
        log.fine(() -> "Attempting to find task with ID: " + searchID + "from parent: " + this.ID);
        TaskStructure foundTask = null;
        if(this.ID.equals(searchID)) // if the ID is the same as the tasks ID
        {
            return this; // return it
            
        }
        else
        {
            for(TaskStructure task : subtasks.values())
            {
                foundTask = task.findTask(searchID); // otherwise search the subtasks
            }
        }
        // TODO: maybe remove this if statement for logging, replace it when the findTask is used in the future
        if(foundTask == null)
        {
            log.finest(() -> "Could not find task with ID: " + searchID);
        }
        else
        {
            log.fine(() -> "Found task: " + searchID);
        }
        return foundTask;
    }
}
