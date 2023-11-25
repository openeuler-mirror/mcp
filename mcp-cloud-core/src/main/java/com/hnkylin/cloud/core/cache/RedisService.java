package com.hnkylin.cloud.core.cache;

import org.springframework.data.redis.connection.RedisZSetCommands.Limit;
import org.springframework.data.redis.connection.RedisZSetCommands.Range;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface RedisService {


    void expirTime(String key, long timeout, TimeUnit unit);

    // ---------------value ops-------------
    void vSet(String key, Object value);

    void vSet(String key, Object value, long timeout, TimeUnit unit);

    @Nullable
    Boolean vSetIfAbsent(String key, Object value);

    void vMultiSet(Map<String, Object> map);

    @Nullable
    Boolean vMultiSetIfAbsent(Map<String, Object> map);

    @Nullable
    Object vGet(String key);

    @Nullable
    Boolean vDelete(String key);

    @Nullable
    Object vGetAndSet(String key, Object value);

    @Nullable
    List<Object> vMultiGet(Collection<String> keys);

    @Nullable
    Long vIncrement(String key, long delta);

    @Nullable
    Double vIncrement(String key, double delta);

    @Nullable
    Integer vAppend(String key, String value);

    @Nullable
    String vGet(String key, long start, long end);

    void vSet(String key, Object value, long offset);

    @Nullable
    Long vSize(String key);

    @Nullable
    Boolean vSetBit(String key, long offset, boolean value);

    @Nullable
    Boolean vGetBit(String key, long offset);

    RedisOperations<String, Object> getOperations();

    Object executeLuaScript(RedisScript redisScript, List<String> keys, Object... argvs);

    // ----------------Set ops----------------------
    @Nullable
    Long sAdd(String key, Object... values);

    @Nullable
    Long sRemove(String key, Object... values);

    @Nullable
    Object sPop(String key);

    @Nullable
    List<Object> sPop(String key, long count);

    @Nullable
    Boolean sMove(String key, Object value, String destKey);

    @Nullable
    Long sSize(String key);

    @Nullable
    Boolean sIsMember(String key, Object o);

    @Nullable
    Set<Object> sIntersect(String key, String otherKey);

    @Nullable
    Set<Object> sIntersect(String key, Collection<String> otherKeys);

    @Nullable
    Long sIntersectAndStore(String key, String otherKey, String destKey);

    @Nullable
    Long sIntersectAndStore(String key, Collection<String> otherKeys, String destKey);

    @Nullable
    Set<Object> sUnion(String key, String otherKey);

    @Nullable
    Set<Object> sUnion(String key, Collection<String> otherKeys);

    @Nullable
    Long sUnionAndStore(String key, String otherKey, String destKey);

    @Nullable
    Long sUnionAndStore(String key, Collection<String> otherKeys, String destKey);

    @Nullable
    Set<Object> sDifference(String key, String otherKey);

    @Nullable
    Set<Object> sDifference(String key, Collection<String> otherKeys);

    @Nullable
    Long sDifferenceAndStore(String key, String otherKey, String destKey);

    @Nullable
    Long sDifferenceAndStore(String key, Collection<String> otherKeys, String destKey);

    @Nullable
    Set<Object> sMembers(String key);

    Object sRandomMember(String key);

    @Nullable
    Set<Object> sDistinctRandomMembers(String key, long count);

    @Nullable
    List<Object> sRandomMembers(String key, long count);

    Cursor<Object> sScan(String key, ScanOptions options);

    // ------------------------hash-----------------------
    Long hDelete(String key, String... hashKeys);

    Boolean hHasKey(String key, String hashKey);

    @Nullable
    Object hGet(String key, String hashKey);

    List<Object> hMultiGet(String key, Collection<String> hashKeys);

    Long hIncrement(String key, String hashKey, long delta);

    Double hIncrement(String key, String hashKey, double delta);

    @Deprecated
    Set<String> hKeys(String key);

    Long hSize(String key);

    void hPutAll(String key, Map<String, Object> m);

    void hPut(String key, String hashKey, Object value);

    Boolean hPutIfAbsent(String key, String hashKey, Object value);

    List<Object> hValues(String key);

    Map<String, Object> hEntries(String key);

    Cursor<Map.Entry<String, Object>> hScan(String key, ScanOptions options);
    // ---------------------List ops-----------------------

    @Nullable
    List<Object> lRange(String key, long start, long end);

    void lTrim(String key, long start, long end);

    @Nullable
    Long lSize(String key);

    @Nullable
    Long lLeftPush(String key, Object value);

    @Nullable
    Long lLeftPushAll(String key, Object... values);

    @Nullable
    Long lLeftPushAll(String key, Collection<Object> values);

    @Nullable
    Long lLeftPushIfPresent(String key, Object value);

    @Nullable
    Long lLeftPush(String key, Object pivot, Object value);

    @Nullable
    Long lRightPush(String key, Object value);

    @Nullable
    Long lRightPushAll(String key, Object... values);

    @Nullable
    Long lRightPushAll(String key, Collection<Object> values);

    @Nullable
    Long lRightPushIfPresent(String key, Object value);

    @Nullable
    Long lRightPush(String key, Object pivot, Object value);

    void lSet(String key, long index, Object value);

    @Nullable
    Long lRemove(String key, long count, Object value);

    @Nullable
    Object lIndex(String key, long index);

    @Nullable
    Object lLeftPop(String key);

    @Nullable
    Object lLeftPop(String key, long timeout, TimeUnit unit);

    @Nullable
    Object lRightPop(String key);

    @Nullable
    Object lRightPop(String key, long timeout, TimeUnit unit);

    @Nullable
    Object lRightPopAndLeftPush(String sourceKey, String destinationKey);

    @Nullable
    Object lRightPopAndLeftPush(String sourceKey, String destinationKey, long timeout, TimeUnit unit);

    // ---------------------zset ops----------------------
    @Nullable
    Boolean zAdd(String key, Object value, double score);

    @Nullable
    Long zAdd(String key, Set<TypedTuple<Object>> tuples);

    @Nullable
    Long zRemove(String key, Object... values);

    @Nullable
    Double zIncrementScore(String key, Object value, double delta);

    @Nullable
    Long zRank(String key, Object o);

    @Nullable
    Long zReverseRank(String key, Object o);

    @Nullable
    Set<Object> zRange(String key, long start, long end);

    @Nullable
    Set<TypedTuple<Object>> zRangeWithScores(String key, long start, long end);

    @Nullable
    Set<Object> zRangeByScore(String key, double min, double max);

    @Nullable
    Set<TypedTuple<Object>> zRangeByScoreWithScores(String key, double min, double max);

    @Nullable
    Set<Object> zRangeByScore(String key, double min, double max, long offset, long count);

    @Nullable
    Set<TypedTuple<Object>> zRangeByScoreWithScores(String key, double min, double max, long offset, long count);

    @Nullable
    Set<Object> zReverseRange(String key, long start, long end);

    @Nullable
    Set<TypedTuple<Object>> zReverseRangeWithScores(String key, long start, long end);

    @Nullable
    Set<Object> zReverseRangeByScore(String key, double min, double max);

    @Nullable
    Set<TypedTuple<Object>> zReverseRangeByScoreWithScores(String key, double min, double max);

    @Nullable
    Set<Object> zReverseRangeByScore(String key, double min, double max, long offset, long count);

    @Nullable
    Set<TypedTuple<Object>> zReverseRangeByScoreWithScores(String key, double min, double max, long offset, long count);

    @Nullable
    Long zCount(String key, double min, double max);

    @Nullable
    Long zSize(String key);

    @Nullable
    Long zZCard(String key);

    @Nullable
    Double zScore(String key, Object o);

    @Nullable
    Long zRemoveRange(String key, long start, long end);

    @Nullable
    Long zRemoveRangeByScore(String key, double min, double max);

    @Nullable
    Long zUnionAndStore(String key, String otherKey, String destKey);

    @Nullable
    Long zUnionAndStore(String key, Collection<String> otherKeys, String destKey);

    @Nullable
    Long zIntersectAndStore(String key, String otherKey, String destKey);

    @Nullable
    Long zIntersectAndStore(String key, Collection<String> otherKeys, String destKey);

    Cursor<TypedTuple<Object>> zScan(String key, ScanOptions options);

    @Nullable
    Set<Object> zRangeByLex(String key, Range range);

    @Nullable
    Set<Object> zRangeByLex(String key, Range range, Limit limit);
}
