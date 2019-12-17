import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Board {

    private final int size;

    private final int[][] board;

    private int available;

    private static final int ADJACENT = -1;
    private static final int EMPTY = 0;
    private static final int SUBMARINE = 1;
    private static final int DESTROYER = 2;
    private static final int CRUISER = 3;
    private static final int CARRIER = 4;

    Board(final int size) {
        this.size = size;
        this.board = new int[size][size];
        this.available = size * size;
    }

    void placeSubmarine() {
        // if there are less than 1 points available in the board we can't place there submarine
        if (available < 1) {
            System.out.println("can not add more submarine");
        } else {
            //find available point on the board
            Point point = calculateFirstPoint();
            if (point != null) {
                //mark the point as submarine
                markPoint(point.getX(), point.getY(), SUBMARINE);
                //mark all the points around as adjustment so they will not be included in other cases
                removeAdjust(List.of(point));
                System.out.println("done");
            } else {
                System.out.println("can not add more submarine");
            }
        }
    }

    void placeDestroyer() {
        // if there are less than 2 points available in the board we can't place there destroyer
        if (available < 2) {
            System.out.println("can not add more destroyer");
        } else {
            //find available point on the board
            Point start = calculateFirstPoint();
            if (start != null) {
                Point to = nextPoint(start, Direction.Right);
                if (to == null) {
                    to = nextPoint(start, Direction.Left);
                }
                if (to == null) {
                    to = nextPoint(start, Direction.Down);
                }
                if (to == null) {
                    to = nextPoint(start, Direction.Up);
                }
                if (to == null) {
                    System.out.println("can not add more destroyer");
                } else {
                    //mark the points as destroyer
                    markPoint(start.getX(), start.getY(), DESTROYER);
                    markPoint(to.getX(), to.getY(), DESTROYER);
                    //mark all the points around as adjustment so they will not be included in other cases
                    removeAdjust(List.of(start, to));
                    System.out.println("done");
                }
            } else {
                System.out.println("can not add more destroyer");
            }
        }
    }

    void placeCruiser() {
        if (available < 3) {
            System.out.println("can not add more cruiser");
        } else {
            //find available point on the board
            Point start = calculateFirstPoint();
            if (start != null) {
                List<Point> cruiser = horizontal(start, 3);
                if (cruiser.size() < 3) {
                    cruiser.clear();
                    cruiser = vertical(start, 3);
                    if (cruiser.size() < 3) {
                        System.out.println("can not add more cruiser");
                    } else {
                        //mark the cruiser points as cruiser
                        for (Point point : cruiser) {
                            markPoint(point.getX(), point.getY(), CRUISER);
                        }
                        //mark all the points around as adjustment so they will not be included in other cases
                        removeAdjust(cruiser);
                        System.out.println("done");
                    }
                } else {
                    //mark the cruiser points as cruiser
                    for (Point point : cruiser) {
                        markPoint(point.getX(), point.getY(), CRUISER);
                    }
                    //mark all the points around as adjustment so they will not be included in other cases
                    removeAdjust(cruiser);
                    System.out.println("done");
                }
            } else {
                System.out.println("can not add more cruiser");
            }
        }
    }

    void placeCarrier() {
        if (available < 4) {
            System.out.println("can not add more carrier");
        } else {
            //find available point on the board
            Point start = calculateFirstPoint();
            if (start != null) {
                List<Point> carrier = horizontal(start, 4);
                if (carrier.size() < 4) {
                    carrier.clear();
                    carrier = vertical(start, 4);
                    if (carrier.size() < 4) {
                        System.out.println("can not add more carrier");
                    } else {
                        //mark the carrier points as carrier
                        for (Point point : carrier) {
                            markPoint(point.getX(), point.getY(), CARRIER);
                        }
                        //mark all the points around as adjustment so they will not be included in other cases
                        removeAdjust(carrier);
                        System.out.println("done");
                    }
                } else {
                    //mark the carrier points as carrier
                    for (Point point : carrier) {
                        markPoint(point.getX(), point.getY(), CARRIER);
                    }
                    //mark all the points around as adjustment so they will not be included in other cases
                    removeAdjust(carrier);
                    System.out.println("done");
                }
            } else {
                System.out.println("can not add more carrier");
            }
        }
    }

    void print() {
        printBorderLine();
        System.out.println();
        for (int i = 0; i < size; i++) {
            System.out.print("|");
            for (int j = 0; j < size; j++) {
                switch (board[i][j]) {
                    case ADJACENT:
                    case EMPTY:
                        System.out.print("         |");
                        break;
                    case SUBMARINE:
                        System.out.print("submarine|");
                        break;
                    case DESTROYER:
                        System.out.print("destroyer|");
                        break;
                    case CRUISER:
                        System.out.print(" cruiser |");
                        break;
                    case CARRIER:
                        System.out.print(" carrier |");
                        break;
                }
            }
            System.out.println();
            printBorderLine();
            System.out.println();
        }
    }

    private void printBorderLine() {
        for (int i = 0; i < size; i++) {
            System.out.print("----------");
        }
    }

    /**
     * search for first point to start the vessel
     * @return the point if found null otherwise
     */
    private Point calculateFirstPoint() {
        Set<Point> tried = new HashSet<>();
        //generate random point on the board
        Point point = Point.calcPoint(size);
        while (!validPosition(point, null)) {
            //to block from loop for ever if we covered all the available points on the board the result will be null
            if (tried.size() > available) {
                return null;
            }
            tried.add(point);
            point = Point.calcPoint(size);
        }
        return point;
    }

    /**
     * build vessel horizontally by only adding valid points (points which are empty and not adjacent to other vessel)
     * @param start - the first point we start from
     * @param size - the vessel size
     * @return the points which where added to the vessel
     */
    private List<Point> horizontal(final Point start, final int size) {
        List<Point> result = new ArrayList<>();
        result.add(start);
        //first try the point to the right of the start point
        Point next = nextPoint(start, Direction.Right);
        //if the point right to the start point is not valid try to build only to the left of the starting point
        if (next == null) {
            leftDirection(start, size, result);
        } else {
            result.add(next);
            //continue as much as possible add points to the right side
            while (result.size() < size) {
                next = nextPoint(next, Direction.Right);
                if (next == null) {
                    break;
                } else {
                    result.add(next);
                }
            }

            //if the vessel is smaller than the expected size try to add points to the left of the start point
            if (result.size() < size) {
                leftDirection(start, size, result);
            }
        }
        return result;
    }

    private void leftDirection(Point first, int size, List<Point> result) {
        Point next;
        next = nextPoint(first, Direction.Left);
        if (next != null) {
            result.add(next);
            while (result.size() < size) {
                next = nextPoint(next, Direction.Left);
                if (next == null) {
                    break;
                } else {
                    result.add(next);
                }
            }
        }
    }

    /**
     * build vessel vertically by only adding valid points (points which are empty and not adjacent to other vessel)
     * @param start - the first point we start from
     * @param size - the vessel size
     * @return the points which where added to the vessel
     */
    private List<Point> vertical(final Point start, final int size) {
        List<Point> result = new ArrayList<>();
        result.add(start);
        //first try the point below the start point
        Point next = nextPoint(start, Direction.Down);
        //if the point below the start point is not valid try to build only above the start point
        if (next == null) {
            upDirection(start, size, result);
        } else {
            //continue as much as possible add points below the right side
            result.add(next);
            while (result.size() < size) {
                next = nextPoint(next, Direction.Down);
                if (next == null) {
                    break;
                } else {
                    result.add(next);
                }
            }
            //if the vessel is smaller than the expected size try to add points above the start point
            if (result.size() < size) {
                upDirection(start, size, result);
            }
        }
        return result;
    }

    private void upDirection(Point first, int size, List<Point> result) {
        Point next;
        next = nextPoint(first, Direction.Up);
        if (next != null) {
            result.add(next);
            while (result.size() < size) {
                next = nextPoint(next, Direction.Up);
                if (next == null) {
                    break;
                } else {
                    result.add(next);
                }
            }
        }
    }

    /**
     * marks all the points around the vessel as adjacent so they will be potential points in future vessels
     * @param points - points of the vessel
     */
    private void removeAdjust(final List<Point> points) {
        for (Point point : points) {
            int start, end;
            if (point.getX() > 0) {
                //marks all the points in a line above the vessel
                start = point.getY() == 0 ? 0 : point.getY() - 1;
                end = point.getY() == size - 1 ? size - 1 : point.getY() + 1;
                for (int i = start; i <= end; i++) {
                    if (board[point.getX() - 1][i] == EMPTY) {
                        markPoint(point.getX() - 1, i, ADJACENT);
                    }
                }

            }
            if (point.getX() < size - 1) {
                //marks all the points in a line below the vessel
                start = point.getY() == 0 ? 0 : point.getY() - 1;
                end = point.getY() == size - 1 ? size - 1 : point.getY() + 1;
                for (int i = start; i <= end; i++) {
                    if (board[point.getX() + 1][i] == EMPTY) {
                        markPoint(point.getX() + 1, i, ADJACENT);
                    }
                }
            }
            //marks the point to the left of the vessel
            if (point.getY() > 0 && board[point.getX()][point.getY() - 1] == EMPTY) {
                markPoint(point.getX(), point.getY() - 1, ADJACENT);
            }
            //marks the point to the right of the vessel
            if (point.getY() < size - 1 && board[point.getX()][point.getY() + 1] == EMPTY) {
                markPoint(point.getX(), point.getY() + 1, ADJACENT);
            }
        }
    }

    /**
     *
     * @param point - the point which is checked
     * @param ignore - point to ignore which is the point next to the point checked but at the same vessel
     * @return if the point is valid
     */
    private boolean validPosition(final Point point, final Point ignore) {
        if (board[point.getX()][point.getY()] != EMPTY) {
            return false;
        }
        if (point.getX() - 1 >= 0 &&
                (point.equals(ignore) && board[point.getX() - 1][point.getY()] == EMPTY)) {
            return false;
        }
        if (point.getX() + 1 < size &&
                (point.equals(ignore) && board[point.getX() + 1][point.getY()] == EMPTY)) {
            return false;
        }
        int start = point.getX() == 0 ? 0 : point.getX() - 1;
        for (int i = start; i <= point.getX() + 1; i++) {
            if (i >= size) {
                break;
            }
            if (point.getY() - 1 >= 0 &&
                    (point.equals(ignore) && board[i][point.getY() - 1] == EMPTY)) {
                return false;
            }
            if (point.getY() + 1 < size &&
                    (point.equals(ignore) && board[i][point.getY()] == EMPTY)) {
                return false;
            }
        }
        return true;
    }

    private void markPoint(final int x, final int y, final int type) {
        board[x][y] = type;
        available--;
    }

    /**
     *
     * @param point - the starting point
     * @param direction - the direction in which next point will be
     * @return the next point based on the direction if the this point is empty and not adjacent to other vessels
     */
    private Point nextPoint(final Point point, final Direction direction) {
        Point next = null;

        if (direction == Direction.Right && point.getY() < size - 1 && board[point.getX()][point.getY() + 1] == EMPTY) {
            next = new Point(point.getX(), point.getY() + 1);
        } else if (direction == Direction.Left && point.getY() > 0 && board[point.getX()][point.getY() - 1] == EMPTY) {
            next = new Point(point.getX(), point.getY() - 1);
        } else if (direction == Direction.Down && point.getX() < size - 1 && board[point.getX() + 1][point.getY()] == EMPTY) {
            next = new Point(point.getX() + 1, point.getY());
        } else if (direction == Direction.Up && point.getX() > 0 && board[point.getX() - 1][point.getY()] == EMPTY) {
            next = new Point(point.getX() - 1, point.getY());
        }
        if (next != null && !validPosition(next, point)) {
            next = null;
        }
        return next;
    }
}
