package com.ourpalm.tank.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.constant.RankEnum;
import com.ourpalm.tank.dao.SeasonRankDao;
import com.ourpalm.tank.domain.AreaIp;
import com.ourpalm.tank.domain.RankRoleIdAndScore;

import redis.clients.jedis.Tuple;

public class SeasonRankDaoImpl extends AbstractJedisDao implements SeasonRankDao {

	@Override
	public void saveRank(int roleid, int score, RankEnum type, int server) {
		getClient().zadd(type.getKey(server), score, String.valueOf(roleid));
	}

	@Override
	public void delRank(int roleId, RankEnum type, int server) {
		getClient().zrem(type.getKey(server), String.valueOf(roleId));
	}

	@Override
	public List<Integer> getRanks(int start, int end, RankEnum type, int server) {
		List<Integer> result = new ArrayList<>();
		Set<String> roles = getClient().zrevrange(type.getKey(server), start, end);
		if (Util.isEmpty(roles))
			return result;

		for (String strId : roles) {
			result.add(Integer.parseInt(strId));
		}
		return result;
	}

	@Override
	public List<RankRoleIdAndScore> getRanksScoreAndRank(int start, int end, RankEnum type, int server) {
		List<RankRoleIdAndScore> result = new ArrayList<>();
		Set<Tuple> roles = getClient().zrevrangeWithScores(type.getKey(server), start, end);
		if (Util.isEmpty(roles))
			return result;
		for (Tuple tuple : roles) {
			RankRoleIdAndScore r = new RankRoleIdAndScore();
			r.setRoleId(Integer.parseInt(tuple.getElement()));
			r.setScore((int) tuple.getScore());
			result.add(r);
		}

		return result;
	}

	@Override
	public void clearRank(RankEnum type, int server) {
		getClient().del(type.getKey(server));
	}

	@Override
	public int getRoleRank(int roleId, RankEnum type, int areaId) {
		Long id = getClient().zrevrank(type.getKey(areaId), String.valueOf(roleId));
		if (id == null)
			return -1;
		return id.intValue();
	}

	@Override
	public int getRoleScore(int roleId, RankEnum type, int areaId) {
		Double id = getClient().zscore(type.getKey(areaId), String.valueOf(roleId));
		if (id == null)
			return 0;
		return id.intValue();
	}

	@Override
	public Map<Integer, AreaIp> getAllIp() {
		Map<Integer, AreaIp> result = new HashMap<Integer, AreaIp>();
		Map<String, String> map = getClient().hgetAll("AREA_IP_LIST_KEY");
		if (Util.isEmpty(map)) {
			return result;
		}
		for (Map.Entry<String, String> entry : map.entrySet()) {
			if (Util.isEmpty(entry.getValue())) {
				continue;
			}
			result.put(Integer.valueOf(entry.getKey()), JSON.parseObject(entry.getValue(), AreaIp.class));
		}

		return result;
	}

}
