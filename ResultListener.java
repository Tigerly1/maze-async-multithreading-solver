/**
 * Interfejs obiektu oczekującego na wynik eksploracji lokacji labiryntu.
 */
public interface ResultListener {
	/**
	 * Metoda wykonywana po zakończeniu ekspoloracji lokacji.
	 * 
	 * @param result rezultat ekspoloracji lokacji
	 */
	public void result(Result result);
}
