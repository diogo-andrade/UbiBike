package pt.ulisboa.tecnico.cmov.ubibike.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by diogo on 06-05-2016.
 */
public class UBIClient {
    private String _host;
    private int _port;
    private Socket _socket;

    public  UBIClient(String host, int port) throws Exception {
        this._host = host;
        this._port = port;
        this._socket = new Socket(_host, _port);
    }

    public String requestLogin(String email, String password) throws Exception {
        _socket.setSoTimeout(30000);
        //create output stream attached to socket
        PrintWriter outToServer = new PrintWriter(new OutputStreamWriter(_socket.getOutputStream()));
        //send msg to server
        outToServer.print("LOGIN"+" " + email + " " + password + "/EOM");
        outToServer.flush();

        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
        //read line from server
        String response[] = inFromServer.readLine().split("/EOM");

        System.out.println(response[0]);

        return response[0];
    }

    public void close() throws IOException {
        _socket.close();
    }
}
