package biblivre.administration.accesscards;

@SuppressWarnings("serial")
public class NoSuchAccessCardException extends Exception {
	private int id;

	public NoSuchAccessCardException(int id) {
		super("No access card with id " + id + " could be found");

		this.id = id;
	}

	public int getId() {
		return id;
	}
}
