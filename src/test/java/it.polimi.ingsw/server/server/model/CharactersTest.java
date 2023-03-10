package it.polimi.ingsw.server.server.model;

import it.polimi.ingsw.exceptions.NoEnoughCoinsException;
import it.polimi.ingsw.exceptions.NoEnoughEntryTilesException;
import it.polimi.ingsw.exceptions.NoEntryException;
import it.polimi.ingsw.server.controller.CharacterDeck;
import it.polimi.ingsw.server.controller.Game;
import it.polimi.ingsw.server.model.Character;
import it.polimi.ingsw.server.model.*;
import junit.framework.TestCase;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static it.polimi.ingsw.server.model.Color.*;

public class CharactersTest extends TestCase {
    ArrayList<String> playerNames = new ArrayList<>();
    Player testPlayer = new Player("test", TowerColor.BLACK,AssistantType.ONE,8);
    AssistantDeck assistantDeck = new AssistantDeck();
    CharacterDeck characterDeck = new CharacterDeck();


    @Test
    void CharacterTest(){
        playerNames.add("a");
        playerNames.add("b");
        Game gametest = Game.getInstanceOfGame();
        gametest.init(playerNames,true, characterDeck);
        GameBoard testGameBoard = gametest.getGameBoard();
        testGameBoard.setCurrentPlayer(testPlayer);
        testGameBoard.refillAssistants(testPlayer);
        testPlayer.setPlayedAssistant(2);

        it.polimi.ingsw.server.model.Character characterTest = new Character();
        characterTest.initialFill(gametest);

        testGameBoard.setCurrentCharacterToDefault(characterTest);
        characterTest.buy();

        gametest.addStudentToIsland(GREEN,2);
        gametest.addStudentToIsland(GREEN, 2);
        gametest.addStudentToIsland(Color.BLUE,2);
        testGameBoard.conquerIslandTEST(2,TowerColor.BLACK);
        gametest.calculateInfluence(2, testPlayer);
        assertEquals(gametest.calculateInfluence(2, testPlayer),1);

        gametest.getPlayers().get(0).addStudentToDining(GREEN);
        gametest.getPlayers().get(0).addProfessor(GREEN);
        gametest.getPlayers().get(1).addStudentToDining(GREEN);
        testGameBoard.setCurrentPlayer(gametest.getPlayers().get(1));
        gametest.reassignProfessor();
        assertTrue(!gametest.getPlayers().get(1).getProfessorsColor().contains(GREEN));

        int i= characterTest.getCost();
        try {
            characterTest.setSelectedStudent(GREEN);
            assertTrue("false", false);
        }
        catch (UnsupportedOperationException e) {
            assertTrue(true);
        }

        try {
            ArrayList<Color> colors = new ArrayList<Color>();
            colors.add(GREEN);
            characterTest.setToBeSwappedStudents(colors);
            assertTrue("false", false);
        }
        catch (UnsupportedOperationException e) {
            assertTrue(true);
        }

        try {
            ArrayList<Color> colors = new ArrayList<Color>();
            colors.add(GREEN);
            characterTest.setSelectedStudents(colors);
            assertTrue("false", false);
        }
        catch (UnsupportedOperationException e) {
            assertTrue(true);
        }

        try {
            characterTest.setSelectedIslandNumber(1);
            assertTrue("false", false);
        }
        catch (UnsupportedOperationException e) {
            assertTrue(true);
        }

    }

    @Test
    void Character1Test(){
        playerNames.add("a");
        playerNames.add("b");
        Game gametest = Game.getInstanceOfGame();
        gametest.init(playerNames,true, characterDeck);
        GameBoard testGameBoard = gametest.getGameBoard();
        testGameBoard.setCurrentPlayer(testPlayer);
        testPlayer.addCoins(5);

        Character1 character1test = new Character1();
        character1test.initialFill(gametest);
        testGameBoard.setCurrentCharacterToDefault(character1test);

        character1test.buy();

        ArrayList<Color> testStudents;
        testStudents = character1test.getStudents();
        Color firstStudent = testStudents.get(0);
        gametest.activateCharacter(firstStudent,0);

        assertEquals(testGameBoard.getNumOfStudentsOnIsland(0, firstStudent),1);

    }

