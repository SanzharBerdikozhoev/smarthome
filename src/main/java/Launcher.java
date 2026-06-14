import whz.pti.Main;
import whz.pti.utils.UserInitializer;

public class Launcher {
    public static void main(String[] args) {
        try {
            UserInitializer.init();
        } catch (Exception e) {
            if(e.getMessage().equals("Benutzer existiert bereits")) {
                System.out.println("Benutzer wurde nicht registriert, da sie bereits existieren.");
            }
        }

        Main.main(args);
    }
}
