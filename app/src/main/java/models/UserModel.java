package models;

import android.content.Context;
import org.json.JSONObject;
import API.ServerHelper;
import utilities.StorageLocal;

public class UserModel extends StorageLocal {

    private Context context;
    private String email;
    private String name;
    private String surname;
    private String password;
    private String birth;
    private String avatar;

    private ServerHelper serverHelper;

    public UserModel(Context context, String email, String password, String name, String surname, String birth){
        super(context);
        setEmail(email);
        setName(name);
        setSurname(surname);
        setPassword(password);
        setBirth(birth);
        setContext(context);
        serverHelper = new ServerHelper(context);
    }

    public UserModel(Context con){
        super(con);
        setContext(con);
        serverHelper = new ServerHelper(context);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**-------------------------------------------------------------------------*/
    public void MyData(final ServerHelper.ServerCallback callback){
        serverHelper.Request(serverHelper.GET,"/users/my", null, callback);
    }

    public void signUp(JSONObject data,final ServerHelper.ServerCallback callback){
        serverHelper.Request(serverHelper.POST, "/signup", data, callback);
    }

    public void signIn(JSONObject data,  final ServerHelper.ServerCallback callback){
        serverHelper.Request(serverHelper.POST,"/signin", data, callback);
    }

    public void LogOutUser(){
        writeLocalData("app_token", "");
    }

    public void findUser(String _id, final ServerHelper.ServerCallback callback){
        serverHelper.Request(serverHelper.GET,"/users/"+_id, null, callback);
    }

    public void findUsers(JSONObject data, final ServerHelper.ServerCallback callback){
        serverHelper.Request(serverHelper.POST,"/users", data, callback);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void putUser(JSONObject data, final ServerHelper.ServerCallback callback){
        serverHelper.Request(serverHelper.PUT,"/users", data, callback);
    }

}
