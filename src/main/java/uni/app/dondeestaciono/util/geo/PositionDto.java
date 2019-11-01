package uni.app.dondeestaciono.util.geo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;

public class PositionDto implements Serializable {

  private static final long serialVersionUID = 1L;

  private double[] numbers;

  /**
   * This constructor initializes numbers field to hold two double numbers. Do not use this
   * constructor if you need the third elevation parameter.
   */
  public PositionDto() {
    this.numbers = new double[2];
  }

  /**
   * Simple coordinate position constructor
   *
   * @param longitude
   * @param latitude
   */
  public PositionDto(double longitude, double latitude) {
    this.numbers = new double[] {longitude, latitude};
  }

  /**
   * Use this constructor if you need elevation parameter
   *
   * @param longitude
   * @param latitude
   * @param elevation
   */
  public PositionDto(double longitude, double latitude, double elevation) {
    this.numbers = new double[] {longitude, latitude, elevation};
  }

  /**
   * Copy constructor
   *
   * @param position
   */
  public PositionDto(PositionDto position) {
    if (position != null) {
      double[] copyNumbers = position.getNumbers();
      this.numbers = new double[copyNumbers.length];
      System.arraycopy(copyNumbers, 0, this.numbers, 0, copyNumbers.length);
    }
  }

  @Override
  public String toString() {
    if (numbers == null) {
      return "null numbers[]";
    }
    StringBuilder value = new StringBuilder("[");
    for (int i = 0; i < numbers.length; i++) {
      value.append(numbers[i]);
      if (i < numbers.length - 1) {
        value.append(", ");
      }
    }
    value.append("]");
    return value.toString();
  }

  public double getLongitude() {
    return numbers[0];
  }

  public void setLongitude(double longitude) {
    this.numbers[0] = longitude;
  }

  public double getLatitude() {
    return numbers[1];
  }

  public void setLatitude(double latitude) {
    this.numbers[1] = latitude;
  }

  @JsonIgnore
  public double getElevation() {
    return numbers[2];
  }

  @JsonIgnore
  public void setElevation(double elevation) {
    this.numbers[2] = elevation;
  }

  @JsonIgnore
  public double[] getNumbers() {
    return numbers;
  }

  @JsonIgnore
  public void setNumbers(double[] numbers) {
    this.numbers = numbers;
  }
}
