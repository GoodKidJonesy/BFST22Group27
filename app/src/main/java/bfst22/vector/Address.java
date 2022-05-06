package bfst22.vector;

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

    public String getStreet() {
        return street.toLowerCase().replace(" ", "");
    }

    public String toString() {
        return street.toLowerCase().replace("é","e").replace("ü","u").replace("ö", "oe").replace("õ","oe").replace("ä","ae") + " " + houseNumber.toLowerCase() + " " + postcode + " " + city.toLowerCase();
    }
    public String getCords(){
        return x + "." + y;
    }
}
