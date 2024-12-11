package me.trigus.pong;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;

public class Game extends JFrame implements Runnable, KeyListener {

    // JFrame settings
    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 800;
    private static final int PADDING_TOP = 100;
    private static final int PADDING_LEFT = 50;
    private static final Rectangle GAME_SIZE = new Rectangle(PADDING_LEFT, PADDING_TOP, WINDOW_WIDTH - PADDING_LEFT * 2, WINDOW_HEIGHT - PADDING_TOP * 2);
    private static final String WINDOW_TITLE = "Pong made by trigus v0.1";

    // fixed game settings
    private static final int KEY_PLAYER_ONE_UP = KeyEvent.VK_W;
    private static final int KEY_PLAYER_ONE_DOWN = KeyEvent.VK_S;
    private static final int KEY_PLAYER_TWO_UP = KeyEvent.VK_UP;
    private static final int KEY_PLAYER_TWO_DOWN = KeyEvent.VK_DOWN;
    private static final int KEY_PAUSE = KeyEvent.VK_SPACE;
    private static final int KEY_EXIT = KeyEvent.VK_ESCAPE;
    private static final int KEY_SINGLE_PLAYER_SWITCH = KeyEvent.VK_Q;
    private static final float PADDLE_WIDTH = 15;
    private static final float PADDLE_HEIGHT = 80;
    private static final float BALL_SIZE = 15;
    private static final int BALL_SPAWN_OFFSET = 150;
    private static final float BALL_VELOCITY = 0.5f;
    private static final float PADDLE_VELOCITY = 0.4f;
    private static final int MIN_FRAME_TIME = 10;

    // graphics settings
    private static final Color BG_COLOR = new Color(25, 25, 25);
    private static final Color TEXT_COLOR = new Color(150, 150, 150);
    private static final Color PADDLE_COLOR = new Color(57, 36, 86);
    private static final Color PADDLE_BORDER_COLOR = new Color(159, 104, 225);
    private static final Color BALL_COLOR = new Color(36, 79, 86);
    private static final Color BALL_BORDER_COLOR = new Color(92, 224, 241);
    private static final Color MARKINGS_COLOR = new Color(30, 90, 50);
    private static final Color MARKINGS_BORDER_COLOR = new Color(60, 180, 100);

    // variable game settings
    private boolean singlePlayer = true;
    private boolean gamePaused = false;
    private boolean exitGame = false;
    private float posPaddleOne = GAME_SIZE.y + GAME_SIZE.height / 2f;
    private float posPaddleTwo = GAME_SIZE.y + GAME_SIZE.height / 2f;
    private float posBallX = GAME_SIZE.x + GAME_SIZE.width / 2f;
    private float posBallY = GAME_SIZE.y + GAME_SIZE.height / 2f;
    private float ballDirection = 225;
    // 0: paddle is not moving, 1: paddle is moving down, -1: paddle is moving up
    private int paddleOneDirection = 0;
    // 0: paddle is not moving, 1: paddle is moving down, -1: paddle is moving up
    private int paddleTwoDirection = 0;
    private int goalsPlayerOne = 0;
    private int goalsPlayerTwo = 0;


    public Game() {
        setTitle(WINDOW_TITLE);
        setSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setUndecorated(true);
        addKeyListener(this);

        add(new JPanel(){
            @Override
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                render(g);
            }
        });

