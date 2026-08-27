package edu.curtin.app;

public abstract class TaskStructure {
    protected String ID;
    protected String description;

    abstract void display(String indent);

    abstract int calcEstTime();

    abstract TaskStructure findTask(String search);

    abstract String printTask();
    
}
/* Tasks that are not broken down have optional effort estimate. all tasks have an ID and a short description */
/*
TODO: this format of searching seems to only work if I extend TaskParent rather than extending TaskStructure as taskStructure is an interface and cannot store variables
this is because all of the ID's must be unique which makes things difficult
 */