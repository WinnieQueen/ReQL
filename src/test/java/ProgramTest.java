import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class ProgramTest {

    private ReQL reQL = new ReQL();

    @Test
    void promptForInput_shouldReturnSpecial() {
        reQL.reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream("Help 1".getBytes())));
        assertEquals("SPECIAL", reQL.promptForInput());
    }

    @Test
    void promptForInput_shouldReturnSpecial2() {
        reQL.reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream("Help 2".getBytes())));
        assertEquals("SPECIAL", reQL.promptForInput());
    }

    @Test
    void promptForInput_shouldReturnSpecial3() {
        reQL.reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream("Quit".getBytes())));
        assertEquals("SPECIAL", reQL.promptForInput());
    }

    @Test
    void promptForInput_shouldReturnSpecial4() {
        reQL.reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(("New" + System.lineSeparator() + "y").getBytes())));
        assertEquals("SPECIAL", reQL.promptForInput());
    }

    @Test
    void promptForInput_shouldReturnWholeString() {
        reQL.reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(("Oops I did it againnnn" + System.lineSeparator() + "I played with your heart" + System.lineSeparator() + "GOT LOST IN THE GAAME;").getBytes())));
        assertEquals("Oops I did it againnnn I played with your heart GOT LOST IN THE GAAME;", reQL.promptForInput());
    }

    @Test
    void promptForInput_shouldReturnEmptyString() {
        reQL.reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(("                                   ").getBytes())));
        assertEquals("", reQL.promptForInput());
    }

    @Test
    void createSchema_shouldReturnTrue() {
    }

    @Test
    void createSchema_shouldReturnFalse() {
    }

    @Test
    void searchFile_shouldReturnTrue() {
    }

    @Test
    void searchFile_shouldReturnFalse() {
    }

    @Test
    void checkForSpecialInput_shouldReturnTrue() {
    }

    @Test
    void checkForSpecialInput_shouldReturnFalse() {
    }
}