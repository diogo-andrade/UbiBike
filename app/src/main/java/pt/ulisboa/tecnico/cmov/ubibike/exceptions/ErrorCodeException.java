package pt.ulisboa.tecnico.cmov.ubibike.exceptions;

/**
 * Created by diogo on 11-05-2016.
 */
public class ErrorCodeException extends Exception {
    int code;
    //Parameterless Constructor
    public ErrorCodeException() {}

    //Constructor that accepts a message
    public ErrorCodeException(int code)
    {
        super(code + "");
        this.code = code;
    }

    public int getCode(){
        return code;
    }
}
