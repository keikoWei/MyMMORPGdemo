package me.dane.mymmorpg.menusystem.menu;

import me.dane.mymmorpg.database.DatabaseMethod;
import me.dane.mymmorpg.menusystem.AbstractMenu;
import me.dane.mymmorpg.menusystem.PlayerMenuUtility;
import me.dane.mymmorpg.methods.PartyMethod;
import net.kyori.adventure.Adventure;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.ArrayList;

public class PartyMenu extends AbstractMenu {

    private final DatabaseMethod database;
    private final BukkitAudiences adventure;


    public PartyMenu(PlayerMenuUtility playerMenuUtility, DatabaseMethod database, BukkitAudiences adventure) {
        super(playerMenuUtility);
        this.database = database;
        this.adventure = adventure;
    }




    @Override
    public String getMenuName() {

        return ChatColor.GRAY + "【隊伍系統選單】";
    }

    @Override
    public int getSlots() {

        return 27;
    }


    @Override
    public void handleMenu(InventoryClickEvent event) throws SQLException {


        switch (event.getCurrentItem().getType()) {

            //創建隊伍按鈕
            case IRON_SWORD:
                Player player = (Player) event.getWhoClicked();

                PartyMethod partyMethod = new PartyMethod(player, database, adventure);

                partyMethod.PartyCreate(player);



                //開啟隊伍狀態欄菜單
                new PartyStatsMenu(playerMenuUtility, database, adventure).open();

                break;

            //搜尋伺服器公開隊伍按鈕
            case COMPASS:
                new PartyPublicMenu(playerMenuUtility, database, adventure).open();


                break;

            //離開選單
            case IRON_DOOR:

                event.getWhoClicked().closeInventory();

                break;
        }

    }

    @Override
    public void setMenuItems() {

        //創建隊伍按鈕設定
        ItemStack party_create = new ItemStack(Material.IRON_SWORD, 1);
        ItemMeta party_create_meta = party_create.getItemMeta();
        party_create_meta.setDisplayName(ChatColor.GREEN + "創建隊伍 !");
        ArrayList<String> party_create_lore = new ArrayList<>();
        party_create_lore.add(ChatColor.AQUA + "確定要創建隊伍嗎? 跟你的好友們一起遊玩 !");
        party_create_lore.add(ChatColor.AQUA + "預設為公開隊伍，至設定中調整預防陌生人加入 !");
        party_create_meta.setLore(party_create_lore);
        party_create.setItemMeta(party_create_meta);


        //搜尋伺服器公開隊伍按鈕設定
        ItemStack party_search = new ItemStack(Material.COMPASS, 1);
        ItemMeta party_search_meta = party_search.getItemMeta();
        party_search_meta.setDisplayName(ChatColor.DARK_RED + "找尋公開隊伍 !");
        ArrayList<String> party_search_lore = new ArrayList<>();
        party_search_lore.add(ChatColor.AQUA + "尋找志同道合的夥伴一起征戰吧 ~ !");
        party_search.setItemMeta(party_search_meta);

        //離開選單按鈕
        ItemStack partyMenu_leave = new ItemStack(Material.IRON_DOOR);
        ItemMeta partyMenu_leave_Meta = partyMenu_leave.getItemMeta();
        partyMenu_leave_Meta.setDisplayName(ChatColor.BLACK + "離開選單");
        partyMenu_leave.setItemMeta(partyMenu_leave_Meta);

        inventory.setItem(11, party_create);
        inventory.setItem(15, party_search);
        inventory.setItem(26, partyMenu_leave);


        setFillerGlass();

    }


}
