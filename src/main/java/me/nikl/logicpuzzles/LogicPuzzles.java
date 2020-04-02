package me.nikl.logicpuzzles;

import me.nikl.gamebox.module.GameBoxModule;
import me.nikl.logicpuzzles.sudoku.Sudoku;
import me.nikl.logicpuzzles.threeinarow.ThreeInARow;

/**
 * Created by Niklas
 *
 * Main class of the GameBox game Sudoku
 */
public class LogicPuzzles extends GameBoxModule {
    public static final String SUDOKU = "sudoku";
    public static final String THREE_IN_A_ROW = "threeinarow";

    @Override
    public void onEnable(){
        registerGame(SUDOKU, Sudoku.class, "su");
        registerGame(THREE_IN_A_ROW, ThreeInARow.class, "tiar");
    }

    @Override
    public void onDisable() {

    }
}
