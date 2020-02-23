package me.nikl.logicpuzzles.threeinarow;

import me.nikl.gamebox.GameBox;
import me.nikl.gamebox.game.Game;
import me.nikl.gamebox.game.GameSettings;
import me.nikl.logicpuzzles.LogicPuzzles;
import me.nikl.gamebox.utility.ItemStackUtility;
import me.nikl.gamebox.utility.StringUtility;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Created by nikl on 25.02.18.
 */
public class ThreeInARow extends Game {
    private ItemStack blueTile;
    private ItemStack whiteTile;
    private ItemStack blueTileTip;
    private ItemStack whiteTileTip;
    private ItemStack wrongHelpItem;
    private ItemStack correctHelpItem;
    private ItemStack backGround;

    public ThreeInARow(GameBox gameBox) {
        super(gameBox, LogicPuzzles.THREE_IN_A_ROW);
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void init() {
        loadItems();
    }

    private void loadItems() {
        blueTile = loadItem("game.blueTile", "BLUE_WOOL");
        whiteTile = loadItem("game.whiteTile", "WHITE_WOOL");
        blueTileTip = loadItem("game.blueTileTip", "BLUE_CONCRETE");
        whiteTileTip = loadItem("game.whiteTileTip", "WHITE_CONCRETE");
        wrongHelpItem = loadItem("game.wrongHelpItem", "ORANGE_STAINED_GLASS_PANE");
        correctHelpItem = loadItem("game.correctHelpItem", "GREEN_STAINED_GLASS_PANE");
        backGround = loadItem("game.backGround", "GRAY_STAINED_GLASS_PANE");
    }

    private ItemStack loadItem(String path, String defaultMaterial) {
        ItemStack itemStack = ItemStackUtility.getItemStack(config.getString(path + ".materialData", defaultMaterial));
        if (itemStack == null) {
            itemStack = ItemStackUtility.getItemStack(defaultMaterial);
            if(itemStack == null) throw new IllegalArgumentException("Default material '" + defaultMaterial + "' is not valid!");
        }
        ItemMeta meta = itemStack.getItemMeta();
        if (config.isString(path + ".displayName")) {
            meta.setDisplayName(StringUtility.color(config.getString(path + ".displayName")));
        }
        if (config.isList(path + ".lore")) {
            meta.setLore(StringUtility.color(config.getStringList(path + ".lore")));
        }
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    @Override
    public void loadSettings() {
        gameSettings.setGameType(GameSettings.GameType.SINGLE_PLAYER);
        gameSettings.setHandleClicksOnHotbar(false);
        gameSettings.setGameGuiSize(54);
        gameSettings.setGameBoxMinimumVersion("2.1.4");
    }

    @Override
    public void loadLanguage() {
        gameLang = new TiarLanguage(this);
    }

    @Override
    public void loadGameManager() {
        gameManager = new TiarManager(this);
    }

    public ItemStack getBlueTile() {
        return blueTile.clone();
    }

    public ItemStack getWhiteTile() {
        return whiteTile.clone();
    }

    public ItemStack getWrongHelpItem() {
        return wrongHelpItem.clone();
    }

    public ItemStack getCorrectHelpItem() {
        return correctHelpItem.clone();
    }

    public ItemStack getBlueTileTip() {
        return blueTileTip.clone();
    }

    public ItemStack getWhiteTileTip() {
        return whiteTileTip.clone();
    }

    public ItemStack getBackGround() {
        return backGround;
    }
}
