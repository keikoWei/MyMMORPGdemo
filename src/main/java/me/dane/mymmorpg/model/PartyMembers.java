package me.dane.mymmorpg.model;

public class PartyMembers {

    private String player_uuid;
    private String player_name;
    private int level;

    private boolean has_party;
    private int current_party_id;

    public PartyMembers(String player_uuid, String player_name, int level, int current_party_id, boolean has_party) {
        this.player_uuid = player_uuid;
        this.player_name = player_name;
        this.level = level;
        this.current_party_id = current_party_id;
        this.has_party = has_party;
    }

    public String getPlayer_uuid() {
        return player_uuid;
    }

    public void setPlayer_uuid(String player_uuid) {
        this.player_uuid = player_uuid;
    }

    public String getPlayer_name() {
        return player_name;
    }

    public void setPlayer_name(String player_name) {
        this.player_name = player_name;
    }

    public int getLevel() {
        return level;
    }

    public void setLeave(int level) {
        this.level = level;
    }

    public boolean isHas_party() {
        return has_party;
    }

    public void setHas_party(boolean has_party) {
        this.has_party = has_party;
    }

    public int getCurrent_party_id() {
        return current_party_id;
    }

    public void setCurrent_party_id(int current_party_id) {
        this.current_party_id = current_party_id;
    }
}
