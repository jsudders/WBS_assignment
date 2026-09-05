package edu.curtin.app;
import java.util.logging.Logger;

public class TaskLeaf extends TaskStructure {
    TaskParent parent;
    Integer estTime; // set as Integer so it can be null as it is optional
    private static final Logger log = Logger.getLogger(TaskParent.class.getName());

    public TaskLeaf(TaskParent parent, String ID, String description, Integer estTime)
    {
        this.parent = parent;
        this.ID = ID;
        this.description = description;
        this.estTime = estTime;
        log.info(() -> "Created TaskLeaf with ID: " + ID + " under parent: " + parent.ID);
        // create leaf object and log it
    }

    public void setEstTime(int inTime) throws WBSException
    {
        if(inTime > 0)
        {
            estTime = inTime;
            System.out.println("New effort estimate for task " + this.ID + " = " + inTime);
            // set the estimated time and print it
            // this action is already logged in performEstimates
        }
        else
        {
            log.warning(() -> "Invalid time (" + inTime + ") set for ID: " + this.ID);
            throw new WBSFormatException("Invalid estimated time for " + this.ID + ". Time must be a positive integer.");
            // unlikely to happen as there are guards in place beforehand
        }
    }

    @Override
    public void display(String indent)
    {
        String displayString;
        displayString = String.format("%s%s: %s", indent, this.ID, this.description); // set the default display string for a leaf with no estimated time
        if(estTime != null)
        {
            displayString = String.format("%s = %d", displayString, this.estTime); // if the leaf has an estimated time, add this to the display
        }

        System.out.println(displayString); // print the formatted string
    }

    @Override
    public String printTask()
    {
        String printString;

        printString = String.format("; %s ; %s ;", this.ID, this.description); // format the printString for the case that the leaf has no parent and no estimated time
        if(estTime != null)
        {
            printString = String.format("%s %d", printString, estTime); // if there is an estimated time, add this to the string
        }
        if(parent != null)
        {
            if(!this.parent.ID.equals("")) // if the parent is not the general parent. skips the space of the parent for better fileIO format
            {
                printString = String.format("%s %s", this.parent.ID, printString); // if there is a parent, add this to the string
            }
        }
        printString += "\n"; // format for the next entry
        return printString;
    }
    

    @Override
    public int calcEstTime()
    {
        if(estTime == null)
        {
            log.fine(() -> "Estimated time for ID " + this.ID + "is null"); 
            return 0; // return 0 if there is no estimated time
        }
        else
        {
            log.fine(() -> "Successfully returned time for ID " + this.ID);
            return estTime; // otherwise return the time
        }
    }

    @Override
    public TaskStructure findTask(String searchID)
    {
        if(this.ID.equals(searchID)) // if the ID is equal to the one being searched for
        {
            log.info(() -> "Found ID " + searchID);
            return this; // return the object and log it
        }
        log.fine(() -> "Search ID: " + searchID + " is not equal to task ID " + this.ID);
        return null; // otherwise return null
    }
}
