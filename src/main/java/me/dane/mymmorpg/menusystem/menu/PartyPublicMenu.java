package me.dane.mymmorpg.menusystem.menu;

import me.dane.mymmorpg.MyMMORPG;
import me.dane.mymmorpg.database.DatabaseMethod;
import me.dane.mymmorpg.menusystem.PaginatedMenu;
import me.dane.mymmorpg.menusystem.PlayerMenuUtility;
import me.dane.mymmorpg.model.PartyListStats;
import me.dane.mymmorpg.model.PartyMembers;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
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

public class PartyPublicMenu extends PaginatedMenu {

    private final DatabaseMethod database;
    private final BukkitAudiences adventure;

    public PartyPublicMenu(PlayerMenuUtility playerMenuUtility, DatabaseMethod database, BukkitAudiences adventure) {
        super(playerMenuUtility);
        this.database = database;
        this.adventure = adventure;
    }

    @Override
    public String getMenuName() {
        return ChatColor.GRAY + "【伺服器上的公開隊伍】";
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) throws SQLException {

        Player player = (Player) event.getWhoClicked();

        ArrayList<PartyListStats> partyListStats = new ArrayList<>(database.findAllPartyLists());



        if (event.getCurrentItem().getType().equals(Material.PLAYER_HEAD)){

            String leader = event.getCurrentItem().getItemMeta().getPersistentDataContainer()
                    .get(new NamespacedKey(MyMMORPG.getPlugin(), "party_leader"), PersistentDataType.STRING);

            Player leader_player = Bukkit.getPlayer(leader);


            player.sendMessage(ChatColor.GRAY + "[隊伍訊息]" + "你已申請加入" + leader + "的隊伍 !");



            TextComponent textComponent = Component.text("[隊伍訊息]").color(TextColor.color(0x3C3F44))
                    .append(Component.text(" 是否同意玩家 : ").color(TextColor.color(0x1F4429)))
                    .append(Component.text(player.getDisplayName()).decorate(TextDecoration.BOLD, TextDecoration.UNDERLINED))
                    .append(Component.text("加入您的隊伍?").color(TextColor.color(0x442926)))
                    .append(Component.text("同意").decorate(TextDecoration.BOLD, TextDecoration.UNDERLINED).clickEvent(ClickEvent.runCommand("/party agree " + player.getDisplayName())))
                    .append(Component.text("不同意").decorate(TextDecoration.BOLD, TextDecoration.UNDERLINED).clickEvent(ClickEvent.runCommand("/party disagree")));

            adventure.player(leader_player).sendMessage(textComponent);

        }else if (event.getCurrentItem().getType().equals(Material.BARRIER)) {

            //離開選單
            player.closeInventory();

        } else if (event.getCurrentItem().getType().equals(Material.DARK_OAK_BUTTON)) {
            if (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("上一頁")) {
                if (page == 0) {
                    player.sendMessage(ChatColor.GRAY + "[隊伍訊息]" + ChatColor.RED + "這已經是第一頁囉");
                } else {
                    page = page - 1;
                    super.open();
                }
            } else if (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("下一頁")) {
                if (!((index + 1) >= partyListStats.size())) {
                    page = page + 1;
                    super.open();
                } else {
                    player.sendMessage(ChatColor.GRAY + "[隊伍訊息]" + ChatColor.RED + "這是最後一頁囉");
                }
            }
        }


    }

    @Override
    public void setMenuItems() throws SQLException {

        addMenuBorder();

        ArrayList<PartyListStats> partyListStats = new ArrayList<>(database.findAllPartyLists());


        if (partyListStats != null) {

            for (int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
                if (index >= partyListStats.size()) break;
                if (partyListStats.get(index) != null) {

                    int party_id = partyListStats.get(index).getParty_id();
                    ArrayList<PartyMembers> partyMembers = new ArrayList<>(database.findALLPartyMembersByParty_id(party_id));


                    ItemStack party_List = new ItemStack(Material.PLAYER_HEAD, 1);
                    ItemMeta party_List_Meta = party_List.getItemMeta();
                    party_List_Meta.setDisplayName(ChatColor.RED + partyListStats.get(index).getParty_name());
                    ArrayList<String> party_List_lore = new ArrayList<>();
                    party_List_lore.add(ChatColor.AQUA + "『 隊長 : " + partyListStats.get(index).getParty_leader() + "』");

                    party_List_lore.add(ChatColor.AQUA + "『 當前隊伍人數 : " + partyMembers.size() + "/" + partyListStats.get(index).getParty_limit() + "』");
                    if (partyListStats.get(index).isParty_is_public() == true) {
                        party_List_lore.add(ChatColor.AQUA + "『 是否公開 : " + "公開中" + "』");
                    } else {
                        party_List_lore.add(ChatColor.AQUA + "『 是否公開 : " + "私人隊伍" + "』");
                    }


                    /**
                     * 加入標籤值
                     */
                    party_List_Meta.getPersistentDataContainer()
                            .set(new NamespacedKey(MyMMORPG.getPlugin(), "party_leader")
                                    , PersistentDataType.STRING, partyListStats.get(index).getParty_leader());




                    party_List_Meta.setLore(party_List_lore);
                    party_List.setItemMeta(party_List_Meta);

                    inventory.addItem(party_List);

                }
            }


        }



    }
}
