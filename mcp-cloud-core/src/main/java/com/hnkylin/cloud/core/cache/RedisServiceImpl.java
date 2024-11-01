package com.hnkylin.cloud.core.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisZSetCommands.Limit;
import org.springframework.data.redis.connection.RedisZSetCommands.Range;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.data.redis.core.script.RedisScript;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

public abstract class RedisServiceImpl {
    /**
     * key 前缀
     */
    protected String prefixKey;

    @Autowired
    protected RedisTemplate<String, Object> redisTemplate;
    @Resource(name = "redisTemplate")
    protected ValueOperations<String, Object> valueOps;
    @Resource(name = "redisTemplate")
    protected SetOperations<String, Object> setOps;
    @Resource(name = "redisTemplate")
    protected HashOperations<String, String, Object> hashOps;
    @Resource(name = "redisTemplate")
    protected ListOperations<String, Object> listOps;
    @Resource(name = "redisTemplate")
    protected ZSetOperations<String, Object> zSetOps;

    @PostConstruct
    public void init() {
        this.prefixKey = initPrefixKey() + ":";
    }

    protected abstract String initPrefixKey();

    protected String fixKey(String key) {
        return this.prefixKey + key;
    }

    protected List<String> fixKeys(Collection<String> otherKeys) {
        List<String> fkoks = null;
        if (otherKeys != null) {
            fkoks = new ArrayList<String>();
            for (String ok : otherKeys) {
                fkoks.add(fixKey(ok));
            }
        }
        return fkoks;
    }

    public void expirTime(String key, long timeout, TimeUnit unit) {
        valueOps.getOperations().expire(fixKey(key), timeout, unit);
    }

    public void vSet(String key, Object value) {
        valueOps.set(fixKey(key), value);
    }

    public void vSet(String key, Object value, long timeout, TimeUnit unit) {
        valueOps.set(fixKey(key), value, timeout, unit);
    }

    public Boolean vSetIfAbsent(String key, Object value) {
        return valueOps.setIfAbsent(fixKey(key), value);
    }

    public void vMultiSet(Map<String, Object> map) {
        if (map.isEmpty()) {
            return;
        }
        Map<String, Object> rawKeys = new LinkedHashMap<>(map.size());
        for (Entry<String, Object> entry : map.entrySet()) {
            rawKeys.put(fixKey(entry.getKey()), entry.getValue());
        }
        valueOps.multiSet(rawKeys);
    }

    public Boolean vMultiSetIfAbsent(Map<String, Object> map) {
        if (map.isEmpty()) {
            return true;
        }
        Map<String, Object> rawKeys = new LinkedHashMap<>(map.size());
        for (Entry<String, Object> entry : map.entrySet()) {
            rawKeys.put(fixKey(entry.getKey()), entry.getValue());
        }
        return valueOps.multiSetIfAbsent(rawKeys);
    }

    public Object vGet(String key) {
        return valueOps.get(fixKey(key));
    }

    public Boolean vDelete(String key) {
        return getOperations().delete(fixKey(key));
    }


    public Object executeLuaScript(RedisScript redisScript, List<String> keys, Object... argvs) {
        List<String> newKeys = new ArrayList<>(keys.size());
        keys.forEach(key -> {
            newKeys.add(fixKey(key));
        });

        return valueOps.getOperations().execute(redisScript, newKeys, argvs);
    }


    public Object vGetAndSet(String key, Object value) {
        return valueOps.getAndSet(fixKey(key), value);
    }

    public List<Object> vMultiGet(Collection<String> keys) {
        if (keys.isEmpty()) {
            return Collections.emptyList();
        }
        return valueOps.multiGet(fixKeys(keys));
    }

    public Long vIncrement(String key, long delta) {
        return valueOps.increment(fixKey(key), delta);
    }

    public Double vIncrement(String key, double delta) {
        return valueOps.increment(fixKey(key), delta);
    }

    public Integer vAppend(String key, String value) {
        return valueOps.append(fixKey(key), value);
    }

    public String vGet(String key, long start, long end) {
        return valueOps.get(fixKey(key), start, end);
    }

    public void vSet(String key, Object value, long offset) {
        valueOps.set(fixKey(key), value, offset);
    }

    public Long vSize(String key) {
        return valueOps.size(fixKey(key));
    }

    public Boolean vSetBit(String key, long offset, boolean value) {
        return valueOps.setBit(fixKey(key), offset, value);
    }

