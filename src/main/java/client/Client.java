package client;

import client.controller.ClientController;
import client.model.ClientGameBoard;
import client.view.Cli;
import client.view.View;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client{

    private Socket socket;
    private String serverIp;
    private int serverPort;
    private ClientGameBoard model;
    private ClientController controller;
    private View view;

    public Client(String ip, int port){
        this.serverIp = ip;
        this.serverPort = port;

        //create view, model, controller since when client receives "config"
        //it already needs to use those components to show strings to the view
        //TODO
        System.out.println("Do you want to use CLI or GUI?");
        Scanner stdin = new Scanner(System.in);
        if(stdin.nextLine().equals("CLI")){
            view = new Cli();
        }
        controller = new ClientController();
        //model is`not initialized , but it will be enough before the start of the game
        model = new ClientGameBoard();

        view.attachController(controller);
        controller.attachModel(model);
        //attach view to controller in order to show simple messages at the start
        controller.attachView(view);
        model.attachView(view);
    }

    public void run() throws IOException {
        Socket socket = new Socket(serverIp, serverPort);

        System.out.println("Connection established with server: " + socket.getRemoteSocketAddress());
        System.out.println("Waiting for configuration");
        ClientConnection connection = new ClientConnection(socket);
        controller.attachConnection(connection);
        connection.attachController(controller);
        connection.init();
    }
}
