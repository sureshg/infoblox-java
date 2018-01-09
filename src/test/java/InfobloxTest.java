import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Infoblox tests.
 *
 * @author Suresh G
 */
class InfobloxTest {

  private String errorMessage() {
    return "Test failed";
  }

  @BeforeEach
  void setUp() {
    System.out.println("SampleTest.setUp");
  }

  @AfterEach
  void tearDown() {
    System.out.println("SampleTest.tearDown");
  }

  @Test
  @DisplayName("A sample test")
  void blah() {
    assumeTrue(5 < 6);
    assertAll(
        () -> assertEquals(2, 2),
        () -> assertNotEquals(1, 3),
        () -> assertTrue(5 < 6, this::errorMessage)
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {"a", "b"})
  void paramTest(String email) {
    assertNotNull(email);
  }

  @RepeatedTest(5)
  void repeatTest() {
    assertEquals(1, 1);
  }

  @TestFactory
  Stream<DynamicTest> dynaTest() {
    return DynamicTest.stream(
        Arrays.asList(1, 2, 3, 4, 5).iterator(),
        n -> "Test for " + n,
        n -> assertTrue(n > 0)
    );
  }
}