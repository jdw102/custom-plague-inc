package ooga.view;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

public class ViewProperties {

  private final ResourceBundle MAP_VIEW_PROPERTIES = ResourceBundle.getBundle(
      "ooga.view.properties.RegionViewColors");

  private final ResourceBundle VIEW_ICON_PROPERTIES = ResourceBundle.getBundle(
      "ooga.view.properties.ViewIcons");

  //todo: add properties, such as button sizes, pane dimensions, etc.

  public ViewProperties() {
  }

  public String getIconPath(String key) {
    try {
      return VIEW_ICON_PROPERTIES.getString(key);
    } catch (MissingResourceException e) {
      return "";
    }
  }

  public String getRegionColor(int key) {
    return MAP_VIEW_PROPERTIES.getString("" + key);
  }

  /**
   * @return a set containing the keys in the ViewIcons properties file.
   */
  public Set<String> getPortKeys() {
    return VIEW_ICON_PROPERTIES.keySet();
  }
}
