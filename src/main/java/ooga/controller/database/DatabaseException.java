package ooga.controller.database;

public class DatabaseException extends RuntimeException {

  public DatabaseException() {
    super();
  }

  public DatabaseException(String msg) {
    super(msg);
  }

  public DatabaseException(String msg, Exception error) {
    super(String.format("%s: %s", msg, error.getMessage()));
  }


}
