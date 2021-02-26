package models;

public class CommentModel {
    private String name;
    private String surname;
    private String avatar;
    private String comment;

    public CommentModel(String name, String surname, String avatar, String comment) {
        this.name = name;
        this.surname = surname;
        this.avatar = avatar;
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getComment() {
        return comment;
    }

}
