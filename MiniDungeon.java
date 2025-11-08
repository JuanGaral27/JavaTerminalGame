import java.util.*;

/* ======================= MAIN GAME ======================= */
public class MiniDungeon {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║        El origen del humilde elda          ║");
        System.out.println("╚════════════════════════════════════════════╝");
        System.out.println("\n...En un pequeño pueblo rodeado de montañas...");
        System.out.println("Vivía un joven huérfano, criado por una familia humilde que le enseñó el valor del esfuerzo.");
        System.out.println("Desde pequeño soñó con dejar su nombre grabado en los libros del gremio de aventureros.");
        System.out.println("Un día, con nada más que su determinación, decide partir hacia la ciudad de Lioren...");
        System.out.println("\nPresiona ENTER para continuar...");
        sc.nextLine();

        System.out.println("Tras varios días de viaje, llegas ante las puertas del Gremio de Aventureros.");
        System.out.println("Un hombre robusto con una cicatriz en el rostro te recibe.");
        System.out.println("\n—Bienvenido, forastero. Antes de comenzar tu travesía, dime... ¿qué tipo de héroe deseas ser?");
        System.out.println("\nElige tu clase:");
        System.out.println("1. Guerrero ");
        System.out.println("2. Mago ");
        System.out.println("3. Caballero ");
        System.out.println("4. Luchador ");
        System.out.println("5. Invocador ");
        System.out.println("6. Berserker ");
        System.out.print("> ");

        int choice = sc.nextInt();
        sc.nextLine();

        Player player = Player.createByClass(choice);

        System.out.println("\n—Muy bien. Desde ahora serás conocido como el " + player.className + " del gremio.");
        System.out.println("Que los dioses te acompañen en tu viaje, aventurero.");
        System.out.println("\nPresiona ENTER para comenzar tu aventura...");
        sc.nextLine();

        GameLoop.start(player);
    }
}

class Player {
    String className;
    int hp, maxHp, mp, maxMp, atkFis, defFis, atkMag, defMag, speed, gold = 0;
    Random rnd = new Random();
    List<Ability> abilities = new ArrayList<>();

    Player(String className, int hp, int mp, int atkFis, int defFis, int atkMag, int defMag, int speed) {
        this.className = className;
        this.hp = this.maxHp = hp;
        this.mp = this.maxMp = mp;
        this.atkFis = atkFis;
        this.defFis = defFis;
        this.atkMag = atkMag;
        this.defMag = defMag;
        this.speed = speed;
    }

    static Player createByClass(int choice) {
        Player p;
        switch (choice) {
            case 1 -> p = new Player("Guerrero", 120, 30, 20, 15, 5, 8, 10);
            case 2 -> p = new Player("Mago", 80, 100, 5, 8, 25, 18, 10);
            case 3 -> p = new Player("Caballero", 140, 40, 18, 20, 8, 12, 8);
            case 4 -> p = new Player("Luchador", 160, 20, 25, 18, 0, 5, 12);
            case 5 -> p = new Player("Invocador", 100, 120, 8, 10, 30, 15, 9);
            case 6 -> p = new Player("Berserker", 130, 40, 28, 16, 5, 8, 14);
            default -> p = new Player("Aventurero", 100, 50, 10, 10, 10, 10, 10);
        }
        p.loadAbilities();
        return p;
    }

