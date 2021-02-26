package models;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import API.ServerHelper;

public class PostModel {

    private String _id;
    private String user;
    private String description;
    private String file;
    private int nlikes;
    private JSONArray likes;
    private int ncomments;
    private JSONArray comments;
    private boolean st_like;
    private String ago;
    private ServerHelper serverHelper;
    private Context context;


    public PostModel(String _id, String file, String description, int nlikes, int ncomments) {
        this._id = _id;
        this.description = description;
        this.file = file;
        this.nlikes = nlikes;
        this.ncomments = ncomments;
    }

    public PostModel(String _id, String user,String ago ,String description, String file, int nlikes, JSONArray likes, int ncomments, JSONArray comments) {
        this._id = _id;
        this.user = user;
        this.description = description;
        this.file = file;
        this.nlikes = nlikes;
        this.likes = likes;
        this.ncomments = ncomments;
        this.comments = comments;
        this.ago = ago;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public int getNlikes() {
        return nlikes;
    }

    public void setNlikes(int nlikes) {
        this.nlikes = nlikes;
    }

    public int getNcomments() {
        return ncomments;
    }

    public void setNcomments(int ncomments) {
        this.ncomments = ncomments;
    }

    public JSONObject getInJSONOject(){
        JSONObject object = null;
        try {
            object = new JSONObject();
            object.put("_id",get_id());
            object.put("user",user);
            object.put("description",getDescription());
            object.put("file",getFile());
            object.put("nlikes",getNlikes());
            object.put("likes",likes);
            object.put("ncomments",getNcomments());
            object.put("comments",comments);
            object.put("st_like",st_like);
            object.put("ago",ago);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }
}
