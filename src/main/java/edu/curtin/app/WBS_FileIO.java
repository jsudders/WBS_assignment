package edu.curtin.app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashMap;
import java.io.FileNotFoundException;

public class WBS_FileIO {
    private static final Logger log = Logger.getLogger(WBS_FileIO.class.getName());
    
    public HashMap<String, TaskStructure> readWBS(String filename) throws IOException, WBSException, FileNotFoundException // let output be a hashmap of all the baseTasks (tasks with no parent set) and throw exceptions to be handled outside
    {
        try(BufferedReader reader = new BufferedReader(new FileReader(filename))) // try to open a bufferedReader with a fileReader from given filename
        {
            HashMap<String, TaskStructure> baseTasks = new HashMap<>();
            log.fine(() -> "Created baseTasks"); // initialise task map
            String line = reader.readLine();
            while(line != null) // while there is still lines in the file
            {
                line = line.strip();
                log.log(Level.INFO, "Processing line: \"{0}\"", line); // strip the line and log the line that is being processed
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
                        if(TaskStructure.findTask(baseTasks, parentTask) instanceof TaskLeaf) // guard in case the file is formatted incorrectly
                        {
                            throw new WBSFormatException("Task " + parentTask + " has subtasks but is meant to be a leaf.");
                        }

                        existingParent = (TaskParent) TaskStructure.findTask(baseTasks, parentTask);

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
                    
                    if(TaskStructure.findTask(baseTasks, task) != null)
                    {
                        throw new WBSFormatException("Cannot add subtask " + task + " as task already exists.");
                    }

                    switch(lineParts.length)
                    {
                        case 3 -> {
                            // task does not have estimate category
                            if(existingParent == null) // if theres no existing parent, add the task with no parent and put it in the baseTasks map
                            {
                                TaskParent newTaskParent = new TaskParent(task, description);
                                baseTasks.put(task, newTaskParent);
                            }
                            else // if it does have a parent, add the task with the parent and put it in the existing parents subtasks
                            {
                                TaskParent newTaskParent = new TaskParent(existingParent, task, description);
                                existingParent.addSubtask(newTaskParent, baseTasks);
                            }
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
                            if(existingParent == null)
                            {
                                TaskLeaf newTaskLeaf = new TaskLeaf(task, description, estimate);
                                baseTasks.put(task, newTaskLeaf);
                            }
                            else
                            {
                                TaskLeaf newTaskLeaf = new TaskLeaf(existingParent, task, description, estimate);
                                existingParent.addSubtask(newTaskLeaf, baseTasks); 
                            }
                        }
                        default -> // task has less than 3 or more than 4 parts
                            throw new WBSFormatException("This line is in an unknown format." + line); 
                    }
                }
                line = reader.readLine(); // read the next line
            }
            return baseTasks;
        }
        catch (FileNotFoundException file)
        {
            log.severe(() -> "Reading failed with error: " + file);
            System.out.println("File does not exist");
            throw file;
        }
        catch (WBSException io)
        {
            log.severe(() -> "Reading file failed with error: " + io.getMessage());
            System.out.println("Error reading the file.");
            throw io;
        }
        // if an error occurs, such as IOException, the baseTasks will not be returned and the program will be closed when this is processed
    }

    public void writeToWBS(String filename, HashMap<String, TaskStructure> baseTasks)
    {
        // if the baseTasks is already empty/null (should have had an error thrown before this), do not attempt to write to file as this will override the data (whilst incorrect) and give an empty file
        if(baseTasks.isEmpty() || baseTasks == null)
        {
            System.out.println("WBS doesnt exist. Quitting now.");
            return;
        }
        try(FileWriter fw = new FileWriter(filename)) // try with resources file writer
        {
            for(TaskStructure task : baseTasks.values()) // for each baseTask, print it (if its a parent task, it will also print subtasks)
            {
                fw.write(task.printTask());
            }
            System.out.println("File successfully written to.");

            log.severe("BaseTasks is empty or file is null");
            System.out.println("Error printing to file.");
        }
        catch(IOException io) // catch any IO exception
        {
            log.severe("Error printing to file");
            System.out.println("Error printing to file: " + io.getMessage());
            // as the file writing is only handled when the file is closed, this IOException is unlikely to matter as an IOException will already be thrown during the reading of the file
        }

        System.out.println("Goodbye.");

    }
    
}
