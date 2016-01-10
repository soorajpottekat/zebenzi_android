package com.zebenzi.ui;

/**
 * Created by Vaugan.Nayagar on 2015/09/29.
 */
public enum FragmentsLookup {

    ACCOUNT("Account", 1),
    HISTORY("History", 2),
    LOGIN("Login", 3),
    NEW_JOB("New Job", 4),
    REGISTER("Register", 5),
    QUOTE("Quote", 6),
    JOB_DETAILS("Job Details", 7);


    private final String name;
    private final int id;

        private FragmentsLookup(String name, int id) {
            this.name = name;
           this.id = id;
        }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public static FragmentsLookup findByName(String fragmentName){
        for(FragmentsLookup f : values()){
            if( f.getName().equalsIgnoreCase(fragmentName)){
                return f;
            }
        }
        return null;
    }

}

