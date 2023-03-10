package it.polimi.ingsw.server.server.controller;

import it.polimi.ingsw.exceptions.NoEntryException;
import it.polimi.ingsw.server.controller.CharacterDeck;
import it.polimi.ingsw.server.controller.Game;
import it.polimi.ingsw.server.model.*;
import junit.framework.TestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class GameTest extends TestCase {
    Game game = Game.getInstanceOfGame();
    CharacterDeck characterDeck = new CharacterDeck();
    ArrayList<String> playersNames = new ArrayList<String>();

    @BeforeEach
    void reset() {
        playersNames.clear();
    }


    @Test
    void twoPlayersInitTest(){

        playersNames.add("Pippo");
        playersNames.add("Pluto");

        game.init(playersNames, true, characterDeck);

        ArrayList<Player> playersNamesTest = new ArrayList<>();
        playersNamesTest = game.getPlayers();

        assertEquals(playersNames.size(), playersNamesTest.size());
        for(Player p : playersNamesTest){
            assertTrue("true", playersNames.contains(p.getName()));
        }
    }

    @Test
    void threePlayersInitTest(){
        playersNames.add("Pippo");
        playersNames.add("Pluto");
        playersNames.add("Paperino");

        game.init(playersNames, true, characterDeck);

        ArrayList<Player> playersNamesTest = new ArrayList<>();
        playersNamesTest = game.getPlayers();

        assertEquals(playersNames.size(), playersNamesTest.size());
        for(Player p : playersNamesTest){
            assertTrue("true", playersNames.contains(p.getName()));
        }
    }

    @Test
    void fourPlayersInitTest(){
        playersNames.add("Pippo");
        playersNames.add("Pluto");
        playersNames.add("Paperino");
        playersNames.add("Topolino");

        game.init(playersNames, true, characterDeck);

        ArrayList<Player> playersNamesTest = new ArrayList<>();
        playersNamesTest = game.getPlayers();

        assertEquals(playersNames.size(), playersNamesTest.size());
        for(Player p : playersNamesTest){
            assertTrue("true", playersNames.contains(p.getName()));
        }
    }

    @Test
    void fivePlayersInitTest(){
        playersNames.add("Pippo");
        playersNames.add("Pluto");
        playersNames.add("Paperino");
        playersNames.add("Topolino");
        playersNames.add("Minnie");
        try{
            game.init(playersNames, true, characterDeck);
        }
        catch (Exception e){
            assertTrue(true);
        }
    }
    @Test
    void calculateInfluenceTest(){
        Player testplayer = new Player("Test", TowerColor.BLACK, AssistantType.ONE,8);
        playersNames.add("a");
        playersNames.add("b");
        game.init(playersNames,true, characterDeck);
        GameBoard testGameBoard = game.getGameBoard();
        testGameBoard.setCurrentPlayer(testplayer);
        testplayer.addStudentToEntrance(Color.GREEN);
        testplayer.addStudentToEntrance(Color.GREEN);
        testplayer.addStudentToEntrance(Color.BLUE);
        testplayer.addProfessor(Color.GREEN);

        assertEquals(game.calculateInfluence(0, testplayer), 0);

        game.setStudentMove(0);

        game.moveStudentToIsland(Color.GREEN, 0);
        game.moveStudentToIsland(Color.GREEN, 0);
        game.moveStudentToIsland(Color.BLUE, 0);

        assertEquals(game.calculateInfluence(0, testplayer), 2);
    }

    @Test
    void moveStudentToDiningTest(){
        playersNames.add("a");
        playersNames.add("b");
        game.init(playersNames,true, characterDeck);
        Player testplayer = new Player("Test", TowerColor.BLACK, AssistantType.ONE,8);
        GameBoard testGameBoard = game.getGameBoard();
        testGameBoard.setCurrentPlayer(testplayer);

        testplayer.addStudentToEntrance(Color.GREEN);

        game.setStudentMove(0);

        game.moveStudentToDining(Color.GREEN);

        assertEquals(testplayer.getNumOfStudentsInDining(Color.GREEN), 1);

    }

    @Test
    void addStudentToEntranceTest(){
        playersNames.add("a");
        playersNames.add("b");
        game.init(playersNames,true, characterDeck);
        Player testplayer = new Player("Test", TowerColor.BLACK, AssistantType.ONE,8);
        GameBoard testGameBoard = game.getGameBoard();
        testGameBoard.setCurrentPlayer(testplayer);

        game.addStudentToEntrance(testplayer, Color.BLUE);
        assertEquals(testplayer.getNumOfStudentsInEntrance(),1);
        assertTrue(testplayer.getStudentsInEntrance().contains(Color.BLUE));

        game.addStudentToEntrance(testplayer);
        game.addStudentToEntrance(testplayer);
        assertEquals(testplayer.getNumOfStudentsInEntrance(),3);

    }

    @Test
    void removeStudentFromDiningTest(){
        playersNames.add("a");
        playersNames.add("b");
        game.init(playersNames,true, characterDeck);
        Player testplayer = new Player("Test", TowerColor.BLACK, AssistantType.ONE,8);
        GameBoard testGameBoard = game.getGameBoard();
        testGameBoard.setCurrentPlayer(testplayer);

        game.addStudentToEntrance(testplayer,Color.RED);
        game.moveStudentToDining(Color.RED);

        assertEquals(testplayer.getNumOfStudentsInDining(Color.RED),1);

        game.removeStudentFromDining(testplayer,Color.RED);
        assertEquals(testplayer.getNumOfStudentsInDining(Color.RED),0);
    }

    @Test
    void setNoEntryTest(){
        playersNames.add("a");
        playersNames.add("b");
        game.init(playersNames,true, characterDeck);
        GameBoard testGameBoard = game.getGameBoard();

        assertEquals(testGameBoard.getNoEntryOnIsland(0 ), false);

        game.setNoEntry(0,true);
        assertEquals(testGameBoard.getNoEntryOnIsland(0 ),true);

    }

    @Test
    void noEntryExceptionTest(){
        playersNames.add("a");
        playersNames.add("b");
        game.init(playersNames,true, characterDeck);
        GameBoard testGameBoard = game.getGameBoard();
        Player testPlayer = new Player("c", TowerColor.BLACK, AssistantType.ONE, 8);

        game.setNoEntry(0,true);

        try {
            game.setNoEntry(0, true);
        }
        catch (NoEntryException e){

        }
        try {
            testGameBoard.calculateInfluence(0, testPlayer);
            assertTrue("false", false);
        }
        catch (NoEntryException e){
        }

        game.setNoEntry(0,true);
        game.calculateInfluence(0, testPlayer);


    }

    @Test
    void moveMotherNatureTest(){
        playersNames.add("a");
        playersNames.add("b");
        game.init(playersNames,true, characterDeck);
        Player testplayer = new Player("Test", TowerColor.BLACK, AssistantType.ONE,8);
        GameBoard testGameBoard = game.getGameBoard();

        testGameBoard.setCurrentPlayer(testplayer);

        testGameBoard.refillAssistants(testplayer);
        //character with rank 8 has 4 steps
        testplayer.setPlayedAssistant(8);
        testGameBoard.placeMotherNatureTEST(2);

        testGameBoard.addStudentToEntrance(testplayer, Color.GREEN);
        testGameBoard.addStudentToEntrance(testplayer, Color.GREEN);
        testGameBoard.addStudentToEntrance(testplayer, Color.GREEN);

        game.setStudentMove(0);

        game.moveStudentToDining(Color.GREEN);
        game.moveStudentToDining(Color.GREEN);
        game.moveStudentToDining(Color.GREEN);

        game.setMotherNatureMove(false);

        game.moveMotherNature(3);
        assertEquals(testGameBoard.getPositionOfMotherNature(),5);

    }
}
