import whz.pti.Main;
import whz.pti.repositories.implementation.UserRepoImpl;

public class Launcher {
    public static void main(String[] args) {
//        Main.main(args);
        UserRepoImpl userRepo = new UserRepoImpl("users");

        System.out.println(userRepo.getAll());
    }
}
