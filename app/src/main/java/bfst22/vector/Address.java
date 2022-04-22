package bfst22.vector;

import bfst22.vector.OSMNode;

public class Address {
    private String city, postcode, houseNumber, street;
    private final float x, y;
    private final long id;

    public Address(OSMNode node) {
        this.x = node.getX();
        this.y = node.getY();
        this.id = node.getId();
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public void setHousenumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public boolean isFull() {
        if (city == null) {
            return false;
        }
        if (postcode == null) {
            return false;
        }
        if (houseNumber == null) {
            return false;
        }
        if (street == null) {
            return false;
        } else {
            return true;
        }
    }

    public String getAddress() {
        String s = street + " " + houseNumber + " " + city;
        return s;
    }

    public float getX(){
        return this.x;
    }
    public float getY(){
        return this.y;
    }
    public long getId(){
        return id;
    }

}
