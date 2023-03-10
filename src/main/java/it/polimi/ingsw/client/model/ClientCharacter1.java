package it.polimi.ingsw.client.model;

import it.polimi.ingsw.client.view.View;
import it.polimi.ingsw.packets.ActivateCharacter2Packet;
import it.polimi.ingsw.packets.ActivateCharacterPacket;
import it.polimi.ingsw.server.model.Color;

import java.util.Arrays;

public class ClientCharacter1 extends ClientCharacter{

    private Color[] students;

    @Override
    public ActivateCharacterPacket createPacket(View view){

        //view.printMessage(getDescription());
        Color student = null;
        boolean correctStudent = false;
        while(!correctStudent) {
            view.printMessage("From this card, select the student you want to move");


            student = view.askStudentColorFromCharacter();

            if(Arrays.stream(students).toList().contains(student))
            {correctStudent=true;}
            else{
                view.printMessage("No such student on this card. Try again");
            }
        }
        view.printMessage("Choose the island you want to move the student to");


        int islandNumber = view.askIslandNumber();

        ActivateCharacter2Packet packet = new ActivateCharacter2Packet(student, islandNumber-1);

        return packet;
    }

    public void setStudents(Color[] students) {
        this.students = students;
    }
    public Color[] getStudents() {
        return students;
    }

}
