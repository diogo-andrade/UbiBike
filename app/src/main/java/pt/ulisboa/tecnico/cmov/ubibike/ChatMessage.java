package pt.ulisboa.tecnico.cmov.ubibike;

import java.io.Serializable;

/**
 * Created by gae on 03/05/2016.
 */
public class ChatMessage implements Serializable{
    public boolean left;
    public String message;

    public ChatMessage(boolean left, String message) {
        super();
        this.left = left;
        this.message = message;
    }
}