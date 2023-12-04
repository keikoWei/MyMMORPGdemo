package me.dane.mymmorpg.menusystem;

import org.bukkit.entity.Player;


public class PlayerMenuUtility {

    private Player owner;

    private Player partyLeader_to_join;

    public PlayerMenuUtility(Player p) {
        this.owner = p;
    }

    public Player getOwner() {
        return owner;
    }

    public Player getParty_Leader() {
        return partyLeader_to_join;
    }

    public void setParty_Leader(Player partyLeader_to_join) {
        this.partyLeader_to_join = partyLeader_to_join;
    }




}
