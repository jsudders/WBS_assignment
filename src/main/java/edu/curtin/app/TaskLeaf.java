package edu.curtin.app;

public class TaskLeaf extends TaskStructure {
    TaskParent parent;
    Integer estTime; // set as Integer so it can be null as it is optional

    public TaskLeaf(TaskParent parent, String ID, String description)
    {
        this.parent = parent;
        this.ID = ID;
        this.description = description;

    }

    public TaskLeaf(String ID, String description) // may not have parent
    {
        this.ID = ID;
        this.description = description;
    }

    public void setEstTime(int inTime)
    {
        estTime = inTime;
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
            return 0;
        }
        else
        {
            return estTime;
        }
    }

    @Override
    public TaskStructure findTask(String searchID)
    {
        if(this.ID.equals(searchID)) // if the ID is equal to the one being searched for
        {
            return this;
        }
        return null;
    }
}
