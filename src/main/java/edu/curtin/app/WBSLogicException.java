package edu.curtin.app;

public class WBSLogicException extends WBSException {
    public WBSLogicException(String msg)
    {
        super(msg);
    }
    // occurs when something went wrong with the addition of tasks to the task structure. This is an internal error and is used mostly as a "just in case" for the final product
}
