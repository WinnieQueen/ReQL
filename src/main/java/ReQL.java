import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReQL {
    private LinkedHashMap<String, String> schema = new LinkedHashMap<>();
    private String lineRegex = "";
    private String filePath = "";
    private String tableName = "";
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
                    clearAll();
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
                String ns = reader.readLine().trim();
                if (!ns.isEmpty())
                    input += ns + " ";
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
        System.out.println(input.trim());
        return input.trim();
    }

    private void createHelp() {
        System.out.println("CREATE TABLE '<table-name>' (<col-names-list>) : line format /<line-format-regex>/ file '<file-path>");
        System.out.println("\t'table-name' needs one or more alpha numeric characters, always beginning with a letter. It cannot contain underscores, spaces, or special characters");
        System.out.println("\t'col-names-list' refers to a comma separated list of column names corresponding to regex groupings in the line format expression. Column names can NOT contain 'tableName' or 'filePath'");
        System.out.println("\t'line-format-expression' is the expression applied to each file line. There MUST be an expression for every column name. They MUST be wrapped in parentheses, and CANNOT contain parentheses. MUST start and end with a forward-slash (/)");
        System.out.println("\t'file-path' is the path to the file on disk where the actual data is stored");
    }

    private void selectHelp() {
        System.out.println("SELECT <col-names> FROM <table-name> WHERE <criteria>");
        System.out.println("\t'col-names' refers to a comma separated list of column names to be included in the result set.");
        System.out.println("\t'table-name' refers to the name of the table to query (maps to the file from the create statement)");
        System.out.println("\t'criteria' needs: col-name <comparison> <value>");
        System.out.println("\t\t'comparison' refers to =, <, >, <= or >=");
        System.out.println("\t\t'value' is whatever you want to compare the column's data to");
    }

    public Boolean createSchema(String input) {
        // /^([0-9]{4}-[0-9]{2}-[0-9]{2}) ([0-9]{2}:[0-9]{2}:[0-9]{2}) ([A-Z]+) (\[.*?\]) ([0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.{1,3}) (.+) : (.*)$/
        boolean validSchema = false;
        if (input != null && !input.trim().isEmpty() && !input.contains("\\(") && !input.contains("\\)")) {
            if (input.matches("^CREATE TABLE '(([a-z]|[A-Z])+\\d*)+' \\(\\w+(, \\w+)*\\) : line format \\/.+\\/ file '.+';$")) {
                String[] regexps = grabRegexps(input);
                String[] columnNames = grabColumnNames(input);
                if (regexps.length == columnNames.length) {
                    if (grabFilePath(input) && grabTableName(input)) {
                        validSchema = true;
                    }
                    for (int i = 0; i < regexps.length; i++) {
                        if (columnNames[i] == "tableName" || columnNames[i] == "filePath") {
                            schema.clear();
                            validSchema = false;
                            break;
                        } else {
                            schema.put(columnNames[i], regexps[i]);
                        }
                    }
                }
            }
        }
        return validSchema;
    }

    public Boolean grabTableName(String input) {
        Boolean validName = false;
        int indexOfLastQuote = input.indexOf('\'', 14);
        String tableName = input.substring(14, indexOfLastQuote);
        if (!tableName.trim().isEmpty() && tableName != null) {
            this.tableName = tableName;
            validName = true;
        }
        return validName;
    }

    public String[] grabRegexps(String input) {
        int firstIndex = input.indexOf("line format /") + 13;
        int lastIndex = input.indexOf("/", firstIndex);
        String regexps = input.substring(firstIndex, lastIndex);
        lineRegex = regexps;
        String[] regexpsArr = regexps.split("\\)[^\\(]*\\(");
        for (int i = 0; i < regexpsArr.length; i++) {
            regexpsArr[i] = regexpsArr[i].replace(")", "");
            regexpsArr[i] = regexpsArr[i].replace("(", "");
            regexpsArr[i] = regexpsArr[i].replace("^", "");
            regexpsArr[i] = regexpsArr[i].replace("$", "");
        }
        return regexpsArr;
    }

    public Boolean grabFilePath(String input) {
        Boolean validFile = false;
        int firstIndex = input.indexOf("file '") + 6;
        int lastIndex = input.indexOf("'", firstIndex);
        String filePath = input.substring(firstIndex, lastIndex);
        if (!filePath.trim().isEmpty() && filePath != null) {
            this.filePath = filePath;
            validFile = true;
        }
        return validFile;
    }

    public String[] grabColumnNames(String input) {
        int firstIndex = input.indexOf("(") + 1;
        int lastIndex = input.indexOf(")", firstIndex);
        String columns = input.substring(firstIndex, lastIndex);
        String[] columnsArr = columns.split(", ");
        return columnsArr;
    }

    public Boolean searchFile(String input) {
        ArrayList<String> qualifyingLines = new ArrayList<>();
        Boolean validSearch = false;
        if (input != null && !input.trim().isEmpty()) {
            if (input.matches("(SELECT (((\\w+(_\\w+)?)(, )?))+) (FROM (\\w+) (WHERE (\\w+) (=|>|<|>=|<=) ('.+')))")) {
                if (tableName.equals(grabFromTableName(input))) {

                    File file = new File(filePath);
                    if (file != null) {
                        try {
                            String[] columnsToGrab = grabColumnsToGrab(input);
                            if (columnsToGrab != null) {
                                String s = Files.readString(Path.of(file.toURI()));
                                Matcher m = Pattern.compile("(WHERE (\\w+) (=|>|<|>=|<=) '(.+)')").matcher(input);
                                if (m.find()) {
                                    String whereColumn = m.group(2);
                                    if (schema.containsKey(whereColumn)) {
                                        validSearch = true;
                                        String comparison = m.group(3);
                                        String compareValue = m.group(4);
                                        m = Pattern.compile(lineRegex, Pattern.MULTILINE).matcher(s);
                                        while (m.find()) {
                                            String columnData = m.group(grabSchemaIndex(whereColumn));
                                            switch(comparison) {
                                                case ">":
                                                    if(columnData.compareTo(compareValue) > 0){
                                                        qualifyingLines.add(m.group(0));
                                                    }
                                                    break;
                                                case "=":
                                                    if(columnData.compareTo(compareValue) == 0){
                                                        qualifyingLines.add(m.group(0));
                                                    }
                                                    break;
                                                case "<":
                                                    if(columnData.compareTo(compareValue) < 0){
                                                        qualifyingLines.add(m.group(0));
                                                    }
                                                    break;
                                                case ">=":
                                                    if(columnData.compareTo(compareValue) >= 0){
                                                        qualifyingLines.add(m.group(0));
                                                    }
                                                    break;
                                                case "<=":
                                                    if(columnData.compareTo(compareValue) <= 0){
                                                        qualifyingLines.add(m.group(0));
                                                    }
                                                    break;
                                                default:
                                                    validSearch = false;
                                                    break;
                                            }
                                        }
                                        if(qualifyingLines.size() == 0) {
                                            System.out.println("I'm sorry, nothing in the file matched your search! Please change your criteria and try again.");
                                        } else {
                                            StringBuilder sb = new StringBuilder();
                                            sb.append("|||");
                                            ArrayList<Integer> indexes = new ArrayList<Integer>();
                                            for(String column : columnsToGrab) {
                                                int numOfSpaces = 40 - column.length();
                                                for(int i = 0; i < numOfSpaces / 2; i++) {
                                                    sb.append(" ");
                                                }
                                                sb.append(column);
                                                for(int i = 0; i < numOfSpaces / 2; i++) {
                                                    sb.append(" ");
                                                }
                                                sb.append("|||");
                                                indexes.add(grabSchemaIndex(column));
                                            }
                                            System.out.println(sb.toString());
                                            for(int i = 0; i < sb.toString().length(); i++) {
                                                System.out.print("_");
                                            }
                                            System.out.println();
                                            for (String line : qualifyingLines) {
                                                m = Pattern.compile(lineRegex).matcher(line);
                                                if(m.find()) {
                                                    System.out.print("|||");
                                                    for(Integer index : indexes) {
                                                        int numOfSpaces = 40 - m.group(index).length();
                                                        for(int i = 0; i < numOfSpaces / 2; i++) {
                                                            System.out.print(" ");
                                                        }
                                                        System.out.print(m.group(index));
                                                        for(int i = 0; i < numOfSpaces / 2; i++) {
                                                            System.out.print(" ");
                                                        }
                                                        System.out.print("|||");
                                                    }
                                                    System.out.println();
                                                }
                                            }
                                        }
                                    }
                                }

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return validSearch;
    }

    public int grabSchemaIndex(String key) {
        int index = 0;
        Object[] keys = schema.keySet().toArray();
        for(int i = 0; i < keys.length; i++) {
            if(keys[i].toString().equals(key)) {
                index = i + 1;
            }
        }
        return index;
    }

    public String grabFromTableName(String input) {
        String tableName = "";
        Matcher m = Pattern.compile("(FROM (\\w|\\d)+)").matcher(input);
        if (m.find()) {
            tableName = m.group(1).replace("FROM ", "");
        }
        return tableName;
    }

    public String[] grabColumnsToGrab(String input) {
        String[] columnsToFind = null;
        Matcher m = Pattern.compile("(SELECT (((\\w|\\d+(_\\w|\\d+)?)(, )?))+)").matcher(input);
        if (m.find()) {
            String columns = m.group(1);
            columns = columns.replace("SELECT ", "");
            String[] columnsArr = columns.split(", ");
            if (columnsArr.length > 0) {
                columnsToFind = columnsArr;
            }
        }
        return columnsToFind;
    }

    public void clearAll() {
        schema.clear();
        tableCreated = false;
        valid = false;
    }
}