    @Test
    void Character2Test(){
        playerNames.add("a");
        playerNames.add("b");
        Game gametest = Game.getInstanceOfGame();
        gametest.init(playerNames,true, characterDeck);
        GameBoard testGameBoard = gametest.getGameBoard();
        testGameBoard.setCurrentPlayer(testPlayer);
        testPlayer.addCoins(5);
        testPlayer.addProfessor(GREEN);

        Character2 character2test = new Character2();
        character2test.initialFill(gametest);
        testGameBoard.setCurrentCharacterToDefault(character2test);

        character2test.buy();

        gametest.addStudentToIsland(GREEN,2);
        gametest.addStudentToIsland(GREEN, 2);
        gametest.addStudentToIsland(Color.BLUE,2);
        testGameBoard.conquerIslandTEST(2,TowerColor.BLACK);

        gametest.calculateInfluence(2, testPlayer);

        assertEquals(gametest.calculateInfluence(2, testPlayer),5);

    }

    @Test
    void Character3Test(){
        playerNames.add("a");
        playerNames.add("b");
        Game gametest = Game.getInstanceOfGame();
        gametest.init(playerNames,true, characterDeck);
        GameBoard testGameBoard = gametest.getGameBoard();
        testGameBoard.setCurrentPlayer(testPlayer);
        testPlayer.addCoins(5);
        testPlayer.addProfessor(GREEN);

        Character3 character3test = new Character3();
        character3test.initialFill(gametest);
        testGameBoard.setCurrentCharacterToDefault(character3test);

        character3test.buy();

        gametest.addStudentToIsland(GREEN,2);
        gametest.addStudentToIsland(GREEN, 2);
        gametest.addStudentToIsland(Color.BLUE,2);
        testGameBoard.conquerIslandTEST(2,TowerColor.BLACK);

        gametest.calculateInfluence(2, testPlayer);

        assertEquals(gametest.calculateInfluence(2, testPlayer),2);

        testGameBoard.setNoEntry(2,true);
        try{
            testGameBoard.calculateInfluence(2, testPlayer);
            assertTrue("false", false);
        }
        catch(NoEntryException e){

        }
    }

    @Test
    void Character4Test(){
        playerNames.add("a");
        playerNames.add("b");
        Game gametest = Game.getInstanceOfGame();
        gametest.init(playerNames,true, characterDeck);
        GameBoard testGameBoard = gametest.getGameBoard();
        testGameBoard.setCurrentPlayer(testPlayer);
        testPlayer.addCoins(5);
        testPlayer.addProfessor(GREEN);

        Character4 character4test = new Character4();
        character4test.initialFill(gametest);
        testGameBoard.setCurrentCharacterToDefault(character4test);

        character4test.buy();
        gametest.activateCharacter(GREEN);

        gametest.addStudentToIsland(GREEN,2);
        gametest.addStudentToIsland(GREEN, 2);
        gametest.addStudentToIsland(Color.BLUE,2);
        testGameBoard.conquerIslandTEST(2,TowerColor.BLACK);

        gametest.calculateInfluence(2, testPlayer);

        assertEquals(gametest.calculateInfluence(2, testPlayer),1);

        testGameBoard.setNoEntry(2,true);
        try{
            testGameBoard.calculateInfluence(2, testPlayer);
            assertTrue("false", false);
        }
        catch(NoEntryException e){

        }
    }

