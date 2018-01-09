package com.oneops.infoblox.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * WAPI {@link Ref} model tests.
 *
 * @author Suresh G
 */
class RefTest {

  private Ref refObj1;
  private Ref refObj2;
  private Ref refObj3;

  @BeforeEach
  void setUp() {
    refObj1 = Ref.of("networkview/ZG5zLm5ldHdvcmtfdmlldyQw:default");
    refObj2 = Ref.of("record:host/ZG5zLmhvc3QkLl9vc3Qx:host1.test.com/default");
    refObj3 = Ref.of("record:ptr/ZG5zLmJpbmRfcHRTAuMTAuMTAuMi5zZXJ2ZXIuaW5mby5jb20");
  }

  @Test
  void create() {
    assertNotNull(refObj1);
    assertNotNull(refObj2);
    assertNotNull(refObj3);
  }

  @Test
  void wapiType() {
    assertEquals("networkview", refObj1.wapiType());
    assertEquals("record:host", refObj2.wapiType());
    assertEquals("record:ptr", refObj3.wapiType());
  }

  @Test
  void refData() {
    assertEquals("ZG5zLm5ldHdvcmtfdmlldyQw", refObj1.refData());
    assertEquals("ZG5zLmhvc3QkLl9vc3Qx", refObj2.refData());
    assertEquals("ZG5zLmJpbmRfcHRTAuMTAuMTAuMi5zZXJ2ZXIuaW5mby5jb20", refObj3.refData());
  }

  @Test
  void names() {
    assertEquals(Collections.singletonList("default"), refObj1.names());
    assertEquals(Arrays.asList("host1.test.com", "default"), refObj2.names());
    assertEquals(Collections.emptyList(), refObj3.names());
  }
}