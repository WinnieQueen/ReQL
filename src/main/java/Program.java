import java.io.File;
import java.util.HashMap;

public class Program {

    private static HashMap<String, String> schema = new HashMap<>();
    private static File file;
    private static Boolean tableCreated = false;

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        Boolean valid = false;
        if (tableCreated) {
            valid = false;
            do {
                System.out.println("Enter a search:");
                String result = promptForInput();
                searchFile(result);
                if (!createSchema(result)) {
                    System.out.println("Sorry, something went wrong while processing your search. Please try again.");
                    valid = false;
                } else {
                    valid = true;
                }
            } while (!valid);
        } else {
            valid = false;
            if (tableCreated) {
                do {
                    System.out.println("Please create your schema:");
                    String result = promptForInput();
                    createSchema(result);
                    if (!createSchema(result)) {
                        System.out.println("Sorry, something went wrong while creating the table. Please try again.");
                        valid = false;
                    } else {
                        valid = true;
                    }
                } while (!valid);
            }
        }
    }

        public static String promptForInput () {

            return null;
        }

        public static Boolean createSchema (String input){

            return false;
        }

        public static Boolean searchFile (String input) {

            return null;
        }
    }