    @Test
    void Character5Test(){
        playerNames.add("a");
        playerNames.add("b");
        Game gametest = Game.getInstanceOfGame();
        gametest.init(playerNames,true, characterDeck);
        GameBoard testGameBoard = gametest.getGameBoard();
        testGameBoard.setCurrentPlayer(testPlayer);
        testPlayer.addCoins(1);

        Character5 character5test = new Character5();
        character5test.initialFill(gametest);
        testGameBoard.setCurrentCharacterToDefault(character5test);

        try {
            character5test.buy();
            assertTrue("false", false);
        }
        catch (NoEnoughCoinsException e){

        }

        testPlayer.addCoins(3);
        character5test.buy();
        gametest.activateCharacter(3);

        assertEquals(testGameBoard.getNoEntryOnIsland(3), true);

        gametest.activateCharacter(4);
        gametest.activateCharacter(5);
        gametest.activateCharacter(6);

        try {
            gametest.activateCharacter(7);
            assertTrue("false", false);
        }
        catch (NoEnoughEntryTilesException e){

        }
        testGameBoard.addNoEntryTile();
    }

    @Test
    void Character6Test(){
        playerNames.add("a");
        playerNames.add("b");
        Game gametest = Game.getInstanceOfGame();
        gametest.init(playerNames,true, characterDeck);
        GameBoard testGameBoard = gametest.getGameBoard();

        Character6 character6test = new Character6();
        character6test.initialFill(gametest);
        testGameBoard.setCurrentCharacterToDefault(character6test);

        gametest.getPlayers().get(0).addStudentToDining(GREEN);
        gametest.getPlayers().get(0).addProfessor(GREEN);

        gametest.getPlayers().get(1).addStudentToDining(GREEN);

        testGameBoard.setCurrentPlayer(gametest.getPlayers().get(1));

        gametest.reassignProfessor();

        assertTrue(gametest.getPlayers().get(1).getProfessorsColor().contains(GREEN));


    }

    @Test
    void Character7Test(){
        playerNames.add("a");
        playerNames.add("b");
        Game gametest = Game.getInstanceOfGame();
        gametest.init(playerNames,true, characterDeck);
        GameBoard testGameBoard = gametest.getGameBoard();
        testGameBoard.setCurrentPlayer(testPlayer);
        testPlayer.addCoins(5);

        Character7 character7test = new Character7();
        character7test.initialFill(gametest);
        testGameBoard.setCurrentCharacterToDefault(character7test);
        character7test.buy();

        /* adding some students to dining room */
        gametest.addStudentToDining(testPlayer,BLUE);
        gametest.addStudentToDining(testPlayer,BLUE);
        gametest.addStudentToDining(testPlayer,PINK);

        /* adding students to entrance */
        gametest.addStudentToEntrance(testPlayer,GREEN);
        gametest.addStudentToEntrance(testPlayer,RED);
        gametest.addStudentToEntrance(testPlayer,RED);
        gametest.addStudentToEntrance(testPlayer,PINK);

        /* students in entrance */
        ArrayList<Color> toBeSwappedTest = new ArrayList<>();
        toBeSwappedTest.add(GREEN);
        toBeSwappedTest.add(RED);

        /* students in dining */
        ArrayList<Color> selectedTest = new ArrayList<>();
        selectedTest.add(BLUE);
        selectedTest.add(PINK);

        gametest.activateCharacter(toBeSwappedTest, selectedTest);

        /* checking new entrance */
        assertEquals(testPlayer.getStudentsInEntrance().contains(RED), true);
        assertEquals(testPlayer.getStudentsInEntrance().contains(PINK), true);
        assertEquals(testPlayer.getStudentsInEntrance().contains(BLUE), true);
        assertEquals(testPlayer.getNumOfStudentsInEntrance(), 4);

        /*checking new diningroom */
        assertEquals(testPlayer.getNumOfStudentsInDining(BLUE), 1);
        assertEquals(testPlayer.getNumOfStudentsInDining(PINK), 0);
        assertEquals(testPlayer.getNumOfStudentsInDining(GREEN), 1);
        assertEquals(testPlayer.getNumOfStudentsInDining(RED), 1);
        assertEquals(testPlayer.getNumOfStudentsInDining(YELLOW), 0);

        /* TESTING EXCEPTIONS */

        toBeSwappedTest.add(PINK);

        try {
            gametest.activateCharacter(toBeSwappedTest, selectedTest);
            assertTrue("false", false);
        }
        catch (IllegalArgumentException e){
        }

        selectedTest.add(YELLOW);
        toBeSwappedTest.remove(0);

        try {
            gametest.activateCharacter(toBeSwappedTest, selectedTest);
            assertTrue("false", false);
        }
        catch (IllegalArgumentException e){
        }

        selectedTest.remove(YELLOW);
        selectedTest.remove(BLUE);

        try {
            gametest.activateCharacter(toBeSwappedTest, selectedTest);
            assertTrue("false", false);
        }
        catch (IllegalArgumentException e){
        }

    }

