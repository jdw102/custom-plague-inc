package ooga.model.region;

public class PathNotFoundException extends Exception {

  private static final String MESSAGE = "Path of type %s was not found, please check your config files";

  public PathNotFoundException(String name) {
    super(String.format(MESSAGE, name));
  }

}
