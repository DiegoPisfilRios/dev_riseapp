package models;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Map;

import API.ServerHelper;

public class PublicModel {

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

    public PublicModel(String _id, String user,String ago ,String description, String file, int nlikes, JSONArray likes, int ncomments, JSONArray comments) {
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

    public PublicModel(Context context){
        this.context = context;
        serverHelper = new ServerHelper(context);
    }

    public Context getContext (){
        return context;
    }

    public int getNlikes() {
        return nlikes;
    }

    public int getNcomments() {
        return ncomments;
    }

    public String getDescription() {
        return description;
    }

    public String getUser() {
        return user;
    }

    public String getFile() {
        return file;
    }

    public String get_id() {
        return _id;
    }

    public JSONArray getLikes() {
        return likes;
    }

    public JSONArray getComments() {
        return comments;
    }

    public boolean isSt_like() {
        return st_like;
    }

    public String getAgo() {
        return ago;
    }

    public void setAgo(String ago) {
        this.ago = ago;
    }

    public void setSt_like(boolean st_like) {
        this.st_like = st_like;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public void findPublic(String _id, final ServerHelper.ServerCallback callback){
        serverHelper.Request(serverHelper.GET,"/publics/"+_id, null, callback);
    }

    public void findMyPublics(final ServerHelper.ServerCallback callback){
        serverHelper.Request(serverHelper.GET,"/publics/my", null, callback);
    }

    public void findAllPublics( int page, int limit, final ServerHelper.ServerCallback callback){
        serverHelper.Request(serverHelper.GET,"/publics?limit="+limit+"&page="+page,null, callback);
    }

    public void findSearchPublics(String search, int page, int limit, final ServerHelper.ServerCallback callback){
        serverHelper.Request(serverHelper.GET,"/publics?search="+search+"&limit="+limit+"&page="+page,null, callback);
    }

    public void findUserPublics(String _id, final ServerHelper.ServerCallback callback){
        serverHelper.Request(serverHelper.GET,"/publics/user/"+_id, null, callback);
    }

    public void postPublic(Map<String, String> map, final ServerHelper.ServerCallback callback){
        try {
            JSONObject data = new JSONObject("{" +
                    "\"text\":" + "\"" + map.get("text") + "\"," +
                    "\"file\":" + "\"" + map.get("file") + "\"" +
                    "}");

            System.out.println("POST:"+data);
            serverHelper.Request(serverHelper.POST,"/publics", data, callback);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getInJSONOject(){
        JSONObject object = null;
        try {
            object = new JSONObject();
            object.put("_id",get_id());
            object.put("user",getUser());
            object.put("description",getDescription());
            object.put("file",getFile());
            object.put("nlikes",getNlikes());
            object.put("likes",getLikes());
            object.put("ncomments",getNcomments());
            object.put("comments",getComments());
            object.put("st_like",isSt_like());
            object.put("ago",getAgo());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    public void putLikePublic(String _id, final ServerHelper.ServerCallback callback){
        serverHelper.Request(serverHelper.PUT,"/publics/"+_id, null, callback);
    }

    public void putCommentPublic(String _id, JSONObject data, final ServerHelper.ServerCallback callback){
        serverHelper.Request(serverHelper.PUT,"/publics/"+_id, data, callback);
    }

    public void findComments(String _id,final ServerHelper.ServerCallback callback){
        serverHelper.Request(serverHelper.GET,"/publics/comment/"+_id, null, callback);
    }

    public void findFavorite(int page, int limit, final ServerHelper.ServerCallback callback){
        serverHelper.Request(serverHelper.GET,"/favorites?limit="+limit+"&page="+page,null, callback);
    }

    public void setUser(String user) {
        this.user = user;
    }
}
