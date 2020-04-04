package me.nikl.logicpuzzles.sudoku;

import me.nikl.gamebox.GameBox;
import me.nikl.gamebox.game.*;
import me.nikl.logicpuzzles.LogicPuzzles;

/**
 * Created by nikl on 23.02.18.
 */
public class Sudoku extends me.nikl.gamebox.game.Game {
    public Sudoku(GameBox gameBox) {
        super(gameBox, LogicPuzzles.SUDOKU);
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void init() {

    }

    @Override
    public void loadSettings() {
        gameSettings.setGameType(GameSettings.GameType.SINGLE_PLAYER);
        gameSettings.setGameGuiSize(54);
        gameSettings.setHandleClicksOnHotbar(false);
    }

    @Override
    public void loadLanguage() {
        gameLang = new SudokuLanguage(this);
    }

    @Override
    public void loadGameManager() {
        gameManager = new SudokuGameManager(this);
    }
}
