package me.dane.mymmorpg.listeners;

import me.dane.mymmorpg.database.DatabaseMethod;
import me.dane.mymmorpg.model.PartyMembers;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;

public class PlayerJoinListener implements Listener {


    private final DatabaseMethod database;

    public PlayerJoinListener(DatabaseMethod database) {
        this.database = database;
    }


    public PartyMembers getPartyMembersFromDatabase(Player player) throws SQLException {

        PartyMembers partyMembers = database.findPartyMembersByUUID(player.getUniqueId().toString());


        if (partyMembers == null) {
            partyMembers = new PartyMembers(player.getUniqueId().toString(), player.getDisplayName(), 0, 0, false);
            database.createPartyMembers(partyMembers);
        }

        return partyMembers;
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {


        Player player = event.getPlayer();
        try {
            PartyMembers partyMembers = getPartyMembersFromDatabase(player);
            database.updatePartyMembers(partyMembers);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("無法更新玩家資料 error !");
        }


    }
}
