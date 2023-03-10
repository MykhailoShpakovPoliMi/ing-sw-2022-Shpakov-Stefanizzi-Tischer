package it.polimi.ingsw.client.model;


import it.polimi.ingsw.client.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
public class ClientGameBoard {
    private View view;
    private List<ClientIsland> islands;
    private List<ClientCloud> clouds;
    private int currentCharacter;
    private ClientCharacter playedCharacters[];
    private int positionOfMotherNature;
    private int numOfCoins;
    private String currentPlayerName = "a";
    private ArrayList<ClientPlayer> players;
    private List<String> userNames;
    private String clientName = "b";

    private boolean advancedSettings;
    private AtomicBoolean isGameOn = new AtomicBoolean(false);

    public void showOnView(){
        if(islands!=null && clouds!=null && (!advancedSettings || playedCharacters!=null) && players!=null ){
            view.showModel(this);
        }
    }

    /*Mike: inside each set method we need to update the view*/
    public List<ClientIsland> getIslands() {
        return islands;
    }

    public String getClientName() {
        return clientName;
    }

    public void setIslands(List<ClientIsland> islands) {
        this.islands = islands;
    }

    public List<ClientCloud> getClouds() {
        return clouds;
    }

    public ClientCloud getCloud(int numOfCloud) { return clouds.get(numOfCloud); }

    public void setClouds(List<ClientCloud> clouds) {
        this.clouds = clouds;
    }

    public ClientCharacter getCurrentCharacter() {
        return playedCharacters[currentCharacter];
    }

    public void setCurrentCharacter(int currentCharacter) {
        this.currentCharacter = currentCharacter;
    }

    public ClientCharacter[] getPlayedCharacters() {
        return playedCharacters;
    }

    public void setPlayedCharacters(ClientCharacter[] playedCharacters) {
        this.playedCharacters = playedCharacters;
    }

    public int getPositionOfMotherNature() {
        return positionOfMotherNature;
    }

    public void setPositionOfMotherNature(int positionOfMotherNature) {
        this.positionOfMotherNature = positionOfMotherNature;
    }

    public void printMessage(String message){
        view.printMessage(message);
    }
    public int getNumOfCoins() {
        return numOfCoins;
    }

    public void setNumOfCoins(int numOfCoins) {
        this.numOfCoins = numOfCoins;
    }

    public String getCurrentPlayerName() {
        return currentPlayerName;
    }

    public void setCurrentPlayerName(String currentPlayerName) {
        this.currentPlayerName = currentPlayerName;
        showOnView();
        view.startTurn();
    }

    public ClientPlayer getPlayer(String playerName) {
        for(ClientPlayer p : players){
            if(p.getName().equals(playerName)){
                return p;
            }
        }
        throw new IllegalArgumentException();
    }

    public void setPlayers(ArrayList<ClientPlayer> players) {
        boolean statusChanged = false;

        if (getPlayers() != null) {
            for (int i = 0; i < getPlayers().size(); i++) {
                if (this.players.get(i).getConnectionStatus() != players.get(i).getConnectionStatus()) {
                    statusChanged = true;
                }
            }
        }

        this.players = players;

        if (statusChanged) {
            showOnView();
        }
    }

    public void setPlayerStatus(String name, boolean connectionStatus) {
        if (getPlayer(name).getConnectionStatus() != connectionStatus) {
            getPlayer(name).setConnectionStatus(connectionStatus);
            showOnView();
        }
    }

    public ArrayList<ClientPlayer> getPlayers() {
        return players;
    }

    public void setUserNames(List<String> userNames) {
        this.userNames = userNames;

        view.showLobby(userNames);

    }

    public void attachView(View view){
        this.view = view;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public boolean isGameOn() {
        return isGameOn.get();
    }

    public void setGameOn(boolean val){
        isGameOn.set(val);
    }

    public void setAdvancedSettings(boolean advancedSettings) {
        this.advancedSettings = advancedSettings;
    }

    public boolean getAdvancedSettings() {
        return advancedSettings;
    }
}
