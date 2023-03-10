package it.polimi.ingsw.server.server.model;

import it.polimi.ingsw.server.model.Assistant;
import it.polimi.ingsw.server.model.AssistantDeck;
import it.polimi.ingsw.server.model.AssistantType;
import junit.framework.TestCase;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class AssistantDeckTest extends TestCase {

    AssistantDeck assistantDeck = new AssistantDeck();

    @Test
    void popAssistantsTest(){

        for(AssistantType assistantType: AssistantType.values()){
            List<Assistant> subDeck = assistantDeck.popAssistants(assistantType);
            for(int i = 0; i < 10; i++){
                assertEquals(i+1, subDeck.get(i).getRank());
                assertEquals((i/2) + 1, subDeck.get(i).getMovements());
                assertEquals(assistantType, subDeck.get(i).getType());
            }
        }

        //AssistantDeck assistantDeck = assistantFactory.getAssistantDeck();

        for(AssistantType assistantType: AssistantType.values()){
            List<Assistant> subDeck = assistantDeck.popAssistants(assistantType);
            assertThrows(IndexOutOfBoundsException.class, () -> subDeck.get(-1));
            assertThrows(IndexOutOfBoundsException.class, () -> subDeck.get(10));
        }
    }

}