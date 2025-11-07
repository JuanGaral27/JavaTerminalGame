import java.util.*;
import java.util.function.Consumer;

public class MiniDungeon {
    public static void main(String[] args) {
        Game game = new Game();
        game.start();
    }
}
class Game {
    private final Scanner in = new Scanner(System.in);
    private final Random rnd = new Random();
    private Player player;
    private List<Floor> floors;
    private final int FLOORS = 3;
    private final int ROOMS_PER_FLOOR = 5;

    void start() {
        showIntro();
        chooseClass();
        createWorld();
        mainLoop();
        System.out.println("\nGracias por jugar. ¡Hasta la próxima aventura!");
    }

    private void showIntro() {
        System.out.println("=== MINI DUNGEON: ¡El Sombrero Parlante y los Pisos Perdidos! ===");
        System.out.println("Eres un aventurero en busca del Sombrero Parlante.");
        System.out.println("Pero antes de iniciar... debes elegir tu clase.\n");
    }

    private void chooseClass() {
        System.out.println("Elige tu clase:");
        System.out.println("1) Mago ");
        System.out.println("2) Guerrero ");
        System.out.println("3) Caballero ");
        System.out.println("4) Asesino ");
        System.out.println("5) Luchador ");
        System.out.println("6) Invocador ");
        System.out.println("7) Berserker ");

        int choice;
        while (true) {
            System.out.print("Ingresa el número de tu elección: ");
            try {
                choice = Integer.parseInt(in.nextLine());
                if (choice >= 1 && choice <= 7) break;
            } catch (Exception e) {}
            System.out.println("Opción inválida. Intenta de nuevo.");
        }

        String className = switch (choice) {
            case 1 -> "Mago";
            case 2 -> "Guerrero";
            case 3 -> "Caballero";
            case 4 -> "Asesino";
            case 5 -> "Luchador";
            case 6 -> "Invocador";
            case 7 -> "Berserker";
            default -> "Guerrero";
        };
        player = Player.createWithClass("Aventurero", className);
        System.out.println("\nHas elegido la clase: " + className);
        player.showStats();
    }

    private void createWorld() {
        floors = new ArrayList<>();
        for (int f = 1; f <= FLOORS; f++) {
            Floor floor = new Floor(f);
            for (int r = 1; r <= ROOMS_PER_FLOOR; r++) {
                floor.addRoom(generateRandomRoom(f, r));
            }
            floors.add(floor);
        }
    }

}