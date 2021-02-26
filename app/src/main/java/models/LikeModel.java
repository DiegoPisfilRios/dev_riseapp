package models;

public class LikeModel {

    private String name;
    private String avatar;
    private boolean state;

    public LikeModel(String name, String avatar, boolean state) {
        this.name = name;
        this.avatar = avatar;
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public String getAvatar() {
        return avatar;
    }

    public boolean isState() {
        return state;
    }
}
