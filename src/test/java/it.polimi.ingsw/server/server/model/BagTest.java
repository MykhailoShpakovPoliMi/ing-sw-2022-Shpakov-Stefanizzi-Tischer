package it.polimi.ingsw.server.server.model;

import it.polimi.ingsw.exceptions.NoEnoughStudentsException;
import it.polimi.ingsw.server.model.Bag;
import junit.framework.TestCase;
import org.junit.jupiter.api.Test;

public class BagTest extends TestCase {

    @Test
    void extractStudentExceptionTest(){
        Bag testbag = new Bag();

        for(int i=0;i<130;i++) {
            testbag.extractStudent();
        }

        try{
            testbag.extractStudent();
            assertTrue("false", false);
        }
        catch (NoEnoughStudentsException e) {
        }
    }
}
