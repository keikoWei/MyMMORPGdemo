package me.dane.mymmorpg.menusystem.menu;

import me.dane.mymmorpg.database.DatabaseMethod;
import me.dane.mymmorpg.menusystem.AbstractMenu;
import me.dane.mymmorpg.menusystem.PlayerMenuUtility;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.ArrayList;

import static me.dane.mymmorpg.listeners.PlayerChatListener.settingPartyNameMap;


public class PartyStatsChangeMenu extends AbstractMenu {

    private final DatabaseMethod database;
    private final BukkitAudiences adventure;

    public PartyStatsChangeMenu(PlayerMenuUtility playerMenuUtility, DatabaseMethod database, BukkitAudiences adventure) {
        super(playerMenuUtility);
        this.database = database;
        this.adventure = adventure;
    }

    @Override
    public String getMenuName() {
        return ChatColor.GRAY + "【隊伍狀態設定】";
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) throws SQLException {

        Player player = (Player) event.getWhoClicked();

        switch (event.getCurrentItem().getType()) {
            case NAME_TAG:
                break;
            case COMPASS:
                break;
            case ITEM_FRAME:
                break;
            case EXPERIENCE_BOTTLE:
                break;

        }

        if (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("[隊伍功能]" + "修改隊伍名稱")) {

            settingPartyNameMap.put(player, true);
            player.sendMessage(ChatColor.GREEN + "請輸入新的隊伍名稱或輸入 'cancel' 以取消操作");
            event.getWhoClicked().closeInventory();
        }

    }


    @Override
    public void setMenuItems() throws SQLException {

        //[功能]隊伍名稱變更
        ItemStack change_party_name = new ItemStack(Material.NAME_TAG, 1);
        ItemMeta change_party_name_meta = change_party_name.getItemMeta();
        change_party_name_meta.setDisplayName(ChatColor.BLUE + "[隊伍功能]" + ChatColor.GREEN + "修改隊伍名稱");
        ArrayList<String> change_party_name_lore = new ArrayList<>();
        change_party_name_lore.add(ChatColor.GRAY + "" + ChatColor.BOLD + "[說明]");
        change_party_name_lore.add(ChatColor.YELLOW + "只有隊長可以更改");
        change_party_name_lore.add(ChatColor.YELLOW + "修改名稱用以招募志同道合的隊員 !");

        change_party_name_meta.setLore(change_party_name_lore);
        change_party_name.setItemMeta(change_party_name_meta);

        //[按鈕]隊伍名稱設定按鈕
        ItemStack change_party_name_button = new ItemStack(Material.YELLOW_WOOL, 1);
        ItemMeta change_party_name_button_meta = change_party_name_button.getItemMeta();
        change_party_name_button_meta.setDisplayName(ChatColor.GREEN + "點此修改隊伍名稱 !");
        change_party_name_button.setItemMeta(change_party_name_button_meta);


        //[功能]是否公開狀態按鈕
        ItemStack is_party_public = new ItemStack(Material.COMPASS, 1);
        ItemMeta is_party_public_meta = is_party_public.getItemMeta();
        is_party_public_meta.setDisplayName(ChatColor.GREEN + "修改隊伍公開狀態 !");
        ArrayList<String> is_party_public_lore = new ArrayList<>();
        is_party_public_lore.add(ChatColor.RED + "" + ChatColor.BOLD + "注意" + ChatColor.AQUA + "請輸入'正確'的玩家名稱");

        is_party_public_meta.setLore(is_party_public_lore);
        is_party_public.setItemMeta(is_party_public_meta);

        //[功能]是否共享物品掉落
        ItemStack is_share_item = new ItemStack(Material.ITEM_FRAME, 1);
        ItemMeta is_share_item_meta = is_share_item.getItemMeta();
        is_share_item_meta.setDisplayName(ChatColor.GREEN + "修改掉落物分配狀態 !");
        ArrayList<String> is_share_item_lore = new ArrayList<>();
        is_share_item_lore.add(ChatColor.RED + "" + ChatColor.BOLD + "注意" + ChatColor.AQUA + "請輸入'正確'的玩家名稱");

        is_share_item_meta.setLore(is_share_item_lore);
        is_share_item.setItemMeta(is_share_item_meta);

        //[功能]是否共享經驗
        ItemStack is_share_exp = new ItemStack(Material.EXPERIENCE_BOTTLE, 1);
        ItemMeta is_share_exp_meta = is_share_exp.getItemMeta();
        is_share_exp_meta.setDisplayName(ChatColor.GREEN + "修改經驗分配狀態 !");
        ArrayList<String> is_share_exp_lore = new ArrayList<>();
        is_share_exp_lore.add(ChatColor.RED + "" + ChatColor.BOLD + "注意" + ChatColor.AQUA + "請輸入'正確'的玩家名稱");

        is_share_exp_meta.setLore(is_share_exp_lore);
        is_share_exp.setItemMeta(is_share_exp_meta);


        inventory.setItem(10, change_party_name);
        inventory.setItem(12, is_party_public);
        inventory.setItem(14, is_share_item);
        inventory.setItem(16, is_share_exp);
        setFillerGlass();

    }
}
