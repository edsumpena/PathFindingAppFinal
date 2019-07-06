package App.EditParameters.Secret;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Secret {
    private String username;
    private String password;

    // https://stackoverflow.com/questions/15749192/how-do-i-load-a-file-from-resource-folder
    public void loadSecret() {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("secret.txt");
        InputStreamReader isReader = new InputStreamReader(is);
        BufferedReader reader = new BufferedReader(isReader);
        try {
            username = reader.readLine();
            password = reader.readLine();
        }
        catch (IOException e) {
            System.err.println("Failed to read secret file");
        }
    }
}
