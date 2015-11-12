package com.jkpg.jurgen.nl.vacationdroid.core.vacationList.logic;


import java.util.ArrayList;

public class VacationsDummy {

    public String name;
    public String description;
    public ArrayList memories;

    public VacationsDummy(){
        name = "Dummy Vacation";
        description = "Dummy Description for a dummy vacation.";
//        memories.add(memory);
    }

    public VacationsDummy(String name, String description){
        this.name = name;
        this.description = description;
//        memories.add(memory);
    }
}
