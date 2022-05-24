package server.model;

import server.controller.Game;
import exceptions.NoEnoughCoinsException;

public class Character11 extends Character {
    /*Removes 3 Students of studentColor from each player`s Dining*/

    private Color selectedStudent;
    private int cost = 3;

    @Override
    public void initialFill(Game game){
        super.initialFill(game);
    }

    public void setSelectedStudent(Color selectedStudent){
        this.selectedStudent = selectedStudent;
    }

    public void execute() {
        for (Player player: game.getPlayers()){
            int numOfStudentsInDining = player.getNumOfStudentsInDining(selectedStudent);

            for (int i = 0; i < (Math.min(3, numOfStudentsInDining)); i++)
                game.getGameBoard().removeStudentFromDining(player, selectedStudent);
                game.getGameBoard().addStudentToBag(selectedStudent);
        }
    }
    @Override
    public void buy() throws NoEnoughCoinsException {
        game.getGameBoard().removeCoins(game.getCurrentPlayer(), cost);
        cost = 4;
    }


}