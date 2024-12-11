package me.trigus.pong;

public class Main {
    public static void main(String[] args) {
        Game game = new Game();
        game.run();

        game.setVisible(false);
        System.exit(0);
    }
}