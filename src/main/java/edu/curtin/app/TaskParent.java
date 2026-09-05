package edu.curtin.app;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
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
        log.info(() -> "General Parent created");
        // create a taskparent with empty subtasks hashmap and no parent. this is only used for generalParent
        // however if I were to change the structure to replace generalParent with a list of the taskParents without parents, this would be used for their bases
    }
    public TaskParent(TaskParent parent, String ID, String description)
    {
        this.parent = parent;
        this.ID = ID;
        this.description = description;
        subtasks = new HashMap<>();
        log.info(() -> "Task parent (with parent) created with ID: " + ID);
        // create taskParent with empty subtasks hashmap (no duplicates allowed) and log it
    }

    public void addSubtask(TaskStructure task, TaskParent generalParent) throws WBSException
    {
        if((task != null) && (generalParent.findTask(task.ID) == null)) // if the task exists and it is not already in the WBS structure
        {
            subtasks.put(task.ID, task);
            log.finer(() -> "Subtask ID:" + task.ID + "added to Parent: " + this.ID);
            // add the task to the taskparents subtask list and log the addition
        }
        else
        {
            throw new WBSFormatException("Cannot add subtask. Subtask either already exists or is null.");
            // if the task is null or already exists in the structure, throw an exception
        }
    }

    @Override
    public void display(String indent)
    {
        String nextIndent = "";
        if(!this.ID.equals("")) // if the object is not the overall parent object, add an indent for the next in the structure
        {
            nextIndent = indent + "    "; 
        }
         // next indent for sub tasks
        System.out.println(String.format("%s%s: %s", indent, this.ID, this.description)); // format the task parent for output
        if(subtasks != null) // if the taskparent has subtasks,
        {
            for(TaskStructure task : subtasks.values()) // loop through each subtask and display it with the indent
            {
                task.display(nextIndent);
            }
        }
    }

    @Override
    public String printTask()
    {
        String printString;

        printString = String.format("; %s ; %s", this.ID, this.description); // format the taskparent for not having a parent

        if(this.parent != null)
        {
            if(!this.parent.ID.equals("")) // if the parent is not null and not the overall parent (helps print structure)
            {
                printString = String.format("%s %s", this.parent.ID, printString); // add the parent to the print string
            }
        }
        printString += "\n"; // format for the next structure
        if(subtasks != null && !subtasks.isEmpty()) // if the parent has subtasks, then loop through each subtask and print it also
        {
            for(TaskStructure task : subtasks.values())
            {
                printString = String.format("%s%s", printString, task.printTask());
            }
        }
        return printString;
    }

    @Override
    public int calcEstTime()
    {
        int totalTime = 0;
        
        if(subtasks != null && !subtasks.isEmpty()) // if the parent has subtasks, loop through them and add their estimated time to the parent's overall tme
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
            if(subtasks != null && !subtasks.isEmpty()) // if the task has subtasks, loop through them and check if they are equal to the search task
            {
                log.log(Level.FINEST, "Task ID: {0} has subtasks", this.ID);
                for(TaskStructure task : subtasks.values())
                {
                    if(foundTask == null)
                    {
                        foundTask = task.findTask(searchID); // otherwise search the subtasks
                    }
                }
            }
        }
        return foundTask;
    }

    public List<TaskLeaf> findRecursiveLeaves (List<TaskLeaf> leafList) throws WBSException
    {
        if(this.subtasks != null && !subtasks.isEmpty()) // if the taskparent has subtasks, loop through each of the subtasks
        {
            for (TaskStructure task : this.subtasks.values())
            {
                switch (task)
                {
                    case TaskParent taskParent ->
                    {
                        log.log(Level.FINE, "taskParent {0} found in recursives of {1}, now performing recursiveleaf on this.", new Object[]{taskParent.ID, this.ID});
                        leafList = taskParent.findRecursiveLeaves(leafList);
                        // if the subtask is another task parent, perform the recursiveleaves on this, add them to the list, and log it
                    }
                    case TaskLeaf taskLeaf -> // whilst the only other possibility is for the task to be a taskLeaf, this structure (not using default) allows us to cast to taskLeaf
                    {
                        if(!(leafList.contains(taskLeaf)))
                        {
                            leafList.add(taskLeaf); // if the subtask is a leaf and its not already in the leaflist (shouldn't be), add it to the leaflist
                        }
                        log.log(Level.INFO, "taskleaf {0} added to recursive list of {1}", new Object[]{taskLeaf.ID, this.ID});
                    }
                    default ->{}// this is impossible
                }
            }
        }
        else
        {
            log.severe(() -> "Task with ID: " + this.ID + " has no subtasks. Task should not be a parent.");
            throw new WBSLogicException("Catastrophic: Task is not formatted correctly.");
        }
        return leafList;
    }
}
