package it.polimi.ingsw.client.model;

import it.polimi.ingsw.server.model.Color;
import it.polimi.ingsw.server.model.TowerColor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class ClientIsland implements Serializable {
    private int numOfIslands;
    private Map<Color, Integer> students;
    private int numOfTowers;
    private TowerColor towersColor;
    private boolean noEntry;

    public int getNumOfIslands() {
        return numOfIslands;
    }

    public void setNumOfIslands(int numOfIslands) {
        this.numOfIslands = numOfIslands;
    }

    public Map<Color, Integer> getStudents() {
        return students;
    }

    public void setStudents(Map<Color, Integer> students) {
        this.students = students;
    }

    public int getNumOfTowers() {
        return numOfTowers;
    }

    public void setNumOfTowers(int numOfTowers) {
        this.numOfTowers = numOfTowers;
    }

    public TowerColor getTowersColor() {
        return towersColor;
    }

    public void setTowersColor(TowerColor towersColor) {
        this.towersColor = towersColor;
    }

    public boolean isNoEntry() {
        return noEntry;
    }

    public void setNoEntry(boolean noEntry) {
        this.noEntry = noEntry;
    }

    public ArrayList<Color> getStudentsAsArray(){
        ArrayList<Color> studentsArray = new ArrayList<>();

        for(Color c : Color.values()){
            for(int i=0;i<this.students.get(c); i++){
                studentsArray.add(c);
            }
        }

        return studentsArray;
    }
}
