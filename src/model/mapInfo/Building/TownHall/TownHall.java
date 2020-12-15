package model.mapInfo.Building.TownHall;

import model.mapInfo.Building.Building;

/**
 * Created by CPU60126_LOCAL on 2020-07-01.
 */
public class TownHall extends Building {
    private int gold;
    private int elixir;
    private int darkElixir;
    public TownHall(int id, String type, int level) {
        super(id, type, level);
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getElixir() {
        return elixir;
    }

    public void setElixir(int elixir) {
        this.elixir = elixir;
    }

    public int getDarkElixir() {
        return darkElixir;
    }

    public void setDarkElixir(int darkElixir) {
        this.darkElixir = darkElixir;
    }
}
