package it.polimi.ingsw.client.model;

import it.polimi.ingsw.client.view.View;
import it.polimi.ingsw.packets.ActivateCharacter1Packet;
import it.polimi.ingsw.packets.ActivateCharacterPacket;
import it.polimi.ingsw.server.model.Color;

public class ClientCharacter4 extends ClientCharacter {

    @Override
    public ActivateCharacterPacket createPacket(View view){

        //view.printMessage(getDescription());
        view.printMessage("Select a student color you want not to count during influence calculation");
        Color color = view.askStudentColorFromBox();

        ActivateCharacter1Packet packet = new ActivateCharacter1Packet(color);

        return packet;
    }
}
