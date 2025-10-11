/*
 * COPYRIGHT 2020 -2024 original authors
 * mailto:bitnomio-backend@bitnomio.es
 *
 * bitnomio-spectrum - Created by bitnomio@bitnomio
 * Date: 4/11/24 Time: 09:27
 *
 */
package es.bitnomio.utilities.persistence.redis;

import es.bitnomio.utilities.constants.AppConfig;
import es.bitnomio.utilities.utils.JsonUtils;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import io.micronaut.cache.DefaultCacheManager;
import io.micronaut.configuration.lettuce.cache.RedisCache;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;


/**
 * <h3>Cache Utility for Redis</h3>
 * Utility class for interacting with Redis cache.
 */
@Singleton
public class CacheRedisUtils {

  private static final Logger log = LoggerFactory.getLogger(CacheRedisUtils.class.getName());

  private final StatefulRedisConnection<String, Object> connection;

  private final DefaultCacheManager<RedisCache> cacheManager;

  public CacheRedisUtils(StatefulRedisConnection<String, Object> connection,
      DefaultCacheManager<RedisCache> cacheManager) {
    this.connection = connection;
    this.cacheManager = cacheManager;
  }

  /**
   * Evaluates if key exists based on get response
   *
   * @param key the needle
   * @return true if exists, false if dont
   */
  public Boolean keyExist(String key) {
    return syncConn().get(key) != null;
  }

  /**
   * Evaluates if key exists in a folder and evaluate get response
   *
   * @param folder the haystack
   * @param key    the needle
   * @return true if exists, false if dont
   */
  public Boolean keyExistsInFolder(String folder, String key) {

    return syncConn().get(getFolderKey(folder, key)) != null;
  }

  /**
   * Retrieves a value from the cache
   *
   * @param key the needle
   * @return value from haystack
   */
  public Optional<Object> getData(String key) {

    return Optional.ofNullable(syncConn().get(key));

  }

  /**
   * Retrieves a value from a folder in the cache
   *
   * @param folder haystack
   * @param key    the needle
   * @return value from haystack
   */
  public Optional<Object>  getDataFromFolder(String folder, String key) {
    return Optional.ofNullable(syncConn().get(getFolderKey(folder, key)));
  }

  /**
   * Puts a K,V pair on the cache
   *
   * @param key   the needle
   * @param value value to store
   */
  public void setData(String key, Object value) {

    asyncConn().set(key, value);

  }

  /**
   * Puts a K,V pair on the cache within a folder
   *
   * @param folder haystack
   * @param key    the needle
   * @param value  value to store
   */
  public void setDataOnFolder(String folder, String key, Object value) {

    asyncConn().set(getFolderKey(folder, key), getStringValue(value));

  }

  /**
   * Puts a K,V pair on the cache with an expiration time expressed in seconds
   *
   * @param key     the needle
   * @param value   value to store
   * @param seconds time to keep it
   */
  public void setDataWithExpiration(String key, Object value, long seconds) {

    asyncConn().setex(key, seconds, getStringValue(value));

  }

  /**
   * @param value object to stringify
   * @return a string object representation
   */
  public String getStringValue(Object value) {

    if (value instanceof String) {
      return (String) value;
    }

    return JsonUtils.objectToJson(value).orElse("");
  }

  /**
   * Removes a K,V pair from cache
   *
   * @param key the needle
   */
  public void removeData(String key) {
    if (keyExist(key)) {
      asyncConn().del(key);
    }
  }

  /**
   * Removes a K,V pair from cache within a folder
   *
   * @param folder haystack
   * @param key    the needle
   */
  public void removeDataFromFolder(String folder, String key) {
    if (keyExistsInFolder(folder, key)) {
      asyncConn().del(getFolderKey(folder, key));
    }
  }

  /**
   * @param folder  haystack
   * @param key     the needle
   * @param value   value to store
   * @param seconds time to keep it
   */
  public void setDataWithExpirationOnFolder(String folder, String key, Object value, long seconds) {

    asyncConn().setex(getFolderKey(folder, key), seconds, getStringValue(value));

  }

  /**
   * Increases the value of a field in a Redis hash.
   *
   * @param key      The key of the hash.
   * @param field    The field to increment.
   * @param increase The value to increment the field by.
   */
  public void increaseCounter(String key, String field, int increase) {

    asyncConn().hincrby(key, field, increase);

  }

  /**
   * Increases the value of a field in a Redis hash within a folder. The name of the key is constructed by concatenating
   * the folder name and key.
   *
   * @param folder   The name of the folder containing the key.
   * @param key      The key of the hash.
   * @param field    The field to increment.
   * @param increase The value to increment the field by.
   */
  public void increaseCounterOnFolder(String folder, String key, String field, int increase) {

    increaseCounter(getFolderKey(folder, key), field, increase);

  }

  /**
   * @param folder haystack
   * @param key    needle
   * @return composed value for redis to implement folder path representation
   */
  public String getFolderKey(String folder, String key) {
    return folder.concat(AppConfig.Markers.COLON).concat(key);
  }

  private RedisAsyncCommands<String, Object> asyncConn() {
    return connection.async();
  }

  private RedisCommands<String, Object> syncConn() {
    return connection.sync();
  }

  /**
   * From here methods are displayed as documentation for more complex operations we may need in the future 2021
   * [Peter]
   */
  private void argsBuilder() {
    var argsBuilder = SetArgs.Builder.ex(15).nx();
  }

  private void retrieveWithCacheManager() {

    var cache = cacheManager.getCache("cache-name");
    cache.put("key", "value");
    var getFromCache = cache.get("key", String.class);
  }


}
