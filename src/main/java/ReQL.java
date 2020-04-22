import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class ReQL {
    private HashMap<String, String> schema = new HashMap<>();
    private File file;
    private Boolean tableCreated = false;
    public BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private Boolean running = true;
    private Boolean valid = false;

    public void run() {
        System.out.println("NOTE: Type 'QUIT' at any time to end the program.");
        System.out.println("NOTE: Type 'HELP 1' at any time to get help with CREATE formatting.");
        System.out.println("NOTE: Type 'HELP 2' at any time to get help with SELECT formatting.");
        System.out.println("NOTE: Type 'NEW' at any time to create a new schema");
        do {
            if (tableCreated) {
                valid = false;
                do {
                    System.out.println("Enter a search (end with ; to end the input):");
                    String result = promptForInput();
                    if (!result.equals("SPECIAL")) {
                        if (!searchFile(result)) {
                            System.out.println("Sorry, something went wrong while processing your search. Please try again.");
                            valid = false;
                        } else {
                            valid = true;
                        }
                    }
                } while (!valid);
            } else {
                valid = false;
                do {
                    System.out.println("Please create your schema (end with ; to end the input):");
                    String result = promptForInput();
                    if (!result.equals("SPECIAL")) {
                        if (!createSchema(result)) {
                            System.out.println("Sorry, something went wrong while creating the table. Please try again.");
                            valid = false;
                        } else {
                            tableCreated = true;
                            valid = true;
                        }
                    }
                } while (!valid);
            }
        } while (running);
        System.out.println("Goodbye!");
    }

    public Boolean checkForSpecialInput(String input) {
        Boolean wasSpecial = true;
        if (input.toUpperCase().equals("QUIT")) {
            running = false;
        } else if (input.toUpperCase().equals("HELP 1")) {
            createHelp();
        } else if (input.toUpperCase().equals("HELP 2")) {
            selectHelp();
        } else if (input.toUpperCase().equals("NEW")) {
            System.out.println("WARNING: this will override your current table. Do you wish to continue? (y/n) ");
            try {
                input = reader.readLine();
                if (input.equals("y")) {
                    valid = true;
                    tableCreated = false;
                } else {
                    System.out.println("Cancelling override.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            wasSpecial = false;
        }
        return wasSpecial;
    }

    public String promptForInput() {
        String input = "";
        try {
            do {
                System.out.print(">");
                input += reader.readLine().trim() + " ";
                if (!input.trim().equals("")) {
                    if (input.charAt(input.length() - 2) == ';') {
                        break;
                    } else if (checkForSpecialInput(input.trim())) {
                        input = "SPECIAL";
                        break;
                    }
                } else {
                    input = "";
                    break;
                }
            } while (true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return input.trim();
    }

    private void createHelp() {
        System.out.println("CREATE TABLE '<table-name>' (<col-names-list>) : line format /<line-format-regex> file '<file-path>");
        System.out.println("\t'table-name' needs one or more alpha numeric characters, always beginning with a letter. It cannot contain underscores, spaces, or special characters");
        System.out.println("\t'col-names-list' refers to a comma separated list of column names corresponding to regex groupings in the line format expression.");
        System.out.println("\t'line-format-expression' is the expression applied to each file line. There MUST be an expression for every column name.");
        System.out.println("\t'file-path' is the path to the file on disk where the actual data is stored");
    }

    private void selectHelp() {
        System.out.println("SELECT <col-names> FROM <table-name> WHERE <criteria>");
        System.out.println("\t'col-names' refers to a comma separated list of column names to be included in the result set");
        System.out.println("\t'table-name' refers to the name of the table to query (maps to the file from the create statement)");
        System.out.println("\t'criteria' needs: col-name <comparison> <value> [<additional-criteria>]");
        System.out.println("\t\t'comparison' refers to =, <, >, <= or >=");
        System.out.println("\t\t'value' is whatever you want to compare the column's data to");
        System.out.println("\t'additional-criteria is formatted just like 'criteria' with either an 'AND' or an 'OR' before it");
    }

    public Boolean createSchema(String input) {

        return false;
    }

    public Boolean searchFile(String input) {

        return false;
    }
}
