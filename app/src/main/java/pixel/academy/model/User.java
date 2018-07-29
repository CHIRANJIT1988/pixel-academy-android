package pixel.academy.model;

/**
 * Created by CHIRANJIT on 7/22/2016.
 */
public class User
{

    public String user_id, fcm_reg_id, name, email, password, phone_number, device_id, date_of_birth, location, gender, confirmation_code;


    public User()
    {

    }

    public User(String name, String email, String date_of_birth, String location, String gender)
    {

        this.name = name;
        this.email = email;
        this.date_of_birth = date_of_birth;
        this.location = location;
        this.gender = gender;
    }


    public void setUserId(String user_id)
    {
        this.user_id = user_id;
    }

    public String getUserId()
    {
        return this.user_id;
    }


    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }


    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getEmail()
    {
        return this.email;
    }


    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getPassword()
    {
        return this.password;
    }


    public void setPhoneNumber(String phone_number)
    {
        this.phone_number = phone_number;
    }

    public String getPhoneNumber()
    {
        return this.phone_number;
    }


    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getLocation()
    {
        return this.location;
    }


    public void setDateOfBirth(String date_of_birth)
    {
        this.date_of_birth = date_of_birth;
    }

    public String getDateOfBirth()
    {
        return this.date_of_birth;
    }


    public void setGender(String gender)
    {
        this.gender = gender;
    }

    public String getGender()
    {
        return this.gender;
    }


    public void setDeviceID(String device_id)
    {
        this.device_id = device_id;
    }

    public String getDeviceID()
    {
        return this.device_id;
    }


    public void setConfirmationCode(String confirmation_code)
    {
        this.confirmation_code = confirmation_code;
    }

    public String getConfirmationCode()
    {
        return this.confirmation_code;
    }
}
