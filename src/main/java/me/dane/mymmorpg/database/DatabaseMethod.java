package me.dane.mymmorpg.database;

import me.dane.mymmorpg.model.PartyID;
import me.dane.mymmorpg.model.PartyListStats;
import me.dane.mymmorpg.model.PartyMembers;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseMethod {

    private final String HOST;
    private final String PORT;

    private final String USER;
    private final String PASSWORD;
    private final String DATABASE_NAME;

    private Connection connection;

    public DatabaseMethod(String HOST, String POST, String USER, String PASSWORD, String DATABASE_NAME) {
        this.HOST = HOST;
        this.PORT = POST;
        this.USER = USER;
        this.PASSWORD = PASSWORD;
        this.DATABASE_NAME = DATABASE_NAME;
    }


    //連線到資料庫
    public Connection getConnection() throws SQLException {

        if (connection != null) {
            return connection;
        }


        //database information
        String url = "jdbc:mysql://" + this.HOST + "/" + this.DATABASE_NAME;


        this.connection = DriverManager.getConnection(url, this.USER, this.PASSWORD);

        System.out.println("MyMMORPG_database資料庫連線成功 ! ! !");

        return this.connection;

    }


    public void initializeDatabase() throws SQLException {

        Statement statement = getConnection().createStatement();

        /**
         *  創建partyList_stats資料表
         **/
        String createPartyList = "CREATE TABLE IF NOT EXISTS partyList_stats " + "(party_id int auto_increment primary key," + " party_name varchar(36), " + " party_limit int," + " party_leader varchar(36)," + " party_is_public boolean,"+ "party_is_share_drop boolean," + "party_is_share_exp boolean," + " foreign key (party_leader) references  party_members(player_name)" + ")";

        statement.execute(createPartyList);


        /**
         * 創建party_members資料表
         */
        String createPartyMember = "CREATE TABLE IF NOT EXISTS party_members (" + " player_uuid VARCHAR(36) primary key," + " player_name VARCHAR(36)," + " level int," + " current_party_id int," + " has_party boolean," + " foreign key (current_party_id) references partyList_stats(party_id)" + ")";

        statement.execute(createPartyMember);

        statement.close();

    }


    public PartyMembers findPartyMembersByUUID(String uuid) throws SQLException {

        PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM party_members WHERE player_uuid = ?");
        statement.setString(1, uuid);

        ResultSet resultSet = statement.executeQuery();

        PartyMembers partyMembers;

        if (resultSet.next()) {

            partyMembers = new PartyMembers(resultSet.getString("player_uuid"), resultSet.getString("player_name"), resultSet.getInt("level"), resultSet.getInt("current_party_id"), resultSet.getBoolean("has_party"));


            statement.close();

            return partyMembers;

        }

        statement.close();
        return null;

    }

    public void createPartyMembers(PartyMembers partyMembers) throws SQLException {

        String sql = "INSERT INTO party_members(player_uuid, player_name, level, current_party_id, has_party) VALUES (?, ?, ?, ?, ?)";

        PreparedStatement statement = getConnection().prepareStatement(sql);

        statement.setString(1, partyMembers.getPlayer_uuid());
        statement.setString(2, partyMembers.getPlayer_name());
        statement.setInt(3, partyMembers.getLevel());
        statement.setLong(4, partyMembers.getCurrent_party_id());
        statement.setBoolean(5, partyMembers.isHas_party());

        statement.executeUpdate();

        statement.close();


    }

    /**
     * update party_members 表中的 所有欄位資料
     */

    public void updatePartyMembers(PartyMembers partyMembers) throws SQLException {

        PreparedStatement statement = getConnection().prepareStatement("UPDATE party_members SET player_name = ?, level = ?, current_party_id = ?, has_party = ? WHERE player_uuid = ?");
        statement.setString(1, partyMembers.getPlayer_name());
        statement.setInt(2, partyMembers.getLevel());
        statement.setInt(3, partyMembers.getCurrent_party_id());
        statement.setBoolean(4, partyMembers.isHas_party());
        statement.setString(5, partyMembers.getPlayer_uuid());

        statement.executeUpdate();

        statement.close();
    }

    public void deletePartyMembers(PartyMembers partyMembers) throws SQLException {

        PreparedStatement statement = getConnection().prepareStatement("DELETE FROM party_members WHERE player_uuid = ?");
        statement.setString(1, partyMembers.getPlayer_uuid());

        statement.executeUpdate();

        statement.close();

    }


    public void createPartyListStats(PartyListStats partyListStats) throws SQLException {

        String sql = "INSERT INTO partyList_stats(party_name, party_limit, party_leader, party_is_public) VALUES (?, ?, ?, ?)";

        PreparedStatement statement = getConnection().prepareStatement(sql);

        statement.setString(1, partyListStats.getParty_name());
        statement.setInt(2, partyListStats.getParty_limit());
        statement.setString(3, partyListStats.getParty_leader());
        statement.setBoolean(4, partyListStats.isParty_is_public());

        statement.executeUpdate();

        statement.close();

    }


    /**
     * update partyList_stats 表中的 "只有party_name"的欄位資料
     * 透過PartyID類的物件
     */
    public void updatePartyListStatsOnly_party_name(PartyID partyID) throws SQLException {

        PreparedStatement statement = getConnection().prepareStatement("UPDATE partyList_stats SET party_name = ? WHERE party_id = ?");


        statement.setString(1, partyID.getParty_name());
        statement.setInt(2, partyID.getParty_id());


        statement.executeUpdate();
        statement.close();
    }

    /**
     * update partyList_stats 表中的 "只有party_leader"的欄位資料
     * 透過PartyID類的物件
     */
    public void updatePartyListStatsOnly_party_leader(PartyID partyID) throws SQLException {

        PreparedStatement statement = getConnection().prepareStatement("UPDATE partyList_stats SET party_leader = ? WHERE party_id = ?");


        statement.setString(1, partyID.getParty_leader());
        statement.setInt(2, partyID.getParty_id());


        statement.executeUpdate();
        statement.close();
    }

    public PartyID findPartyByParty_leader(String party_leader) throws SQLException {

        PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM partyList_stats WHERE party_leader = ?");
        statement.setString(1, party_leader);

        ResultSet resultSet = statement.executeQuery();

        PartyID partyID = null;

        if (resultSet.next()) {

            partyID = new PartyID(
                    resultSet.getInt("party_id"),
                    resultSet.getString("party_name"),
                    resultSet.getInt("party_limit"),
                    resultSet.getString("party_leader"),
                    resultSet.getBoolean("party_is_public"));


            statement.close();


        }

        return partyID;
    }


    /**
     * 拿partyMember 表中，欄位current_party_id去搜尋party_List
     */
    public PartyListStats findPartyListByParty_ID(int party_id) throws SQLException {

        PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM partyList_stats WHERE party_id = ?");
        statement.setInt(1, party_id);
        ResultSet resultSet = statement.executeQuery();

        PartyListStats partyListStats;

        if (resultSet.next()) {

            partyListStats = new PartyListStats(
                    resultSet.getString("party_name"),
                    resultSet.getInt("party_limit"),
                    resultSet.getString("party_leader"),
                    resultSet.getBoolean("party_is_public"));

            statement.close();

            return partyListStats;
        }

        return null;
    }


    /**
     * /搜尋partyList_stats表中的所有資料,並儲存到List中
     */
    public List<PartyListStats> findAllPartyLists() {
        List<PartyListStats> partyList = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM partyList_stats");
             ResultSet resultSet = preparedStatement.executeQuery()) {

            //loop 並儲存在ArrayList 類型 <PartyListStats> 中
            while (resultSet.next()) {

                String partyName = resultSet.getString("party_name");
                int partyLimit = resultSet.getInt("party_limit");
                String partyLeader = resultSet.getString("party_leader");
                boolean partyIsPublic = resultSet.getBoolean("party_is_public");


                PartyListStats partyListStats = new PartyListStats(partyName, partyLimit, partyLeader, partyIsPublic);
                partyList.add(partyListStats);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return partyList;
    }

    /**
     * /搜尋party_members表中的所有資料,並儲存到List中
     */
    public List<PartyMembers> findALLPartyMembersByParty_id(int party_id) {

        List<PartyMembers> partyMembersList = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM party_members WHERE current_party_id = ?");
        ) {
            // 设置参数值
            preparedStatement.setInt(1, party_id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                // 遍历结果集
                while (resultSet.next()) {
                    String player_uuid = resultSet.getString("player_uuid");
                    String player_name = resultSet.getString("player_name");
                    int level = resultSet.getInt("level");
                    int current_party_id = resultSet.getInt("current_party_id");
                    boolean has_party = resultSet.getBoolean("has_party");

                    // 创建 PartyMembers 对象并添加到列表中
                    PartyMembers partyMembers = new PartyMembers(player_uuid, player_name, level, current_party_id, has_party);
                    partyMembersList.add(partyMembers);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return partyMembersList;
    }

    public void deletePartyListStats(PartyListStats partyListStats) throws SQLException {

        PreparedStatement statement = getConnection().prepareStatement("DELETE FROM partyList_stats WHERE party_leader = ?");
        statement.setString(1, partyListStats.getParty_leader());

        statement.executeUpdate();

        statement.close();

    }


}

