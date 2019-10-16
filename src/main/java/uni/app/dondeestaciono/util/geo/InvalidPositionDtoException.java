package uni.app.dondeestaciono.util.geo;

public class InvalidPositionDtoException extends RuntimeException {
 
	private static final long serialVersionUID = 1L;

	public InvalidPositionDtoException(PositionDto position) {
		super("Invalid position dto: " + position);
	}

}