    public Boolean vGetBit(String key, long offset) {
        return valueOps.getBit(fixKey(key), offset);
    }

    public RedisOperations<String, Object> getOperations() {
        return valueOps.getOperations();
    }

    public Long sAdd(String key, Object... values) {
        return setOps.add(fixKey(key), values);
    }

    public Long sRemove(String key, Object... values) {
        return setOps.remove(fixKey(key), values);
    }

    public Object sPop(String key) {
        return setOps.pop(fixKey(key));
    }

    public List<Object> sPop(String key, long count) {
        return setOps.pop(fixKey(key), count);
    }

    public Boolean sMove(String key, Object value, String destKey) {
        return setOps.move(fixKey(key), value, destKey);
    }

    public Long sSize(String key) {
        return setOps.size(fixKey(key));
    }

    public Boolean sIsMember(String key, Object o) {
        return setOps.isMember(fixKey(key), o);
    }

    public Set<Object> sIntersect(String key, String otherKey) {
        return setOps.intersect(fixKey(otherKey), fixKey(otherKey));
    }

    public Set<Object> sIntersect(String key, Collection<String> otherKeys) {
        return setOps.intersect(fixKey(key), fixKeys(otherKeys));
    }

    public Long sIntersectAndStore(String key, String otherKey, String destKey) {
        return setOps.intersectAndStore(fixKey(key), fixKey(otherKey), fixKey(destKey));
    }

    public Long sIntersectAndStore(String key, Collection<String> otherKeys, String destKey) {
        return setOps.intersectAndStore(fixKey(key), fixKeys(otherKeys), fixKey(destKey));
    }

    public Set<Object> sUnion(String key, String otherKey) {
        return setOps.union(fixKey(key), fixKey(otherKey));
    }

    public Set<Object> sUnion(String key, Collection<String> otherKeys) {
        return setOps.union(fixKey(key), fixKeys(otherKeys));
    }

    public Long sUnionAndStore(String key, String otherKey, String destKey) {
        return setOps.unionAndStore(fixKey(key), fixKey(otherKey), fixKey(destKey));
    }

    public Long sUnionAndStore(String key, Collection<String> otherKeys, String destKey) {
        return setOps.unionAndStore(fixKey(key), fixKeys(otherKeys), fixKey(destKey));
    }

    public Set<Object> sDifference(String key, String otherKey) {
        return setOps.difference(fixKey(key), fixKey(otherKey));
    }

    public Set<Object> sDifference(String key, Collection<String> otherKeys) {
        return setOps.difference(fixKey(key), fixKeys(otherKeys));
    }

    public Long sDifferenceAndStore(String key, String otherKey, String destKey) {
        return setOps.differenceAndStore(fixKey(key), fixKey(otherKey), fixKey(destKey));
    }

    public Long sDifferenceAndStore(String key, Collection<String> otherKeys, String destKey) {
        return setOps.differenceAndStore(fixKey(key), fixKeys(otherKeys), destKey);
    }

    public Set<Object> sMembers(String key) {
        return setOps.members(fixKey(key));
    }

    public Object sRandomMember(String key) {
        return setOps.randomMember(fixKey(key));
    }

    public Set<Object> sDistinctRandomMembers(String key, long count) {
        return setOps.distinctRandomMembers(fixKey(key), count);
    }

    public List<Object> sRandomMembers(String key, long count) {
        return setOps.randomMembers(fixKey(key), count);
    }

    public Cursor<Object> sScan(String key, ScanOptions options) {
        return setOps.scan(fixKey(key), options);
    }

    public Long hDelete(String key, String... hashKeys) {
        String[] hks = null;
        if (hashKeys != null && hashKeys.length > 0) {
            hks = new String[hashKeys.length];
            for (int i = 0; i < hashKeys.length; i++) {
                hks[i] = fixKey(hashKeys[i]);
            }
        }
        return hashOps.delete(fixKey(key), hks);
    }

    public Boolean hHasKey(String key, String hashKey) {
        return hashOps.hasKey(fixKey(key), fixKey(hashKey));
    }

    public Object hGet(String key, String hashKey) {
        return hashOps.get(fixKey(key), fixKey(hashKey));
    }

    public List<Object> hMultiGet(String key, Collection<String> hashKeys) {
        return hashOps.multiGet(fixKey(key), fixKeys(hashKeys));
    }

    public Long hIncrement(String key, String hashKey, long delta) {
        return hashOps.increment(fixKey(key), fixKey(hashKey), delta);
    }

