package com.example.eweli.sm_projekt;


public enum Category {
    GEOGRAPHY,
    ANIMALS,
    FOOD,
    ALL;


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
