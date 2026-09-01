package edu.curtin.app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
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
                log.log(Level.FINE, "Processing line: {0}", line);
                if(!line.isEmpty())
                {
                    String [] lineParts = line.split(";");
                    String parentTask = lineParts[0].strip();
                    log.info(() -> "ParentTask : " + parentTask);
                    String task;
                    String description;
                    TaskParent existingParent = null;
                    Integer estimate;

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
            
                    task = lineParts[1].strip();
                    description = lineParts[2].strip();
                    
                    if(existingParent == null)
                    {
                        existingParent = generalParent;
                    }
                    
                    switch(lineParts.length)
                    {
                        case 3 -> {
                            // task does not have estimate category
                            TaskParent newTaskParent = new TaskParent(existingParent, task, description);
                            existingParent.addSubtask(newTaskParent, generalParent);
                        }
                        case 4 -> {
                            // the task does have estimate category
                            estimate = Integer.valueOf(lineParts[3].strip());
                            TaskLeaf newTaskLeaf = new TaskLeaf(existingParent, task, description, estimate);
                            existingParent.addSubtask(newTaskLeaf, generalParent);
                        }
                        default -> // task has less than 3 or more than 4 parts
                            throw new WBSFormatException("This line is in an unknown format." + line);
                    }

                    
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

    public void writeToWBS(String filename, TaskParent generalParent)
    {
        // try with resources print writer
        // loop through generalParent children and write to file, make sure to exclude generalParent itself
        try(FileWriter fw = new FileWriter(filename))
        {
            if(generalParent != null && fw != null)
            {
                if(!generalParent.subtasks.isEmpty())
                {
                    for(TaskStructure task : generalParent.subtasks.values())
                    {
                        fw.write(task.printTask());
                    }
                }
            }
        }
        catch(IOException io)
        {
            log.severe("Error printing to file");
            System.out.println("Error printing to file");
        }
    }
    
}
