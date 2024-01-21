/**
 * Kierunki ruchu
 */
public enum Direction {
	/**
	 * Na północ
	 */
	NORTH {
		@Override
		public Location step(Location currentLocation) {
			return new Location(currentLocation.col(), currentLocation.row() + 1);
		}
	},
	/**
	 * Na południe
	 */
	SOUTH {
		@Override
		public Location step(Location currentLocation) {
			return new Location(currentLocation.col(), currentLocation.row() - 1);
		}
	},
	/**
	 * Na wschód
	 */
	EAST {
		@Override
		public Location step(Location currentLocation) {
			return new Location(currentLocation.col() + 1, currentLocation.row());
		}

	},
	/**
	 * Na zachód
	 */
	WEST {
		@Override
		public Location step(Location currentLocation) {
			return new Location(currentLocation.col() - 1, currentLocation.row());
		}
	};

	/**
	 * Zwraca położenie po wykonaniu ruchu w danym kierunku
	 * 
	 * @param currentLocation aktualne położenie
	 * @return następne położenie
	 */
	abstract public Location step(Location currentLocation);
}
