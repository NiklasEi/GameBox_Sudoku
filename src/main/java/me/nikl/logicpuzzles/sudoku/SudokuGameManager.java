package me.nikl.logicpuzzles.sudoku;

import me.nikl.gamebox.GameBoxSettings;
import me.nikl.gamebox.game.exceptions.GameStartException;
import me.nikl.gamebox.game.manager.GameManager;
import me.nikl.gamebox.game.rules.GameRule;
import me.nikl.gamebox.utility.FileUtility;
import me.nikl.gamebox.utility.ItemStackUtility;
import me.nikl.gamebox.utility.Permission;
import me.nikl.gamebox.utility.StringUtility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Created by Niklas.
 *
 * Sudoku GameManager
 */
public class SudokuGameManager implements GameManager {
    private static int NEW_LINE_CHAR_LENGTH;
    private static int LINE_LENGTH;
    private Sudoku game;
    private Map<UUID, SudokuGame> games = new HashMap<>();
    private SudokuLanguage lang;
    private Map<String, SudokuGameRules> gameTypes = new HashMap<>();
    private RandomAccessFile raf;
    private Random random;
    private Map<Integer, ItemStack> cover = new HashMap<>();
    private Map<Integer, ItemStack> tip = new HashMap<>();
    private Map<Integer, ItemStack> number = new HashMap<>();
    private boolean problemWhileLoading = false;

    public SudokuGameManager(Sudoku game){
        this.game = game;
        this.lang = (SudokuLanguage) game.getGameLang();
        this.random = new Random(System.currentTimeMillis());

        File puzzle = new File(game.getDataFolder().toString() + File.separatorChar + "puzzles.yml");
        if(!puzzle.exists()){
            Bukkit.getConsoleSender().sendMessage(lang.PREFIX + ChatColor.RED + " Puzzles file is missing!");
            problemWhileLoading = true;
            return;
        }
        try {
            this.raf  = new RandomAccessFile(puzzle, "r");
            NEW_LINE_CHAR_LENGTH = FileUtility.retrieveLineSeparator(puzzle).getBytes().length;
            LINE_LENGTH = raf.readLine().getBytes().length + NEW_LINE_CHAR_LENGTH;
        } catch (FileNotFoundException e) {
            Bukkit.getConsoleSender().sendMessage(lang.PREFIX + ChatColor.RED + " Puzzles file is missing!");
            problemWhileLoading = true;
            return;
        } catch (IOException e) {
            e.printStackTrace();
            problemWhileLoading = true;
            return;
        }
        loadItems();
    }

