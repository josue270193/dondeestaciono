package uni.app.dondeestaciono.util.geo;

public class UltimateGeoJSONBuilder {

	private static final UltimateGeoJSONBuilder INSTANCE = new UltimateGeoJSONBuilder();

	public static UltimateGeoJSONBuilder getInstance() {
		return INSTANCE;
	}

	private UltimateGeoJSONBuilder() {
	}

	public String toGeoJSON(GeoJSONObjectDto geoJsonObjectDto) {
		if (geoJsonObjectDto == null) {
			return BuilderConstants.NULL_VALUE;
		}

		if (geoJsonObjectDto instanceof PointDto) {
			return PointBuilder.getInstance().toGeoJSON((PointDto) geoJsonObjectDto);
		}

		if (geoJsonObjectDto instanceof LineStringDto) {
			return LineStringBuilder.getInstance().toGeoJSON((LineStringDto) geoJsonObjectDto);
		}

		if (geoJsonObjectDto instanceof PolygonDto) {
			return PolygonBuilder.getInstance().toGeoJSON((PolygonDto) geoJsonObjectDto);
		}

		return BuilderConstants.NULL_VALUE;
	}

}