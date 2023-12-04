package me.dane.mymmorpg.menusystem.menu;

import me.dane.mymmorpg.MyMMORPG;
import me.dane.mymmorpg.database.DatabaseMethod;
import me.dane.mymmorpg.menusystem.AbstractMenu;
import me.dane.mymmorpg.menusystem.PlayerMenuUtility;
import me.dane.mymmorpg.methods.PartyMethod;
import me.dane.mymmorpg.model.PartyListStats;
import me.dane.mymmorpg.model.PartyMembers;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PartyStatsMenu extends AbstractMenu {

    private final DatabaseMethod database;
    private final BukkitAudiences adventure;



    public PartyStatsMenu(PlayerMenuUtility playerMenuUtility, DatabaseMethod database, BukkitAudiences adventure) {
        super(playerMenuUtility);
        this.database = database;
        this.adventure = adventure;
    }

    @Override
    public String getMenuName() {
        return ChatColor.GRAY + "【隊伍狀態選單】";
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) throws SQLException {

        Player player = (Player) event.getWhoClicked();

        PartyMethod partyMethod = new PartyMethod(player, database, adventure);

        switch (event.getCurrentItem().getType()) {
            case WRITTEN_BOOK:
                break;
            case SPRUCE_DOOR:
                new PartyLeaveConfirmMenu(playerMenuUtility, database, adventure).open();
                break;
            case DARK_OAK_DOOR:
                new PartyDisbandedConfirmMenu(playerMenuUtility, database, adventure).open();
                break;
            case SPRUCE_SIGN :
                if (partyMethod.player_is_leader(player)) {
                    new PartyStatsChangeMenu(playerMenuUtility, database,  adventure).open();
                }
                break;
            case IRON_DOOR:
                event.getWhoClicked().closeInventory();
                break;
            case PLAYER_HEAD:

                String member_to_kick = event.getCurrentItem().getItemMeta().getPersistentDataContainer()
                        .get(new NamespacedKey(MyMMORPG.getPlugin(), "player_name"), PersistentDataType.STRING);

                Player to_kick = Bukkit.getPlayer(member_to_kick);
                partyMethod.PartyMemberKick(player, to_kick);

                event.getWhoClicked().closeInventory();
                break;
        }

    }

    @Override
    public void setMenuItems() throws SQLException {


        String menu_Owner = playerMenuUtility.getOwner().getUniqueId().toString();

        PartyMembers partyMembers = database.findPartyMembersByUUID(menu_Owner);
        int party_id = partyMembers.getCurrent_party_id();

        PartyListStats partyListStats = database.findPartyListByParty_ID(party_id);

        List<PartyMembers> party_size = database.findALLPartyMembersByParty_id(party_id);


        //創建隊伍狀態按鈕
        ItemStack party_stats = new ItemStack(Material.WRITTEN_BOOK, 1);
        ItemMeta party_stats_meta = party_stats.getItemMeta();
        party_stats_meta.setDisplayName(ChatColor.GRAY + "【隊伍當前狀態】");
        ArrayList<String> party_stats_lore = new ArrayList<>();
        party_stats_lore.add(ChatColor.AQUA + "『 隊伍名稱 : " + partyListStats.getParty_name() + "』");
        party_stats_lore.add(ChatColor.AQUA + "『 隊長 : " + partyListStats.getParty_leader() + "』");
        party_stats_lore.add(ChatColor.AQUA + "『 隊伍人數 : " + party_size.size()  + "/" + partyListStats.getParty_limit() + "』");

        boolean is_public = partyListStats.isParty_is_public();

        if (is_public == true) {
            party_stats_lore.add(ChatColor.AQUA + "『 隊伍是否公開 : " + "公開" + "』");
        } else {
            party_stats_lore.add(ChatColor.AQUA + "『 隊伍是否公開 : " + "非公開" + "』");
        }

        party_stats_meta.setLore(party_stats_lore);
        party_stats.setItemMeta(party_stats_meta);

        //離開隊伍按鈕
        ItemStack party_leave = new ItemStack(Material.SPRUCE_DOOR, 1);
        ItemMeta party_leave_meta = party_leave.getItemMeta();
        party_leave_meta.setDisplayName(ChatColor.RED + "離開隊伍");
        ArrayList<String> party_leave_lore = new ArrayList<>();
        party_leave_lore.add(ChatColor.AQUA + "確定要離開隊伍嗎 ?");

        party_leave_meta.setLore(party_leave_lore);
        party_leave.setItemMeta(party_leave_meta);


        //解散隊伍按鈕
        ItemStack party_disbanded = new ItemStack(Material.DARK_OAK_DOOR, 1);
        ItemMeta party_disbanded_meta = party_disbanded.getItemMeta();
        party_disbanded_meta.setDisplayName(ChatColor.GRAY + "解散隊伍");
        ArrayList<String> party_disbanded_lore = new ArrayList<>();
        party_disbanded_lore.add(ChatColor.AQUA + "確定要解散隊伍嗎 ?");

        party_disbanded_meta.setLore(party_disbanded_lore);
        party_disbanded.setItemMeta(party_disbanded_meta);

        //邀請玩家按鈕
        ItemStack player_invite = new ItemStack(Material.PLAYER_HEAD, 1);
        ItemMeta player_invite_meta = player_invite.getItemMeta();
        player_invite_meta.setDisplayName(ChatColor.GREEN + "邀請線上的玩家 !");
        ArrayList<String> player_invite_lore = new ArrayList<>();
        player_invite_lore.add(ChatColor.RED + "" + ChatColor.BOLD + "注意" + ChatColor.AQUA + "請輸入'正確'的玩家名稱");

        player_invite_meta.setLore(player_invite_lore);
        player_invite.setItemMeta(player_invite_meta);

        //更改隊伍設定菜單按鈕
        ItemStack stats_change = new ItemStack(Material.SPRUCE_SIGN, 1);
        ItemMeta stats_change_meta = stats_change.getItemMeta();
        stats_change_meta.setDisplayName(ChatColor.GREEN + "更改隊伍相關設定");
        ArrayList<String> stats_change_lore = new ArrayList<>();
        stats_change_lore.add(ChatColor.RED + "" + ChatColor.BOLD + "注意" + ChatColor.AQUA + "只有隊長可以進行設定");

        stats_change_meta.setLore(stats_change_lore);
        stats_change.setItemMeta(stats_change_meta);

        //隊伍成員列表
        ItemStack party_member = new ItemStack(Material.ARMOR_STAND, 1);
        ItemMeta party_member_meta = party_member.getItemMeta();
        party_member_meta.setDisplayName(ChatColor.GREEN + "目前隊伍成員");
        ArrayList<String> party_member_lore = new ArrayList<>();
        party_member_lore.add(ChatColor.RED + "--->>>>>>" );

        party_member_meta.setLore(party_member_lore);
        party_member.setItemMeta(party_member_meta);


        //離開選單按鈕
        ItemStack partyMenu_leave = new ItemStack(Material.IRON_DOOR);
        ItemMeta partyMenu_leave_Meta = partyMenu_leave.getItemMeta();
        partyMenu_leave_Meta.setDisplayName(ChatColor.BLACK + "離開選單");
        partyMenu_leave.setItemMeta(partyMenu_leave_Meta);



        int i = 10;
        for (PartyMembers partyMembers_all : party_size){

            ItemStack members = new ItemStack(Material.PLAYER_HEAD, 1);
            ItemMeta members_meta = members.getItemMeta();
            members_meta.setDisplayName(partyMembers_all.getPlayer_name());

            /**
             * 加入標籤值
             */
            members_meta.getPersistentDataContainer()
                    .set(new NamespacedKey(MyMMORPG.getPlugin(), "player_name")
                            , PersistentDataType.STRING, partyMembers_all.getPlayer_name());

            members.setItemMeta(members_meta);


            inventory.setItem(i, members);
            i++;
        }


        inventory.setItem(0, party_stats);
        inventory.setItem(1, party_disbanded);
        inventory.setItem(2, party_leave);
        inventory.setItem(3, player_invite);
        inventory.setItem(4, stats_change);
        inventory.setItem(9, party_member);
        inventory.setItem(53, partyMenu_leave);

        setFillerGlass();
    }

}
