package com.seekting.killer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Control implements Serializable {

    public static final String TYPE_BAR = "0";
    public static final String TYPE_PERSON = "1";

    public static final String PERSON_ACTION_DIE = "0";
    public static final String PERSON_ACTION_ALIVE = "1";
    public static final String PERSON_ACTION_ADD = "2";

    public static final String BAR_ACTION_UP = "0";
    public static final String BAR_ACTION_DOWN = "1";
    public static final String BAR_ACTION_SCREAM = "2";
    public static final String BAR_ACTION_SOUND = "3";
    public static final String BAR_ACTION_ASK = "4";
    @SerializedName("action")
    private String action;

    @SerializedName("ids")
    private List<String> ids;

    @SerializedName("type")
    private String type;

    public void setAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public List<String> getIds() {
        return ids;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return
                "Control{" +
                        "action = '" + action + '\'' +
                        ",ids = '" + ids + '\'' +
                        ",type = '" + type + '\'' +
                        "}";
    }
}