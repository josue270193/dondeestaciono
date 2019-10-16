package uni.app.dondeestaciono.util.geo;

import java.io.Serializable;

public abstract class GeoJSONObjectDto implements Serializable {

  private static final long serialVersionUID = 1L;

  private BoundingBoxDto bbox;

  public abstract GeoJSONObjectTypeEnum getGeoJSONObjectType();

  public BoundingBoxDto getBbox() {
    return bbox;
  }

  public void setBbox(BoundingBoxDto bbox) {
    this.bbox = bbox;
  }
}