    public Double hIncrement(String key, String hashKey, double delta) {
        return hashOps.increment(fixKey(key), fixKey(hashKey), delta);
    }

    public Set<String> hKeys(String key) {
        return hashOps.keys(fixKey(key));
    }

    public Long hSize(String key) {
        return hashOps.size(fixKey(key));
    }

    public void hPutAll(String key, Map<String, Object> map) {
        if (map.isEmpty()) {
            return;
        }
        Map<String, Object> rawKeys = new LinkedHashMap<>(map.size());
        for (Entry<String, Object> entry : map.entrySet()) {
            rawKeys.put(fixKey(entry.getKey()), entry.getValue());
        }
        hashOps.putAll(fixKey(key), rawKeys);
    }

    public void hPut(String key, String hashKey, Object value) {
        hashOps.put(fixKey(key), fixKey(hashKey), value);
    }

    public Boolean hPutIfAbsent(String key, String hashKey, Object value) {
        return hashOps.putIfAbsent(fixKey(key), fixKey(hashKey), value);
    }

    public List<Object> hValues(String key) {
        return hashOps.values(fixKey(key));
    }

    public Map<String, Object> hEntries(String key) {
        return hashOps.entries(fixKey(key));
    }

    public Cursor<Entry<String, Object>> hScan(String key, ScanOptions options) {
        return hashOps.scan(fixKey(key), options);
    }

    public List<Object> lRange(String key, long start, long end) {
        return listOps.range(fixKey(key), start, end);
    }

    public void lTrim(String key, long start, long end) {
        listOps.trim(fixKey(key), start, end);
    }

    public Long lSize(String key) {
        return listOps.size(fixKey(key));
    }

    public Long lLeftPush(String key, Object value) {
        return listOps.leftPush(fixKey(key), value);
    }

    public Long lLeftPushAll(String key, Object... values) {
        return listOps.leftPushAll(fixKey(key), values);
    }

    public Long lLeftPushAll(String key, Collection<Object> values) {
        return listOps.leftPushAll(fixKey(key), values);
    }

    public Long lLeftPushIfPresent(String key, Object value) {
        return listOps.leftPushIfPresent(fixKey(key), value);
    }

    public Long lLeftPush(String key, Object pivot, Object value) {
        return listOps.leftPush(fixKey(key), pivot, value);
    }

    public Long lRightPush(String key, Object value) {
        return listOps.rightPush(fixKey(key), value);
    }

    public Long lRightPushAll(String key, Object... values) {
        return listOps.rightPushAll(fixKey(key), values);
    }

    public Long lRightPushAll(String key, Collection<Object> values) {
        return listOps.rightPushAll(fixKey(key), values);
    }

    public Long lRightPushIfPresent(String key, Object value) {
        return listOps.rightPushIfPresent(fixKey(key), value);
    }

    public Long lRightPush(String key, Object pivot, Object value) {
        return listOps.rightPush(fixKey(key), pivot, value);
    }

    public void lSet(String key, long index, Object value) {
        listOps.set(fixKey(key), index, value);
    }

    public Long lRemove(String key, long count, Object value) {
        return listOps.remove(fixKey(key), count, value);
    }

    public Object lIndex(String key, long index) {
        return listOps.index(fixKey(key), index);
    }

    public Object lLeftPop(String key) {
        return listOps.leftPop(fixKey(key));
    }

    public Object lLeftPop(String key, long timeout, TimeUnit unit) {
        return listOps.leftPop(fixKey(key), timeout, unit);
    }

    public Object lRightPop(String key) {
        return listOps.rightPop(fixKey(key));
    }

    public Object lRightPop(String key, long timeout, TimeUnit unit) {
        return listOps.rightPop(fixKey(key), timeout, unit);
    }

    public Object lRightPopAndLeftPush(String sourceKey, String destinationKey) {
        return listOps.rightPopAndLeftPush(fixKey(sourceKey), fixKey(destinationKey));
    }

    public Object lRightPopAndLeftPush(String sourceKey, String destinationKey, long timeout, TimeUnit unit) {
        return listOps.rightPopAndLeftPush(fixKey(sourceKey), fixKey(destinationKey), timeout, unit);
    }

    public Boolean zAdd(String key, Object value, double score) {
        return zSetOps.add(fixKey(key), value, score);
    }

    public Long zAdd(String key, Set<TypedTuple<Object>> tuples) {
        return zSetOps.add(fixKey(key), tuples);
    }

