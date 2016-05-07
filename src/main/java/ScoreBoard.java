import exceptions.FrameIsFullException;
import exceptions.InvalidScoreException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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

        IntStream.range(0, NUMBER_OF_FRAMES)
                .boxed()
                .forEach(i -> {
                    this.frames.add(new Frame(i));
                });
    }

    public void recordPoints(final int numPins) throws InvalidScoreException, FrameIsFullException {
        if (this.frames.get(this.currentFrameId).hasFinished()) {
            this.currentFrameId++;
        }

        final Frame currentFrame = this.frames.get(this.currentFrameId);
        currentFrame.recordScore(numPins);

        tryUpdateLastFrameBonus(currentFrame);
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

    public String toString() {
        throw new NotImplementedException();
    }

    /**
     * Returns true when the score board is completely filled and the game is done
     *
     * @return true when the score board is filled
     */
    public boolean isComplete() {
        return this.currentFrameId == NUMBER_OF_FRAMES - 1 && this.frames.get(this.currentFrameId).hasFinished();
    }

    /**
     * Returns the last tallied frame score. If the game is complete, then the score is the final score for the game.
     *
     * @return latest score known in the game
     */
    public List<Optional<Integer>> getFrameScores() {
        int runningSum = 0;
        final List<Optional<Integer>> frameScores = new ArrayList<>(NUMBER_OF_FRAMES);

        for (final Frame frame : this.frames) {
            final Optional<Integer> frameScore = frame.getFrameScore();
            if (frameScore.isPresent()) {
                runningSum += frameScore.get();
                frameScores.add(Optional.of(runningSum));
            } else {
                frameScores.add(Optional.empty());
            }
        }

        return frameScores;
    }
}
