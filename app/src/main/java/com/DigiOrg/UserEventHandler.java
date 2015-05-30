package com.DigiOrg;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Vector;
import java.util.Date;

/**
 * Created by MATT on 3/27/2015.
 */
public class UserEventHandler implements Serializable {

    Vector <UserEvent> eventListChrono;
    Vector <UserEvent> eventListPriority;
    Vector <UserEvent> eventListDeleted;
    Vector <UserReminder> eventListReminder;

    public UserEventHandler (){
        eventListChrono = new Vector <UserEvent> ();
        eventListPriority = new Vector <UserEvent> ();
        eventListDeleted = new Vector<UserEvent>();
        eventListReminder = new Vector<UserReminder>();
    }



    public boolean addEvent (String name, Date date, String comment, boolean is_priority) {
        //make sure the event name is not already in the vector
        if (!nameIsUnique(name))
            return false;
        UserEvent temp = new UserEvent (name, date, comment,  is_priority);
        eventListChrono.addElement(temp);
        //add to priority list if it is a priority event
        if (is_priority)
            eventListPriority.addElement(temp);
        updateChronoOrder();
        return true;
    }


    private void updateChronoOrder (){
        boolean flag = true;   // set flag to true to begin first pass
        UserEvent temp;   //holding variable

        while (flag)
        {
            flag = false;    //set flag to false awaiting a possible swap
            for (int j=0;  j < eventListChrono.size() -1;  j++)
            {
                UserEvent t1 = (eventListChrono.get(j));
                UserEvent t2 = (eventListChrono.get(j+1));
                //SimpleDateFormat t1Date = new SimpleDateFormat();
                //t1Date.
                //Date t2Date = new Date();
                //t2Date.g(t1.getDate().get2DigitYearStart());

                if ( t1.getDate().after(t2.getDate()))   // change to .before maybe
                {
                    UserEvent t1Obj = new UserEvent(t1.getName(), t1.getDate(), t1.getComment(), t1.isPriority());
                    UserEvent t2Obj = new UserEvent(t2.getName(), t2.getDate(), t2.getComment(), t2.isPriority());
                    //num[ j ] = num[ j+1 ];
                    eventListChrono.setElementAt(t2Obj, j);
                    //num[ j+1 ] = temp;
                    eventListChrono.setElementAt(t1Obj, j+1);

                    flag = true;           //shows a swap occurred
                }
            }
        }
        return;
    }


    public void removeEvent (int index) {
        addToDeletedVector(eventListChrono.get(index));
        String strName = eventListChrono.get(index).getName().toLowerCase();
        // also need to remove from priority event vector
        removeEventPriority(strName);
        eventListChrono.removeElementAt(index);
        return;
    }



    public void removeEventPriority (String toRemove){
        for (int i = 0; i < eventListPriority.size(); i++) {
            if (toRemove.equals(eventListPriority.get(i).getName().toLowerCase()))
                eventListPriority.removeElementAt(i);
        }
        return;
    }

    public void addToDeletedVector (UserEvent userEvent) {
        eventListDeleted.addElement(userEvent);
        if (eventListDeleted.size() > 4) {
            eventListDeleted.removeElementAt(eventListDeleted.size()-1);
        }
        return;
    }

    //may not need this function if the IDE can help implement it
    public void movePriorityEventGreater (int index) {
        //make sure it is not at the top of the list already and has more than 1 element
        if (index == 0 || eventListPriority.size() < 2)
            return;

        UserEvent t1 = (eventListPriority.get(index));
        UserEvent t2 = (eventListPriority.get(index-1));
        UserEvent t1Obj = new UserEvent(t1.getName(), t1.getDate(), t1.getComment(), t1.isPriority());
        UserEvent t2Obj = new UserEvent(t2.getName(), t2.getDate(), t2.getComment(), t2.isPriority());

        eventListPriority.setElementAt(t1Obj, index-1);
        eventListPriority.setElementAt(t2Obj, index);
        return;
    }


    //may not need this function if the IDE can help implement it
    public void movePriorityEventLess (int index) {
        //make sure it is not at the bottom of the list already and has more than 1 element
        if (index == eventListPriority.size() - 1 || eventListPriority.size() < 2)
            return;

        UserEvent t1 = (eventListPriority.get(index));
        UserEvent t2 = (eventListPriority.get(index+1));
        UserEvent t1Obj = new UserEvent(t1.getName(), t1.getDate(), t1.getComment(), t1.isPriority());
        UserEvent t2Obj = new UserEvent(t2.getName(), t2.getDate(), t2.getComment(), t2.isPriority());

        eventListPriority.setElementAt(t1Obj, index+1);
        eventListPriority.setElementAt(t2Obj, index);
        return;
    }


    public boolean nameIsUnique (String name) {
        name = name.toLowerCase();
        for (int i = 0; i < eventListChrono.size(); i++) {
            String test = eventListChrono.get(i).getName().toLowerCase();
            if (name.equals(test)) {
                //event name is already in the vector. attempt failed
                return false;
            }
        }
        // not found, can return true
        return true;
    }


    public boolean updateName (String newName, int index) {
        String currentName = eventListChrono.get(index).getName();
        if (nameIsUnique(newName)) {
        // see if and where it is in the priority list to update there too
            for (int i = 0; i < eventListPriority.size(); i++) {
                String temp = eventListPriority.get(i).getName().toLowerCase();
                if (temp.equals(currentName.toLowerCase())) {
                    eventListPriority.get(i).setName(newName);
                }
            }
            eventListChrono.get(index).setName(newName);
            return true;
        }
        else
            return false;
    }

