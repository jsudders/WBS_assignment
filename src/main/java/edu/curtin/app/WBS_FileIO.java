package edu.curtin.app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class WBS_FileIO {
    
    public void readWBS(String filename) throws IOException // let output be task object and add exceptions
    {
        try(BufferedReader reader = new BufferedReader(new FileReader(filename)))
        {
            String line = reader.readLine();
            while(line != null)
            {
                line = line.strip();
                if(!line.isEmpty())
                {
                    String [] lineParts = line.split(";");
                    String parentTask = lineParts[0];
                    String task = lineParts[1];
                    /* case 0: ID of parent task
                    case 1: current task ID 
                    case 2: task description
                    case 3: only on tasks that are not broken down, effort estimate*/

                    switch(lineParts.length)
                    {
                        case 2: // description
                            String description = lineParts[2];
                            break;

                        case 3: // effort estimate
                            int estimate = Integer.parseInt(lineParts[3]);
                            break;
                        default:
                            // throw new WBSFormatException


                    }
                }
                line = reader.readLine();
            }
        }
        catch (IOException io)
        {

        }
    }
    // return task structure
}
