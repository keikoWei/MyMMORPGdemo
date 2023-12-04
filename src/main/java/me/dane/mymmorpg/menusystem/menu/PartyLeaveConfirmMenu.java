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

public class PartyLeaveConfirmMenu extends AbstractMenu {

    private final DatabaseMethod database;
    private final BukkitAudiences adventure;
    public PartyLeaveConfirmMenu(PlayerMenuUtility playerMenuUtility, DatabaseMethod database, BukkitAudiences adventure) {
        super(playerMenuUtility);
        this.database = database;
        this.adventure = adventure;
    }

    @Override
    public String getMenuName() {
        return ChatColor.RED + "" + ChatColor.BOLD + "【確定要離開隊伍嗎 ?】";
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) throws SQLException {

        switch (event.getCurrentItem().getType()){

            case GREEN_WOOL :

                Player player = (Player) event.getWhoClicked();

                PartyMethod partyMethod = new PartyMethod(player, database, adventure);

                partyMethod.PartyLeave(player);

                event.getWhoClicked().closeInventory();

                break;
            case RED_WOOL :
                new PartyStatsMenu(playerMenuUtility, database, adventure).open();
                break;
            case IRON_DOOR :
                event.getWhoClicked().closeInventory();
                break;
        }

    }

    @Override
    public void setMenuItems() throws SQLException {

        //確認離開隊伍按鈕
        ItemStack Yes_leave_button = new ItemStack(Material.GREEN_WOOL, 1);
        ItemMeta Yes_leave_button_meta = Yes_leave_button.getItemMeta();
        Yes_leave_button_meta.setDisplayName(ChatColor.RED + "確定要離開隊伍嗎 ?");
        Yes_leave_button.setItemMeta(Yes_leave_button_meta);

        //取消離開隊伍按鈕
        ItemStack NO_leave_button = new ItemStack(Material.RED_WOOL, 1);
        ItemMeta NO_leave_button_meta = NO_leave_button.getItemMeta();
        NO_leave_button_meta.setDisplayName(ChatColor.GREEN + "我在想想看 ~");
        NO_leave_button.setItemMeta(NO_leave_button_meta);

        //離開選單按鈕
        ItemStack partyMenu_leave = new ItemStack(Material.IRON_DOOR);
        ItemMeta partyMenu_leave_Meta = partyMenu_leave.getItemMeta();
        partyMenu_leave_Meta.setDisplayName(ChatColor.BLACK + "離開選單");
        partyMenu_leave.setItemMeta(partyMenu_leave_Meta);


        inventory.setItem(11, Yes_leave_button);
        inventory.setItem(15, NO_leave_button);
        inventory.setItem(26, partyMenu_leave);



    }
}