    @Test
    void Character8Test(){
        playerNames.add("a");
        playerNames.add("b");
        Game gametest = Game.getInstanceOfGame();
        gametest.init(playerNames,true, characterDeck);
        GameBoard testGameBoard = gametest.getGameBoard();
        testGameBoard.setCurrentPlayer(testPlayer);
        testPlayer.addCoins(5);
        testGameBoard.refillAssistants(testPlayer);
        testPlayer.setPlayedAssistant(2);

        Character8 character8test = new Character8();
        character8test.initialFill(gametest);
        testGameBoard.setCurrentCharacterToDefault(character8test);

        character8test.buy();

        testGameBoard.addStudentToEntrance(testPlayer, Color.GREEN);
        testGameBoard.addStudentToEntrance(testPlayer, Color.GREEN);
        testGameBoard.addStudentToEntrance(testPlayer, Color.GREEN);

        gametest.moveStudentToDining(Color.GREEN);
        gametest.moveStudentToDining(Color.GREEN);
        gametest.moveStudentToDining(Color.GREEN);

        gametest.setStudentMove(3);
        gametest.setMotherNatureMove(false);
        testGameBoard.placeMotherNatureTEST(3);
        gametest.moveMotherNature(3);

        assertEquals(testGameBoard.getPositionOfMotherNature(), 6);

    }

    @Test
    void Character9Test(){
        playerNames.add("a");
        playerNames.add("b");
        Game gametest = Game.getInstanceOfGame();
        gametest.init(playerNames,true, characterDeck);
        GameBoard testGameBoard = gametest.getGameBoard();
        testGameBoard.setCurrentPlayer(testPlayer);
        testPlayer.addCoins(5);

        Character9 character9test = new Character9();
        character9test.initialFill(gametest);
        testGameBoard.setCurrentCharacterToDefault(character9test);
        character9test.buy();

        /* adding students to entrance */
        gametest.addStudentToEntrance(testPlayer,GREEN);
        gametest.addStudentToEntrance(testPlayer,RED);
        gametest.addStudentToEntrance(testPlayer,RED);
        gametest.addStudentToEntrance(testPlayer,PINK);

        /* students in entrance */
        ArrayList<Color> toBeSwappedTest = new ArrayList<>();
        toBeSwappedTest.add(GREEN);
        toBeSwappedTest.add(RED);

        /* students on Character Card */
        Color[] studentsTest = character9test.getStudentsSlot();
        ArrayList<Color> selectedTest = new ArrayList<Color>();
        selectedTest.add(studentsTest[0]);
        selectedTest.add(studentsTest[1]);

        gametest.activateCharacter(toBeSwappedTest,selectedTest);

        /* checking new entrance */
        assertEquals(testPlayer.getStudentsInEntrance().contains(RED), true);
        assertEquals(testPlayer.getStudentsInEntrance().contains(PINK), true);
        assertEquals(testPlayer.getStudentsInEntrance().contains(selectedTest.get(0)), true);
        assertEquals(testPlayer.getStudentsInEntrance().contains(selectedTest.get(1)), true);
        assertEquals(testPlayer.getNumOfStudentsInEntrance(), 4);


        Color[] studentTest2 = character9test.getStudentsSlot();
        assertEquals(studentTest2[4]==toBeSwappedTest.get(0), true);
        assertEquals(studentTest2[5]==toBeSwappedTest.get(1), true);



        /* TESTING EXCEPTIONS */


        toBeSwappedTest.add(PINK);

        try {
            gametest.activateCharacter(toBeSwappedTest, selectedTest);
            assertTrue("false", false);
        }
        catch (IllegalArgumentException e){
        }

        selectedTest.add(YELLOW);
        toBeSwappedTest.remove(0);

        try {
            gametest.activateCharacter(toBeSwappedTest, selectedTest);
            assertTrue("false", false);
        }
        catch (IllegalArgumentException e){
        }

        selectedTest.remove(YELLOW);
        selectedTest.remove(BLUE);

    }

