import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Board board = new Board(Integer.parseInt(args[0]));
        Scanner in = new Scanner(System.in);
        String command;
        command = in.nextLine();
        while (!"quit".equals(command)) {
            switch (command) {
                case "submarine":
                    board.placeSubmarine();
                    break;
                case "destroyer":
                    board.placeDestroyer();
                    break;
                case "cruiser":
                    board.placeCruiser();
                    break;
                case"carrier":
                    board.placeCarrier();
                    break;
                case "print":
                    board.print();
                    break;
                default:
                    System.out.println("wrong command");

            }
            command = in.nextLine();
        }
    }

}
