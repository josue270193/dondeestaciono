package uni.app.dondeestaciono.route.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum EnumBlockType {
  MANIFESTACION("manifestancion"),
  OPERATIVO("operativo"),
  INCIDENTE("incidente");

  @Getter private String value;

  public static EnumBlockType getByValue(String valueRoute) {
    for (EnumBlockType enumRoutePermit : values()) {
      if (enumRoutePermit.getValue().compareToIgnoreCase(valueRoute) == 0) {
        return enumRoutePermit;
      }
    }
    return null;
  }
}