    private void loadItems() {
        if(!game.getConfig().isConfigurationSection("items.grid")){
            Bukkit.getConsoleSender().sendMessage(lang.PREFIX + ChatColor.RED + " items.grid section missing in config");
            problemWhileLoading = true;
            return;
        }
        ConfigurationSection items = game.getConfig().getConfigurationSection("items.grid");
        ItemStack tipItem, coverItem, numberItem;
        for (int i = 1; i < 10; i++){
            if(!game.getConfig().isConfigurationSection("items.grid." + i)){
                Bukkit.getConsoleSender().sendMessage(lang.PREFIX + ChatColor.RED + " items.grid." + i + " section missing in config");
                cover.put(i, new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE, 1));
                tip.put(i, new ItemStack(Material.CLAY, i));
                number.put(i, new ItemStack(Material.WHITE_WOOL, i));
                continue;
            }

            tipItem = ItemStackUtility.getItemStack(items.getString(i + "." + "tip" + ".materialData", "CLAY"));
            coverItem = ItemStackUtility.getItemStack(items.getString(i + "." + "cover" + ".materialData", "LIGHT_GRAY_STAINED_GLASS_PANE"));
            numberItem = ItemStackUtility.getItemStack(items.getString(i + "." + "number" + ".materialData", "WHITE_WOOL"));
            if(tipItem == null){
                tipItem = new ItemStack(Material.CLAY, 1);
                game.warn(ChatColor.RED.toString() + i + "." + "tip" + ".materialData" + " not valid material");
            }
            if(coverItem == null){
                coverItem = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE, 1);
                game.warn(ChatColor.RED.toString() + i + "." + "cover" + ".materialData" + " not valid material");
            }
            if(numberItem == null){
                numberItem = new ItemStack(Material.WHITE_WOOL, 1);
                game.warn(ChatColor.RED.toString() + i + "." + "number" + ".materialData" + " not valid material");
            }
            ItemMeta meta = tipItem.getItemMeta();
            meta.setDisplayName(StringUtility.color(items.getString(i + "." + "tip" + ".displayName", "&l&n" + i)));
            tipItem.setItemMeta(meta);
            meta = coverItem.getItemMeta();
            meta.setDisplayName(StringUtility.color(items.getString(i + "." + "cover" + ".displayName", "%value%")));
            coverItem.setItemMeta(meta);
            meta = numberItem.getItemMeta();
            meta.setDisplayName(StringUtility.color(items.getString(i + "." + "number" + ".displayName", "%value%")));
            numberItem.setItemMeta(meta);
            tip.put(i, tipItem.clone());
            cover.put(i, coverItem.clone());
            number.put(i, numberItem.clone());
        }
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
        SudokuGame game = games.get(inventoryClickEvent.getWhoClicked().getUniqueId());
        if(game == null) return;
        game.onClick(inventoryClickEvent);
    }

    @Override
    public void onInventoryClose(InventoryCloseEvent inventoryCloseEvent) {
        removeFromGame(inventoryCloseEvent.getPlayer().getUniqueId());
    }

    @Override
    public boolean isInGame(UUID uuid) {
        return games.containsKey(uuid);
    }

    @Override
    public void startGame(Player[] players, boolean playSounds, String... strings) throws GameStartException {
        if(raf == null || problemWhileLoading) throw new GameStartException(GameStartException.Reason.ERROR);
        if (strings.length != 1) {
            Bukkit.getLogger().log(Level.WARNING, " unknown number of arguments to start a game: " + Arrays.asList(strings));
            throw new GameStartException(GameStartException.Reason.ERROR);
        }
        SudokuGameRules rule = gameTypes.get(strings[0]);
        if (rule == null) {
            Bukkit.getLogger().log(Level.WARNING, " unknown game mode: " + Arrays.asList(strings));
            throw new GameStartException(GameStartException.Reason.ERROR);
        }
        String puzzle = null;
        // for time out reasons when someone messed with the puzzles file...
        int count = 0;
        try {
            while (puzzle == null || !(puzzle.toCharArray().length >= 81)) {
                if(count > 20){
                    Bukkit.getConsoleSender().sendMessage(lang.PREFIX + ChatColor.RED + " Unable to find a puzzle - Something is wrong with the puzzles file!");
                    Bukkit.getConsoleSender().sendMessage(lang.PREFIX + ChatColor.RED + " Delete the puzzles file and reload the plugin.");
                    throw new GameStartException(GameStartException.Reason.ERROR);
                }
                count ++;
                raf.seek(random.nextInt(2000) * LINE_LENGTH);
                puzzle = raf.readLine();
            }
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage(lang.PREFIX + ChatColor.RED + " I/O Exception while looking for a puzzle!");
            throw new GameStartException(GameStartException.Reason.ERROR);
        }
        if (!game.payIfNecessary(players[0], rule.getCost())) {
            throw new GameStartException(GameStartException.Reason.NOT_ENOUGH_MONEY);
        }
        games.put(players[0].getUniqueId(), new SudokuGame(rule, game, players[0], playSounds, puzzle, cover, tip, number));
    }


    @Override
    public void removeFromGame(UUID uuid) {
        SudokuGame game = games.get(uuid);
        if (game == null) return;
        game.quit();
        games.remove(uuid);
    }

    @Override
    public void loadGameRules(ConfigurationSection buttonSec, String buttonID) {
        double cost = buttonSec.getDouble("cost", 0.);
        boolean saveStats = buttonSec.getBoolean("saveStats", false);
        boolean restartButton = buttonSec.getBoolean("restartButton", true);
        int token = buttonSec.getInt("token", 0);
        int money = buttonSec.getInt("money", 0);
        gameTypes.put(buttonID, new SudokuGameRules(buttonID, cost, money, token, restartButton, saveStats));
    }

    @Override
    public Map<String, ? extends GameRule> getGameRules() {
        return gameTypes;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    // ToDo move to abstract game class
    public void onGameEnd(Player winner, String key) {
        SudokuGameRules rule = gameTypes.get(key);
        game.onGameWon(winner, rule, 1.);
        if(GameBoxSettings.econEnabled){
            if(!Permission.BYPASS_GAME.hasPermission(winner, game.getGameID())){
                winner.sendMessage((lang.PREFIX + lang.GAME_WON_MONEY.replaceAll("%reward%", rule.getMoneyToWin()+"")));
            } else {
                winner.sendMessage((lang.PREFIX + lang.GAME_WON));
            }
        } else {
            winner.sendMessage((lang.PREFIX + lang.GAME_WON));
        }
        game.onGameWon(winner, rule, 1);
    }
}
