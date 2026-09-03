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
            if(!this.parent.ID.equals("")) // if the parent is not the overall parent (helps print structure)
            {
                printString = String.format("%s %s", this.parent.ID, printString);
            }
        }
        printString += "\n";
        if(subtasks != null && !subtasks.isEmpty())
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
            if(subtasks != null)
            {
                log.finest("Task ID: " + this.ID + " has subtasks");
                for(TaskStructure task : subtasks.values())
                {
                    if(foundTask == null)
                    {
                        foundTask = task.findTask(searchID); // otherwise search the subtasks
                    }
                }
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

    public List<TaskLeaf> findRecursiveLeaves (List<TaskLeaf> leafList) throws WBSException
    {
        if(this.subtasks != null) // shouldn't happen if IO happens correctly
        {
            for (TaskStructure task : this.subtasks.values())
            {
                switch (task) {
                    case TaskParent taskParent -> 
                        {
                            leafList = taskParent.findRecursiveLeaves(leafList);
                            log.log(Level.INFO, "taskParent {0} found in recursives of {1}, now performing recursiveleaf on this.", new Object[]{taskParent.ID, this.ID});
                        }   
                    case TaskLeaf taskLeaf -> {
                        if(!(leafList.contains(taskLeaf)))
                        {
                            leafList.add(taskLeaf);
                        }
                        log.log(Level.INFO, "taskleaf {0} added to recursive list of {1}", new Object[]{taskLeaf.ID, this.ID});
                    }
                    default -> {
                        throw new WBSLogicException("Catastophic: Task is not formatted correctly.");
                    }
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
