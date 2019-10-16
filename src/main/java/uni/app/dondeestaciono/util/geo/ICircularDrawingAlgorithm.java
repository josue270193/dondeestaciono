package uni.app.dondeestaciono.util.geo;

import java.util.List;

public interface ICircularDrawingAlgorithm {

  /**
   * Generate points of circle that center point and radius in meters is given
   *
   * @param center center point
   * @param radiusInMeters radius of circle in meters
   * @return
   */
  List<PointDto> getCirclePoints(PointDto center, double radiusInMeters);

  /**
   * Generate positions of circle that center position and radius in meters is given
   *
   * @param center center point
   * @param radiusInMeters radius of circle in meters
   * @return
   */
  List<PositionDto> getCirclePositions(PositionDto center, double radiusInMeters);

  /**
   * Generate positions of arc.
   *
   * @param center center point of arc
   * @param radiusInMeters radius of arc in meters
   * @param startingAngle starting angle in degrees
   * @param endingAngle ending angle in degrees
   * @return
   */
  List<PositionDto> getArcPositions(
      PositionDto center, double radiusInMeters, double startingAngle, double endingAngle);
}
