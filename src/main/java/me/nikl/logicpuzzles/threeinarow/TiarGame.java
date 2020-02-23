package me.nikl.logicpuzzles.threeinarow;

import me.nikl.gamebox.nms.NmsFactory;
import me.nikl.gamebox.utility.Sound;
import me.nikl.logicpuzzles.ExtendedInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nikl on 25.02.18.
 */
public class TiarGame {
    private ExtendedInventory extendedInventory;
    private TiarLanguage language;
    private ThreeInARow game;
    private TiarRules rule;
    private Player player;
    private Map<Integer, ItemStack> helpItems = new HashMap<>();
    private Integer[] grid = new Integer[36];
    private List<Integer> tipSlots = new ArrayList<>();
    private ItemStack backGround;
    private Sound won = Sound.VILLAGER_YES
            , click = Sound.WOOD_CLICK;
    private boolean finished = false;

    public TiarGame(ThreeInARow threeInARow, TiarRules rules, Player player, String game){
        this.player = player;
        language = (TiarLanguage) threeInARow.getGameLang();
        this.game = threeInARow;
        backGround = this.game.getBackGround();
        this.rule = rules;
        Inventory upperInventory = threeInARow.createInventory(54, language.GAME_TITLE);
        this.extendedInventory = new ExtendedInventory(upperInventory, player.getInventory());
        prepareInventory(game);
        player.openInventory(upperInventory);
    }

    private void prepareInventory(String game) {
        placeBackGround();
        String tips = game.split(";")[0];
        int gridSlot = -1;
        for (char character : tips.toCharArray()) {
            gridSlot ++;
            if (character == '0'){
                grid[gridSlot] = 0;
                continue;
            }
            if (character == '1') {
                grid[gridSlot] = 1;
                extendedInventory.setItem(gridToInventory(gridSlot), this.game.getWhiteTileTip());
                tipSlots.add(gridToInventory(gridSlot));
                continue;
            }
            if (character == '2') {
                grid[gridSlot] = 2;
                extendedInventory.setItem(gridToInventory(gridSlot), this.game.getBlueTileTip());
                tipSlots.add(gridToInventory(gridSlot));
            }
        }
        placeHelpItems();
    }

    private void placeBackGround() {
        for (int i = 0; i < 7; i++) {
            extendedInventory.setItem(i, backGround);
            extendedInventory.setItem(i*9, backGround);
            extendedInventory.setItem(i*9 + 8, backGround);
            extendedInventory.setItem(i*9 + 7, backGround);
        }
    }

    private void placeHelpItems() {
        for (int i = 0; i < 6; i++) {
            helpItems.put(gridToInventory(i) - 9, game.getWrongHelpItem());
            helpItems.put(gridToInventory(i*6 + 5) + 1, game.getWrongHelpItem());
            updateHelpItem(i*7);
        }
    }

    private void updateHelpItem(int gridSlot) {
        int row = gridSlot / 6;
        int column = gridSlot % 6;
        updateRowHelpItem(row);
        updateColumnHelpItem(column);
    }

    private void updateColumnHelpItem(int column) {
        int blue = 0;
        int white = 0;
        for (int row = 0; row < 6; row++) {
            if (grid[row*6 + column] == 1) white++;
            if (grid[row*6 + column] == 2) blue++;
        }
        int helpItemSlot = column + 54;
        if (blue == 3 && white == 3) {
            helpItems.put(helpItemSlot, game.getCorrectHelpItem());
            extendedInventory.setItem(helpItemSlot, game.getCorrectHelpItem());
            return;
        }
        helpItems.put(helpItemSlot, createHelpItem(white, blue));
        extendedInventory.setItem(helpItemSlot, helpItems.get(helpItemSlot));
    }

    private ItemStack createHelpItem(int white, int blue) {
        ItemStack helpItem = game.getWrongHelpItem();
        ItemMeta meta = helpItem.getItemMeta();
        List<String> lore = meta.getLore();
        for (int i = 0; i<lore.size(); i++) {
            lore.set(i, lore.get(i)
                    .replace("%blue_count%", String.valueOf(blue))
                    .replace("%white_count%", String.valueOf(white)));
        }
        meta.setLore(lore);
        helpItem.setItemMeta(meta);
        return helpItem;
    }

