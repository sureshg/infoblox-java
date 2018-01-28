package com.oneops.infoblox.model.ref;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
  private Ref wildCardRef;

  @BeforeEach
  void setUp() {
    netViewRef = Ref.of("networkview/ZG5zLm5ldHdvcmtfdmlldyQw:default");
    hostRef = Ref.of("record:host/ZG5zLmhvc10ZXN0LWhvc3Qx:test-host1.oneops.com/Internal");
    ptrRef = Ref.of("record:ptr/ZG5zLmJpbmRfcHRTAuMTAuMTAuMi5zZXJ2ZXIuaW5mby5jb20");
    ipv4Ref = Ref.of("record:host_ipv4addr/YWRkcmQRl:10.10.10.20/test-host1.oneops.com/Internal");
    wildCardRef = Ref.of("record:cname/ZG5zLxdfxLio:%2A.test-cname.oneops.com/Internal");
  }

  @Test
  void wapiType() {
    assertEquals("networkview", netViewRef.wapiType());
    assertEquals("record:host", hostRef.wapiType());
    assertEquals("record:ptr", ptrRef.wapiType());
    assertEquals("record:host_ipv4addr", ipv4Ref.wapiType());
    assertEquals("record:cname", wildCardRef.wapiType());
  }

  @Test
  void refData() {
    assertEquals("ZG5zLm5ldHdvcmtfdmlldyQw", netViewRef.refData());
    assertEquals("ZG5zLmhvc10ZXN0LWhvc3Qx", hostRef.refData());
    assertEquals("ZG5zLmJpbmRfcHRTAuMTAuMTAuMi5zZXJ2ZXIuaW5mby5jb20", ptrRef.refData());
    assertEquals("YWRkcmQRl", ipv4Ref.refData());
    assertEquals("ZG5zLxdfxLio", wildCardRef.refData());
  }

  @Test
  void names() {
    assertEquals(Collections.singletonList("default"), netViewRef.names());
    assertEquals(Collections.emptyList(), ptrRef.names());
    assertEquals(Arrays.asList("test-host1.oneops.com", "Internal"), hostRef.names());
    assertEquals(
        Arrays.asList("10.10.10.20", "test-host1.oneops.com", "Internal"), ipv4Ref.names());
    // Wildcard domains
    assertEquals(Arrays.asList("%2A.test-cname.oneops.com", "Internal"), wildCardRef.names());
    assertTrue(wildCardRef.hasFqdn("*.test-cname.oneops.com"));
    assertFalse(wildCardRef.hasFqdn("%2A.test-cname.oneops.com"));
  }
}
