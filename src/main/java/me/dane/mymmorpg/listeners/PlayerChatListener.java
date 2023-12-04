package me.dane.mymmorpg.listeners;

import me.dane.mymmorpg.database.DatabaseMethod;
import me.dane.mymmorpg.methods.PartyMethod;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class PlayerChatListener implements Listener {

    public static Map<Player, Boolean> settingPartyNameMap = new HashMap<>();

    private final DatabaseMethod database;

    private final BukkitAudiences adventure;

    public PlayerChatListener(DatabaseMethod database, BukkitAudiences adventure) {
        this.database = database;
        this.adventure = adventure;
    }


    public static Map<Player, Boolean> getSettingPartyNameMap() {
        return settingPartyNameMap;
    }

    public static void setSettingPartyNameMap(Map<Player, Boolean> settingPartyNameMap) {
        PlayerChatListener.settingPartyNameMap = settingPartyNameMap;
    }
    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) throws SQLException {
        Player player = event.getPlayer();


        // 检查玩家是否在等待输入新的队伍名
        if (settingPartyNameMap.getOrDefault(player, true)) {
            // 取消事件，防止消息发送到聊天框
            event.setCancelled(true);

            // 获取玩家在聊天框中输入的消息
            String newPartyName = event.getMessage();

            while (newPartyName.length() > 36) {
                // 如果输入的长度大于36，向玩家发送提醒消息
                player.sendMessage(ChatColor.GRAY + "[隊伍訊息]" + ChatColor.RED + "設定長度請小於36 !  再次輸入或者 輸入cancel取消 !");
                return; // 提示後直接返回，等待下一次輸入
            }


                // 检查是否取消操作
                if (newPartyName.equalsIgnoreCase("cancel")) {
                    player.sendMessage(ChatColor.RED + "操作已取消");
                } else {

                    PartyMethod partyMethod = new PartyMethod(player, database, adventure);
                    partyMethod.party_name_change(player, newPartyName);

                }


            // 设置玩家状态为不在等待输入新的队伍名
            settingPartyNameMap.put(player, false);
        }
    }
}
