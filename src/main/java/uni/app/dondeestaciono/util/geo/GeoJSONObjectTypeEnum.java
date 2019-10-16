package uni.app.dondeestaciono.util.geo;

public enum GeoJSONObjectTypeEnum {
  Point(PointDto.class),
  LineString(LineStringDto.class),
  Polygon(PolygonDto.class);

  private final Class dtoClass;

  private GeoJSONObjectTypeEnum(Class dtoClass) {
    this.dtoClass = dtoClass;
  }

  public Class getDtoClass() {
    return dtoClass;
  }
}
