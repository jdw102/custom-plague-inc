package ooga.model.region;

public class FactorNotFoundException extends Exception {

  private static final String MESSAGE = "%s factor does not exist in region #%s, please check your configuration files";

  public FactorNotFoundException(String factor, int id) {
    super(String.format(MESSAGE, factor, id));
  }

}
