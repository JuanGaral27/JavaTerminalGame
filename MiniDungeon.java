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
class Player {
    String name, playerClass;
    int level, xp, xpToNext;
    int hp, maxHp, mp, maxMp;
    int atkFis, defFis, atkMag, defMag, velocidad;

    List<Item> inventory = new ArrayList<>();
    Weapon weapon = null;
    Armor armor = null;
    Boots boots = null;

    Player(String name, String playerClass, int hp, int mp, int atkFis, int defFis, int atkMag, int defMag, int velocidad) {
        this.name = name;
        this.playerClass = playerClass;
        this.level = 1;
        this.xp = 0;
        this.xpToNext = 50;
        this.maxHp = hp; this.hp = hp;
        this.maxMp = mp; this.mp = mp;
        this.atkFis = atkFis;
        this.defFis = defFis;
        this.atkMag = atkMag;
        this.defMag = defMag;
        this.velocidad = velocidad;
        inventory.add(new Consumable("Poción Pequeña", "Restaura 15 HP", p -> p.heal(15)));
        inventory.add(new Weapon("Espada Oxidada", 1, 3));
    }

    static Player createWithClass(String name, String className) {
        return switch(className.toLowerCase()) {
            case "mago" -> new Player(name,"Mago",60,50,4,6,15,10,5);
            case "guerrero" -> new Player(name,"Guerrero",80,20,10,10,8,8,8);
            case "caballero" -> new Player(name,"Caballero",100,10,7,15,4,12,9);
            case "asesino" -> new Player(name,"Asesino",65,25,13,6,12,5,15);
            case "luchador" -> new Player(name,"Luchador",120,10,18,15,0,5,7);
            case "invocador" -> new Player(name,"Invocador",70,80,0,5,18,12,8);
            case "berserker" -> new Player(name,"Berserker",90,25,16,8,4,4,18);
            default -> new Player(name,"Guerrero",80,20,10,10,8,8,8);
        };
    }

    void showStats() {
        System.out.println("\n=== " + name + " - " + playerClass + " ===");
        System.out.println("Nivel: " + level + " | XP: " + xp + "/" + xpToNext);
        System.out.println("HP: " + hp + "/" + maxHp + " | MP: " + mp + "/" + maxMp);
        System.out.println("AtkF: " + getAtkFis() + "  DefF: " + getDefFis() +
                "  AtkM: " + getAtkMag() + "  DefM: " + getDefMag() + "  Vel: " + getVel());
        System.out.println("Arma: " + (weapon!=null ? weapon.name : "Ninguna"));
        System.out.println("Armadura: " + (armor!=null ? armor.name : "Ninguna"));
        System.out.println("Botas: " + (boots!=null ? boots.name : "Ninguna"));
    }

    void attack(Enemy enemy) {
        Random rnd = new Random();
        int dmg = Math.max(1,getAtkFis() - enemy.defFis/2);
        if (rnd.nextInt(100)<15) dmg*=2; // crítico
        enemy.takeDamage(dmg);
        System.out.println("Atacas a " + enemy.name + " infligiendo " + dmg + " daño físico.");
    }

    void heal(int amount) {
        hp = Math.min(maxHp, hp + amount);
        System.out.println("Te curas " + amount + " HP. Ahora: " + hp + "/" + maxHp);
    }

    boolean isAlive() { return hp > 0; }

    int getAtkFis() { return atkFis + (weapon!=null?weapon.atkBonus:0); }
    int getDefFis() { return defFis + (armor!=null?armor.defBonus:0); }
    int getAtkMag() { return atkMag; }
    int getDefMag() { return defMag; }
    int getVel() { return velocidad + (boots!=null?boots.speedBonus:0); }

    void useAbility(List<Enemy> enemies) {
        if (mp>=10) {
            Enemy target = enemies.get(0);
            int dmg = atkMag*2;
            target.takeDamage(dmg);
            mp -=10;
            System.out.println("Infliges " + dmg + " de daño mágico a " + target.name);
            if (!target.isAlive()) { System.out.println(target.name + " ha sido derrotado."); enemies.remove(target); }
        } else {
            System.out.println("No tienes suficiente MP.");
        }
    }

    void useItem() {
        if (inventory.isEmpty()) { System.out.println("No tienes items."); return; }
        System.out.println("Items:");
        for (int i=0;i<inventory.size();i++) System.out.println((i+1)+") "+inventory.get(i).name);
        Scanner in = new Scanner(System.in);
        int choice=1;
        try{ choice=Integer.parseInt(in.nextLine())-1;}catch(Exception e){}
        if (choice>=0 && choice<inventory.size()) {
            Item it = inventory.get(choice);
            it.use(this);
            inventory.remove(it);
        }
    }
}

class Enemy {
    String name;
    int hp, maxHp, atkFis, defFis, atkMag, defMag, velocidad, mp, maxMp;
    Random rnd = new Random();

