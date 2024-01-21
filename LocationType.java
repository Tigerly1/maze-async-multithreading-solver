/**
 * Możliwe typy lokacji znajdujących się w labiryncie.
 */
public enum LocationType {
	/**
	 * Ściana. Odebranie tego typu lokacji oznacza niestety porażkę. Program
	 * źle przetwarzał otrzymywane wyniki i trafił do wnętrza ściany.
	 */
	WALL,
	/**
	 * Wyjście z labiryntu. Sukces!
	 */
	EXIT, 
	/**
	 * Przejście, generalnie pusta przestrzeń pozwalająca na przemieszczanie
	 * się w labiryncie
	 */
	PASSAGE;
}
