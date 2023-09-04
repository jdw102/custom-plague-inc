package ooga.controller.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class DatabaseConnection {

  public static final String URL = "https://ooga-team8-default-rtdb.firebaseio.com";
  private static final Logger LOG = LogManager.getLogger(DatabaseConnection.class);

  private JSONObject retrieveJSON(String url) throws DatabaseException {
    HttpURLConnection h;
    try {
      h = (HttpURLConnection) new URL(url).openConnection();
    } catch (IOException e) {
      String msg = String.format("Could not establish connection to URL %s", url);
      LOG.error(msg);
      throw new DatabaseException(msg, e);
    }

    try {
      h.setRequestMethod("GET");
    } catch (ProtocolException e) {
      String msg = "Could not set request method GET";
      LOG.error(msg);
      throw new DatabaseException(msg, e);
    }

    StringBuilder sb = new StringBuilder();
    try {
      BufferedReader b = new BufferedReader(new InputStreamReader(h.getInputStream()));
      String l;
      while ((l = b.readLine()) != null) {
        sb.append(l);
      }
      b.close();

    } catch (IOException e) {
      String msg = String.format("Could not read from %s", url);
      LOG.error(msg);
      throw new DatabaseException(msg, e);
    }

    if (sb.toString().equals("null")) {
      String msg = String.format("No node %s found", url);
      LOG.error(msg);
      throw new DatabaseException(msg);
    }

    JSONParser jp = new JSONParser();
    JSONObject j;
    try {
      j = (JSONObject) jp.parse(sb.toString());
      return j;
    } catch (ParseException e) {
      String msg = String.format("Could not read data retrieved from %s as JSON", url);
      LOG.error(msg);
      throw new DatabaseException(msg, e);
    }
  }


  private void putJSON(String url, JSONObject obj) throws DatabaseException {
    HttpURLConnection h;
    try {
      h = (HttpURLConnection) new URL(url).openConnection();
    } catch (IOException e) {
      String msg = String.format("Could not establish connection to URL %s", url);
      LOG.error(msg);
      throw new DatabaseException(msg, e);
    }

    try {
      h.setRequestMethod("PUT");
      h.setDoOutput(true);
    } catch (ProtocolException e) {
      String msg = "Could not set request method PUT";
      LOG.error(msg);
      throw new DatabaseException(msg, e);
    }

    try {
      OutputStreamWriter o = new OutputStreamWriter(h.getOutputStream());

      o.write(obj.toJSONString());

      o.close();
      h.getInputStream();
    } catch (IOException e) {
      String msg = String.format("Could not write JSON to %s", url);
      LOG.error(msg);
      throw new DatabaseException(msg, e);
    }
  }

  public JSONObject getUser(String user) throws DatabaseException {
    String fullURL = String.format("%s/saves/%s.json", URL, user);

    try {
      return retrieveJSON(fullURL);
    } catch (DatabaseException e) {
      LOG.error(String.format("No user %s found at %s", user, fullURL));
      throw new DatabaseException(String.format("Unrecognized user %s", user));
    }
  }

  public void putUser(String userName, JSONObject userData) throws DatabaseException {
    String fullURL = String.format("%s/saves/%s.json", URL, userName);

    try {
      putJSON(fullURL, userData);
    } catch (DatabaseException e) {
      LOG.error(String.format("Could not add user %s", userName));
    }
  }

  public JSONObject getSave(String user, String gameType) throws DatabaseException {
    String fullURL = String.format("%s/saves/%s/%s.json", URL, user, gameType);

    try {
      return retrieveJSON(fullURL);
    } catch (DatabaseException e) {
      LOG.error(
          String.format("No save for user %s : game %s found at %s. Checking if user exists:", user,
              gameType, fullURL));
      try {
        getUser(user);
      } catch (DatabaseException f) {
        LOG.warn(String.format(
            "Could not find user %s: this means that user is not registered in the database.",
            user));
        throw new DatabaseException(String.format("User %s does not exist", user));
      }
      LOG.warn(String.format(
          "User %s found: this means that the user does not have a save for game %s stored in the database.",
          user, gameType));
      throw new DatabaseException(
          String.format("User %s does not have saved data for game %s", user, gameType));
    }
  }


  public void putSave(String user, String gameType, JSONObject saveData) throws DatabaseException {
    String fullURL = String.format("%s/saves/%s/%s.json", URL, user, gameType);

    try {
      putJSON(fullURL, (JSONObject) saveData.get("saveData"));
    } catch (DatabaseException e) {
      LOG.error(String.format("Could not save user %s savedata for game %s", user, gameType));
    }
  }

}