        setVisible(true);
    }

    @Override
    public void run() {
        // before loop

        long lastTime = System.currentTimeMillis();

        while(!exitGame) {
            // time logic
            long now = System.currentTimeMillis();
            long deltaTime = now - lastTime;

            if (deltaTime < MIN_FRAME_TIME) { continue; }
            else { lastTime = now; }

            // pause logic
            if (gamePaused) { continue; }

            // update paddles
            posPaddleOne += paddleOneDirection * (int) (PADDLE_VELOCITY * deltaTime);
            posPaddleTwo += paddleTwoDirection * (int) (PADDLE_VELOCITY * deltaTime);

            // paddle position check
            if ( posPaddleOne < PADDLE_HEIGHT / 2 + GAME_SIZE.y) posPaddleOne = PADDLE_HEIGHT / 2 + GAME_SIZE.y;
            else if(posPaddleOne > GAME_SIZE.y + GAME_SIZE.height - 1 - PADDLE_HEIGHT / 2) posPaddleOne = GAME_SIZE.y + GAME_SIZE.height - 1 - PADDLE_HEIGHT / 2;
            if (posPaddleTwo < PADDLE_HEIGHT / 2 + GAME_SIZE.y) posPaddleTwo = PADDLE_HEIGHT / 2 + GAME_SIZE.y;
            else if(posPaddleTwo > GAME_SIZE.y + GAME_SIZE.height - 1 - PADDLE_HEIGHT / 2) posPaddleTwo = GAME_SIZE.y + GAME_SIZE.height - 1 - PADDLE_HEIGHT / 2;

            // update ball
            posBallX += (float) (BALL_VELOCITY * deltaTime * Math.sin(Math.toRadians(ballDirection)));
            posBallY += (float) (BALL_VELOCITY * deltaTime * Math.cos(Math.toRadians(ballDirection)));

            // ball position check top
            if (posBallY < BALL_SIZE / 2 + GAME_SIZE.y) { ballDirection = 180 - ballDirection; }
            if (posBallY > GAME_SIZE.y + GAME_SIZE.height - 1 - BALL_SIZE / 2) { ballDirection = 180 - ballDirection; }
            // ball position check paddles
            if((posBallY + BALL_SIZE / 2 >= posPaddleOne - PADDLE_HEIGHT / 2) && (posBallY - BALL_SIZE / 2 <= posPaddleOne + PADDLE_HEIGHT / 2)) { // ball is on height with paddle one
                if (posBallX - BALL_SIZE / 2 - GAME_SIZE.x <= PADDLE_WIDTH) { //ball touches paddle
                    float hitPoint = (posPaddleOne - posBallY) / (PADDLE_HEIGHT / 2);
                    ballDirection = 90 + hitPoint * 45;
                }
            }
            if((posBallY + BALL_SIZE / 2 >= posPaddleTwo - PADDLE_HEIGHT / 2) && (posBallY - BALL_SIZE / 2 <= posPaddleTwo + PADDLE_HEIGHT / 2)) { // ball is on height with paddle two
                if (posBallX + BALL_SIZE / 2 >= GAME_SIZE.x + GAME_SIZE.width - PADDLE_WIDTH) { //ball touches paddle
                    float hitPoint = (posPaddleTwo - posBallY) / (PADDLE_HEIGHT / 2);
                    ballDirection = 270 - hitPoint * 45;
                }
            }

            // ball position check side
            if (posBallX - GAME_SIZE.x < BALL_SIZE / 2) {
                goalsPlayerTwo += 1;
                posBallY = (float) (GAME_SIZE.y + GAME_SIZE.height / 2);
                posBallX = (float) (GAME_SIZE.x + GAME_SIZE.width / 2 - BALL_SPAWN_OFFSET);
                ballDirection = 90;
            }
            if (posBallX > GAME_SIZE.x + GAME_SIZE.width - 1 - BALL_SIZE / 2) {
                goalsPlayerOne += 1;
                posBallY = (float) (GAME_SIZE.y + GAME_SIZE.height / 2);
                posBallX = (float) (GAME_SIZE.x + GAME_SIZE.width / 2 + BALL_SPAWN_OFFSET);
                ballDirection = 270;
            }

            // computer logic
            if (singlePlayer) {
                if (posBallY - PADDLE_HEIGHT / 15 > posPaddleTwo + PADDLE_HEIGHT / 2) {
                    paddleTwoDirection = 1;
                } else if(posBallY + PADDLE_HEIGHT / 15 < posPaddleTwo - PADDLE_HEIGHT / 2) {
                    paddleTwoDirection = -1;
                } else {
                    paddleTwoDirection = 0;
                }
            }

            // ball direction < 0
            if (ballDirection < 0) ballDirection += 360;
            // ball direction >= 360
            if (ballDirection >= 360) ballDirection -= 360;

            // win condition
            if (goalsPlayerTwo == 8 || goalsPlayerOne == 8) {
                gamePaused = true;
            }

            // repaint :)
            repaint();
        }
    }

    private void render(Graphics g){
        // fill background
        g.setColor(BG_COLOR);
        g.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        //window decoration
        g.setColor(TEXT_COLOR);
        g.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        g.setColor(BG_COLOR);
        g.fillRect(2, 2, WINDOW_WIDTH - 4, WINDOW_HEIGHT - 4);

        // render background
        g.setColor(MARKINGS_BORDER_COLOR);
        g.fillRect(GAME_SIZE.x - 6, GAME_SIZE.y - 4, GAME_SIZE.width + 12, GAME_SIZE.height + 8);
        g.setColor(BG_COLOR);
        g.fillRect(GAME_SIZE.x - 2, GAME_SIZE.y, GAME_SIZE.width + 4, GAME_SIZE.height);
        g.setColor(MARKINGS_BORDER_COLOR);
        g.fillOval((GAME_SIZE.x * 2 + GAME_SIZE.width) / 2 - 100, (GAME_SIZE.height + GAME_SIZE.y * 2) / 2 - 100, 200, 200);
        g.setColor(BG_COLOR);
        g.fillOval((GAME_SIZE.x * 2 + GAME_SIZE.width) / 2 - 96, (GAME_SIZE.height + GAME_SIZE.y * 2) / 2 - 96, 192, 192);
        g.setColor(MARKINGS_BORDER_COLOR);
        g.fillRect(GAME_SIZE.x + GAME_SIZE.width / 2 - 2, GAME_SIZE.y, 4, GAME_SIZE.height);


        // render ball
        g.setColor(BALL_COLOR);
        g.fillOval((int) (posBallX - BALL_SIZE / 2), (int) (posBallY - BALL_SIZE / 2), (int) BALL_SIZE, (int) BALL_SIZE);
        g.setColor(BALL_BORDER_COLOR);
        g.drawOval((int) (posBallX - BALL_SIZE / 2), (int) (posBallY - BALL_SIZE / 2), (int) BALL_SIZE, (int) BALL_SIZE);

        // render paddles
        g.setColor(PADDLE_COLOR);
        g.fillRect(GAME_SIZE.x, (int) (posPaddleOne - PADDLE_HEIGHT / 2), (int) PADDLE_WIDTH, (int) PADDLE_HEIGHT); // paddle player one
        g.fillRect((int) (GAME_SIZE.x + GAME_SIZE.width - PADDLE_WIDTH), (int) (posPaddleTwo - PADDLE_HEIGHT / 2), (int) PADDLE_WIDTH, (int) PADDLE_HEIGHT); // paddle player two
        g.setColor(PADDLE_BORDER_COLOR);
        g.drawRect(GAME_SIZE.x, (int) (posPaddleOne - PADDLE_HEIGHT / 2), (int) PADDLE_WIDTH, (int) PADDLE_HEIGHT); // paddle player one
        g.drawRect((int) (GAME_SIZE.x + GAME_SIZE.width - PADDLE_WIDTH), (int) (posPaddleTwo - PADDLE_HEIGHT / 2), (int) PADDLE_WIDTH, (int) PADDLE_HEIGHT); // paddle player two

        // render gui
        g.setColor(TEXT_COLOR);
        g.setFont(Font.getFont("Arial Black"));
        String goalCount = goalsPlayerOne + " : " + goalsPlayerTwo;
        Rectangle2D rect= g.getFontMetrics().getStringBounds(goalCount, g);

        // add CPU info, if singleplayer
        if (singlePlayer) {
            goalCount += " CPU";
        }

        // add winner text, if someone won
        if (goalsPlayerOne == 8) {
            goalCount = "WINNER - " + goalCount;
        } else if (goalsPlayerTwo == 8) {
            goalCount = goalCount + " - WINNER";
        }

        g.drawString(goalCount, (int) ((double) WINDOW_WIDTH / 2 - (rect.getWidth() / 2)), 50);

        String bottomText = "Exit: ESC, Pause: SPACE, Switch SinglePlayer: Q, Controls Player One: W/S, Controls Player Two: UP/DOWN";
        g.drawString(bottomText, 50, WINDOW_HEIGHT - 50);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KEY_PLAYER_ONE_DOWN -> {
                paddleOneDirection = 1;
            } case KEY_PLAYER_ONE_UP -> {
                paddleOneDirection = -1;
            } case KEY_PLAYER_TWO_DOWN -> {
                if (!singlePlayer) paddleTwoDirection = 1;
            } case KEY_PLAYER_TWO_UP -> {
                if (!singlePlayer) paddleTwoDirection = -1;
            } case KEY_PAUSE -> {
                gamePaused = !gamePaused;
                if (goalsPlayerOne == 8 || goalsPlayerTwo == 8) {
                    goalsPlayerOne = 0;
                    goalsPlayerTwo = 0;
                }
            } case KEY_SINGLE_PLAYER_SWITCH -> {
                singlePlayer = !singlePlayer;
                if (!singlePlayer) paddleTwoDirection = 0;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KEY_PLAYER_ONE_DOWN -> {
                paddleOneDirection = 0;
            } case KEY_PLAYER_ONE_UP -> {
                paddleOneDirection = 0;
            } case KEY_PLAYER_TWO_DOWN -> {
                if (!singlePlayer) paddleTwoDirection = 0;
            } case KEY_PLAYER_TWO_UP -> {
                if (!singlePlayer) paddleTwoDirection = 0;
            } case KEY_EXIT -> {
                exitGame = true;
            }
        }
    }
}
