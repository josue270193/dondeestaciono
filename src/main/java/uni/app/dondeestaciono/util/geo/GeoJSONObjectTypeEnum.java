package uni.app.dondeestaciono.util.geo;

public enum GeoJSONObjectTypeEnum {
  POINT(PointDto.class),
  LINE_STRING(LineStringDto.class),
  POLYGON(PolygonDto.class);

  private final Class dtoClass;

  private GeoJSONObjectTypeEnum(Class dtoClass) {
    this.dtoClass = dtoClass;
  }

  public Class getDtoClass() {
    return dtoClass;
  }
}
