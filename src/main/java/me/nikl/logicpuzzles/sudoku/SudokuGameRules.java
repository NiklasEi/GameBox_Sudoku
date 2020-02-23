package me.nikl.logicpuzzles.sudoku;

import me.nikl.gamebox.data.toplist.SaveType;
import me.nikl.gamebox.game.rules.GameRuleRewards;

/**
 * Created by Niklas
 *
 * Game rules container for Sudoku
 */
public class SudokuGameRules extends GameRuleRewards {
    public SudokuGameRules(String key, double cost, int reward, int tokens, boolean saveStats){
        super(key, saveStats, SaveType.WINS, cost, reward, tokens);
    }
}
