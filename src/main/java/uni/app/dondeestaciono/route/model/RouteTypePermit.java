package uni.app.dondeestaciono.route.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum RouteTypePermit {
  PROHIBIDO_ESTACIONAR("PROHIBIDO ESTACIONAR"),
  PROHIBIDO_ESTACIONAR_DETENERSE("PROHIBIDO ESTACIONAR Y DETENERSE"),
  PERMITIDO_ESTACIONAR("PERMITIDO ESTACIONAR"),
  PERMITIDO_ESTACIONAR_90_GRADO("PERMITIDO ESTACIONAR A 90°"),
  PERMITIDO_ESTACIONAR_45_GRADO("PERMITIDO ESTACIONAR A 45°");

  @Getter private String value;

  public static RouteTypePermit getByValue(String valueRoute) {
    for (RouteTypePermit routeTypePermit : values()) {
      if (routeTypePermit.getValue().compareToIgnoreCase(valueRoute) == 0) {
        return routeTypePermit;
      }
    }
    return null;
  }
}
