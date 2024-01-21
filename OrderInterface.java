/**
 * Interfejs umożliwiający zlecenie zbadania lokacji.
 */
public interface OrderInterface {

	/**
	 * Metoda pozwala na ustawienie obiektu, do którego przekazywane będą wyniki
	 * eksploracji pomieszczeń. Metoda musi zostać jednokrotnie wykonana przed
	 * pierwszym zleceniem.
	 * 
	 * @param listener obiekt oczekujący na wyniki eksploracji
	 */
	public void setResultListener(ResultListener listener);

	/**
	 * Zlecenie eksploracji wskazanej lokacji labiryntu. Metoda zapisuje zlecenie i
	 * kończy pracę. Zlecenie wykonane zostanie w dowolnym terminie.
	 * 
	 * @param location położenie w labiryncie do zbadania
	 * @return unikalny identyfikator zlecenia.
	 */
	public int order(Location location);
}
