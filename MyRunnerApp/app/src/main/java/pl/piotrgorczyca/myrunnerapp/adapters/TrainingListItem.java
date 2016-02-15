package pl.piotrgorczyca.myrunnerapp.adapters;

import java.util.Calendar;
import java.util.Date;

import pl.piotrgorczyca.myrunnerapp.helper.CalendarHelper;

/**
 * Created by Piotr on 2015-12-13. Enjoy!
 */
public class TrainingListItem {
    private String name;
    private String place;
    private int distance;
    private String date;
    private int tid;


    private int user_id;

    public TrainingListItem(String name, String place, int distance, String date, int tid) {
        this.setName(name);
        this.setPlace(place);
        this.setDistance(distance);
        this.setDate(date);
        this.setTid(tid);
    }

    public TrainingListItem(String name, String place, int distance, String date, int tid, int user_id) {
        this(name, place, distance, date, tid);
        this.setUserId(user_id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getDate() {
        return CalendarHelper.countRemainingTime(date);
    }

    public String getFullDate() { return date; }

    public void setDate(String date) {
        this.date = date;
    }


    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }


    public int getUserId() {
        return user_id;
    }

    public void setUserId(int user_id) {
        this.user_id = user_id;
    }


}
