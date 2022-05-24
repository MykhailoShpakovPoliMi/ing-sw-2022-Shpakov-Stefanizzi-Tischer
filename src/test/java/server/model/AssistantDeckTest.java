package server.model;
import junit.framework.TestCase;
import org.junit.jupiter.api.Test;
import server.model.Assistant;
import server.model.AssistantDeck;
import server.model.AssistantFactory;
import server.model.AssistantType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AssistantDeckTest extends TestCase {
    AssistantFactory assistantFactory = new AssistantFactory();
    AssistantDeck assistantDeck = assistantFactory.getAssistantDeck();

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

        AssistantDeck assistantDeck = assistantFactory.getAssistantDeck();
        for(AssistantType assistantType: AssistantType.values()){
            List<Assistant> subDeck = assistantDeck.popAssistants(assistantType);
            assertThrows(IndexOutOfBoundsException.class, () -> subDeck.get(-1));
            assertThrows(IndexOutOfBoundsException.class, () -> subDeck.get(10));
        }
    }

}