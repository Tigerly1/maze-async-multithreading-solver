    import java.awt.*;
    import java.util.*;
    import java.util.List;
    import java.util.concurrent.*;
    import java.util.concurrent.atomic.AtomicInteger;
    import java.util.concurrent.ThreadLocalRandom;


    public class TestParallelEmployer {

        private static final int MAZE_SIZE = 15;
        private static final char UNEXPLORED = '?';
        private static final char WALL = '#';
        private static final char PASSAGE = 'P';
        private static final char EXIT = 'E';
        private static char[][] maze; // Actual maze
        private static char[][] discoveredMaze; // Player's knowledge of the maze

        public static void main(String[] args) {
            // Initialize the maze displays
            maze = generateRandomMaze(MAZE_SIZE);

            discoveredMaze = new char[MAZE_SIZE][MAZE_SIZE];
            for (char[] row : discoveredMaze) {
                Arrays.fill(row, UNEXPLORED);
            }

            // Print the initial undiscovered maze
            printMaze(maze);

            //printMaze(discoveredMaze);
            long currentTimeMillis = System.currentTimeMillis();

            // Calculate seconds (divide by 1000 and get remainder)
            long seconds = (currentTimeMillis / 1000) % 60;

            // Calculate remaining milliseconds
            long milliseconds = currentTimeMillis % 1000;

            System.out.println("Seconds: " + seconds + " Milliseconds: " + milliseconds);
            MockOrderInterface mockOrderInterface = new MockOrderInterface();

            ParallelEmployer employer = new ParallelEmployer();
            employer.setOrderInterface(mockOrderInterface);

            Thread findExitThread = new Thread(() -> {
                Location startLocation = new Location(0, 0);
                List<Direction> allowedDirections = Arrays.asList(Direction.NORTH, Direction.EAST);
                Location exitLocation = null;
                exitLocation = employer.findExit(startLocation, allowedDirections);


                System.out.println("Exit found at: " + exitLocation);
            });

            // Start the findExitThread
            findExitThread.start();

            // Wait for the thread to complete
            try {
                findExitThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            currentTimeMillis = System.currentTimeMillis();

            // Calculate seconds (divide by 1000 and get remainder)
            seconds = (currentTimeMillis / 1000) % 60;

            // Calculate remaining milliseconds
            milliseconds = currentTimeMillis % 1000;

            // Print the current time in seconds and milliseconds
            System.out.println("Seconds: " + seconds + " Milliseconds: " + milliseconds);
            // Print the discovered maze after exploration
            printMaze(discoveredMaze);

            mockOrderInterface.shutdownExecutorService();

        }

        private static char[][] generateRandomMaze(int size) {
            char[][] generatedMaze;
            boolean solvable;
            do {
                generatedMaze = new char[size][size];
                for (int row = 0; row < size; row++) {
                    for (int col = 0; col < size; col++) {
                        generatedMaze[row][col] = ThreadLocalRandom.current().nextDouble() < 0.2 ? WALL : PASSAGE;
                    }
                }

                Random random = new Random();
                generatedMaze[0][0] = PASSAGE;
                generatedMaze[0][1] = PASSAGE;
                generatedMaze[1][0] = PASSAGE;
                generatedMaze[0][2] = WALL;
                generatedMaze[1][2] = WALL;
                int exitRow, exitCol;
                do {
                    exitRow = random.nextInt(size);
                    exitCol = random.nextInt(size);
                } while (exitRow == 0 && exitCol == 0); // Ensure the exit is not at the starting point
                generatedMaze[exitRow][exitCol] = EXIT;

                solvable = isMazeSolvable(generatedMaze, size); // Implement this method to check maze solvability
            } while (!solvable || !isDifficultEnough(generatedMaze, size)); // Implement `isDifficultEnough` to measure difficulty

            return generatedMaze;
        }

        private static boolean isMazeSolvable(char[][] maze, int size) {
            boolean[][] visited = new boolean[size][size];
            Queue<Point> queue = new LinkedList<>();
            queue.add(new Point(0, 0));

            while (!queue.isEmpty()) {
                Point point = queue.poll();

                if (maze[point.x][point.y] == EXIT) {
                    return true; // Exit found
                }

                // Check all four directions
                int[] dx = {0, 1, 0, -1};
                int[] dy = {1, 0, -1, 0};

                for (int i = 0; i < 4; i++) {
                    int newX = point.x + dx[i];
                    int newY = point.y + dy[i];

                    if (newX >= 0 && newX < size && newY >= 0 && newY < size &&
                            maze[newX][newY] != WALL && !visited[newX][newY]) {
                        visited[newX][newY] = true;
                        queue.add(new Point(newX, newY));
                    }
                }
            }

            return false; // No path to exit
        }

        private static boolean isDifficultEnough(char[][] maze, int size) {
            int difficultyThreshold = size * size / 7; // Example threshold, adjust as needed
            int pathLength = bfsPathLength(maze, size);

            return pathLength >= difficultyThreshold;
        }


        private static int bfsPathLength(char[][] maze, int size) {
            boolean[][] visited = new boolean[size][size];
            Queue<Point> queue = new LinkedList<>();
            Map<Point, Integer> distance = new HashMap<>(); // Stores distance from start
            Point start = new Point(0, 0);
            queue.add(start);
            visited[0][0] = true;
            distance.put(start, 0);

            while (!queue.isEmpty()) {
                Point point = queue.poll();
                int dist = distance.get(point);

                if (maze[point.x][point.y] == EXIT) {
                    return dist; // Return the length of the path to the exit
                }

                // Check all four directions
                int[] dx = {0, 1, 0, -1};
                int[] dy = {1, 0, -1, 0};

                for (int i = 0; i < 4; i++) {
                    int newX = point.x + dx[i];
                    int newY = point.y + dy[i];

                    if (newX >= 0 && newX < size && newY >= 0 && newY < size &&
                            maze[newX][newY] != WALL && !visited[newX][newY]) {
                        visited[newX][newY] = true;
                        queue.add(new Point(newX, newY));
                        distance.put(new Point(newX, newY), dist + 1);
                    }
                }
            }

            return -1; // If the exit is not reachable

        }

        private static void updateAndPrintMaze(Location location, LocationType type) {
            char displayChar = getDisplayChar(type);
            discoveredMaze[location.row()][location.col()] = displayChar;
            printMaze(discoveredMaze);
        }

        private static char getDisplayChar(LocationType type) {
            return switch (type) {
                case WALL -> WALL;
                case EXIT -> EXIT;
                case PASSAGE -> PASSAGE;
                default -> UNEXPLORED;
            };
        }

        private static void printMaze(char[][] mazeToPrint) {
            clearConsole();
            System.out.print("START OF THE MAZE");
            System.out.println();

            for (char[] row : mazeToPrint) {
                for (char cell : row) {
                    System.out.print(cell + " ");
                }
                System.out.println();
            }
            System.out.print("END OF MAZE ");
            System.out.println();


        }

        private static void clearConsole() {
            System.out.print("\033[H\033[2J");
            System.out.flush();
        }


        // Mock OrderInterface implementation
        static class MockOrderInterface implements OrderInterface {
            private final AtomicInteger globalOrderId = new AtomicInteger(0);
            private ResultListener resultListener;

            private final Set<Location> requestedLocations = Collections.newSetFromMap(new ConcurrentHashMap<>());

            @Override
            public void setResultListener(ResultListener listener) {
                this.resultListener = listener;
            }

            public void shutdownExecutorService() {
                executorService.shutdown();
                try {
                    if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                        executorService.shutdownNow();
                        if (!executorService.awaitTermination(60, TimeUnit.SECONDS))
                            System.err.println("Executor service did not terminate");
                    }
                } catch (InterruptedException ie) {
                    executorService.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
            @Override
            public int order(Location location) {
                if (!requestedLocations.add(location)) {
                    System.out.println("LOCATION EXPLORED TWICE");
                    System.out.println(location.col() + " " + location.row());
                    throw new RuntimeException("Location " + location + " has been ordered multiple times");
                }
                int currentOrderId = globalOrderId.getAndIncrement();


                // Simulate a delay and then send the result
                executorService.submit(() -> {
                    try {
                        Random random = new Random();


                        Thread.sleep(random.nextInt(2)); // Simulate processing time
                        // Generate a mock result based on the location
                        Result result = generateMockResult(location, currentOrderId);
                        if (result.type()==LocationType.EXIT){
                            long currentTimeMillis = System.currentTimeMillis();

                            // Calculate seconds (divide by 1000 and get remainder)
                            long seconds = (currentTimeMillis / 1000) % 60;

                            // Calculate remaining milliseconds
                            long milliseconds = currentTimeMillis % 1000;

                            // Print the current time in seconds and milliseconds
                            System.out.println("Seconds: " + seconds + " Milliseconds: " + milliseconds);
                        }
                        char displayChar = getDisplayChar(result.type());
                        discoveredMaze[location.row()][location.col()] = displayChar;
//                        if ( currentOrderId % ((maze.length * maze.length) / 2) == 0){
//
//                            TestParallelEmployer.updateAndPrintMaze(location, result.type()); // Update and print the maze
//                        }

                        resultListener.result(result);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });

                return currentOrderId;
            }

            private Result generateMockResult(Location location, int orderId) {
                LocationType type = maze[location.row()][location.col()] == '#' ? LocationType.WALL :
                        maze[location.row()][location.col()] == 'E' ? LocationType.EXIT :
                                LocationType.PASSAGE;

                // Determine allowed directions - in a real scenario, you'd check adjacent cells
                List<Direction> allowedDirections = new ArrayList<>();
                if (location.row() > 0 && maze[location.row() - 1][location.col()] != '#') allowedDirections.add(Direction.SOUTH);
                if (location.row() < maze.length - 1 && maze[location.row() + 1][location.col()] != '#') allowedDirections.add(Direction.NORTH);
                if (location.col() > 0 && maze[location.row()][location.col() - 1] != '#') allowedDirections.add(Direction.WEST);
                if (location.col() < maze[0].length - 1 && maze[location.row()][location.col() + 1] != '#') allowedDirections.add(Direction.EAST);


                return new MockResult(orderId, type, allowedDirections);
            }
            private final ExecutorService executorService =  Executors.newFixedThreadPool(500);
        }

        // Mock Result implementation
        static class MockResult implements Result {
            private final int orderId;
            private final LocationType type;
            private final List<Direction> allowedDirections;

            public MockResult(int orderId, LocationType type, List<Direction> allowedDirections) {
                this.orderId = orderId;
                this.type = type;
                this.allowedDirections = allowedDirections;
            }

            @Override
            public int orderID() {
                return orderId;
            }

            @Override
            public LocationType type() {
                return type;
            }

            @Override
            public List<Direction> allowedDirections() {
                return allowedDirections;
            }
        }
    }
