package com.a.n.smartgym.Objects;

import java.util.ArrayList;

/**
 * Created by nirb on 21/06/2017.
 */

public class Muscle{
    private ArrayList<String> main = new ArrayList<>();
    private ArrayList<String> sub = new ArrayList<>();


    public ArrayList<String> getMain() {
        return main;
    }

    public void setMain(ArrayList<String> main) {
        this.main = main;
    }

    public ArrayList<String> getSub() {
        return sub;
    }

    public void setSub(ArrayList<String> sub) {
        this.sub = sub;
    }
}