    public Long zRemove(String key, Object... values) {
        return zSetOps.remove(fixKey(key), values);
    }

    public Double zIncrementScore(String key, Object value, double delta) {
        return zSetOps.incrementScore(fixKey(key), value, delta);
    }

    public Long zRank(String key, Object o) {
        return zSetOps.rank(fixKey(key), o);
    }

    public Long zReverseRank(String key, Object o) {
        return zSetOps.reverseRank(fixKey(key), o);
    }

    public Set<Object> zRange(String key, long start, long end) {
        return zSetOps.range(fixKey(key), start, end);
    }

    public Set<TypedTuple<Object>> zRangeWithScores(String key, long start, long end) {
        return zSetOps.rangeWithScores(fixKey(key), start, end);
    }

    public Set<Object> zRangeByScore(String key, double min, double max) {
        return zSetOps.rangeByScore(fixKey(key), min, max);
    }

    public Set<TypedTuple<Object>> zRangeByScoreWithScores(String key, double min, double max) {
        return zSetOps.rangeByScoreWithScores(fixKey(key), min, max);
    }

    public Set<Object> zRangeByScore(String key, double min, double max, long offset, long count) {
        return zSetOps.rangeByScore(fixKey(key), min, max, offset, count);
    }

    public Set<TypedTuple<Object>> zRangeByScoreWithScores(String key, double min, double max, long offset,
                                                           long count) {
        return zSetOps.rangeByScoreWithScores(fixKey(key), min, max, offset, count);
    }

    public Set<Object> zReverseRange(String key, long start, long end) {
        return zSetOps.reverseRange(fixKey(key), start, end);
    }

    public Set<TypedTuple<Object>> zReverseRangeWithScores(String key, long start, long end) {
        return zSetOps.reverseRangeWithScores(fixKey(key), start, end);
    }

    public Set<Object> zReverseRangeByScore(String key, double min, double max) {
        return zSetOps.reverseRangeByScore(fixKey(key), min, max);
    }

    public Set<TypedTuple<Object>> zReverseRangeByScoreWithScores(String key, double min, double max) {
        return zSetOps.reverseRangeByScoreWithScores(fixKey(key), min, max);
    }

    public Set<Object> zReverseRangeByScore(String key, double min, double max, long offset, long count) {
        return zSetOps.reverseRangeByScore(fixKey(key), min, max, offset, count);
    }

    public Set<TypedTuple<Object>> zReverseRangeByScoreWithScores(String key, double min, double max, long offset,
                                                                  long count) {
        return zSetOps.reverseRangeByScoreWithScores(fixKey(key), min, max, offset, count);
    }

    public Long zCount(String key, double min, double max) {
        return zSetOps.count(fixKey(key), min, max);
    }

    public Long zSize(String key) {
        return zSetOps.size(fixKey(key));
    }

    public Long zZCard(String key) {
        return zSetOps.zCard(fixKey(key));
    }

    public Double zScore(String key, Object o) {
        return zSetOps.score(fixKey(key), o);
    }

    public Long zRemoveRange(String key, long start, long end) {
        return zSetOps.removeRange(fixKey(key), start, end);
    }

    public Long zRemoveRangeByScore(String key, double min, double max) {
        return zSetOps.removeRangeByScore(fixKey(key), min, max);
    }

    public Long zUnionAndStore(String key, String otherKey, String destKey) {
        return zSetOps.unionAndStore(fixKey(key), fixKey(otherKey), fixKey(destKey));
    }

    public Long zUnionAndStore(String key, Collection<String> otherKeys, String destKey) {
        return zSetOps.unionAndStore(fixKey(key), fixKeys(otherKeys), fixKey(destKey));
    }

    public Long zIntersectAndStore(String key, String otherKey, String destKey) {
        return zSetOps.unionAndStore(fixKey(key), fixKey(otherKey), fixKey(destKey));
    }

    public Long zIntersectAndStore(String key, Collection<String> otherKeys, String destKey) {
        return zSetOps.intersectAndStore(fixKey(key), fixKeys(otherKeys), fixKey(destKey));
    }

    public Cursor<TypedTuple<Object>> zScan(String key, ScanOptions options) {
        return zSetOps.scan(fixKey(key), options);
    }

    public Set<Object> zRangeByLex(String key, Range range) {
        return zSetOps.rangeByLex(fixKey(key), range);
    }

    public Set<Object> zRangeByLex(String key, Range range, Limit limit) {
        return zSetOps.rangeByLex(fixKey(key), range, limit);
    }

}
