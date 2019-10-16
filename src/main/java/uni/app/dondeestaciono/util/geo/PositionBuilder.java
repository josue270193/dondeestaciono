package uni.app.dondeestaciono.util.geo;

public class PositionBuilder {

	private static final int MINIMUM_POSITION_DIMENSION = 2;

	private static final PositionBuilder INSTANCE = new PositionBuilder();
	
	
	public static PositionBuilder getInstance(){
		return INSTANCE;
	}

	private PositionBuilder(){
	}
	
	public String position(PositionDto position) {
		if(position == null || position.getNumbers().length < MINIMUM_POSITION_DIMENSION){
			throw new InvalidPositionDtoException(position);
		}
		
		double[] numbers = position.getNumbers();
		StringBuilder builder = new StringBuilder(BuilderConstants.OPEN_BRACKET);
		for (int i = 0; i < numbers.length; i++) {
			builder.append(numbers[i]);
			if(i < numbers.length - 1){
				builder.append(BuilderConstants.COMMA_SPACE);
			}
		}
		builder.append(BuilderConstants.CLOSE_BRACKET);
		
		return builder.toString();
	}

}