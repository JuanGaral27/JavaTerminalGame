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

private Room generateRandomRoom(int floorNum, int roomNum) {
        int chance = rnd.nextInt(100);
        if (chance < 60) {
            List<Enemy> enemies = Enemy.randomEnemiesForFloor(floorNum);
            return new Room("Sala " + roomNum + " (peligrosa)", "Una sala llena de enemigos.", enemies, null);
        } else if (chance < 85) {
            Item it = Item.randomItemForFloor(floorNum);
            return new Room("Sala " + roomNum + " (brillante)", "Algo brilla en el suelo.", null, it);
        } else {
            return new Room("Sala " + roomNum + " (vacía)", "Una sala silenciosa y polvorienta.", null, null);
        }
    }

    private void mainLoop() {
        for (Floor floor : floors) {
            System.out.println("\n--- Piso " + floor.floorNum + " ---");
            for (Room room : floor.rooms) {
                System.out.println("\nEntrando en " + room.name + ": " + room.description);
                if (room.enemies != null) {
                    combat(room.enemies);
                    if (!player.isAlive()) {
                        System.out.println("Has muerto... Fin del juego.");
                        return;
                    }
                    if (rnd.nextInt(100) < 50) {
                        Item drop = Item.randomItemForFloor(floor.floorNum);
                        System.out.println("¡Has encontrado un objeto: " + drop.name + "!");
                        player.inventory.add(drop);
                    }
                }
                if (room.item != null) {
                    System.out.println("¡Hay un objeto en la sala: " + room.item.name + "!");
                    player.inventory.add(room.item);
                }
            }
        }
    }

    private void combat(List<Enemy> enemies) {
        System.out.println("Enemigos encontrados: " + enemies.size());
        for (Enemy e : enemies) System.out.println("- " + e.name + " HP: " + e.hp);
        while (!enemies.isEmpty() && player.isAlive()) {
            System.out.println("\nTu turno:");
            player.showStats();
            System.out.println("1) Ataque básico  2) Usar habilidad  3) Usar objeto");
            int choice = 1;
            try { choice = Integer.parseInt(in.nextLine()); } catch(Exception e){}

            if (choice == 1) {
                Enemy target = enemies.get(0);
                player.attack(target);
                if (!target.isAlive()) {
                    System.out.println(target.name + " ha sido derrotado.");
                    enemies.remove(target);
                }
            } else if (choice == 2) {
                player.useAbility(enemies);
            } else if (choice == 3) {
                player.useItem();
            }

            for (Enemy e : new ArrayList<>(enemies)) {
                if (e.isAlive()) e.attack(player);
                if (!player.isAlive()) break;
            }
        }
    }
}

