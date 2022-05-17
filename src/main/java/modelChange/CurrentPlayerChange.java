package modelChange;

import client.model.ClientGameBoard;

public class CurrentPlayerChange extends ModelChange{

    String currentPlayerName;

    public CurrentPlayerChange(String currentPlayerName){
        this.currentPlayerName=currentPlayerName;
    }

    public void execute(ClientGameBoard gameBoard){
        gameBoard.setCurrentPlayerName(currentPlayerName);
    }
}
