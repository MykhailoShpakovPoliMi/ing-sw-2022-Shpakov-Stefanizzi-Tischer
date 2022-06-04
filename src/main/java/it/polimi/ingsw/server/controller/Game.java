package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.exceptions.EndOfChangesException;
import it.polimi.ingsw.exceptions.WrongActionException;
import it.polimi.ingsw.modelChange.ExceptionChange;
import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.packets.Packet;
import it.polimi.ingsw.server.model.Character;
import it.polimi.ingsw.exceptions.NoEntryException;
import it.polimi.ingsw.exceptions.RepeatedAssistantRankException;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Game implements GameForClient{
    private static Game instanceOfGame;
    private GameBoard gameBoard;
    private ArrayList<Player> players;
    private boolean advancedSettings;
    private Game(){}
    private int studentMove;
    private boolean motherNatureMove;
    private boolean useCloudMove;

    public static Game getInstanceOfGame() {
        if(instanceOfGame==null){
            instanceOfGame = new Game();
        }
        return instanceOfGame;
    }

    public void init(List<String> playersNames, boolean advancedSettings, CharacterDeck characterDeck){
        players = new ArrayList<Player>();
        switch (playersNames.size()) {
            case 2:
                this.players.add(new Player(playersNames.get(0), TowerColor.WHITE, AssistantType.ONE, 8));
                this.players.add(new Player(playersNames.get(1), TowerColor.BLACK, AssistantType.TWO, 8));
                break;
            case 3:
                this.players.add(new Player(playersNames.get(0), TowerColor.WHITE, AssistantType.ONE, 6));
                this.players.add(new Player(playersNames.get(1), TowerColor.BLACK, AssistantType.TWO, 6));
                this.players.add(new Player(playersNames.get(2), TowerColor.GREY, AssistantType.THREE, 6));
                break;
            case 4:
                this.players.add(new Player(playersNames.get(0), TowerColor.WHITE, AssistantType.ONE, 8));
                this.players.add(new Player(playersNames.get(1), TowerColor.BLACK, AssistantType.TWO, 8));
                this.players.add(new Player(playersNames.get(2), TowerColor.WHITE, AssistantType.THREE, 0));
                this.players.add(new Player(playersNames.get(3), TowerColor.BLACK, AssistantType.FOUR, 0));
                break;
            default:
                throw new InvalidParameterException();
        }

        gameBoard = GameBoard.getInstanceOfGameBoard();
        gameBoard.init(this, playersNames.size());

        this.advancedSettings = advancedSettings;
        if (advancedSettings) {
            Character[] playedCharacters = new Character[3];
            for (int i = 0; i < 3; i++) {
                playedCharacters[i] = characterDeck.popCharacter();
                playedCharacters[i].initialFill(this);
                gameBoard.setPlayedCharacters(playedCharacters);
            }
        }

        gameBoard.setCurrentCharacterToDefault(new Character());
        gameBoard.getCurrentCharacter().initialFill(this);

        /*refill assistants of players and distribute 1 coin in case of adv settings*/
        for (Player p : players) {
            gameBoard.refillAssistants(p);
            gameBoard.refillEntrance(p);
            if (advancedSettings)
                p.addCoins(1);
        }

        Collections.shuffle(players);
        gameBoard.setCurrentPlayer(players.get(0));
    }

    public void launchGame(){
        studentMove = 0;
        motherNatureMove = false;
        useCloudMove = false;

        //create separate thread that will wait until all clients insert assistants and execute action phase
        //it will execute until game ends
        new Thread(() -> {
            try {
                playGame();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public Player getCurrentPlayer(){
        return gameBoard.getCurrentPlayer();
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public synchronized void moveStudentToIsland(Color studentColor, int islandNumber){
        if (studentMove > (players.size() == 3? 4: 3))
            throw new WrongActionException();

        gameBoard.addStudentToIsland(gameBoard.getCurrentPlayer(), studentColor, islandNumber);
        studentMove++;
        this.notify();
    }

    public synchronized void moveStudentToDining(Color studentColor){
        if (studentMove > (players.size() == 3? 4: 3))
            throw new WrongActionException();

        removeStudentFromEntrance(studentColor);
        addStudentToDining(gameBoard.getCurrentPlayer(), studentColor);
        studentMove++;
        this.notify();
    }

    public void addStudentToDining(Player player, Color studentColor){
        gameBoard.addStudentToDining(player, studentColor);

        /*if this is advanced version of game, I pay 1 coin to player for each 3rd student*/
        if ( advancedSettings && player.getNumOfStudentsInDining(studentColor) % 3==0 ){
            gameBoard.addCoins(player, 1);
        }
        //check if professor gets reassigned
        reassignProfessor();
    }

    public void addStudentToIsland(Color studentColor, int islandNumber){
        gameBoard.addStudentToIsland(studentColor, islandNumber);
    }

    public void removeStudentFromEntrance(Color studentColor){
        gameBoard.removeStudentFromEntrance(gameBoard.getCurrentPlayer(), studentColor);
    }

    public void removeStudentFromDining(Player player, Color studentColor){
        gameBoard.removeStudentFromDining(player, studentColor);
    }

    public Color getStudent(){
        return gameBoard.getStudentFromBag();
    }

    //method that reassigns island and puts certain tower on it
    public void reassignIsland(int islandNumber){
        /*no players with 0 towers allowed except in case of 4 player game*/
        Player master = players.get(0);
        Player loser = players.get(0);

        int masterInfluence = 0;
        int influenceToCompare;

        /*if an island has noEntry tile on it, then the island does not get conquered*/
        if (calculateInfluence(islandNumber, master) == -1)
            return;

        // Looking for a player ( or team ) with the highest influence
        // Iterate through players that hold towers
        for (Player p : players.stream().filter((Player pl) -> pl.getNumOfTowers() > 0).toList()){
            influenceToCompare = calculateInfluence(islandNumber, p);

            // Iterate through players that does not hold any towers
            for (Player q : players.stream().filter((Player pl) -> pl.getNumOfTowers() == 0).toList()){
                //if players have the same tower color then they are from
                //the same team and thus their influence should be added
                if (p.getTowerColor().equals(q.getTowerColor()))
                    influenceToCompare += calculateInfluence(islandNumber, q);
            }


            if(influenceToCompare > masterInfluence || (influenceToCompare == masterInfluence &&
                    p.getTowerColor().equals(gameBoard.getTowersColorOnIsland(islandNumber)))) {
                /*assign to master the player from the team that holds towers*/
                master = p;
                masterInfluence = influenceToCompare;
            }
        }

        /*if there are 2 players ( or 2 teams ) with the same maximum influence score then nobody conquers an island*/
        for (Player p : players.stream().filter((Player pl) -> pl.getNumOfTowers() > 0).toList()){

            if (!p.getTowerColor().equals(master.getTowerColor())){
                influenceToCompare = calculateInfluence(islandNumber, p);

                // Iterate through players that does not hold any towers
                for (Player q : players.stream().filter((Player pl) -> pl.getNumOfTowers() == 0).toList()){
                    //if players have the same tower color then they are from
                    //the same team and thus their influence should be added
                    if (p.getTowerColor().equals(q.getTowerColor()))
                        influenceToCompare += calculateInfluence(islandNumber, q);
                }

                /*found a player or a team that has the same influence score*/
                if (masterInfluence == influenceToCompare)
                    return;
            }
        }

        /*the code below works in case of teams as well*/

        //Search for the player to take his towers back
        //check whether an island has towers
        if (gameBoard.getNumOfTowersOnIsland(islandNumber) > 0) {

            /*return towers to the player that holds them for the entire team*/
            for (Player p : players.stream().filter((Player pl) -> pl.getNumOfTowers() > 0).toList()) {
                if (p.getTowerColor().equals(gameBoard.getTowersColorOnIsland(islandNumber)))
                    loser = p;
            }
            /*to avoid useless model change, make a check*/
            if (!master.equals(loser))
                gameBoard.addTowersToPlayer(gameBoard.getNumOfTowersOnIsland(islandNumber), loser);
        }

        /*to avoid useless model change, make a check*/
        if (!master.equals(loser)) {
            gameBoard.addTowersToIsland(islandNumber, master);
            //suppose that the case with the master that has 0 towers is impossible
            //if a player has no enough towers then he puts all towers that he owns and as a result becomes a winner
            //otherwise he puts as many towers as islands inside the island that he wants to conquer
            gameBoard.removeTowersFromPlayer(Math.min(master.getNumOfTowers(), gameBoard.getNumOfMergedIslands(islandNumber)), master);
            gameBoard.mergeIslands();
        }
    }

    /*calculates influence score on specified Island*/
    public int calculateInfluence(int islandNumber, Player player){
        int influence = -1; //return -1 when NoEntry Tile is on selected island
        try {
            influence = gameBoard.calculateInfluence(islandNumber, player);
        }
        catch (NoEntryException e){
            /*returns noEntry to the character card*/
            gameBoard.addNoEntryTile();
        }
        return influence;
    }

    public void reassignProfessor(){
        gameBoard.getCurrentCharacter().reassignProfessor();
    }

    public void setNoEntry(int islandNumber, boolean noEntry){
        gameBoard.setNoEntry(islandNumber, noEntry);
    }

    public synchronized void moveMotherNature(int steps){
        if (studentMove != (players.size() == 3? 4: 3) || motherNatureMove) {
            //client can't make this action if students weren't moved or if mother nature was already moved
            throw new WrongActionException();
        }

        gameBoard.moveMotherNature(steps);
        //each time a client moves MN, I need to try to resolve an island because it might be conquered
        reassignIsland(gameBoard.getPositionOfMotherNature());

        //let client know that he can move to the next step
        ExceptionChange exceptionChange = new ExceptionChange(new EndOfChangesException());
        gameBoard.notify(exceptionChange);

        motherNatureMove = true;
        this.notify();
    }

    public void buyCharacter(int characterNumber){
            gameBoard.buyCharacter(characterNumber);
    }

    public void activateCharacter(int islandNumber){
        gameBoard.activateCharacter(islandNumber);
    }

    public void activateCharacter(ArrayList<Color> toBeSwappedStudents, ArrayList<Color> selectedStudents){
        gameBoard.activateCharacter(toBeSwappedStudents, selectedStudents);
    }

    public void activateCharacter(Color color, int islandNumber){
        gameBoard.activateCharacter(color, islandNumber);
    }

    public void activateCharacter(Color color){
        gameBoard.activateCharacter(color);
    }

    public synchronized void useCloud(int cloudNumber){
        if (studentMove != (players.size() == 3? 4: 3) || !motherNatureMove || useCloudMove) {
            //client can't make this action if students weren't moved
            //or if mother nature wasn't already moved or cloud was already used
            throw new WrongActionException();
        }
        gameBoard.useCloud(cloudNumber);
        useCloudMove = true;
        this.notify();
    }

    //sets assistant of current player and makes the next player current to let him call use assistant from virtual view
    public synchronized void useAssistant(int assistantRank){

        //checks if player can use this assistant
        if (checkAssistant(assistantRank, gameBoard.getCurrentPlayer())) {
            //sends 1 model change
            gameBoard.setPlayedAssistantRank(assistantRank, gameBoard.getCurrentPlayer());
        }
        else
            throw new RepeatedAssistantRankException();

        if (players.indexOf(gameBoard.getCurrentPlayer()) + 1 < players.size()) {
            //set the next player to chose assistant card
            //do this operation for all player except the last one,
            //because the next current player must be defined based on assistants ranks played

            //sends model change that executes newTurn on a client side
            gameBoard.setCurrentPlayer(players.get((players.indexOf(gameBoard.getCurrentPlayer()) + 1) % players.size()));
        }

        //notifies a waiting thread in newRound
        this.notify();
    }

    private synchronized void playGame() throws InterruptedException {
        while(!checkEndGame()){
            newRound();
        }

        //TODO manage end of game
        //GameBoard might send endOfGameChange
    }


    private void newRound() throws InterruptedException {
        //TODO control if the player is active each time before waiting for client action
        //if player is not active then skip him
        gameBoard.refillClouds();
        gameBoard.setCurrentPlayer(players.get(0));

        //wake up server thread that waits for refill clouds and set currentPlayer to happen
        //only after that server thread will attach virtual views to gameBoard
        this.notifyAll();

        /*planing phase*/
        //players are sorted in order in which they should play assistant card
        for (Player p : players) {
            while (p.getPlayedAssistant() == null) {
                //waits until client doesn't insert assistant card
                this.wait();
            }
        }

        /*sorts players based on rank, from lowest to highest so that the next turn
        is started by player that played the card with the lowest rank*/
        Collections.sort(players);

        /*action phase*/
        for (Player p : players) {
            if (!checkEndGame()) {
                newTurn(p);
            }
        }

        for (Player p : players) {
            gameBoard.setPlayedAssistantRank(0, p);
        }
    }

    private void newTurn(Player p) throws InterruptedException {
        /*virtual view controls current player before forwarding any method to controller*/
        gameBoard.setCurrentPlayer(p);

        while (studentMove != (players.size() == 3? 4: 3) || !motherNatureMove || !useCloudMove){
            //need to wait until all 3 conditions are satisfied
            //thread gets waked up by other threads that invoke moveStudent(), moveMN() and useCloud()
            this.wait();
        }

        studentMove = 0;
        motherNatureMove = false;
        useCloudMove = false;
    }

    /*check whether a player can play an assistant with a certain rank*/
    private boolean checkAssistant(int assistantRank, Player player){
        Set<Integer> playedAssistantsRanks = players.stream().filter(p -> p!=player && p.getPlayedAssistant()!=null).
                map(p -> p.getPlayedAssistant().getRank()).collect(Collectors.toSet());

        /*if a player wants to play rank not contained in his hand, then return false*/
        if (!player.getAssistantsRanks().contains(assistantRank))
            return false;

        /*if a player decides to play an assistant with rank already played by someone,
        it is allowable only when player has no other options*/
        if (playedAssistantsRanks.contains(assistantRank)){
            for (int rank: player.getAssistantsRanks()) {
                /*if player has an assistant with rank not yet played*/
                if (!playedAssistantsRanks.contains(rank))
                    return false;
            }
        }

        return true;
    }

    /*the game is finished when there is a player that has no assistants or
    has no towers or there is a team that has no towers (in case of 4 players) or
    the bag is empty or the number of islands is less than 3*/
    private boolean checkEndGame(){
        for(Player p : players){
            if(p.getAssistantsRanks().isEmpty()){
                return true;
            }
        }
        for (Player p : players){
            if(p.checkEmptyTowers()){
                if(players.size()>3){
                    for (Player q : players){
                        if (!p.equals(q) && p.getTowerColor().equals(q.getTowerColor()) && q.checkEmptyTowers())
                            return true;
                    }
                } else return true;
            }
        }
        return gameBoard.checkBagEmpty() || gameBoard.getNumOfIslands()<=3;
    }

    /*the packet received executes certain methods on Game*/
    public void usePacket(Packet packet){
        packet.execute(this);
    }

    /*TEST METHODS*/

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public void addStudentToEntrance(Player player){
        gameBoard.addStudentToEntrance(player, getStudent());
    }

    public void addStudentToEntrance(Player player, Color studentColor){
        gameBoard.addStudentToEntrance(player, studentColor);
    }

    public void setStudentMove(int studentMove){
        this.studentMove = studentMove;
    }

    public void setMotherNatureMove(boolean motherNatureMove){
        this.motherNatureMove=motherNatureMove;
    }


}