    Enemy(String name,int hp,int mp,int atkFis,int defFis,int atkMag,int defMag,int velocidad){
        this.name=name; this.hp=hp; this.maxHp=hp; this.mp=mp; this.maxMp=mp;
        this.atkFis=atkFis; this.defFis=defFis; this.atkMag=atkMag; this.defMag=defMag;
        this.velocidad=velocidad;
    }

    void attack(Player player){
        if(mp>=5 && atkMag>0 && rnd.nextInt(100)<40){ // 40% de usar habilidad mágica si tiene MP
            int dmg = Math.max(1, atkMag - player.defMag/2);
            player.hp -= dmg;
            mp -=5;
            System.out.println(name + " usa habilidad mágica e inflige " + dmg + " daño mágico!");
        } else {
            int dmg = Math.max(1, atkFis - player.getDefFis()/2);
            player.hp -= dmg;
            System.out.println(name + " ataca físicamente e inflige " + dmg + " daño.");
        }
    }

    boolean isAlive(){ return hp>0; }
    void takeDamage(int d){ hp-=d; if(hp<0) hp=0; }

    static List<Enemy> randomEnemiesForFloor(int floor){
        List<Enemy> enemies = new ArrayList<>();
        Random rnd = new Random();
        int horda = 1 + rnd.nextInt(floor+1); // Oleadas
        for(int i=0;i<horda;i++){
            int tipo = rnd.nextInt(10);
            switch(tipo){
                case 0 -> enemies.add(new Enemy("Flor monstruosa",30+floor*5,10,6+floor,2+floor,0,2,5+floor));
                case 1 -> enemies.add(new Enemy("Serpiente gigante",25+floor*5,15,7+floor,1+floor,4,2,6+floor));
                case 2 -> enemies.add(new Enemy("Golem",50+floor*5,0,10+floor,6+floor,0,4,3+floor));
                case 3 -> enemies.add(new Enemy("Ciclope",45+floor*5,10,9+floor,4+floor,2,3,4+floor));
                case 4 -> enemies.add(new Enemy("Esqueleto arquero",30+floor*5,5,8+floor,2+floor,0,1,8+floor));
                case 5 -> enemies.add(new Enemy("Gargola poseída",35+floor*5,15,7+floor,5+floor,3,4,6+floor));
                case 6 -> enemies.add(new Enemy("Pejelagarto",28+floor*5,5,6+floor,3+floor,0,2,7+floor));
                case 7 -> enemies.add(new Enemy("Esqueleto caballero",40+floor*5,0,9+floor,6+floor,0,2,4+floor));
                case 8 -> enemies.add(new Enemy("No-muerto arcanista",25+floor*5,20,3+floor,2+floor,8+floor,5+floor,5+floor));
                default -> enemies.add(new Enemy("Peruanos guerreros",20+floor*5,5,5+floor,2+floor,0,1,10+floor));
            }
        }
        return enemies;
    }
}

class Floor{
    int floorNum;
    List<Room> rooms = new ArrayList<>();
    Floor(int num){ floorNum=num; }
    void addRoom(Room r){ rooms.add(r);}
}

class Room{
    String name, description;
    List<Enemy> enemies;
    Item item;
    Room(String n,String d,List<Enemy> enemies,Item item){ name=n; description=d; this.enemies=enemies; this.item=item;}
}

abstract class Item{
    String name;
    Item(String n){ name=n; }
    abstract void use(Player player);

    static Item randomItemForFloor(int floor){
        Random rnd = new Random();
        int tipo = rnd.nextInt(3);
        return switch(tipo){
            case 0 -> new Weapon("Espada "+floor,floor,5+floor);
            case 1 -> new Armor("Armadura "+floor,floor);
            default -> new Consumable("Poción","Restaura 20 HP", p->p.heal(20));
        };
    }
}

class Weapon extends Item{
    int atkBonus, bonusStat;
    Weapon(String n,int bonusAtk,int bonusStat){ super(n); atkBonus=bonusAtk; this.bonusStat=bonusStat; }
    void use(Player p){ System.out.println("Equipas " + name); p.weapon=this;}
}

class Armor extends Item{
    int defBonus;
    Armor(String n,int def){ super(n); defBonus=def; }
    void use(Player p){ System.out.println("Equipas " + name); p.armor=this;}
}

class Boots extends Item{
    int speedBonus;
    Boots(String n,int speed){ super(n); speedBonus=speed; }
    void use(Player p){ System.out.println("Equipas " + name); p.boots=this;}
}

class Consumable extends Item{
    String desc;
    Consumer<Player> effect;
    Consumable(String n,String d, Consumer<Player> e){ super(n); desc=d; effect=e;}
    void use(Player p){ effect.accept(p); System.out.println("Usas " + name);}
}

