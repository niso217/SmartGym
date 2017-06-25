package com.a.n.smartgym.DBModel;


import java.util.Date;

/**
 * Created by Tan on 1/26/2016.
 */
public class Visits {

    public static final String TAG = Visits.class.getSimpleName();
    public static final String TABLE = "Visits";
    // Labels Table Columns names
    public static final String KEY_USER_ID = "userid";
    public static final String KEY_VISIT_ID = "visitid";
    public static final String KEY_DATE = "date";

    private String visitid;
    private String userid;
    private Date date;

    public String getVisitid() {
        return visitid;
    }

    public void setVisitid(String visitid) {
        this.visitid = visitid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
