package com.marchex.bowling;

import com.marchex.bowling.exceptions.FrameIsFullException;
import com.marchex.bowling.exceptions.InvalidScoreException;

import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * This class encapsulates a score board for a bowling game for one user.
 */
public class ScoreBoard {
    private static final int NUMBER_OF_FRAMES = 10;

    private LinkedList<Frame> frames;

    private int currentFrameId;

    public ScoreBoard() {
        this.currentFrameId = 0;

        this.frames = new LinkedList<>();
        IntStream.range(0, NUMBER_OF_FRAMES)
                .boxed()
                .forEach(i -> {
                    this.frames.add(new Frame(i));
                });
    }

    /**
     * Record the next ball points.
     *
     * @param points number of points to add
     * @throws InvalidScoreException
     * @throws FrameIsFullException
     */
    public void recordPoints(final int points) throws InvalidScoreException, FrameIsFullException {
        if (this.isComplete()) {
            return;
        }

        if (this.frames.get(this.currentFrameId).hasFinished()) {
            this.currentFrameId++;
        }

        final Frame currentFrame = this.frames.get(this.currentFrameId);
        currentFrame.recordScore(points);
        tryUpdateLastFrameBonus(currentFrame);
    }

    /**
     * Returns true when the score board is completely filled and the game is done
     *
     * @return true when the score board is filled
     */
    public boolean isComplete() {
        return this.currentFrameId == NUMBER_OF_FRAMES - 1 &&
                this.frames.get(this.currentFrameId).hasFinished();
    }

    /**
     * Returns the total of all available scored frames
     *
     * @return latest score known in the game
     */
    public int scoreBoardTotal() {
        return this.frames.stream()
                .map(Frame::getFrameScore)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .mapToInt(Integer::intValue)
                .sum();
    }

    private void tryUpdateLastFrameBonus(final Frame currentFrame) {
        final Frame lastFrame = this.currentFrameId >= 1 ? this.frames.get(this.currentFrameId - 1) : null;
        final Frame twoFramesPrior = (this.currentFrameId >= 2) ? this.frames.get(this.currentFrameId - 2) : null;

        if (lastFrame != null) {
            lastFrame.setBonus(currentFrame);
        }

        if (twoFramesPrior != null) {
            twoFramesPrior.setBonus(lastFrame, currentFrame);
        }
    }

}
