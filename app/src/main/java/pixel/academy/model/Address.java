package pixel.academy.model;

import java.io.Serializable;

/**
 * Created by CHIRANJIT on 10/8/2015.
 */
public class Address implements Serializable
{

    public String pincode, home_address, locality, city, state, country;
    public double distance, latitude, longitude;


    public Address()
    {

    }

    public Address(String locality, String home_address, String pincode, String city, String state, String country, double latitude, double longitude, double distance)
    {

        this.locality = locality;
        this.home_address = home_address;
        this.pincode = pincode;
        this.city = city;
        this.state = state;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
    }
}