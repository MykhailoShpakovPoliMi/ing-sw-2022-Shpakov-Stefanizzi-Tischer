import exceptions.NoEnoughStudentsException;
import junit.framework.TestCase;
import model.Bag;
import model.Color;
import org.junit.jupiter.api.Test;

public class BagTest extends TestCase {

    @Test
    void extractStudentExceptionTest(){
        Bag testbag = new Bag(130);

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
