package org.shadowrun.logic;

import javafx.scene.image.Image;

import java.util.List;

public class Animation {

    private List<Image> frames;

    private int currentFrame = 0;

    private long lastTime;

    private long period;

    private int fps;

    public Animation(List<Image> frames, int fps) {
        this.frames = frames;
        this.fps = fps;
        this.period = (long)((double)((1 / (double)fps) * 1_000_000_000));
    }

    public List<Image> getFrames() {
        return frames;
    }

    public double getLastTime() {
        return lastTime;
    }

    public double getPeriod() {
        return period;
    }

    public int getFps() {
        return fps;
    }

    public void setFrames(List<Image> frames) {
        this.frames = frames;
    }

    public int getCurrentFrameIndex() {
        return currentFrame;
    }

    public Image getCurrentFrame() {
        return frames.get(currentFrame);
    }

    public void update(long dt) {
        lastTime += dt;
        if(lastTime >= period) {
            lastTime -= period;
            currentFrame = (currentFrame + 1) % frames.size();
            update(0);
        }
    }
}
