package uni.app.dondeestaciono.util.geo;

import java.util.List;

public class PolygonDto extends GeometryDto {

  private static final long serialVersionUID = 1L;

  private List<LineStringDto> linearRings;

  @Override
  public GeoJSONObjectTypeEnum getGeoJSONObjectType() {
    return GeoJSONObjectTypeEnum.Polygon;
  }

  public List<LineStringDto> getLinearRings() {
    return linearRings;
  }

  public void setLinearRings(List<LineStringDto> linearRings) {
    this.linearRings = linearRings;
  }
}
