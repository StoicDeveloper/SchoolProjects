package com.example.medley.objects;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.HashSet;
public class Task {

    //instance variables
    private static int taskCounter = 1000;
    private String taskText;
    private int taskID;
    private HashSet<Tag> tags;
    private int priority; //1,2,3 where 1 is high priority
    private String dateCreated;
    private String deadline;

    public Task(String text){ //bare bones task creation constructor
        taskText = text;
        tags = new HashSet<Tag>();
        priority = -1;
        setDateCreated();
        taskID = taskCounter;
        taskCounter++;
    }

    public Task(String text, int priority){
        taskText = text;
        this.priority = priority;
        setDateCreated();
        taskID = taskCounter;
        taskCounter++;
    }

    //Getters
    public String getTaskText(){ return this.taskText; }

    public int getPriority(){ return this.priority;}

    public HashSet<Tag> getAllTags(){
        return tags;
    }

    //Setters
    public void setTaskText(String taskText) {
        this.taskText = taskText;
    }

    public void setPriority(int newPriority){
        this.priority = newPriority;
    }

    //instance methods
    public void addTag(Tag newTag){
        tags.add(newTag);
    }//end addTag method

    public boolean searchTags(Tag searchTag){
       return tags.contains(searchTag);
    }//end searchTags method

    private void setDateCreated(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy HH:mm:ss");
        Date date = new Date();
        String dateCreated = dateFormat.format(date);

        this.dateCreated = dateCreated;
    }

    public void setDeadline(){
        //figure out later
    }

    public int getTaskID(){
        return this.taskID;
    }

    public int getNumTags(){
        return tags.size();
    }

    public boolean equals(Object object){
        boolean equalTo = false;

        if (object instanceof Task){
            if (this.taskText.equals(((Task) object).getTaskText()) && this.taskID == ((Task) object).getTaskID()){
                equalTo = true;
            }
        }

        return equalTo;
    }
}