    public void updateComment (String newComment, int index, String eventName) {
        // see if and where it is in the priority list to update there too
        for (int i = 0; i < eventListPriority.size(); i++) {
            String temp = eventListPriority.get(i).getName().toLowerCase();
            if (temp.equals(eventName.toLowerCase())) {
                eventListPriority.get(i).setComment(newComment);
            }
        }
        eventListChrono.get(index).setComment(newComment);
        return;
    }

    public void updatePriority (boolean newPriority, int index, UserEvent ue ) {

        if (newPriority == true && eventListChrono.get(index).isPriority() == false) {
            eventListChrono.get(index).setPriority(true);
            eventListPriority.addElement(eventListChrono.get(index));
        }

        else if (newPriority == false && eventListChrono.get(index).isPriority() == true) {
            // need to find and remove from the priority vector
            for (int i = 0; i < eventListPriority.size(); i++) {
                String temp = eventListPriority.get(i).getName().toLowerCase();
                if (temp.equals(ue.getName().toLowerCase())) {
                    eventListPriority.removeElementAt(i);
                }
            }
            eventListChrono.get(index).setPriority(false);
        }
        // the other 2 cases do not constitute a change
        return;
    }

    public void updateDate (Date newDate, int index, String eventName) {
        // see if and where it is in the priority list to update there too
        for (int i = 0; i < eventListPriority.size(); i++) {
            String temp = eventListPriority.get(i).getName().toLowerCase();
            if (temp.equals(eventName.toLowerCase())) {
                eventListPriority.get(i).setDate(newDate);
            }
        }
        eventListChrono.get(index).setDate(newDate);
    }

    public int getChronoIndexFromPriorityIndex (int priority_index) {
        String priorityName = eventListPriority.get(priority_index).getName().toLowerCase();
        for (int i = 0; i < eventListChrono.size(); i++) {
            if (priorityName.equals(eventListChrono.get(i).getName().toLowerCase())) {
                return i;
            }
        }
        return -1;
    }


    // may not need. remove if not needed
    public void addEventPriority (String name){
        // find the object in the chrono list
        UserEvent temp = null;

        for (int i = 0; i < eventListChrono.size(); i++) {
            String test = eventListChrono.get(i).getName();
            if (name.equals(test));
            //need test
            temp = eventListChrono.get(i);
        }

        if (temp == null) {
            System.out.println("error, not found");
        }
        else {
            Date dateTemp = temp.getDate();
            String commentTemp = temp.getComment();
            String nameTemp = temp.getName();
            UserEvent newObj = new UserEvent(nameTemp, dateTemp, commentTemp, true);
            eventListPriority.add(temp);
        }
        return;
    }

    public boolean addReminderNormal (String name, Date date, int frequencyNum, String frequencyType, String comment) {
        if (reminderNameIsUnique(name)) {
            UserReminder temp = new UserReminder(name, date, frequencyNum, frequencyType, comment);
            eventListReminder.add(temp);
            return true;
        }
        else
            return false;
    }

    public boolean addReminderByDays (String name, Date date, Vector<String> days, String comment) {
        if (reminderNameIsUnique(name)) {
            UserReminder temp = new UserReminder(name, date, days, comment);
            eventListReminder.add(temp);
            return true;
        }
        else
            return false;
    }

    public boolean reminderNameIsUnique (String name) {
        name = name.toLowerCase();
        for (int i = 0; i < eventListReminder.size(); i++) {
            String test = eventListReminder.get(i).getName().toLowerCase();
            if (name.equals(test)) {
                //event name is already in the vector. attempt failed
                return false;
            }
        }
        // not found, can return true
        return true;
    }


    public void removeReminder (int index) {
        eventListReminder.removeElementAt(index);
        return;
    }

/*
    public boolean updateReminderName (String newName, int index) {
        if (diffNameMinusIndex(newName, index)) {
            eventListReminder.get(index).setName(newName);
            return true;
        }
        else
            return false;
    }
*/
public boolean diffNameMinusIndexEvent (String newName, int index) {
    for (int i = 0; i < eventListChrono.size(); i++) {
        if (newName.trim().toLowerCase().equals(eventListChrono.get(i).getName().trim().toLowerCase()) && i != index) {
            return false;
        }
    }
    if (eventListChrono.get(index).isPriority()) {
        for (int i = 0; i < eventListPriority.size(); i++) {
            if (eventListChrono.get(index).getName().trim().toLowerCase().equals(eventListPriority.get(i).getName().trim().toLowerCase())) {
                eventListPriority.get(i).setName(newName);
            }
        }
    }
    eventListChrono.get(index).setName(newName);
    return true;
}

    public void deleteOldEvents () {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date now = new Date(calendar.get(Calendar.YEAR)-1900, calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
        for (int i = 0; i < eventListChrono.size(); i++) {
            if (now.after(eventListChrono.get(i).getDate())) {
                removeEvent(i);
            }
        }
        return;
    }


    public boolean diffNameMinusIndex (String newName, int index) {
        for (int i = 0; i < eventListReminder.size(); i++) {
            if (newName.trim().toLowerCase().equals(eventListReminder.get(i).getName().trim().toLowerCase()) && i != index) {
                return false;
            }
        }
        eventListReminder.get(index).setName(newName);
        return true;
    }


    public void updateReminderComment (String newComment, int index) {
        eventListReminder.get(index).setComment(newComment);
        return;
    }

    public void updateFrequencyType (String newType, int index) {
        eventListReminder.get(index).setFrequencyType(newType);
    }

    public void updateFrequency (int newNum, int index) {
        eventListReminder.get(index).setFrequency(newNum);
    }

}

