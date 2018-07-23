package me.nikl.logicpuzzles.threeinarow;

import me.nikl.gamebox.data.toplist.SaveType;
import me.nikl.gamebox.game.rules.GameRuleRewards;

/**
 * Created by Niklas (and TheIntelloBox for ThreeInARow)
 *
 * Game rules container for ThreeInARow
 */
public class ThreeInARowGameRules extends GameRuleRewards {
    private boolean restartButton;

    public ThreeInARowGameRules(String key, double cost, int reward, int tokens, boolean restartButton, boolean saveStats){
        super(key, saveStats, SaveType.WINS, cost, reward, tokens);
        this.restartButton = restartButton;
    }

    public boolean hasRestartButton() {
        return restartButton;
    }
}
