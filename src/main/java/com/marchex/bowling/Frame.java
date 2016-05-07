package com.marchex.bowling;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.marchex.bowling.exceptions.FrameIsFullException;
import com.marchex.bowling.exceptions.InvalidScoreException;

import java.util.ArrayList;
import java.util.Optional;


public class Frame {
    private static final int DEFAULT_NUM_BALLS_PER_FRAME = 2;
    private static final int LAST_FRAME_NUMBER = 9;
    private static final int DEFAULT_TOTAL_SCORE = 10;
    private static final int MAX_BALLS_IN_LAST_FRAME = 3;

    private int frameId;
    private ArrayList<Integer> balls;
    private Optional<Integer> bonus;

    public Frame(final int frameId) {
        Preconditions.checkArgument(frameId <= LAST_FRAME_NUMBER, "Valid FrameID is between 0 and 9.");

        this.frameId = frameId;
        this.balls = new ArrayList<>(numAllowedBalls());
        this.bonus = Optional.empty();
    }

    public int numAllowedBalls() {
        if (!isLastFrame()) {
            return DEFAULT_NUM_BALLS_PER_FRAME;
        } else {
            // the last frame gets 2 balls by default unless the player rolled either a strike in
            // ball 1 or rolled a spare within ball 2
            return MAX_BALLS_IN_LAST_FRAME;
        }
    }

    /**
     * Record a score for this frame. When the score added blows the frame over expected top score (normally 10), then
     * an InvalidScoreException is thrown. If all balls for a frame is played trying to add another score causes
     * FrameIsFullException to be thrown.
     *
     * @param score The score
     */
    public void recordScore(final int score) throws FrameIsFullException, InvalidScoreException {
        if (hasFinished()) {
            throw new FrameIsFullException("The frame with frameId " + this.frameId + " is full");
        }

        if (getScore() + score > DEFAULT_TOTAL_SCORE) {
            if (!isLastFrame()) {
                throw new InvalidScoreException("Cannot add a score that makes the frame blow up beyond 10");
            } else {
                // The last frame can only continue to record score if the first two frames earned a strike or a spare
                if (this.balls.size() == 2 && !(isStrike() || isSpare())) {
                    throw new InvalidScoreException("Cannot add more score to the last frame");
                }
            }
        }

        this.balls.add(score);
    }

    public boolean isStrike() {
        if (!isLastFrame()) {
            return this.balls.size() == 1 && this.balls.get(0) == DEFAULT_TOTAL_SCORE;
        } else {
            return this.balls.stream().anyMatch(i -> i == DEFAULT_TOTAL_SCORE);
        }
    }

    public boolean isSpare() {
        return this.balls.size() >= 2 && getScore() >= DEFAULT_TOTAL_SCORE;
    }

    private boolean isLastFrame() {
        return this.frameId == LAST_FRAME_NUMBER;
    }

    /**
     * @return true when the number of balls played is equal to the number of allowed balls to play depending on the
     * frame number
     */
    public boolean hasFinished() {
        if (!isLastFrame() && isStrike()) {
            return true;
        }

        if (isLastFrame()) {
            if (!(isStrike() || isSpare())) {
                return this.balls.size() == DEFAULT_NUM_BALLS_PER_FRAME;
            } else {
                return this.balls.size() == MAX_BALLS_IN_LAST_FRAME;
            }
        }

        return this.balls.size() == numAllowedBalls();
    }

    /**
     * @return Total of the scores from the balls played in the frame
     */
    @VisibleForTesting
    protected int getScore() {
        return this.balls.stream().mapToInt(Integer::intValue).sum();
    }


    /**
     * This function tallies the score for the frame It throws a NoScoreAvailableException when the frame is not ready
     * to be tallied
     *
     * @return Optional<Integer> is empty if the frame is not ready to be scored.
     */
    public Optional<Integer> getFrameScore() {
        if (isReadyToTally()) {
            int sum = getScore();

            if ((isSpare() || isStrike()) && this.bonus.isPresent()) {
                sum += this.bonus.get();
            }

            return Optional.of(sum);
        } else {
            return Optional.empty();
        }
    }

    /**
     * @return true when all the allowed balls are played and if the frame is not the last frame, depending on whether
     * there is a strike or spare
     */
    @VisibleForTesting
    protected boolean isReadyToTally() {
        if (!isLastFrame()) {
            if (isStrike() || isSpare()) {
                return this.bonus.isPresent();
            }
        }

        return hasFinished();
    }

    /**
     * Sets the bonus for a frame that's carried over from the next frame.
     *
     * @param nextFrame the next frame in the game
     */
    public void setBonus(final Frame nextFrame) {
        if (nextFrame == null || nextFrame.getFrameId() != this.frameId + 1) {
            return;
        }

        if (isStrike() && nextFrame.isReadyToTally()) {
            this.bonus = Optional.of(nextFrame.getFrameScore().get());
        }

        if (isSpare() && nextFrame.getFirstBallScore().isPresent()) {
            this.bonus = Optional.of(nextFrame.getFirstBallScore().get());
        }
    }

    /**
     * Set bonus for two consecutive strikes according to rules in https://en.wikipedia.org/wiki/Strike_(bowling)
     *
     * @param nextFrame  the next frame
     * @param nextFrame2 the frame 2 frames from current
     */
    public void setBonus(final Frame nextFrame, final Frame nextFrame2) {
        if (nextFrame == null || nextFrame.getFrameId() != this.frameId + 1) {
            return;
        }

        if (nextFrame2 == null || nextFrame2.getFrameId() != this.frameId + 2) {
            return;
        }

        if (isStrike() && nextFrame.isStrike()) {
            final int nextFrameScore = nextFrame.getScore();
            final Optional<Integer> nextFrame2FirstBall = nextFrame2.getFirstBallScore();

            if (nextFrame2FirstBall.isPresent()) {
                this.bonus = Optional.of(nextFrameScore + nextFrame2FirstBall.get());
            }
        }
    }

    /**
     * Convenient method to get the score of the first ball
     *
     * @return Optional.empty if the first ball hasn't played yet
     */
    public Optional<Integer> getFirstBallScore() {
        if (this.balls.size() >= 1) {
            return Optional.of(this.balls.get(0));
        } else {
            return Optional.empty();
        }

    }

    public int getFrameId() {
        return this.frameId;
    }
}
