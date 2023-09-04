package ooga.util;

import java.util.ResourceBundle;

public class DefaultPointsOperation implements BiOperation {

  private static final ResourceBundle PARAMETER_BUNDLE = ResourceBundle.getBundle(
      "PointsOperationParameters");
  private final double LOGMULTIPLE = Double.parseDouble(
      PARAMETER_BUNDLE.getString("logCoefficient"));

  private final double LOGCONSTANT = Double.parseDouble(PARAMETER_BUNDLE.getString("constant"));

  @Override
  public double operate(double d1, double d2) {
    if (d1 == 0) {
      return 0;
    }
    double logRatio = Math.max(Math.log(d2 / (d1 + LOGCONSTANT)) * LOGMULTIPLE, 0);
    return logRatio;
  }
}
