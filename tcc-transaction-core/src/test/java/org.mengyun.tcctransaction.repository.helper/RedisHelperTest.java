package org.mengyun.tcctransaction.repository.helper;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.powermock.api.mockito.PowerMockito;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisHelperTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();

  @Test
  public void testGetRedisKey() {
    Assert.assertArrayEquals(new byte[] {(byte)58, (byte)58},
        RedisHelper.getRedisKey("", ":", ""));

    Assert.assertArrayEquals(new byte[] {65, 71, 71, 103,
        108, 111, 98, 97, 108, 84, 114, 97, 110, 115, 97, 99, 116,
            105, 111, 110, 73, 100, 58, 44, 98, 114, 97, 110, 99,
                104, 81, 117, 97, 108, 105, 102, 105, 101, 114, 58},
                    RedisHelper.getRedisKey("AGG", "", ""));
  }

  @Test
  public void testGetRedisKeyThrowsException() {
    thrown.expect(NullPointerException.class);
    RedisHelper.getRedisKey("foo", null);
  }

  @Test
  public void testGetVersionKey() {
    Assert.assertArrayEquals(new byte[]{86, 69, 82, 58, 65, 71, 71, 58},
            RedisHelper.getVersionKey("AGG", "", ""));
  }

  @Test
  public void testGetVersionKeyThrowsException() {
    thrown.expect(NullPointerException.class);
    RedisHelper.getVersionKey("foo", null);
  }

  @Test
  public void testIsSupportScanCommand() {
    Assert.assertTrue(RedisHelper.isSupportScanCommand(
            PowerMockito.mock(Jedis.class)));
  }

  @Test
  public void testIsSupportScanCommandThrowsException1() {
    thrown.expect(NullPointerException.class);
    RedisHelper.isSupportScanCommand(PowerMockito.mock(JedisPool.class));
  }

  @Test
  public void testIsSupportScanCommandThrowsException2() {
    thrown.expect(NullPointerException.class);
    RedisHelper.isSupportScanCommand((JedisPool) null);
  }
}
