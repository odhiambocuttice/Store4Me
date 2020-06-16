package com.android.store4me;

public class StoreRequests_FromBackpacks {

    String BackPackContents;
    String BackpackOwner;
    String Name;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }



    public StoreRequests_FromBackpacks(String backPackContents, String backpackOwner, String name) {
        BackPackContents = backPackContents;
        BackpackOwner = backpackOwner;
        Name = name;
    }

    public StoreRequests_FromBackpacks() {
    }

    public StoreRequests_FromBackpacks(String backPackContents, String backpackOwner) {
        BackPackContents = backPackContents;
        BackpackOwner = backpackOwner;
    }

    public String getBackPackContents() {
        return BackPackContents;
    }

    public void setBackPackContents(String backPackContents) {
        BackPackContents = backPackContents;
    }

    public String getBackpackOwner() {
        return BackpackOwner;
    }

    public void setBackpackOwner(String backpackOwner) {
        BackpackOwner = backpackOwner;
    }
}
