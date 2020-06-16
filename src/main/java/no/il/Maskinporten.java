package no.il;

import no.il.utils.Print;
import no.il.utils.PropertiesReader;

public class Maskinporten {


    PropertiesReader props = null;

    public static void main(String args[]) {

        String path = "";

        if (args != null && args.length == 1 && args[0] != null) {
            path = args[0];
        } else {
            System.out.println("Usage: java -jar maskinporten.jar <property file name>");
            System.exit(0);
        }

        Maskinporten theProgram = new Maskinporten(path);
        theProgram.goProgram();
    }

    public Maskinporten(String path) {
        try {
            props = PropertiesReader.load(path);
            if (props.saveResult()) {
                Print.initiateFile();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void goProgram() {

        Client clientStuff = new Client((props));
        clientStuff.goClient();

        AccessToken accessTokenStuff = new AccessToken(props);
        accessTokenStuff.goAccessToken();

        Scope scopeStuff = new Scope(props);
        scopeStuff.goScopes();

        Certificate certStuff = new Certificate(props);
        certStuff.goCertificate();


        // If the result is saved to file, then close the file.
        if (props.saveResult()) {
            Print.closeFile();
        }

    }










}
