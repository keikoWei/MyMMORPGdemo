package me.dane.mymmorpg.model;

public class PartyListStats {

    private int party_id;
    private String party_name;

    private int party_limit;

    private String party_leader;

    private boolean party_is_public;

    private boolean party_is_share_drop;
    private boolean party_is_share_exp;



    public PartyListStats(String party_name, int party_limit, String party_leader, boolean party_is_public) {
        this.party_name = party_name;
        this.party_limit = party_limit;
        this.party_leader = party_leader;
        this.party_is_public = party_is_public;
    }


    public int getParty_id() {
        return party_id;
    }

    public void setParty_id(int party_id) {
        this.party_id = party_id;
    }

    public String getParty_name() {
        return party_name;
    }

    public void setParty_name(String party_name) {
        this.party_name = party_name;
    }

    public int getParty_limit() {
        return party_limit;
    }

    public void setParty_limit(int party_limit) {
        this.party_limit = party_limit;
    }

    public String getParty_leader() {
        return party_leader;
    }

    public void setParty_leader(String party_leader) {
        this.party_leader = party_leader;
    }

    public boolean isParty_is_public() {
        return party_is_public;
    }

    public void setParty_is_public(boolean party_is_public) {
        this.party_is_public = party_is_public;
    }

    public boolean isParty_is_share_drop() {
        return party_is_share_drop;
    }

    public void setParty_is_share_drop(boolean party_is_share_drop) {
        this.party_is_share_drop = party_is_share_drop;
    }

    public boolean isParty_is_share_exp() {
        return party_is_share_exp;
    }

    public void setParty_is_share_exp(boolean party_is_share_exp) {
        this.party_is_share_exp = party_is_share_exp;
    }
}
