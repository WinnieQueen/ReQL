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
    void grabTableName_shouldReturnTrue() {
        String toPassIn = "CREATE TABLE 'MyyyyyTable' (date, time, log_level, src_ip, username, msg) : line format /^([0-9]{4}-[0-9]{2}-[0-9]{2}) ([0-9]{2}:[0-9]{2}:[0-9]{2}) (\\[.*?\\]) ([0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.{1,3}) (.+) : (.*)$/ file 'access_log.txt';";
        Boolean actual = reQL.grabTableName(toPassIn);
        assertEquals(true, actual);
    }

    @Test
    void grabRegexps() {
        String[] expected = {"[0-9]{4}-[0-9]{2}-[0-9]{2}", "[0-9]{2}:[0-9]{2}:[0-9]{2}", "\\[.*?\\]", "[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.{1,3}", ".+", ".*"};
        String toPassIn = "CREATE TABLE 'MyyyyyTable' (date, time, log_level, src_ip, username, msg) : line format /^([0-9]{4}-[0-9]{2}-[0-9]{2}) ([0-9]{2}:[0-9]{2}:[0-9]{2}) (\\[.*?\\]) ([0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.{1,3}) (.+) : (.*)$/ file 'access_log.txt';";
        String[] actual = reQL.grabRegexps(toPassIn);
        assertEquals(expected.length, actual.length);
        for(int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual[i]);
        }
    }

    @Test
    void grabFilePath() {
        String toPassIn = "CREATE TABLE 'MyyyyyTable' (date, time, log_level, src_ip, username, msg) : line format /^([0-9]{4}-[0-9]{2}-[0-9]{2}) ([0-9]{2}:[0-9]{2}:[0-9]{2}) (\\[.*?\\]) ([0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.{1,3}) (.+) : (.*)$/ file 'access_log.txt';";
        Boolean actual = reQL.grabFilePath(toPassIn);
        assertEquals(true, actual);
    }

    @Test
    void grabColumnNames() {
        String[] expected = {"date", "time", "log_level", "src_ip", "username", "msg"};
        String toPassIn = "CREATE TABLE 'MyyyyyTable' (date, time, log_level, src_ip, username, msg) : line format /^([0-9]{4}-[0-9]{2}-[0-9]{2}) ([0-9]{2}:[0-9]{2}:[0-9]{2}) (\\[.*?\\]) ([0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.{1,3}) (.+) : (.*)$/ file 'access_log.txt';";
        String[] actual = reQL.grabColumnNames(toPassIn);
        assertEquals(expected.length, actual.length);
        for(int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual[i]);
        }
    }

    @Test
    void createSchema_shouldReturnTrue() {
        String toPassIn = "CREATE TABLE 'MyyyyyTable' (date, time, log_level, src_ip, username, msg) : line format /^([0-9]{4}-[0-9]{2}-[0-9]{2}) ([0-9]{2}:[0-9]{2}:[0-9]{2}) (\\[.*?\\]) ([0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.{1,3}) (.+) : (.*)$/ file 'access_log.txt';";
        boolean actual = reQL.createSchema(toPassIn);
        assertEquals(true, actual);
    }

    @Test
    void createSchema_hasTooManyRegex() {
        String toPassIn = "CREATE TABLE 'MyyyyyTable' (date, time, log_level, src_ip) : line format /^([0-9]{4}-[0-9]{2}-[0-9]{2}) ([0-9]{2}:[0-9]{2}:[0-9]{2}) (\\[.*?\\]) ([0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.{1,3}) (.+) : (.*)$/ file 'access_log.txt';";
        boolean actual = reQL.createSchema(toPassIn);
        assertEquals(false, actual);
    }

    @Test
    void createSchema_hasTooManyColumns() {
        String toPassIn = "CREATE TABLE 'MyyyyyTable' (date, time, log_level, src_ip, username, msg, newColumn, newColumn2) : line format /^([0-9]{4}-[0-9]{2}-[0-9]{2}) ([0-9]{2}:[0-9]{2}:[0-9]{2}) (\\[.*?\\]) ([0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.{1,3}) (.+) : (.*)$/ file 'access_log.txt';";
        boolean actual = reQL.createSchema(toPassIn);
        assertEquals(false, actual);
    }

    @Test
    void createSchema_hasBadFormat() {
        String toPassIn = "CREATE TABLE 'MyyyyyTable' (date, time, log_level, src_ip) : line format ^([0-9]{4}-[0-9]{2}-[0-9]{2}) ([0-9]{2}:[0-9]{2}:[0-9]{2}) (\\[.*?\\]) ([0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.{1,3}) (.+) : (.*)$ file 'access_log.txt';";
        boolean actual = reQL.createSchema(toPassIn);
        assertEquals(false, actual);
    }

    @Test
    void searchFile_shouldReturnTrue() {
        String toPassIn = "CREATE TABLE 'MyyyyyTable' (date, time, log_level, src_ip) : line format ^([0-9]{4}-[0-9]{2}-[0-9]{2}) ([0-9]{2}:[0-9]{2}:[0-9]{2}) (\\[.*?\\]) ([0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.{1,3}) (.+) : (.*)$ file 'access_log.txt';";
        reQL.grabFilePath(toPassIn);
        reQL.searchFile("SELECT date, time, src_ip FROM mylog WHERE date >= '2019-01-01'");
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