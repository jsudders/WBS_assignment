package edu.curtin.app;

public class WBSFormatException extends WBSException {
    public WBSFormatException(String msg)
    {
        super(msg);
    }
    //WBSFormatException class, used when the file is not formatted correctly or user tries to enter data which does not comply with the WBS Structure
}
