package uni.app.dondeestaciono.util.geo;

import java.util.ArrayList;
import java.util.List;

public class CircularDrawingAlgorithm implements ICircularDrawingAlgorithm {

  private static final double FULL_CIRCLE_ANGLE = 360;
  private static final double INITIAL_ANGLE = 0;

  private static CircularDrawingAlgorithm instance;

  private CircularDrawingAlgorithm() {}

  public static CircularDrawingAlgorithm getInstance() {
    if (instance == null) {
      instance = new CircularDrawingAlgorithm();
    }
    return instance;
  }

  @Override
  public List<PointDto> getCirclePoints(PointDto center, double radiusInMeters) {
    List<PointDto> circlePoints = new ArrayList<>();

    PointDto firstPoint = GeoCalculator.getDestinationPoint(center, radiusInMeters, INITIAL_ANGLE);
    circlePoints.add(firstPoint);
    for (double i = 1; i < FULL_CIRCLE_ANGLE; i++) {
      PointDto destination = GeoCalculator.getDestinationPoint(center, radiusInMeters, i);
      circlePoints.add(destination);
    }
    circlePoints.add(firstPoint);

    return circlePoints;
  }

  @Override
  public List<PositionDto> getArcPositions(
      PositionDto center, double radiusInMeters, double startingAngle, double endingAngle) {
    List<PositionDto> arcPositions = new ArrayList<>();
    arcPositions.add(center);
    for (double i = startingAngle; i <= endingAngle; i++) {
      PositionDto destination = GeoCalculator.getDestinationPosition(center, radiusInMeters, i);
      arcPositions.add(destination);
    }
    arcPositions.add(center);
    return arcPositions;
  }

  @Override
  public List<PositionDto> getCirclePositions(PositionDto center, double radiusInMeters) {
    List<PositionDto> circlePoints = new ArrayList<>();
    PositionDto firstPoint =
        GeoCalculator.getDestinationPosition(center, radiusInMeters, INITIAL_ANGLE);
    circlePoints.add(firstPoint);

    for (double i = 1; i < FULL_CIRCLE_ANGLE; i++) {
      PositionDto destination = GeoCalculator.getDestinationPosition(center, radiusInMeters, i);
      circlePoints.add(destination);
    }

    circlePoints.add(firstPoint);

    return circlePoints;
  }
}
