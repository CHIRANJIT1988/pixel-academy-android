package pixel.academy.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by CHIRANJIT on 9/19/2016.
 */
public class Tutor implements Serializable
{

    public Address addressObj = new Address();
    public List<Education> educationList = new ArrayList<>();
    public TutorPreference preferenceObj = new TutorPreference();
    public Occupation occupationObj = new Occupation();


    public static List<Tutor> tutorList = new ArrayList<>();
    public int tutor_id;
    public String first_name, last_name, gender, email, profile_pic, phone_no;

    public Tutor()
    {

    }

    public Tutor(int tutor_id, String first_name, String last_name, String gender, String profile_pic, String email, String phone_no,
                 Address addressObj, List<Education> educationList, TutorPreference preferenceObj, Occupation occupationObj)
    {

        this.tutor_id = tutor_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.gender = gender;
        this.email = email;
        this.profile_pic = profile_pic;
        this.phone_no = phone_no;

        this.addressObj = addressObj;
        this.preferenceObj = preferenceObj;
        this.occupationObj = occupationObj;
        this.educationList = educationList;
    }
}
