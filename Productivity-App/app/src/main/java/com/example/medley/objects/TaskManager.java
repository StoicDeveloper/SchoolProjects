package com.example.medley.objects;

import java.util.HashSet;
import java.util.Iterator;

public class TaskManager {
    private HashSet<Task> masterListTasks;
    private HashSet<Tag> masterListTags;

    public TaskManager(){
        this.masterListTasks = new HashSet<Task>();
        this.masterListTags = new HashSet<Tag>();
    }

    public void addTask(Task task){
        masterListTasks.add(task);
        masterListTags.addAll(task.getAllTags());
    }

    public void addTag(Tag tag){
        masterListTags.add(tag);
    }

    public void addTagToTask(Task task, Tag tag) {
        task.addTag(tag);
        masterListTags.add(tag);
    }

    public HashSet<Task> getAllTasksWithTag(Tag tag){
        HashSet<Task> returnSet = new HashSet<Task>();
        Task curTask;

        // Create an iterator to iterate over the master list of tasks
        Iterator it = masterListTasks.iterator();

        while (it.hasNext()) {
            curTask = (Task)it.next();

            if (curTask.searchTags(tag)){
                returnSet.add(curTask);
            }
        }

        return returnSet;
    }

    public HashSet<Tag> getAllTagsFromTask(Task task){
        return task.getAllTags();
    }

    public HashSet<Tag> getAllTagsFromTask(int taskID){
        Iterator it = masterListTasks.iterator();
        Task curTask;
        HashSet<Tag> returnSet = new HashSet<Tag>();

        while (it.hasNext()) {
            curTask = (Task)it.next();

            if (curTask.getTaskID() == taskID){
                returnSet.addAll(curTask.getAllTags());
            }
        }

        return returnSet;
    }

}
