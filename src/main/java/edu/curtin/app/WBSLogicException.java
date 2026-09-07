package edu.curtin.app;

public class WBSLogicException extends WBSException {
    public WBSLogicException(String msg)
    {
        super(msg);
    }
    //WBSLogicException class, used when the file is formatted correctly, but it does not make sense for WBS structure, or when a user tries to input data that does not comply with the WBS structure
}
