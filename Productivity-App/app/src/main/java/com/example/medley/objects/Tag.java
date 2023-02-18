package com.example.medley.objects;

public class Tag {
    private String tagText;
    private int tagID;
    private final int maxLength = 14;
    private static int tagCounter = 0;

    public Tag(String tagText, int tagID){
        if (textLengthValid(tagText)){
            this.tagText = tagText;
            this.tagID = tagID;
        }
    }

    public Tag(String tagText){
        if (textLengthValid(tagText)){
            this.tagText = tagText;
            this.tagID = tagCounter;
            tagCounter++;
        }
    }

    public String getTagText(){
        return this.tagText;
    }

    public boolean setTagText(){
        boolean returnValue = true;

        if (textLengthValid(tagText)){
            this.tagText = tagText;
        }
        else{
            returnValue = false;
        }

        return returnValue;
    }

    private boolean textLengthValid(String tagText) {
        boolean returnValue = true;
        if (tagText.length() > maxLength) {
            returnValue = false;
        }

        return returnValue;
    }

    public int getTagID() {
        return this.tagID;
    }

    public boolean equals(Object object){
        boolean equalTo = false;

        if (object instanceof Tag){
            if (this.tagText.equals(((Tag) object).getTagText()) && this.tagID == ((Tag) object).getTagID()){
                equalTo = true;
            }
        }

        return equalTo;
    }
}
