package com.a.n.smartgym.DBModel;

/**
 * Created by Tan on 1/26/2016.
 */
public class User {
    public static final String TAG = User.class.getSimpleName();
    public static final String TABLE = "User";

    // Labels Table Columns names
    public static final String KEY_USER_ID = "userid";
    public static final String KEY_FIRST_NAME = "firstname";
    public static final String KEY_LAST_NAME = "lastname";


    private String id ;
    private String fname;
    private String lname;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }
}
