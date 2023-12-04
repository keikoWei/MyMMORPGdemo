package me.dane.mymmorpg;

import me.dane.mymmorpg.commands.PartyCommand;
import me.dane.mymmorpg.database.DatabaseMethod;
import me.dane.mymmorpg.listeners.MenuListener;
import me.dane.mymmorpg.listeners.PlayerChatListener;
import me.dane.mymmorpg.listeners.PlayerJoinListener;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class MyMMORPG extends JavaPlugin {
    private static MyMMORPG plugin;



    private BukkitAudiences adventure;



    public BukkitAudiences adventure() {
        if (this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }


    private DatabaseMethod database;

    @Override
    public void onEnable() {


        // Plugin startup logic
        System.out.println("MyMMORPG插件啟動 !!");


        //config.yml
        this.saveDefaultConfig();
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        //database
        //JDBC - Java Database Connectivity API
        this.database = new DatabaseMethod(
                getConfig().getString("database.host"),
                getConfig().getString("database.port"),
                getConfig().getString("database.user"),
                getConfig().getString("database.password"),
                getConfig().getString("database.database_name"));
        try {
            this.database.initializeDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("MyMMORPG 資料庫初始化失敗 ! ! !");
        }


        /**
         * adventure api
         */
        // Initialize an audiences instance for the plugin
        this.adventure = BukkitAudiences.create(this);
        // then do any other initialization


        //註冊監聽事件
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(database), this);
        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerChatListener(database, adventure),this);


        //註冊命令
        getCommand("party").setExecutor(new PartyCommand(database, adventure));

        plugin = this;



    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
    }

    public DatabaseMethod getDatabase() {
        return database;
    }

    public static MyMMORPG getPlugin() {
        return plugin;
    }
}