    void loadAbilities() {
        switch (className) {
            case "Guerrero" -> abilities.addAll(Arrays.asList(
                new Ability("Corte Feroz", "Daño físico medio", 5, 1.5, 0),
                new Ability("Defensa de Hierro", "Aumenta defensa física", 8, 0, 5),
                new Ability("Embate Heroico", "Daño físico alto, gasta PM", 10, 2.0, 0),
                new Ability("Grito de Guerra", "Aumenta ataque físico", 8, 0, 4)
            ));
            case "Mago" -> abilities.addAll(Arrays.asList(
                new Ability("Bola de Fuego", "Daño mágico fuerte", 10, 0, 2.0),
                new Ability("Rayo", "Daño mágico rápido", 8, 0, 1.5),
                new Ability("Escudo Arcano", "Aumenta defensa mágica", 10, 0, 0),
                new Ability("Sanación", "Cura HP", 12, 0, 0)
            ));
            case "Caballero" -> abilities.addAll(Arrays.asList(
                new Ability("Corte Justo", "Ataque físico balanceado", 6, 1.2, 0),
                new Ability("Muro Sagrado", "Aumenta todas las defensas", 10, 0, 0),
                new Ability("Golpe de Honor", "Daño físico moderado", 8, 1.5, 0),
                new Ability("Oración", "Recupera un poco de HP y MP", 12, 0, 0)
            ));
            case "Luchador" -> abilities.addAll(Arrays.asList(
                new Ability("Puño Sangriento", "Usa vida para causar gran daño", 0, 2.5, 0, 15),
                new Ability("Contraataque", "Devuelve parte del daño recibido", 5, 0, 0),
                new Ability("Rugido del Alma", "Aumenta ataque físico", 8, 0, 0),
                new Ability("Ultimo Aliento", "Gran golpe si HP < 30%", 5, 3.0, 0)
            ));
            case "Invocador" -> abilities.addAll(Arrays.asList(
                new Ability("Invocar Dragón Bebé", "Ataque mágico fuerte", 15, 0, 2.5),
                new Ability("Invocar Golem", "Aumenta defensa", 12, 0, 0),
                new Ability("Invocar Serpiente Mística", "Ataque mágico medio", 10, 0, 2.0),
                new Ability("Bendición de Espíritus", "Cura y aumenta ataque mágico", 15, 0, 0)
            ));
            case "Berserker" -> abilities.addAll(Arrays.asList(
                new Ability("Furia", "Aumenta ataque, reduce defensa", 8, 0, 0, 10),
                new Ability("Golpe Rabioso", "Daño alto, consume HP", 5, 3.0, 0, 10),
                new Ability("Instinto Salvaje", "Duplica velocidad por 3 turnos", 8, 0, 0),
                new Ability("Matanza", "Ataque físico extremo", 10, 3.5, 0, 15)
            ));
        }
    }

    void showStats() {
        System.out.println("\n==== ESTADÍSTICAS DE " + className.toUpperCase() + " ====");
        System.out.println("Vida: " + hp + "/" + maxHp);
        System.out.println("Magia: " + mp + "/" + maxMp);
        System.out.println("Ataque físico: " + atkFis);
        System.out.println("Defensa física: " + defFis);
        System.out.println("Ataque mágico: " + atkMag);
        System.out.println("Defensa mágica: " + defMag);
        System.out.println("Velocidad: " + speed);
        System.out.println("Oro: " + gold + " monedas");
        System.out.println("==============================\n");
    }
}

class Ability {
    String name, desc;
    int mpCost;
    double dmgFis, dmgMag;
    int hpCost = 0;

    Ability(String name, String desc, int mpCost, double dmgFis, double dmgMag) {
        this.name = name; this.desc = desc; this.mpCost = mpCost;
        this.dmgFis = dmgFis; this.dmgMag = dmgMag;
    }

    Ability(String name, String desc, int mpCost, double dmgFis, double dmgMag, int hpCost) {
        this(name, desc, mpCost, dmgFis, dmgMag);
        this.hpCost = hpCost;
    }
}

class Enemy {
    String name;
    int hp, atkFis, defFis, atkMag, defMag, velocidad;

    Enemy(String name, int hp, int atkFis, int defFis, int atkMag, int defMag, int velocidad) {
        this.name = name;
        this.hp = hp;
        this.atkFis = atkFis;
        this.defFis = defFis;
        this.atkMag = atkMag;
        this.defMag = defMag;
        this.velocidad = velocidad;
    }

