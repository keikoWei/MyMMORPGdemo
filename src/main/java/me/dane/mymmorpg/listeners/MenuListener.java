package me.dane.mymmorpg.listeners;

import me.dane.mymmorpg.menusystem.AbstractMenu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

import java.sql.SQLException;

public class MenuListener implements Listener {

    @EventHandler
    public void onMenuClick(InventoryClickEvent e) throws SQLException {

        InventoryHolder holder = e.getInventory().getHolder();
        //If the inventoryholder of the inventory clicked on
        // is an instance of Menu, then gg. The reason that
        // an InventoryHolder can be a Menu is because our Menu
        // class implements InventoryHolder!!
        if (holder instanceof AbstractMenu) {
            e.setCancelled(true); //prevent them from fucking with the inventory
            if (e.getCurrentItem() == null) { //deal with null exceptions
                return;
            }
            //Since we know our inventoryholder is a menu, get the Menu Object representing
            // the menu we clicked on
            AbstractMenu menu = (AbstractMenu) holder;
            //Call the handleMenu object which takes the event and processes it
            menu.handleMenu(e);
        }

    }

}
