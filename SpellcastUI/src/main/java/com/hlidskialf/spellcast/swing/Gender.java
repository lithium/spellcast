package com.hlidskialf.spellcast.swing;

/**
 * Created by wiggins on 1/16/15.
 */
public class Gender {
    public final static String GenderMale = "male";
    public final static String GenderFemale = "female";
    public final static String GenderUnspecified = "none";

    public String scrubGender(String gender) {
        String g = gender.toLowerCase().trim();
        if (gender.equals(GenderFemale)) {
            return GenderFemale;
        }
        else
        if (gender.equals(GenderMale)) {
            return GenderMale;
        }
        return GenderUnspecified;
    }
}