    private void updateRowHelpItem(int row) {
        int blue = 0;
        int white = 0;
        for (int column = 0; column < 6; column++) {
            if (grid[row*6 + column] == 1) white++;
            if (grid[row*6 + column] == 2) blue++;
        }
        int helpItemSlot = row * 9 + 6;
        if (blue == 3 && white == 3) {
            helpItems.put(helpItemSlot, game.getCorrectHelpItem());
            extendedInventory.setItem(helpItemSlot, game.getCorrectHelpItem());
            return;
        }
        helpItems.put(helpItemSlot, createHelpItem(white, blue));
        extendedInventory.setItem(helpItemSlot, helpItems.get(helpItemSlot));
    }

    private int gridToInventory(int gridSlot) {
        int row = gridSlot / 6;
        int column = gridSlot % 6;
        return row * 9 + column;
    }

    private int inventoryToGrid (int inventorySlot) {
        int row = inventorySlot / 9;
        int column = inventorySlot % 9;
        if (column > 5 || row > 5) return -1;
        return (row)*6 + column;
    }

    private void clickSlot(int inventorySlot, boolean rightClick) {
        int gridSlot = inventoryToGrid(inventorySlot);
        if (gridSlot < 0) return;
        if (tipSlots.contains(inventorySlot)) return;
        game.playSound(player, click);
        grid[gridSlot] = (grid[gridSlot] + (rightClick ? -1 : 1)) % 3;
        updateGridSlot(gridSlot);
    }

    private void updateGridSlot(int gridSlot) {
        switch (grid[gridSlot]) {
            case 0:
                extendedInventory.setItem(gridToInventory(gridSlot), null);
                break;
            case 1:
                extendedInventory.setItem(gridToInventory(gridSlot), game.getWhiteTile());
                break;
            case 2:
                extendedInventory.setItem(gridToInventory(gridSlot), game.getBlueTile());
                break;
        }
        updateHelpItem(gridSlot);
    }

    public boolean onClick(InventoryClickEvent inventoryClickEvent) {
        if (inventoryClickEvent.getClick() == ClickType.DOUBLE_CLICK || finished) return true;
        clickSlot(inventoryClickEvent.getRawSlot(), inventoryClickEvent.getClick() == ClickType.RIGHT);
        if (isFinished()) {
            game.onGameWon(player, rule, 1);
            NmsFactory.getNmsUtility().updateInventoryTitle(player, language.GAME_TITLE_WON);
            game.playSound(player, won);
            this.finished = true;
        }
        return true;
    }

    public void onClose() {
    }

    private boolean isFinished() {
        int blueRow, blueColumn;
        int whiteRow, whiteColumn;
        StringBuilder columnStringBuilder = new StringBuilder();
        StringBuilder rowStringBuilder = new StringBuilder();
        for (int column = 0; column < 6; column++) {
            blueColumn = 0; whiteColumn = 0; columnStringBuilder.setLength(0);
            blueRow = 0; whiteRow = 0; rowStringBuilder.setLength(0);
            for (int row = 0; row < 6; row++) {
                if (grid[row * 6 + column] == 1) whiteColumn++;
                if (grid[row * 6 + column] == 2) blueColumn++;
                columnStringBuilder.append(grid[row * 6 + column]);
                if (grid[column * 6 + row] == 1) whiteRow++;
                if (grid[column * 6 + row] == 2) blueRow++;
                rowStringBuilder.append(grid[column * 6 + row]);
            }
            if (blueColumn != 3 || whiteColumn != 3) return false;
            if (blueRow != 3 || whiteRow != 3) return false;
            String columnStr = columnStringBuilder.toString();
            if (columnStr.contains("111") || columnStr.contains("222")) return false;
            String rowStr = rowStringBuilder.toString();
            if (rowStr.contains("111") || rowStr.contains("222")) return false;
        }
        return true;
    }
}
