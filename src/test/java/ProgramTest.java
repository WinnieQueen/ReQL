import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class ProgramTest {

    ReQL reQL = new ReQL();

    @BeforeEach
    void setUp() {
        reQL.clearAll();
    }

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
    void grabTableName_shouldReturnTrue() {
        String toPassIn = "CREATE TABLE 'MyyyyyTable' (date, time, log_level, src_ip, username, msg) : line format /^([0-9]{4}-[0-9]{2}-[0-9]{2}) ([0-9]{2}:[0-9]{2}:[0-9]{2}) (\\[.*?\\]) ([0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.{1,4}) (.+) : (.*)$/ file 'access_log.txt';";
        Boolean actual = reQL.grabTableName(toPassIn);
        assertEquals(true, actual);
    }

    @Test
    void grabRegexps() {
        String[] expected = {"[0-9]{4}-[0-9]{2}-[0-9]{2}", "[0-9]{2}:[0-9]{2}:[0-9]{2}", "\\[.*?\\]", "[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.{1,4}", ".+", ".*"};
        String toPassIn = "CREATE TABLE 'MyyyyyTable' (date, time, log_level, src_ip, username, msg) : line format /^([0-9]{4}-[0-9]{2}-[0-9]{2}) ([0-9]{2}:[0-9]{2}:[0-9]{2}) (\\[.*?\\]) ([0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.{1,4}) (.+) : (.*)$/ file 'access_log.txt';";
        String[] actual = reQL.grabRegexps(toPassIn);
        assertEquals(expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual[i]);
        }
    }

    @Test
    void grabFilePath() {
        String toPassIn = "CREATE TABLE 'MyyyyyTable' (date, time, log_level, src_ip, username, msg) : line format /^([0-9]{4}-[0-9]{2}-[0-9]{2}) ([0-9]{2}:[0-9]{2}:[0-9]{2}) (\\[.*?\\]) ([0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.{1,4}) (.+) : (.*)$/ file 'access_log.txt';";
        Boolean actual = reQL.grabFilePath(toPassIn);
        assertEquals(true, actual);
    }

    @Test
    void grabColumnNames() {
        String[] expected = {"date", "time", "log_level", "src_ip", "username", "msg"};
        String toPassIn = "CREATE TABLE 'MyyyyyTable' (date, time, log_level, src_ip, username, msg) : line format /^([0-9]{4}-[0-9]{2}-[0-9]{2}) ([0-9]{2}:[0-9]{2}:[0-9]{2}) (\\[.*?\\]) ([0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.{1,4}) (.+) : (.*)$/ file 'access_log.txt';";
        String[] actual = reQL.grabColumnNames(toPassIn);
        assertEquals(expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual[i]);
        }
    }

    @Test
    void createSchema_shouldReturnTrue() {
        String toPassIn = "CREATE TABLE 'MyyyyyTable' (date, time, log_level, src_ip, username, msg) : line format /^([0-9]{4}-[0-9]{2}-[0-9]{2}) ([0-9]{2}:[0-9]{2}:[0-9]{2}) (\\[.*?\\]) ([0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.{1,4}) (.+) : (.*)$/ file 'access_log.txt';";
        boolean actual = reQL.createSchema(toPassIn);
        assertEquals(true, actual);
    }

    @Test
    void createSchema_hasTooManyRegex() {
        String toPassIn = "CREATE TABLE 'MyyyyyTable' (date, time, log_level, src_ip) : line format /^([0-9]{4}-[0-9]{2}-[0-9]{2}) ([0-9]{2}:[0-9]{2}:[0-9]{2}) (\\[.*?\\]) ([0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.{1,4}) (.+) : (.*)$/ file 'access_log.txt';";
        boolean actual = reQL.createSchema(toPassIn);
        assertEquals(false, actual);
    }

    @Test
    void createSchema_hasTooManyColumns() {
        String toPassIn = "CREATE TABLE 'MyyyyyTable' (date, time, log_level, src_ip, username, msg, newColumn, newColumn2) : line format /^([0-9]{4}-[0-9]{2}-[0-9]{2}) ([0-9]{2}:[0-9]{2}:[0-9]{2}) (\\[.*?\\]) ([0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.{1,4}) (.+) : (.*)$/ file 'access_log.txt';";
        boolean actual = reQL.createSchema(toPassIn);
        assertEquals(false, actual);
    }

    @Test
    void createSchema_hasBadFormat() {
        String toPassIn = "CREATE TABLE 'MyyyyyTable' (date, time, log_level, src_ip) : line format ^([0-9]{4}-[0-9]{2}-[0-9]{2}) ([0-9]{2}:[0-9]{2}:[0-9]{2}) (\\[.*?\\]) ([0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.{1,4}) (.+) : (.*)$ file 'access_log.txt';";
        boolean actual = reQL.createSchema(toPassIn);
        assertEquals(false, actual);
    }

    @Test
    void searchFile_shouldReturnTrue() {
        setup();
        reQL.createSchema("CREATE TABLE 'MyyyyyTable' (date, time, log_level, src_ip, username, msg) : line format /^([0-9]{4}-[0-9]{2}-[0-9]{2}) ([0-9]{2}:[0-9]{2}:[0-9]{2}) (\\[.*?\\]) ([0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.{1,4}) (.+) : (.*)$/ file 'access_log.txt';");
        assertEquals(true, reQL.searchFile("SELECT date, time, src_ip FROM MyyyyyTable WHERE date >= '2019-01-01'"));
    }

    @Test
    void searchFile_shouldReturnFalse() {
        setup();
        reQL.createSchema("CREATE TABLE 'MyyyyyTable' (date, time, log_level, src_ip, username, msg) : line format /^([0-9]{4}-[0-9]{2}-[0-9]{2}) ([0-9]{2}:[0-9]{2}:[0-9]{2}) (\\[.*?\\]) ([0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.{1,4}) (.+) : (.*)$/ file 'access_log.txt';");
        assertEquals(false, reQL.searchFile("SELEC date, time, src_ip FROM MyyyyyTable WHERE date >= '2019-01-01'"));
    }

    @Test
    void checkForSpecialInput_help1ShouldReturnTrue() {
        Boolean actual = reQL.checkForSpecialInput("Help 1");
        assertEquals(true, actual);
    }

    @Test
    void checkForSpecialInput_help2ShouldReturnTrue() {
        Boolean actual = reQL.checkForSpecialInput("Help 2");
        assertEquals(true, actual);
    }

    @Test
    void checkForSpecialInput_quitShouldReturnTrue() {
        Boolean actual = reQL.checkForSpecialInput("quit");
        assertEquals(true, actual);
    }

    @Test
    void checkForSpecialInput_newShouldReturnTrue() {
        reQL.reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream("y".getBytes())));
        Boolean actual = reQL.checkForSpecialInput("new");
        assertEquals(true, actual);
    }

    @Test
    void checkForSpecialInput_helpShouldReturnFalse() {
        Boolean actual = reQL.checkForSpecialInput("Help");
        assertEquals(false, actual);
    }

    @Test
    void grabFromTableName() {
        setup();
        String toPassIn = "SELECT date, time, src_ip FROM mylog WHERE date >= '2019-01-01'";
        String expected = "mylog";
        String actual = reQL.grabFromTableName(toPassIn);
        assertEquals(expected, actual);
    }

    @Test
    void grabColumnsToGrab() {
        setup();
        String toPassIn = "SELECT date, time, src_ip FROM mylog WHERE date >= '2019-01-01'";
        String[] expected = {"date", "time", "src_ip"};
        String[] actual = reQL.grabColumnsToGrab(toPassIn);
        assertEquals(expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual[i]);
        }
    }

    void setup() {
        String toPassIn = "CREATE TABLE 'MyyyyyTable' (date, time, log_level, src_ip) : line format ^([0-9]{4}-[0-9]{2}-[0-9]{2}) ([0-9]{2}:[0-9]{2}:[0-9]{2}) (\\[.*?\\]) ([0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.{1,3}) (.+) : (.*)$ file 'access_log.txt';";
        reQL.grabFilePath(toPassIn);
        reQL.grabTableName(toPassIn);
    }
}