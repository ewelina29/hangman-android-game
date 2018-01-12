package com.example.eweli.sm_projekt;

/**
 * Created by eweli on 02.01.2018.
 */

public enum Category {
    HISTORY,
    LITERATURE;


    public static int getCount() {
        return Category.values().length;
    }

    public static String[] getCategories(){
        String [] categories = new String [getCount()];
        int counter = 0;
        for (Category c : Category.values()){
            categories[counter] = c.name();
            counter++;
        }

        return categories;
    }
}
