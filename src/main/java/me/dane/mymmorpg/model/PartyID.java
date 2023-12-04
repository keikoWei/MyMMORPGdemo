package me.dane.mymmorpg.model;

public class PartyID extends PartyListStats{

    private int party_id;

    public PartyID(int party_id, String party_name, int party_limit, String party_leader, boolean party_is_public) {
        super(party_name, party_limit, party_leader, party_is_public);
        this.party_id = party_id;
    }

    public int getParty_id() {
        return party_id;
    }

    public void setParty_id(int party_id) {
        this.party_id = party_id;
    }


}
