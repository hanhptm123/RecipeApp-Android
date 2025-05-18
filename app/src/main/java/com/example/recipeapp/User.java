package com.example.recipeapp;

public class User {
    private int id;
    private String name;
    private String email;
    private int isBanned;
    private String avatar;
    private String banReason;

    public User(int id, String name, String email, int isBanned, String avatar, String banReason) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.isBanned = isBanned;
        this.avatar = avatar;
        this.banReason = banReason;
    }

    // Getter
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public int getIsBanned() { return isBanned; }
    public String getAvatar() { return avatar; }
    public String getBanReason() { return banReason; }

    // Setter
    public void setIsBanned(int isBanned) { this.isBanned = isBanned; }
    public void setBanReason(String banReason) { this.banReason = banReason; }
}