    static List<Enemy> randomEnemies(int floor) {
        Random rnd = new Random();
        int cantidad = 1 + rnd.nextInt(1 + floor / 2);
        List<Enemy> list = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            int tipo = rnd.nextInt(6);
            switch (tipo) {
                case 0 -> list.add(new Enemy("Flor Monstruosa", 80 + floor * 10, 15 + floor, 10 + floor, 5 + floor, 8 + floor, 8 + floor));
                case 1 -> list.add(new Enemy("Serpiente Gigante", 100 + floor * 12, 18 + floor, 12 + floor, 8 + floor, 6 + floor, 10 + floor));
                case 2 -> list.add(new Enemy("Esqueleto Guerrero", 90 + floor * 11, 20 + floor, 14 + floor, 0, 4 + floor, 7 + floor));
                case 3 -> list.add(new Enemy("Gárgola Poseída", 120 + floor * 14, 22 + floor, 15 + floor, 12 + floor, 10 + floor, 9 + floor));
                case 4 -> list.add(new Enemy("Pejelagarto", 110 + floor * 13, 19 + floor, 13 + floor, 5 + floor, 6 + floor, 10 + floor));
                default -> list.add(new Enemy("Peruano Aventurero", 130 + floor * 15, 25 + floor, 14 + floor, 10 + floor, 10 + floor, 12 + floor));
            }
        }
        return list;
    }
}

class GameLoop {
    static void start(Player player) {
        Scanner sc = new Scanner(System.in);
        int floor = 1;

        while (player.hp > 0) {
            System.out.println("\n══════════════════════════════════════");
            System.out.println("🌙 PISO " + floor + " – Un nuevo desafío...");
            System.out.println("══════════════════════════════════════");
            List<Enemy> enemies = Enemy.randomEnemies(floor);

            System.out.println("Enemigos encontrados:");
            for (Enemy e : enemies)
                System.out.println("- " + e.name + " (HP: " + e.hp + ")");

            System.out.println("\nPresiona ENTER para luchar...");
            sc.nextLine();

            for (Enemy e : enemies) {
                while (e.hp > 0 && player.hp > 0) {
                    System.out.println("\nTu HP: " + player.hp + "/" + player.maxHp + " | MP: " + player.mp + "/" + player.maxMp);
                    System.out.println("Enemigo: " + e.name + " (" + e.hp + " HP)");
                    System.out.println("1. Ataque básico");
                    System.out.println("2. Usar habilidad");
                    System.out.print("> ");
                    int action = sc.nextInt();
                    sc.nextLine();

                    if (action == 1) {
                        int dmg = Math.max(1, player.atkFis - e.defFis / 2);
                        e.hp -= dmg;
                        System.out.println("Atacas con tu arma e infliges " + dmg + " de daño.");
                    } else {
                        for (int i = 0; i < player.abilities.size(); i++) {
                            Ability a = player.abilities.get(i);
                            System.out.println((i + 1) + ". " + a.name + " (" + a.desc + ")");
                        }
                        System.out.print("> ");
                        int ab = sc.nextInt() - 1;
                        sc.nextLine();
                        if (ab >= 0 && ab < player.abilities.size()) {
                            Ability a = player.abilities.get(ab);
                            if (player.mp >= a.mpCost) {
                                player.mp -= a.mpCost;
                                if (a.hpCost > 0) player.hp -= a.hpCost;
                                int dmg = (int) ((player.atkFis * a.dmgFis) + (player.atkMag * a.dmgMag));
                                e.hp -= dmg;
                                System.out.println("Usas " + a.name + " e infliges " + dmg + " de daño!");
                            } else {
                                System.out.println("No tienes suficiente MP!");
                            }
                        }
                    }

                    if (e.hp <= 0) {
                        System.out.println(e.name + " ha sido derrotado!");
                        player.gold += 15 + floor * 5;
                        break;
                    }

                    int edmg = Math.max(1, e.atkFis - player.defFis / 2);
                    player.hp -= edmg;
                    System.out.println(e.name + " te ataca e inflige " + edmg + " de daño.");
                }
                if (player.hp <= 0) break;
            }

            if (player.hp <= 0) {
                System.out.println("\nHas caído en combate...");
                System.out.println("Tu nombre será recordado entre los héroes caídos...");
                break;
            }

            floor++;
            player.showStats();
            System.out.println("¿Continuar al siguiente piso? (s/n)");
            if (!sc.nextLine().toLowerCase().equals("s")) break;
        }
    }
}
