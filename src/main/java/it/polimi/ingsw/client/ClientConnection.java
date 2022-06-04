package it.polimi.ingsw.client;

import it.polimi.ingsw.client.controller.ClientController;
import it.polimi.ingsw.exceptions.EndOfChangesException;
import it.polimi.ingsw.modelChange.EndOfGameChange;
import it.polimi.ingsw.modelChange.LobbyChange;
import it.polimi.ingsw.modelChange.ModelChange;
import it.polimi.ingsw.packets.Packet;

import java.io.*;
import java.net.Socket;

public class  ClientConnection {
    private Socket socket;
    private ObjectInputStream socketIn;
    private ObjectOutputStream socketOut;
    private ClientController clientController;

    private Thread trackerThread;
    private boolean isActive;

    private String name;

    public String getName() {
        return name;
    }

    public ClientConnection(Socket socket) {
        this.socket = socket;
    }

    public boolean isActive() {
        return isActive;
    }

    public void close(){
        //stop connection tracker
        isActive = false;
        trackerThread.interrupt();

        /*first I deregister client from server, then I close socket*/
        System.out.println("Closing connection");
        clientController.detachConnection();
        try{
            socket.close();
        }catch (IOException e){
            System.err.println(e.getMessage());
        }

        System.out.println("Done!");
    }

    public void attachController(ClientController controller){
        this.clientController = controller;
    }

    public void send(Packet packet) throws IOException{
        synchronized (this) {
            socketOut.writeObject(packet);
            socketOut.flush();
            socketOut.reset();
        }

        /*each send of packet is followed by read of model change or pong message*/
        boolean waitEndOfChanges = true;
        while(waitEndOfChanges){
            try {
                waitModelChange();
            }
            catch (EndOfChangesException e){
                waitEndOfChanges = false;
            }
        }
    }

    public void waitModelChange() throws IOException{
        Object modelChange = new Object();

        try {
            modelChange = socketIn.readObject();
            clientController.changeModel((ModelChange) modelChange);
        }
        catch (ClassCastException e) {
            try {
                //after start, can receive only gameBoardChange or pong messages
                String fromServer = (String) modelChange;
                if (fromServer.equals("pong")) {
                    System.out.println("ClientConnection says: server sent pong");
                }
                else {
                    System.out.println("ClientConnection says: Error from server received: \n" + fromServer);
                }
            }
            catch (ClassCastException e2) {
                System.out.println("ClientConnection says: Error from server side");
            }
        }
        catch (ClassNotFoundException e) {
            System.out.println("ClientConnection says: error class not found ex");
        }
    }

    public void init() throws IOException {
        isActive = true;
        //client receives strings from server with readUTF() until it gets added to lobby
        //then it receives objects with readObject()
        socketOut = new ObjectOutputStream(socket.getOutputStream());
        socketOut.flush();

        socketIn = new ObjectInputStream(socket.getInputStream());

        trackerThread = new Thread( new ConnectionTracker(this, socketOut, socketIn));
        trackerThread.start();

        String fromServer = socketIn.readUTF();
        //new Thread(new ConnectionTracker(this, socketOut, socketIn)).start();

        while (!fromServer.equals("start")) {
            //if the pong message from server was received , client should wait for the next different message
            while (fromServer.equals("pong")){
                System.out.println("ClientConnection says: Client received from server:" + fromServer);
                fromServer = socketIn.readUTF();
            }
            if (fromServer.equals("config")) {
                //System.out.println("ClientConnection says: config started " + fromServer);
                //let client insert a configuration
                /*ask view to print the messages and request input*/
                boolean inputCorrect = false;
                while (!inputCorrect) {
                    int numOfPlayers = clientController.askNumOfPlayers();
                    //send configurations to the server
                    //guarantee that ConnectionTracker doesn't write in socket at the same time
                    synchronized (this) {
                            socketOut.writeObject((Object)numOfPlayers);
                            socketOut.flush();
                            socketOut.reset();
                    }

                    fromServer = socketIn.readUTF();

                    //if the pong message from server was received , client should wait for the next different message
                    while(fromServer.equals("pong")){
                        fromServer = socketIn.readUTF();
                    }
                    if (fromServer.equals("ok")) {
                        inputCorrect = true;
                        System.out.println("ClientConnection says: Client received from server:" + fromServer);
                    } else {
                        inputCorrect = false;
                        System.out.println("ClientConnection says: Error from server received:" + fromServer);
                        continue;
                    }
                }

                inputCorrect = false;

                while (!inputCorrect) {

                    String advancedSettings = clientController.askAdvancedSettings();

                    //connectionTracker and ClientConnection should not write in socket at the same time
                    synchronized (this) {
                        socketOut.writeObject(advancedSettings);
                        socketOut.flush();
                        socketOut.reset();
                    }

                    fromServer = socketIn.readUTF();
                    while (fromServer.equals("pong")) {
                        fromServer = socketIn.readUTF();
                    }
                    if (fromServer.equals("ok")) {
                        inputCorrect = true;
                        System.out.println("ClientConnection says: Client received from server: " + fromServer);
                    } else {
                        inputCorrect = false;
                        System.out.println("ClientConnection says: Error from server received: \n" + fromServer);
                    }

                }

            }
            if (fromServer.equals("name")) {
                boolean inputCorrect = false;

                while (!inputCorrect) {
                    name = clientController.askName();

                    //connectionTracker and ClientConnection should not write in socket at the same time
                    synchronized (this) {
                        socketOut.writeObject(name);
                        socketOut.flush();
                        socketOut.reset();
                    }

                    //Server added me to Lobby  if my name is ok
                    Object modelChange = new Object();

                    boolean waitingModelChange = true;
                    while (waitingModelChange) {
                        try {
                            modelChange = socketIn.readObject();
                            //can receive lobbychange, endofgamechange or ping or start or error
                            clientController.changeModel((LobbyChange) modelChange);
                            inputCorrect = true;
                        }
                        catch (ClassCastException e) {
                            try {
                                clientController.changeModel((EndOfGameChange) modelChange);
                                inputCorrect = true;
                                waitingModelChange = false;

                                //set fromServer to stop current thread
                                fromServer = "stop";
                            }
                            //TODO add management of GameBoardChange modelChange in case this is the client that tries to reconnect with the same name
                            catch (ClassCastException e2){
                                try {
                                    fromServer = (String) modelChange;
                                    if (fromServer.equals("pong")) {
                                        System.out.println("ClientConnection says: server sent pong");
                                        continue;
                                    } else if (fromServer.equals("start")) {
                                        waitingModelChange = false;
                                    } else {
                                        clientController.printMessage("Error from server received: \n" + fromServer);
                                        waitingModelChange = false;
                                    }
                                }
                                catch (ClassCastException e3) {
                                    System.out.println("ClientConnection says: error class cast ex");
                                }
                            }
                        }
                        catch (ClassNotFoundException e) {
                            System.out.println("ClientConnection says: error class not found ex");
                        }
                    }
                }
            }
            if (fromServer.equals("start")) {
                System.out.println("ClientConnection says: start received");
                clientController.setClientName(name);
                //command that starts the game
                clientController.startTurn();
            }
            else if (fromServer.equals("stop")){
                fromServer = "start";
                //set to start in order to exit from loop
            }
            else {
                fromServer = socketIn.readUTF();
            }
        }
    }
}