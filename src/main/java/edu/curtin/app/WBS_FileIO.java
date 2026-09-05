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
        TaskParent generalParent = new TaskParent("", "");
        log.fine(() -> "Created generalParent"); // initialise generalParent with no taskID and no description
        try(BufferedReader reader = new BufferedReader(new FileReader(filename))) // try to open a bufferedReader with a fileReader from given filename
        {
            String line = reader.readLine();
            while(line != null) // while there is still lines in the file
            {
                line = line.strip();
                log.log(Level.INFO, "Processing line: {0}", line); // strip the line and log the line that is being processed
                if(!line.isEmpty())
                {
                    String task;
                    String description;
                    TaskParent existingParent = null;
                    Integer estimate;
                    String [] lineParts = line.split(";", -1); // split the line with the limit as -1 so that blank estimates can be found
                    String parentTask = lineParts[0].strip(); // parent ID is set to the fist part of the line

                    if(!(parentTask.isBlank()) && !(parentTask.isEmpty())) // if the line has a parentTask
                    {
                        log.fine(() -> "\tParentTask = " + parentTask);
                        existingParent = (TaskParent) generalParent.findTask(parentTask); // find the taskStructure in the WBS owner

                        if(existingParent == null) // if the task cannot be found, through an error
                        {
                            log.severe(() -> "Parent: " + parentTask + "does not exist");
                            throw new WBSFormatException("The parent on line: " + line + " does not exist");
                        } // it is possible to change this to adding a new task parent (in case the file just has the definition of the parent further down)
                        //  however, this can be an issue if its due to user error instead of file formatting
                    }
                    task = lineParts[1].strip();
                    description = lineParts[2].strip();
                    // all taskStructures have this information, so preset task and description
                    
                    if(existingParent == null) // if the task is not a subtask of anything, set the parent to be the WBS owner
                    {
                        existingParent = generalParent;
                    }
                    
                    switch(lineParts.length)
                    {
                        case 3 -> {
                            // task does not have estimate category
                            TaskParent newTaskParent = new TaskParent(existingParent, task, description);
                            existingParent.addSubtask(newTaskParent, generalParent);
                            // create the taskParent and add it as a subtask to the taskparent
                        }
                        case 4 -> {
                            // the task does have estimate category
                            if(lineParts[3].equals("")) // if there is a line for estimates, but nothing in it, guard the Integer.valueOf whilst still adding the estimate
                            {
                                estimate = null; 
                            }
                            else
                            {
                                estimate = Integer.valueOf(lineParts[3].strip()); // if there is already an estimate present, strip the line and add it to the taskLeaf
                            }
                            TaskLeaf newTaskLeaf = new TaskLeaf(existingParent, task, description, estimate);
                            existingParent.addSubtask(newTaskLeaf, generalParent);
                            // create a new task leaf and add it as a subtask to the taskparent
                        }
                        default -> // task has less than 3 or more than 4 parts
                            throw new WBSFormatException("This line is in an unknown format." + line); 
                    }

                    
                }
                line = reader.readLine(); // read the next line
            }
        }
        catch (IOException io)
        {
            log.severe(() -> "Reading file failed with error: " + io);
            System.out.println("Error reading the file.");
        }
        return generalParent;
        // if an error occurs, such as IOException, the generalParent will be null and the program will be closed when this is processed
    }

    public void writeToWBS(String filename, TaskParent generalParent)
    {
        // try with resources file writer
        // loop through generalParent children and write to file, make sure to exclude generalParent itself
        try(FileWriter fw = new FileWriter(filename))
        {
            if(generalParent != null && fw != null) // if initialisation of filewriter worked and WBS structure is valid
            {
                if(!generalParent.subtasks.isEmpty()) // if the WBS owner has subtasks
                {
                    for(TaskStructure task : generalParent.subtasks.values()) // loop through each subtask and print the values
                    {
                        fw.write(task.printTask());
                    }
                }
            }
        }
        catch(IOException io) // catch any IO exception
        {
            log.severe("Error printing to file");
            System.out.println("Error printing to file");
            // as the file writing is only handled when the file is closed, this IOException is unlikely to matter as an IOException will already be thrown during the reading of the file
        }
    }
    
}
