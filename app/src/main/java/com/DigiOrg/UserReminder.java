package com.DigiOrg;

import java.io.Serializable;
import java.util.Date;
import java.util.Vector;

/**
 * Created by machine-head on 4/20/15.
 */
public class UserReminder implements Serializable{

    private String _name;
    private Date _date;
    Vector <String> _days; //sunday is day 0, saturday 6
    int _frequency = -1;
    String _frequency_type; // 0 = hourly, 1 = daily, 2 = weekly, 3 = monthly, 4= yearly
    String _comment;
    boolean _is_active;

    public UserReminder (String name, Date date,Vector<String> days, String comment) {
        _name = name;
        _date = date;
        _days = days;
        _comment = comment;
        _is_active = true;
    }

    public UserReminder (String name, Date date, int frequency, String frequency_type, String comment) {
        _name = name;
        _date = date;
        _frequency = frequency;
        _frequency_type = frequency_type;
        _comment = comment;
        _is_active = true;
    }

    public String getName() {return _name;}

    public Date getDate() {return _date;}

    public String getComment () {return _comment;}

    public int getFrequency () {return _frequency;}

    public String getFrequencyType () {return _frequency_type;}

    public boolean isActive () {return _is_active;}

    public Vector<String> getDays () {return _days;}


    public void setName (String newName) {_name = newName;}

    public void setComment (String newComment) {_comment = newComment;}

    public void setDate (Date newDate) {_date = newDate;}

    public void setActive (boolean isActive) {_is_active = isActive;}

    public void setFrequency (int frequency) {_frequency = frequency;}

    public void setFrequencyType (String frequencyType) {_frequency_type = frequencyType;}

    public void setDays (Vector<String> days) {_days = days;}




}
