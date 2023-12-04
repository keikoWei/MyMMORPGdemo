package me.dane.mymmorpg.methods;

import me.dane.mymmorpg.database.DatabaseMethod;
import me.dane.mymmorpg.model.PartyID;
import me.dane.mymmorpg.model.PartyListStats;
import me.dane.mymmorpg.model.PartyMembers;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PartyMethod {

    private final DatabaseMethod database;
    private final BukkitAudiences adventure;

    public PartyMethod(Player player, DatabaseMethod database, BukkitAudiences adventure) {
        this.database = database;
        this.adventure = adventure;
    }


    /**
     * 創建隊伍方法，僅有點擊菜單創建這個情況
     * 已有隊伍的情況，會直接開啟隊伍狀態選單
     */
    public void PartyCreate(Player player) throws SQLException {

        PartyListStats partyListStats = new PartyListStats("預設隊伍名稱", 5, player.getDisplayName(), true);

        try {
            database.createPartyListStats(partyListStats);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        PartyID partyID = database.findPartyByParty_leader(player.getDisplayName());

        int party_id = partyID.getParty_id();

        PartyMembers partyMembers =
                new PartyMembers(player.getUniqueId().toString(), player.getDisplayName(), player.getLevel(), party_id, true);

        database.updatePartyMembers(partyMembers);

        player.sendMessage(ChatColor.GRAY + "[隊伍訊息]" + ChatColor.YELLOW + "你成功創建了隊伍 !");


    }

    /**
     * 隊伍加入選項，需判斷隊伍是否滿人
     * 當隊伍有新玩家加入，發送通知給其他隊員
     */

    //申請人和被申請人
    public void PartyJoin(Player applicant, Player respondent) throws SQLException {

        PartyListStats partyListStats = database.findPartyByParty_leader(respondent.getDisplayName());
        int party_id = partyListStats.getParty_id();
        List<PartyMembers> party_size = database.findALLPartyMembersByParty_id(party_id);

        if (party_size.size() < partyListStats.getParty_limit()) {

            PartyMembers partyMembers = database.findPartyMembersByUUID(applicant.getUniqueId().toString());
            partyMembers.setCurrent_party_id(party_id);
            partyMembers.setHas_party(true);
            database.updatePartyMembers(partyMembers);

            applicant.sendMessage(ChatColor.GRAY + "[隊伍訊息]" + ChatColor.GREEN + "你已成功加入 " + partyListStats.getParty_name());

            for (PartyMembers party_member : party_size) {

                Player player = Bukkit.getPlayer(party_member.getPlayer_name());

                player.sendMessage(ChatColor.GRAY + "[隊伍訊息]" + applicant.getDisplayName() + "已成功加入隊伍 !");

            }

        } else {
            applicant.sendMessage(ChatColor.GRAY + "[隊伍訊息]" + ChatColor.RED + "該隊伍人數已滿 !");
        }

    }

    /**
     * 隊伍離開方法
     * 更新該玩家party_members 的欄位資料
     * 僅剩一人時，會同時觸發隊伍解散，刪除party_list_stats表中的資料
     * 如果離開的玩家是隊長且隊伍人數大於1，隊長轉移
     */
    public void PartyLeave(Player player) throws SQLException {


        if (player_is_leader_no_message(player)) {

            PartyID partyID = database.findPartyByParty_leader(player.getDisplayName());
            List<PartyMembers> partyMembersArrayList = database.findALLPartyMembersByParty_id(partyID.getParty_id());

            if (partyMembersArrayList.size() > 1) {

                List<PartyMembers> newPartyMembersList = new ArrayList<>(partyMembersArrayList);

                for (PartyMembers partyMembers : newPartyMembersList) {
                    if (!partyMembers.getPlayer_name().equals(player.getDisplayName())) {
                        String new_leader = partyMembers.getPlayer_name();
                        partyID.setParty_leader(new_leader);
                        database.updatePartyListStatsOnly_party_leader(partyID);

                        for (PartyMembers party_member : partyMembersArrayList) {
                            Player member = Bukkit.getPlayer(UUID.fromString(party_member.getPlayer_uuid()));
                            if (member != null) {
                                member.sendMessage(ChatColor.GRAY + "[隊伍訊息]" + ChatColor.RED + "玩家 : " + new_leader + " 已被提升為隊長 !");
                            }
                        }
                        break;
                    }
                }
            }
        }

        PartyMembers partyMembers = database.findPartyMembersByUUID(player.getUniqueId().toString());
        int party_id = partyMembers.getCurrent_party_id();
        List<PartyMembers> party_member_list = database.findALLPartyMembersByParty_id(party_id);

        for (PartyMembers partyMembers_all : party_member_list) {

            Player member  = Bukkit.getPlayer(partyMembers_all.getPlayer_name());

            member.sendMessage(ChatColor.GRAY + "[隊伍訊息]" + ChatColor.RED + "玩家 : " + player.getDisplayName() + " 已離開隊伍 !");
        }

        if (party_member_list.size() == 1) {

            PartyListStats partyListStats = database.findPartyListByParty_ID(partyMembers.getCurrent_party_id());

            database.deletePartyListStats(partyListStats);

        }

        partyMembers = new PartyMembers(
                player.getUniqueId().toString(),
                player.getDisplayName(),
                player.getLevel(),
                0,
                false);

        database.updatePartyMembers(partyMembers);

        player.sendMessage(ChatColor.GRAY + "[隊伍訊息]" + ChatColor.RED + "你已離開了隊伍 !");




    }

    /**
     * 將隊伍成員踢出
     * 先判斷執行者是否為隊長，且被踢出的玩家是否是隊伍成員
     */
    public void PartyMemberKick(Player leader, Player to_kick) throws SQLException {


        //踢出玩家 + 傳送被剔除玩家訊息
        if (player_is_leader(leader)) {
            PartyMembers partyMembers_to_kick = database.findPartyMembersByUUID(to_kick.getUniqueId().toString());
            PartyMembers partyMembers_leader = database.findPartyMembersByUUID(leader.getUniqueId().toString());

            // 只有當前隊伍的成員才能被踢出
            if (partyMembers_to_kick.getCurrent_party_id() == partyMembers_leader.getCurrent_party_id()) {

                partyMembers_to_kick.setCurrent_party_id(0);
                partyMembers_to_kick.setHas_party(false);

                database.updatePartyMembers(partyMembers_to_kick);

                to_kick.sendMessage(ChatColor.GRAY + "[隊伍訊息]" + ChatColor.RED + "你已被請離隊伍 !");

                int party_id = partyMembers_leader.getCurrent_party_id();
                List<PartyMembers> partyMembersArrayList = database.findALLPartyMembersByParty_id(party_id);

                for (PartyMembers party_member : partyMembersArrayList) {
                    Player member = Bukkit.getPlayer(UUID.fromString(party_member.getPlayer_uuid()));
                    if (member != null && !member.equals(to_kick)) {
                        member.sendMessage(ChatColor.GRAY + "[隊伍訊息]" + ChatColor.RED + "玩家 : " + to_kick.getDisplayName() + " 已被請離隊伍 !");
                    }
                }
            } else {
                leader.sendMessage(ChatColor.GRAY + "[隊伍訊息]" + ChatColor.RED + "該玩家不在你的隊伍中 !");
            }
        }


    }

    /**
     * 僅隊長可操作，需先判對是否為隊長 (method : player_is_leader)
     * 解散時update 所有隊伍成員的 party_members 表，欄位的狀態
     */
    public void Party_Disbanded(Player leader) throws SQLException {


        PartyMembers partyMembers = database.findPartyMembersByUUID(leader.getUniqueId().toString());
        int party_id = partyMembers.getCurrent_party_id();
        PartyListStats partyListStats = database.findPartyListByParty_ID(party_id);

        if (player_is_leader(leader)) {

            List<PartyMembers> partyMembersArrayList = database.findALLPartyMembersByParty_id(party_id);

            for (PartyMembers party_member : partyMembersArrayList) {
                party_member.setCurrent_party_id(0);
                party_member.setHas_party(false);
                database.updatePartyMembers(party_member);

                Player member = Bukkit.getPlayer(UUID.fromString(party_member.getPlayer_uuid()));
                member.sendMessage(ChatColor.GRAY + "[隊伍訊息]" + ChatColor.RED + "你的隊伍已解散 !");

            }


            database.deletePartyListStats(partyListStats);
            leader.sendMessage(ChatColor.GRAY + "[隊伍訊息]" + ChatColor.RED + "你解散了你的隊伍 !");

        }

    }

    /**
     * 搜尋 party_members表 判斷玩家是否有隊伍
     */
    public boolean player_has_party(Player player) throws SQLException {

        PartyMembers partyMembers = database.findPartyMembersByUUID(player.getUniqueId().toString());

        if (partyMembers.isHas_party()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判斷玩家是否是對伍隊長，無提示訊息
     */
    public boolean player_is_leader_no_message(Player player) throws SQLException {

        PartyMembers partyMembers = database.findPartyMembersByUUID(player.getUniqueId().toString());
        int party_id = partyMembers.getCurrent_party_id();
        PartyListStats partyListStats = database.findPartyListByParty_ID(party_id);

        if (player.getDisplayName().equals(partyListStats.getParty_leader())) {

            return true;
        } else {

            return false;
        }
    }

    public boolean player_is_leader(Player player) throws SQLException {

        PartyMembers partyMembers = database.findPartyMembersByUUID(player.getUniqueId().toString());
        int party_id = partyMembers.getCurrent_party_id();
        PartyListStats partyListStats = database.findPartyListByParty_ID(party_id);

        if (player.getDisplayName().equals(partyListStats.getParty_leader())) {

            return true;
        } else {

            player.sendMessage(ChatColor.GRAY + "[隊伍訊息] " + ChatColor.RED + "你不是隊長，無法執行該操作");
            return false;
        }
    }


    /**
     * 更改隊伍名稱，僅隊長可操作，需先判對是否為隊長 (method : player_is_leader)
     * 先判斷長度是否大於36
     */
    public void party_name_change(Player player, String name) throws SQLException {

        if (player_is_leader(player)) {

            PartyID partyID = database.findPartyByParty_leader(player.getDisplayName());
            partyID.setParty_name(name);
            database.updatePartyListStatsOnly_party_name(partyID);
            player.sendMessage(ChatColor.GRAY + "[隊伍訊息]" + ChatColor.GREEN + "隊伍名稱已設定為: " + name);

        }
    }

    /**
     * 隊伍隊長轉移
     * 參數 原隊長 / 新隊長
     * 需判斷是否為隊長，並發送提示訊息給隊員
     */
    public void party_leader_change(Player leader, Player new_leader) throws SQLException {

        if (player_is_leader(leader)) {

            PartyID partyID = database.findPartyByParty_leader(leader.getDisplayName());
            partyID.setParty_leader(new_leader.getDisplayName());
            database.updatePartyListStatsOnly_party_leader(partyID);

            List<PartyMembers> partyMembersArrayList = database.findALLPartyMembersByParty_id(partyID.getParty_id());

            for (PartyMembers party_member : partyMembersArrayList) {
                Player member = Bukkit.getPlayer(UUID.fromString(party_member.getPlayer_uuid()));
                if (member != null) {
                    member.sendMessage(ChatColor.GRAY + "[隊伍訊息]" + ChatColor.RED + "玩家 : " + new_leader.getDisplayName() + " 已被提升為隊長 !");
                }
            }


        }

    }


}