    @Test
    void Character10Test(){
        playerNames.add("a");
        playerNames.add("b");
        Game gametest = Game.getInstanceOfGame();
        gametest.init(playerNames,true, characterDeck);
        GameBoard testGameBoard = gametest.getGameBoard();
        testGameBoard.setCurrentPlayer(testPlayer);
        testPlayer.addCoins(5);

        Character10 character10test = new Character10();
        character10test.initialFill(gametest);
        testGameBoard.setCurrentCharacterToDefault(character10test);

        character10test.buy();

        ArrayList<Color> studentsTest = new ArrayList<>();
        studentsTest = character10test.getStudents();

        gametest.activateCharacter(studentsTest.get(0));

        assertEquals(testPlayer.getNumOfStudentsInDining(studentsTest.get(0)),1);
        assertEquals(character10test.getStudents().size(), 4);


    }

    @Test
    void Character11Test(){
        playerNames.add("a");
        playerNames.add("b");
        Game gametest = Game.getInstanceOfGame();
        gametest.init(playerNames,true, characterDeck);
        GameBoard testGameBoard = gametest.getGameBoard();
        testGameBoard.setCurrentPlayer(testPlayer);
        testPlayer.addCoins(5);

        Character11 character11test = new Character11();
        character11test.initialFill(gametest);
        testGameBoard.setCurrentCharacterToDefault(character11test);

        character11test.buy();

        ArrayList<Player> players = new ArrayList<>();
        players = gametest.getPlayers();

        players.get(0).addStudentToDining(GREEN);
        players.get(0).addStudentToDining(GREEN);
        players.get(0).addStudentToDining(GREEN);

        players.get(1).addStudentToDining(GREEN);
        players.get(1).addStudentToDining(GREEN);
        players.get(1).addStudentToDining(GREEN);
        players.get(1).addStudentToDining(GREEN);


        gametest.activateCharacter(GREEN);

        assertEquals(players.get(0).getNumOfStudentsInDining(GREEN), 0);
        assertEquals(players.get(1).getNumOfStudentsInDining(GREEN), 1);

        gametest.activateCharacter(GREEN);
        assertEquals(players.get(1).getNumOfStudentsInDining(GREEN),0);




    }

    @Test
    void Character12Test() {
        playerNames.add("a");
        playerNames.add("b");
        Game gametest = Game.getInstanceOfGame();
        gametest.init(playerNames, true, characterDeck);
        GameBoard testGameBoard = gametest.getGameBoard();
        testGameBoard.setCurrentPlayer(testPlayer);
        testPlayer.addCoins(5);
        testPlayer.addProfessor(GREEN);

        Character12 character12test = new Character12();
        character12test.initialFill(gametest);
        testGameBoard.setCurrentCharacterToDefault(character12test);

        character12test.buy();

        testGameBoard.addStudentToIsland(GREEN, 2);
        testGameBoard.addStudentToIsland(GREEN, 2);
        testGameBoard.addStudentToIsland(Color.BLUE, 2);
        testGameBoard.conquerIslandTEST(2, TowerColor.BLACK);

        gametest.activateCharacter(2);

        assertEquals(gametest.calculateInfluence(2, testPlayer), 3);
    }
}
