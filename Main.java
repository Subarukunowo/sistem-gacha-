import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

// Class untuk representasi Item yang akan di-roll
abstract class Item {
    protected String name;
    protected int rarity; // 3, 4, 5 stars

    public Item(String name, int rarity) {
        this.name = name;
        this.rarity = rarity;
    }

    public String getName() {
        return name;
    }

    public int getRarity() {
        return rarity;
    }

    public abstract void display();
}

// Class untuk Karakter
class Character extends Item {
    private String element;

    public Character(String name, int rarity, String element) {
        super(name, rarity);
        this.element = element;
    }

    @Override
    public void display() {
        System.out.println("Character: " + name + " | Rarity: " + rarity + " | Element: " + element);
    }
}

// Class untuk Senjata
class Weapon extends Item {
    private String type;

    public Weapon(String name, int rarity, String type) {
        super(name, rarity);
        this.type = type;
    }

    @Override
    public void display() {
        System.out.println("Weapon: " + name + " | Rarity: " + rarity + " | Type: " + type);
    }
}

class Player {
    private int primogems;
    private List<Item> inventory;

    public Player(int initialPrimogems) {
        this.primogems = initialPrimogems;
        this.inventory = new ArrayList<>();
    }

    public int getPrimogems() {
        return primogems;
    }

    public void addPrimogems(int amount) {
        primogems += amount;
        System.out.println("You earned " + amount + " primogems! Total primogems: " + primogems);
    }

    public boolean spendPrimogems(int amount) {
        if (primogems >= amount) {
            primogems -= amount;
            System.out.println("You spent " + amount + " primogems. Remaining primogems: " + primogems);
            return true;
        } else {
            System.out.println("Not enough primogems!");
            return false;
        }
    }

    public void addItemToInventory(Item item) {
        inventory.add(item);
        System.out.println(item.getName() + " has been added to your inventory.");
    }

    public void displayInventory() {
        System.out.println("Inventory:");
        for (Item item : inventory) {
            item.display();
        }
    }
}

class GachaSystem {
    private List<Item> items;
    private Random random;
    private int pullCount;
    private final int HARD_PITY = 90; // Pity untuk karakter, bisa berbeda untuk senjata

    public GachaSystem() {
        items = new ArrayList<>();
        random = new Random();
        pullCount = 0;
        populateItems(); // Populasikan item yang ada di banner
    }

    private void populateItems() {
        // Tambahkan Karakter dan Senjata ke dalam banner
        items.add(new Character("Diluc", 5, "Pyro"));
        items.add(new Character("Mona", 5, "Hydro"));
        items.add(new Weapon("Aquila Favonia", 5, "Sword"));
        items.add(new Character("Xiangling", 4, "Pyro"));
        items.add(new Weapon("Favonius Lance", 4, "Polearm"));
        items.add(new Weapon("Black Tassel",3,"Claymore"));
        // Tidak ada item dengan rarity 3 dalam contoh ini
    }

    public Item wish() {
        pullCount++;
        if (pullCount >= HARD_PITY) {
            return guaranteeFiveStar();
        } else {
            int chance = random.nextInt(90); // Menghasilkan angka acak dari 0 hingga 99
            if (chance < 1) { // 1% chance untuk 5-star item
                return getRandomItemByRarity(5);
            } else if (chance < 10) { // 9% chance untuk 4-star item
                return getRandomItemByRarity(4);
            } else { // 90% chance untuk 3-star item
                return getRandomItemByRarity(3);
            }
        }
    }

    private Item guaranteeFiveStar() {
        pullCount = 0; // Reset pity
        return getRandomItemByRarity(5);
    }

    private Item getRandomItemByRarity(int rarity) {
        List<Item> filteredItems = new ArrayList<>();
        for (Item item : items) {
            if (item.getRarity() == rarity) {
                filteredItems.add(item);
            }
        }
        if (filteredItems.isEmpty()) {

            System.out.println("No items found with rarity " + rarity);
            return null;
        }
        return filteredItems.get(random.nextInt(filteredItems.size()));
    }

    public void displayPullResult(Item item) {
        if (item != null) {
            System.out.println("Congratulations! You got:");
            item.display();
        } else {
            System.out.println("No item was pulled.");
        }
    }
}

class DailyQuest {
    private static final int PRIMOGEMS_REWARD = 100;

    public void completeDailyQuest(Player player) {
        System.out.println("Completing daily quest...");
        player.addPrimogems(PRIMOGEMS_REWARD);
        System.out.println("Daily quest completed!");
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Player player = new Player(0); // Player starts with 0 primogems
        GachaSystem gacha = new GachaSystem();
        DailyQuest dailyQuest = new DailyQuest();

        while (true) {
            System.out.println("Choose an action:");
            System.out.println("1. Complete daily quest");
            System.out.println("2. Perform a single gacha pull (160 primogems)");
            System.out.println("3. Perform a ten-pull (1600 primogems)");
            System.out.println("4. View inventory");
            System.out.println("5. Exit");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    dailyQuest.completeDailyQuest(player);
                    break;
                case 2:
                    if (player.spendPrimogems(160)) {
                        Item item = gacha.wish();
                        if (item != null) { // Check if item is not null before adding to inventory
                            player.addItemToInventory(item);
                            gacha.displayPullResult(item);
                        } else {
                            System.out.println("No item was pulled.");
                        }
                    }
                    break;
                case 3:
                    if (player.spendPrimogems(1600)) {
                        for (int i = 0; i < 10; i++) {
                            Item item = gacha.wish();
                            if (item != null) { // Check if item is not null before adding to inventory
                                player.addItemToInventory(item);
                                gacha.displayPullResult(item);
                            } else {
                                System.out.println("No item was pulled.");
                            }
                        }
                    }
                    break;
                case 4:
                    player.displayInventory();
                    break;
                case 5:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please choose again.");
            }
        }
    }
}
