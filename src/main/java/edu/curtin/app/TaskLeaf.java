package edu.curtin.app;
import java.util.logging.Logger;

public class TaskLeaf extends TaskStructure {
    TaskParent parent;
    Integer estTime; // set as Integer so it can be null as it is optional
    private static final Logger log = Logger.getLogger(TaskParent.class.getName());

    public TaskLeaf(TaskParent parent, String ID, String description)
    {
        this.parent = parent;
        this.ID = ID;
        this.description = description;
        log.fine(() -> "Created TaskLeaf with ID: " + ID + " under parent: " + parent.ID);
    }

    public TaskLeaf(String ID, String description) // may not have parent
    {
        this.ID = ID;
        this.description = description;
    }

    public void setEstTime(int inTime) throws WBSException
    {
        if(inTime > 0)
        {
            estTime = inTime;
            log.fine(() -> "Time for ID " + this.ID + "set to " + inTime);
        }
        else
        {
            log.warning(() -> "Invalid time (" + inTime + ") set for ID: " + this.ID);
            throw new WBSFormatException("Invalid estimated time for " + this.ID + ". Time must be a positive integer.");
            
        }
    }

    @Override
    public void display(String indent)
    {
        String displayString;
        displayString = String.format("%s%s: %s", indent, this.ID, this.description);
        if(estTime != null)
        {
            displayString = String.format("%s = %f", displayString, this.estTime);
        }

        System.out.println(displayString);
    }

    @Override
    public String printTask()
    {
        String printString;

        printString = String.format("; %s ; %s", this.ID, this.description);
        if(estTime != null)
        {
            printString = String.format("%s ; %d", printString, estTime);
        }
        if(parent != null)
        {
            printString = String.format("%s %s", this.parent.ID, printString);
        }
        return printString;
    }
    

    @Override
    public int calcEstTime()
    {
        if(estTime == null)
        {
            log.info(() -> "Estimated time for ID " + this.ID + "is null");
            return 0;
        }
        else
        {
            log.fine(() -> "Successfully returned time for ID " + this.ID);
            return estTime;
        }
    }

    @Override
    public TaskStructure findTask(String searchID)
    {
        if(this.ID.equals(searchID)) // if the ID is equal to the one being searched for
        {
            log.info(() -> "Found ID " + searchID);
            return this;
        }
        log.fine(() -> "Search ID: " + searchID + " is not equal to task ID " + this.ID);
        return null;
    }
}
