package it.polimi.ingsw.client.model;

import it.polimi.ingsw.client.view.View;
import it.polimi.ingsw.packets.ActivateCharacter3Packet;
import it.polimi.ingsw.packets.ActivateCharacterPacket;


public class ClientCharacter5 extends ClientCharacter {
    @Override
    public ActivateCharacterPacket createPacket(View view){

        view.printMessage(getDescription());
        view.printMessage("Select the island you want to move the noEntry Tile to");
        int islandNumber = view.askIslandNumber();

        ActivateCharacter3Packet packet = new ActivateCharacter3Packet(islandNumber--);

        return packet;
    }
}
