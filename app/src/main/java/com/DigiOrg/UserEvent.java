package com.DigiOrg;
import java.io.Serializable;
import java.util.Date;


/**
 * Created by MATT on 3/27/2015.
 */
public class UserEvent implements Serializable {

    private String _name;
    private Date _date;
    boolean _is_priority;
    String _comment;

    //constructor
    public UserEvent (String name, Date date, String comment, boolean is_priority) {
        _name = name;
        _date = date;
        _comment = comment;
        _is_priority = is_priority;
    }

    public String getName() {return _name;}

    public Date getDate() {return _date;}

    public String getComment () {return _comment;}

    public boolean isPriority (){ return _is_priority;}

    public void setName (String newName) {_name = newName;}

    public void setComment (String newComment) {_comment = newComment;}

    public void setPriority (boolean newPriority) {_is_priority = newPriority;}

    public void setDate (Date newDate) {_date = newDate;}
}





/* FROM android dev site

Date(int year, int month, int day, int hour, int minute)
This constructor was deprecated in API level 1. Use GregorianCalendar(int, int, int, int, int) instead.

boolean	after(Date date)
Returns if this Date is after the specified Date.
boolean	before(Date date)
Returns if this Date is before the specified Date.
Object	clone()
Returns a new Date with the same millisecond value as this Date.
int	compareTo(Date date)
Compare the receiver to the specified Date to determine the relative ordering.
boolean	equals(Object object)
Compares the specified object to this Date and returns if they are equal.

*/