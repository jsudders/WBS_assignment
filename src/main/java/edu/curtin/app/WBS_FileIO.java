package edu.curtin.app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

public class WBS_FileIO {
    private static final Logger log = Logger.getLogger(WBS_FileIO.class.getName());
    
    public TaskParent readWBS(String filename) throws IOException, WBSException // let output be task object and add exceptions
    {
        TaskParent generalParent = new TaskParent("WBS BREAKDOWN STRUCTURE", "");
        log.fine(() -> "Created generalParent");
        try(BufferedReader reader = new BufferedReader(new FileReader(filename)))
        {
            String line = reader.readLine();
            while(line != null)
            {
                line = line.strip();
                log.fine("Processing line: " + line);
                if(!line.isEmpty())
                {
                    String [] lineParts = line.split(";");
                    String parentTask = lineParts[0].strip();
                    log.info(() -> "ParentTask : " + parentTask);
                    String task = "";
                    String description = ""; // initialise
                    TaskParent existingParent = null;
                    Integer estimate = null;

                    if(!(parentTask.isBlank()) && !(parentTask.isEmpty()))
                    {
                        log.info(() -> "parentTask = " + parentTask);
                        existingParent = (TaskParent) generalParent.findTask(parentTask);

                        if(existingParent == null)
                        {
                            log.severe(() -> "Parent: " + parentTask + "does not exist");
                            throw new WBSException("The parent on line: " + line + " does not exist");
                        }
                    }

                    
                    /* case 0: ID of parent task
                    case 1: current task ID 
                    case 2: task description
                    case 3: only on tasks that are not broken down, effort estimate*/
            
                    log.info("BeforeStrip: " + lineParts[1]);
                    task = lineParts[1].strip();
                    log.info("Task : " + task);
                
                            

                    description = lineParts[2].strip();
                    log.info("Description : " + description);
                    
                    if(lineParts.length == 4)
                    {
                        estimate = Integer.parseInt(lineParts[3].strip());
                    }
                    else if (lineParts.length > 4)
                    {
                        throw new WBSFormatException("Unknown data column in file.");
                    }

                    
                    if(existingParent != null) // the task has a parent
                    {
                        if(lineParts.length <= 3) // the task does not have an effort category
                        {
                            TaskParent newTaskParent = new TaskParent(existingParent, task, description);
                            // create the new task with existing parent
                            existingParent.addSubtask(newTaskParent, generalParent);
                            //  add the task as a subtask
                        }
                        else if(lineParts.length == 4) // the task has an effort category, i.e. its a leaf
                        {
                            TaskLeaf newTaskLeaf = new TaskLeaf(existingParent, task, description);
                            // create a new leaf with the existing parent, task and description
                            existingParent.addSubtask(newTaskLeaf, generalParent);
                            // add the subtask to the parent
                            if(estimate != null)
                            {
                                newTaskLeaf.setEstTime(estimate);
                            }
                        }
                    }
                    else // there is no parent
                    {
                        if(lineParts.length <= 3) // the task does not have an effort category
                        {
                            TaskParent newTaskParent = new TaskParent(generalParent, task, description);
                            // create the new task with generalParent instead of existingParent
                            generalParent.addSubtask(newTaskParent, generalParent);
                            //  add the task as a subtask
                        }
                        else if(lineParts.length == 4) // the task has an effort category, i.e. its a leaf
                        {
                            TaskLeaf newTaskLeaf = new TaskLeaf(generalParent, task, description);
                            // create a new leaf with generalparent
                            generalParent.addSubtask(newTaskLeaf, generalParent);
                            // add subtask
                            if(estimate != null)
                            {
                                newTaskLeaf.setEstTime(estimate);
                            }
                            
                        }
                    }
                    //TODO: might be good to use a template pattern here for adding these
                    // also need to add estimate
                    
                }
                line = reader.readLine();
            }
        }
        catch (IOException io)
        {
            log.warning(() -> "Reading file failed with error: " + io);
            System.out.println("Error reading the file.");
        }
        return generalParent;
    }
    
}
