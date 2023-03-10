package it.polimi.ingsw.server.model;

import it.polimi.ingsw.client.model.ClientCharacter;
import it.polimi.ingsw.exceptions.NoEnoughCoinsException;
import it.polimi.ingsw.exceptions.NoEntryException;
import it.polimi.ingsw.server.controller.Game;

/** adds 2 points to influence of currentPlayer */
public class Character2 extends Character {

    private int id=2;
    private int cost = 2;
    private String description = "You get 2 more influence points";

    @Override
    public void initialFill(Game game){
        super.initialFill(game);
    }


    public int calculateInfluence(Island island, int islandNumber, Player player){
        if (!island.getNoEntry()) {
            int score = 0;

            if (game.getCurrentPlayer().equals(player))
                score = 2;

            for (Color color : player.getProfessorsColor()) {
                score += island.getNumOfStudents(color);
            }
            if (player.getTowerColor().equals(island.getTowersColor())) {
                score += island.getNumOfTowers();
            }
            return score;
        }
        else {
            /*takes no entry tile away from the island*/
            game.setNoEntry(islandNumber,false);
            throw new NoEntryException();
        }
    }

    @Override
    public ClientCharacter createClientCharacter() {
        ClientCharacter character = new ClientCharacter();
        character.setId(id);
        character.setCost(cost);
        character.setDescription(description);
        character.setFirstUse(firstUse);

        return character;
    }

    @Override
    public void buy() throws NoEnoughCoinsException {
        game.getGameBoard().removeCoinsFromPlayer(game.getCurrentPlayer(), cost);
        //if it's first use then we need to leave one coin on the card
        if (firstUse){
            game.getGameBoard().addCoinsToBank(cost-1);
            firstUse = false;
        }
        else {
            game.getGameBoard().addCoinsToBank(cost);
        }
        cost = 3;
    }

}
