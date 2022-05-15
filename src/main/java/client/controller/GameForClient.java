package client.controller;

import server.model.Color;

import java.util.ArrayList;

public interface GameForClient {
    public void moveStudentToIsland(Color studentColor, int islandNumber);

    public void moveStudentToDining(Color studentColor);

    public void useCloud(int cloudNumber);

    public void moveMotherNature(int steps);

    public void useAssistant(int assistantRank);

    public void buyCharacter(int characterNumber);

    public void activateCharacter(int islandNumber);

    public void activateCharacter(ArrayList<Color> toBeSwappedStudents, ArrayList<Color> selectedStudents);

    public void activateCharacter(Color color, int islandNumber);

    public void activateCharacter(Color color);

}
