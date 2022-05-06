package packets;

import server.controller.GameForClient;
import server.model.Color;

public class MoveStudentToIslandPacket extends Packet{

    @Override
    public void execute(GameForClient game) {
        Color studentColor;
        int islandNumber;

        try{
            studentColor = (Color)getParameters().get(0);
            islandNumber = (int)getParameters().get(1);
        }
        catch (ClassCastException e){
            throw new ClassCastException("Invalid arguments");
        }

        game.moveStudentToIsland(studentColor, islandNumber);
    }
}
