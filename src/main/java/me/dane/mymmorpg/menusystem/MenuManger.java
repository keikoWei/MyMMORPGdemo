package me.dane.mymmorpg.menusystem;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class MenuManger {

    private static MenuManger menuManger;

    private static final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();

    public static PlayerMenuUtility getPlayerMenuUtility(Player player) {

        PlayerMenuUtility playerMenuUtility;

        if (!(playerMenuUtilityMap.containsKey(player))) { //See if the player has a playerMenuUtility "saved" for them

            //This player doesn't. Make one for them add  it to the hashmap
            playerMenuUtility = new PlayerMenuUtility(player);

            playerMenuUtilityMap.put(player, playerMenuUtility);

            return playerMenuUtility;
        } else {
            return playerMenuUtilityMap.get(player); //Return the object by using the provided player
        }
    }

    public static MenuManger getMenuManger() {
        return menuManger;
    }
}
