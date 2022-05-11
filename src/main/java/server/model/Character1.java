package server.model;

import server.controller.Game;
import exceptions.NoEnoughCoinsException;

import java.util.ArrayList;

/** Move selected student to selected island */
public class Character1 extends Character {
    private ArrayList<Color> students;
    protected Color selectedStudent;
    protected int selectedIslandNumber;
    private int cost = 1;

    @Override
    public void initialFill(Game game){
        super.initialFill(game);
        students=new ArrayList<Color>();
        for(int i=0; i<4; i++){
            students.add(game.getStudent());
        }
    }

    @Override
    public void execute(){
        students.remove(selectedStudent);
        game.addStudentToIsland(selectedStudent, selectedIslandNumber);
        students.add(game.getStudent());
    }

    @Override
    public void setSelectedStudent(Color selectedStudent){
        this.selectedStudent=selectedStudent;
    }

    @Override
    public void setSelectedIslandNumber(int selectedIslandNumber){
        this.selectedIslandNumber = selectedIslandNumber;
    }

    @Override
    public void buy() throws NoEnoughCoinsException {
        game.getGameBoard().removeCoins(game.getCurrentPlayer(), cost);
        cost = 2;
    }

    @Override
    public Color[] getStudentsSlot(){
        return (Color[]) students.toArray();
    }

    /*TEST METHODS*/
    public ArrayList<Color> getStudents(){
        return (ArrayList<Color>) students.clone();

    }

}