package me.dane.mymmorpg.commands;

import me.dane.mymmorpg.database.DatabaseMethod;
import me.dane.mymmorpg.menusystem.MenuManger;
import me.dane.mymmorpg.menusystem.menu.PartyMenu;
import me.dane.mymmorpg.menusystem.menu.PartyStatsMenu;
import me.dane.mymmorpg.methods.PartyMethod;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


import java.sql.SQLException;

public class PartyCommand implements CommandExecutor {

    private final DatabaseMethod database;
    private final BukkitAudiences adventure;

    public PartyCommand(DatabaseMethod database,  BukkitAudiences adventure) {
        this.database = database;
        this.adventure = adventure;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        Player player = (Player) commandSender;

        PartyMethod partyMethod = new PartyMethod(player, database, adventure);

        if(commandSender instanceof Player) {

            if (strings.length == 0) {

                //判斷玩家是否有隊伍，否開啟隊伍首頁菜單，有的畫開啟隊伍狀態菜單
                try {
                    if (partyMethod.player_has_party(player)){
                        PartyStatsMenu menu = new PartyStatsMenu(MenuManger.getPlayerMenuUtility(player), database, adventure);
                        menu.open();
                    }else {
                        PartyMenu menu = new PartyMenu(MenuManger.getPlayerMenuUtility(player), database, adventure);
                        menu.open();
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else if (s.equalsIgnoreCase("party") && strings.length > 0 && strings[0].equalsIgnoreCase("agree")) {

                String applicantName = strings[1];

                try {
                    partyMethod.PartyJoin(Bukkit.getPlayer(applicantName),player);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            }
        }


        return false;
    }
}