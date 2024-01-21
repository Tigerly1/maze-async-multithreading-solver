import java.util.List;

/**
 * Interfejs rezultatu eksploracji pomieszczenia.
 */
public interface Result {
	/**
	 * Numer zlecenia, którego rezultat dotyczy
	 * 
	 * @return numer zlecenia
	 */
	public int orderID();

	/**
	 * Typ lokacji
	 * 
	 * @return typ lokacji
	 */
	public LocationType type();

	/**
	 * Możliwe kierunki ruchu z danej lokacji. Sensowna wartość zwracana jest w
	 * przypadku lokacji typu PASSAGE. W innych przypadkach zwracana jest pusta
	 * lista. Wynik jest typu read-only.
	 * 
	 * @return lista kierunków
	 */
	public List<Direction> allowedDirections();
}


