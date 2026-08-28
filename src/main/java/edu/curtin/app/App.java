package edu.curtin.app;

import java.io.IOException;
import java.util.logging.Logger;

public class App {
    // either create a list of TaskStructure/Parents that don't have any parents or create a TaskStructure or TaskParent to house them all
    TaskParent generalParent = new TaskParent(null, "", "");
    private static final Logger log = Logger.getLogger(App.class.getName());

    public static void main(String[] args) {
        // if(args.length != 1) 
        // {
        //     throw new InvalidParameterException("Please only have one argument, or surround the file path in quotes.");
        //  } // Error
        // else
        // {
        //     String filename = args[0];
        //     new App().menu(filename);
        // }
        // this isn't working for now, so i will fix it later
        String filename = "wbs_data.txt";
        new App().menu(filename);
    }

    public void menu(String filename)
    {
        boolean running = true;
        while(running)
        {
            while(true) // while no valid file has been entered
            {
                try
                {
                    WBS_FileIO fileIO = new WBS_FileIO();
                    fileIO.readWBS(filename);
                }
                catch(IOException io)
                {
                    System.out.println("Cannot open file. File is invalid. Please try again.");
                }
                catch(WBSException fe)
                {
                    System.out.println(fe + ". Quitting program now.");
                    running = false;
                    break;
                }
                catch(Exception e)
                {
                    log.severe(() -> "Unknown exception " + e);
                    System.out.println("Exception: " + e);
                    running = false;
                    break;
                }
    
            }

        }
    }
}
