import java.util.List;

/**
 * Interfejs zleceniodawcy zadań eksporacji labiryntu.
 */
public interface Employer {
	/**
	 * Metoda ustawia dostęp do obiektu umożliwiającego zlecanie eksploracji
	 * wskazanych lokacji labiryntu.
	 * 
	 * @param order narzędzie do eksploracji labiryntu
	 */
	public void setOrderInterface(OrderInterface order);

	/**
	 * Rozpoczyna się poszukiwanie wyjścia. Do metody przekazywane jest początkowe
	 * położenie w labiryncie, od którego zaczyna się poszukiwanie. Wraz z
	 * położeniem przekazywana jest informacji o możliwych kierunkach, w jakich
	 * można się przemieścić. Metoda zostanie wykonana w innym wątku niż ten, który
	 * wykonał setOrderInterface.
	 * 
	 * @param startLocation     pozycja startowa
	 * @param allowedDirections dozwolone kierunki dalszej ekspoloracji labiryntu
	 * @return położenie, w którym znajduje się wyjście z labiryntu
	 */
	public Location findExit(Location startLocation, List<Direction> allowedDirections) throws InterruptedException;
}
