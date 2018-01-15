package com.oneops.infoblox.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * WAPI {@link Ref} model tests.
 *
 * @author Suresh G
 */
@DisplayName("Infoblox WAPI Object reference tests.")
class RefTest {

  private Ref netViewRef;
  private Ref hostRef;
  private Ref ipv4Ref;
  private Ref ptrRef;

  @BeforeEach
  void setUp() {
    netViewRef = Ref.of("networkview/ZG5zLm5ldHdvcmtfdmlldyQw:default");
    hostRef = Ref.of("record:host/ZG5zLmhvc10ZXN0LWhvc3Qx:test-host1.oneops.com/Internal");
    ptrRef = Ref.of("record:ptr/ZG5zLmJpbmRfcHRTAuMTAuMTAuMi5zZXJ2ZXIuaW5mby5jb20");
    ipv4Ref =
        Ref.of(
            "record:host_ipv4addr/YWRkcmVzcyQuX2RlZmF1bHQuY29tLndhbG1hcnQub25lb3BzLXRl:10.10.10.20/test-host1.oneops.com/Internal");
  }

  @Test
  void create() {
    assertNotNull(netViewRef);
    assertNotNull(hostRef);
    assertNotNull(ipv4Ref);
    assertNotNull(ptrRef);
  }

  @Test
  void wapiType() {
    assertEquals("networkview", netViewRef.wapiType());
    assertEquals("record:host", hostRef.wapiType());
    assertEquals("record:ptr", ptrRef.wapiType());
    assertEquals("record:host_ipv4addr", ipv4Ref.wapiType());
  }

  @Test
  void refData() {
    assertEquals("ZG5zLm5ldHdvcmtfdmlldyQw", netViewRef.refData());
    assertEquals("ZG5zLmhvc10ZXN0LWhvc3Qx", hostRef.refData());
    assertEquals("ZG5zLmJpbmRfcHRTAuMTAuMTAuMi5zZXJ2ZXIuaW5mby5jb20", ptrRef.refData());
    assertEquals("YWRkcmVzcyQuX2RlZmF1bHQuY29tLndhbG1hcnQub25lb3BzLXRl", ipv4Ref.refData());
  }

  @Test
  void names() {
    assertEquals(Collections.singletonList("default"), netViewRef.names());
    assertEquals(Collections.emptyList(), ptrRef.names());
    assertEquals(Arrays.asList("test-host1.oneops.com", "Internal"), hostRef.names());
    assertEquals(
        Arrays.asList("10.10.10.20", "test-host1.oneops.com", "Internal"), ipv4Ref.names());
  }
}
