package edu.curtin.app;
import java.util.logging.Logger;

public class TaskLeaf extends TaskStructure {
    private Integer estTime; // set as Integer so it can be null as it is optional
    private static final Logger log = Logger.getLogger(TaskParent.class.getName());

    public TaskLeaf(TaskParent parent, String ID, String description, Integer estTime)
    {
        this.parent = parent;
        this.ID = ID;
        this.description = description;
        this.estTime = estTime;
        log.info(() -> "Created TaskLeaf with ID: " + ID + " under parent: " + parent.ID);
        // create leaf object with a parent and log it
    }

    public TaskLeaf(String ID, String description, Integer estTime)
    {
        this.ID = ID;
        this.description = description;
        this.estTime = estTime;
        log.info(() -> "Created TaskLeaf with ID: " + ID + " as a base task.");
        // create leaf object that has no parent
    }

    public Integer getEstTime()
    {
        return this.estTime;
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
        if(estTime != null) // if there is an estimated time, add this to the string
        {
            printString = String.format("%s %d", printString, estTime); 
        }
        if(hasParent()) // if there is a parent, add this to the string
        {
            printString = String.format("%s %s", this.parent.ID, printString); 
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
        log.finest(() -> "Search ID: " + searchID + " is not equal to task ID " + this.ID);
        return null; // otherwise return null
    }
}
