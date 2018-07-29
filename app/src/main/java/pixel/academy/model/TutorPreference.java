package pixel.academy.model;

import java.io.Serializable;

/**
 * Created by CHIRANJIT on 9/21/2016.
 */
public class TutorPreference implements Serializable
{

    public String classes, subjects, fees_range, duration_per_day, days_per_week, medium, board;


    public TutorPreference()
    {

    }


    public TutorPreference(String classes, String subjects, String fees_range, String duration_per_day, String days_per_week, String medium, String board)
    {

        this.classes = classes;
        this.subjects = subjects;
        this.fees_range = fees_range;
        this.duration_per_day = duration_per_day;
        this.days_per_week = days_per_week;
        this.medium = medium;
        this.board = board;
    }
}