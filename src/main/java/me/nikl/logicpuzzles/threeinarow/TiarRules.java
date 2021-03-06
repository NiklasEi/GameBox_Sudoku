package me.nikl.logicpuzzles.threeinarow;

import me.nikl.gamebox.data.toplist.SaveType;
import me.nikl.gamebox.game.rules.GameRuleRewards;

/**
 * Created by nikl on 25.02.18.
 */
public class TiarRules extends GameRuleRewards {
    private boolean helpItems;

    public TiarRules(String key, boolean saveStats, double moneyToPay, double reward, int token, boolean helpItems) {
        super(key, saveStats, SaveType.WINS, moneyToPay, reward, token);
        this.helpItems = helpItems;
    }

    public boolean isHelpItems() {
        return helpItems;
    }
}
