package com.ourpalm.tank.domain;

import java.util.Map;

/**
 * Created by g on 2016/6/2.
 */
public class SyncMatchBody {
    private String match_id;
    private String match_name;
    private String game_mode;
    private String game_map;
    private String game_zone;
    private int start_time;
    private int expire_time;
    private TeamInfo team_red;
    private TeamInfo team_blue;
    private Map<String, String> extra;

    public String getMatch_id() {
        return match_id;
    }

    public void setMatch_id(String match_id) {
        this.match_id = match_id;
    }

    public String getMatch_name() {
        return match_name;
    }

    public void setMatch_name(String match_name) {
        this.match_name = match_name;
    }

    public String getGame_mode() {
        return game_mode;
    }

    public void setGame_mode(String game_mode) {
        this.game_mode = game_mode;
    }

    public String getGame_map() {
        return game_map;
    }

    public void setGame_map(String game_map) {
        this.game_map = game_map;
    }

    public String getGame_zone() {
        return game_zone;
    }

    public void setGame_zone(String game_zone) {
        this.game_zone = game_zone;
    }

    public int getStart_time() {
        return start_time;
    }

    public void setStart_time(int start_time) {
        this.start_time = start_time;
    }

    public int getExpire_time() {
        return expire_time;
    }

    public void setExpire_time(int expire_time) {
        this.expire_time = expire_time;
    }

    public TeamInfo getTeam_red() {
        return team_red;
    }

    public void setTeam_red(TeamInfo team_red) {
        this.team_red = team_red;
    }

    public TeamInfo getTeam_blue() {
        return team_blue;
    }

    public void setTeam_blue(TeamInfo team_blue) {
        this.team_blue = team_blue;
    }

    public Map<String, String> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, String> extra) {
        this.extra = extra;
    }
}
