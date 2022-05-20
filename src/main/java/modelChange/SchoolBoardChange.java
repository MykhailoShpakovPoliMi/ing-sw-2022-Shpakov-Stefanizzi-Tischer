package modelChange;

import client.model.ClientGameBoard;
import server.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SchoolBoardChange extends ModelChange{

    private String playerName;
    private ArrayList<Color> entrance;
    private Map<Color,Integer> diningRoom = new HashMap<Color, Integer>();
    private Map<Color,Integer> professors = new HashMap<Color, Integer>();
    private int numOfTowers;
    private TowerColor towersColor;

    @Override
    public void execute(ClientGameBoard gameBoard){
        gameBoard.getPlayer(playerName).getSchoolBoard().setEntrance(entrance);
        gameBoard.getPlayer(playerName).getSchoolBoard().setNumOfTowers(numOfTowers);
        gameBoard.getPlayer(playerName).getSchoolBoard().setTowersColor(towersColor);
        gameBoard.getPlayer(playerName).getSchoolBoard().setDiningRoom(diningRoom);
        gameBoard.getPlayer(playerName).getSchoolBoard().setProfessors(professors);
    }

    public SchoolBoardChange(Player player){
        playerName=player.getName();
        this.entrance = player.getStudentsInEntrance();
        this.numOfTowers = player.getNumOfTowers();
        this.towersColor = player.getTowerColor();
        for(Color c : Color.values()){
            this.diningRoom.put(c,player.getNumOfStudentsInDining(c));
        }
        for(Color c : player.getProfessorsColor()){
            this.professors.put(c, 1);
        }
    }
}
