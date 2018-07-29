package pixel.academy.model;

import java.io.Serializable;

/**
 * Created by CHIRANJIT on 9/21/2016.
 */
public class Education implements Serializable
{

    public String qualification, division, qualification_status, academic_achievement, college;


    public Education()
    {

    }

    public Education(String qualification, String division, String qualification_status, String academic_achievement, String college)
    {

        this.qualification = qualification;
        this.division = division;
        this.qualification_status = qualification_status;
        this.academic_achievement = academic_achievement;
        this.college = college;
    }
}