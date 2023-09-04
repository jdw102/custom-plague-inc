package ooga.controller.database;

import java.util.List;
import org.json.simple.JSONObject;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class DatabaseConnectorTest {

  //Delete dbConnTestAdd user from database before running for accurate results!

  private DatabaseConnection dc = new DatabaseConnection();
  private static final String USER = "dbConnTest";

  @Test
  void getUser() {
    assertDoesNotThrow(() -> dc.getUser(USER));
    JSONObject j = dc.getUser(USER);
    assertEquals(1, j.keySet().size());
    assertTrue(j.containsKey("Plague"));
  }

  @Test
  void getSave() {
    assertDoesNotThrow(() -> dc.getSave(USER, "Plague"));
    JSONObject j = dc.getSave(USER, "Plague");
    assertEquals(6, j.keySet().size());
    for (String s : List.of("antagonistProgress", "days", "points", "protagonistId", "protagonistName", "regionStates")) {
      assertTrue(j.containsKey(s));
    }
    assertEquals("asdf", j.get("protagonistName"));
  }

  @Test
  void userBadSave() {
    try {
      dc.getSave(USER, "bad");
      assertTrue(false);
    } catch (DatabaseException e) {
      assertEquals(String.format("User %s does not have saved data for game bad", USER), e.getMessage());
    }
  }

  @Test
  void badUserThruSave() {
    try {
      dc.getSave("dbConnTestBad", "bad");
      assertTrue(false);
    } catch (DatabaseException e) {
      assertEquals("User dbConnTestBad does not exist", e.getMessage());
    }
  }

  @Test
  void badGetUser() {
    try {
      dc.getUser("baduser");
      assertTrue(false);
    } catch (DatabaseException e) {
      assertEquals("Unrecognized user baduser", e.getMessage());
    }
  }

  @Test
  void addUserAndSave() {
    String name = "dbConnTestAdd";
    try {
      //put data
      JSONObject j = new JSONObject();
      j.put("testKey", "testVal");
      dc.putUser(name, j);
      assertDoesNotThrow(() -> dc.getUser(name));

      //check put data
      JSONObject get = dc.getUser(name);
      assertEquals(1, get.keySet().size());
      assertTrue(get.containsKey("testKey"));

      //put save data
      JSONObject littleSave = new JSONObject();
      littleSave.put("saveKey1", "saveVal");
      JSONObject save = new JSONObject();
      save.put("saveData", littleSave);
      dc.putSave(name, "testAddSave", save);
      assertDoesNotThrow(() -> dc.getSave(name, "testAddSave"));

      //check put save data
      JSONObject getSave = dc.getSave(name, "testAddSave");
      assertTrue(getSave.containsKey("saveKey1"));

      //check overall
      JSONObject last = dc.getUser(name);
      assertEquals(2, last.keySet().size());


    } catch (DatabaseException e) {
      assertTrue(false);
    }
  }





}