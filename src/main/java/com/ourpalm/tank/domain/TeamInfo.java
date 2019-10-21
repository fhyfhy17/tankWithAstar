package com.ourpalm.tank.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by g on 2016/6/2.
 */
public class TeamInfo {
    private String team_name;
    private List<UserInfo> user_infos;

    public TeamInfo() {
        this.user_infos = new ArrayList<>();
    }

    public String getTeam_name() {
        return team_name;
    }

    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }

    public List<UserInfo> getUser_infos() {
        return user_infos;
    }

    public void setUser_infos(List<UserInfo> user_infos) {
        this.user_infos = user_infos;
    }

    public void addUser(UserInfo user) {
        this.user_infos.add(user);
    }

}
