package server;

import modelChange.LobbyChange;
import modelChange.ModelChange;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

public class Server {
    private int port;
    private ServerSocket serverSocket;
    private Map<String, Connection> waitingConnection = new HashMap<>();
    private List<VirtualView> playingClients = new ArrayList<VirtualView>();
    private int numOfPlayers = 0;
    private boolean advancedSettings;
    private int connections = 0;
    private boolean gameReady = false;
    public Connection currentConnection;

    public Server(int port) throws IOException{
        this.port=port;
        serverSocket = new ServerSocket(port);
    }

    public void addToLobby(Connection connection, String name){
//        System.out.println("Server says: before if: " + waitingConnection.keySet().stream().toList());
        if (waitingConnection.keySet().stream().map(s -> s.toLowerCase(Locale.ROOT)).collect(Collectors.toList()).contains(name.toLowerCase(Locale.ROOT))) {
            System.out.println("Server says: name already used");
            throw new IllegalArgumentException();
        }
        else {
            waitingConnection.put(name, connection);

            //notify other clients that list of clients in addToLobby has changed
            ModelChange lobbyChange = new LobbyChange(waitingConnection.keySet().stream().toList());
            for (String n : waitingConnection.keySet()) {
                synchronized (waitingConnection.get(n)) {
                    this.currentConnection = waitingConnection.get(n);
                    waitingConnection.get(n).send(lobbyChange);
                }
            }

            if (waitingConnection.size() == numOfPlayers) {
                gameReady = true;
                /*create game and other classes*/
                for (String n : waitingConnection.keySet()) {
                    synchronized (waitingConnection.get(n)) {
                        waitingConnection.get(n).notifyAll();
                    }
                }
                createGame();
            }
        }
//        System.out.println("Server says: after else:" + waitingConnection.keySet().stream().toList());
    }

    private void createGame(){
        for(Connection c : waitingConnection.values()){

        }
    }

    /*only for addToLobby*/
    public void deregisterConnectionFromLobby(Connection connection) {
        /*remove connection from waiting playingClients map*/
        for (String name: waitingConnection.keySet()){
            if (waitingConnection.get(name) == connection) {
                synchronized (connection){
                    waitingConnection.remove(name);
                    System.out.println("I deregistered connection named:" + name);
                }
            }
        }

        ModelChange lobbyChange = new LobbyChange(waitingConnection.keySet().stream().toList());

        for (String name: waitingConnection.keySet()){
            synchronized (waitingConnection.get(name)){
                waitingConnection.get(name).send(lobbyChange);
            }
        }

        connections--;
        System.out.println("I deregistered connection");

    }

    /*receives ConnectionStatusChange of that particular client that became inactive or active*/
    public void changeConnectionStatus(ModelChange playerConnectionStatus){
        for (VirtualView client: playingClients){
            client.update(playerConnectionStatus);
        }
    }

    public void run(){
        System.out.println("Server listening on port: " + port);

        while(true){
            try {
                /*receive new connection request*/
                Socket socket = serverSocket.accept();
                ObjectOutputStream socketOut = new ObjectOutputStream(socket.getOutputStream());
                socketOut.flush();
                ObjectInputStream socketIn = new ObjectInputStream(socket.getInputStream());

                //if server doesn't receive any message from client in 10 sec, then socket gets closed
                //socket.setSoTimeout(10*1000);

                System.out.println("Connection number: " + (connections+1));

                if (connections == 0){

                    Object fromClient = new Object();
                    String pingMessage = "";

                    try {
                        //if it is not a string , ClassCastException gets raised
                        fromClient = socketIn.readObject();
                        if (fromClient.equals("ping")) {
                            System.out.println("Server received Ping from Client");
                            //client sent ping message, server responds with pong
                            socketOut.writeUTF("pong");
                            socketOut.flush();
                            socketOut.reset();
                        }
                    }
                    catch(ClassNotFoundException | ClassCastException e){
                        System.out.println("Error received from first client");
                        }

                    /*allows client configurate the game settings since he is the first one*/
                    socketOut.writeUTF("config");
                    socketOut.flush();
                    socketOut.reset();

                    boolean numOfPlayersCorrect = false;

                    while (!numOfPlayersCorrect) {
                        try {
                            //exits while only after executing try correctly
                            try {
                                fromClient = socketIn.readObject();
                                //server waits an integer from client and not string
                                //if the string was sent, then ClassCastException is raised
                                numOfPlayers = (int) fromClient;
                                System.out.println("Server received from client: " + numOfPlayers);

                                if (numOfPlayers < 2 || numOfPlayers > 4)
                                    throw new IllegalArgumentException();

                                numOfPlayersCorrect = true;
                            } catch (ClassNotFoundException | ClassCastException e) {
                                try {
                                    //if it is not a string , ClassCastException gets raised
                                    pingMessage = (String) fromClient;
                                    if (pingMessage.equals("ping")) {
                                        //client sent ping message, server responds with pong
                                        socketOut.writeUTF("pong");
                                        socketOut.flush();
                                        socketOut.reset();
                                    }
                                    else {
                                        //throw exception asking client to insert again
                                        throw new IllegalArgumentException();
                                    }
                                } catch (ClassCastException e2) {
                                    throw new IllegalArgumentException();
                                }
                            }
                        }
                        catch (IllegalArgumentException e) {
                            socketOut.writeUTF("Incorrect number of players value. Please try again");
                            socketOut.flush();
                            socketOut.reset();
                            //the check is done also on the client side
                        }
                    }

                    socketOut.writeUTF("ok");
                    socketOut.flush();
                    socketOut.reset();

                    //equals true if the string is equal to "true" and false if string is equal to "false"
                    boolean settingsCorrect = false;

                    while (!settingsCorrect){
                        try{
                            fromClient = socketIn.readObject();
                            if ( ((String)fromClient).equals("true") ){
                                advancedSettings = true;
                                settingsCorrect = true;
                            }
                            else if (((String)fromClient).equals("false") ){
                                advancedSettings = false;
                                settingsCorrect = true;
                            }
                            else if ( ((String)fromClient).equals("ping") ){
                                //client sent ping message, server responds with pong
                                socketOut.writeUTF("pong");
                                socketOut.flush();
                                socketOut.reset();
                            }
                            else{
                                throw new IllegalArgumentException();
                            }
                        }
                        catch (ClassNotFoundException | ClassCastException | IllegalArgumentException e){
                            socketOut.writeUTF("Incorrect advanced settings value. Please try again");
                            socketOut.flush();
                            socketOut.reset();
                            //the check is done also on the client side
                        }
                    }

                    System.out.println("Server received from client: " + advancedSettings);
                    socketOut.writeUTF("ok");
                    socketOut.flush();
                    socketOut.reset();
                }

                connections++;
                new Thread(new Connection(socket, socketIn, socketOut, this)).start();
                System.out.println("Server created");

            } catch (IOException e){
                System.err.println("Connection error!" + e);
            }
        }
    }

    public boolean isGameReady(){
        return gameReady;
    }
}
