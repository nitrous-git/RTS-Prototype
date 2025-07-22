package Main;

import Panel.GamePanel;
import Panel.MinimapPanel;
import Panel.SelectionPanel;

public class GameLoop implements Runnable {
    private final GamePanel GP;
    private final MinimapPanel MP;
    private final SelectionPanel SP;
    private volatile boolean running = false;

    public Thread gameThread;
    private boolean isRunning;
    private final int FPS_SET = 60;
    private final int UPS_SET = 100;

    public GameLoop(GamePanel GP, MinimapPanel MP, SelectionPanel SP) {
        this.GP   = GP;
        this.MP   = MP;
        this.SP = SP;
    }

    public void start() {
        running = true;
        gameThread = new Thread(this, "GameLoop");
        gameThread.start();
    }
    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        double timePerFrame = 1000000000.0/FPS_SET; //in nanoseconds
        double timePerUpdate = 1000000000.0/UPS_SET; //in nanoseconds

        long previousTime = System.nanoTime();

        int frames = 0;
        int updates = 0;
        long lastCheck = System.currentTimeMillis();

        double deltaU = 0;
        double deltaF = 0;

        isRunning = true;
        while (isRunning) {

            long currentTime = System.nanoTime();

            // when dU is >= 1, we remove 1 and add the remainder to dU
            // dU is percentage of frame lost when (currentTime - previousTime) / timePerFrame
            deltaU += (currentTime - previousTime) / timePerUpdate;
            deltaF += (currentTime - previousTime) / timePerFrame;
            previousTime = currentTime;

            if (deltaU >= 1) {
                // update the game
                GP.update();
                updates++;
                deltaU--;
            }

            if (deltaF >= 1) {
                // render the game
                GP.repaint();
                MP.repaint();
                SP.repaint();
                deltaF--;
                frames++;
            }

            if (System.currentTimeMillis() - lastCheck >= 1000) {
                lastCheck = System.currentTimeMillis();
                //System.out.println("FPS : " + frames + " | UPS : " + updates);
                frames = 0;
                updates = 0;
            }
        }
    }

}
