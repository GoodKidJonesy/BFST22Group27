package bfst22.vector;

import bfst22.vector.OSMNode;

public class Address {
    private String city, postcode, houseNumber, street;
    private final float x, y;

    public Address(OSMNode node) {
        this.x = node.getX();
        this.y = node.getY();
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
}
