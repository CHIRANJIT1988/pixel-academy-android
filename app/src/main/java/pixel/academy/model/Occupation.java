package pixel.academy.model;

import java.io.Serializable;

/**
 * Created by CHIRANJIT on 9/21/2016.
 */
public class Occupation implements Serializable
{

    public String occupation, employer, start_date, end_date;


    public Occupation()
    {

    }

    public Occupation(String occupation, String employer, String start_date, String end_date)
    {

        this.occupation = occupation;
        this.employer = employer;
        this.start_date = start_date;
        this.end_date = end_date;
    }
}
