package com.android.store4me.GettersSetters;

public class StoreRequest_FromBackpacks2 {

    String Shopname;
    String PhoneNumber;
    String Price;

    public StoreRequest_FromBackpacks2() {
    }

    public StoreRequest_FromBackpacks2(String shopname, String phoneNumber, String price) {
        Shopname = shopname;
        PhoneNumber = phoneNumber;
        Price = price;
    }

    public String getShopname() {
        return Shopname;
    }

    public void setShopname(String shopname) {
        Shopname = shopname;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }
}
