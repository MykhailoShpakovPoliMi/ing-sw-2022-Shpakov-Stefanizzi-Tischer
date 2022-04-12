package model;

import controller.Game;
import exceptions.NoEnoughCoinsException;

import java.util.ArrayList;

/** Move selected student to selected island */
public class Character1 extends Character{
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
        game.moveStudentToIsland(selectedStudent, selectedIslandNumber);
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
        game.getCurrentPlayer().removeCoins(cost);
        cost = 2;
    }

}