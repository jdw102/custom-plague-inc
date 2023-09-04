package ooga.model.region;

public class PopulationNotFoundException extends Exception {

  private static final String MESSAGE = "%s does not exist, please check your config files";

  public PopulationNotFoundException(String name) {
    super(String.format(MESSAGE, name));
  }
}
