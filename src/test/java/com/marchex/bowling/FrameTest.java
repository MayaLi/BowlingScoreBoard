package com.marchex.bowling;

import com.marchex.bowling.exceptions.FrameIsFullException;
import com.marchex.bowling.exceptions.InvalidScoreException;
import org.junit.Test;

import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class FrameTest {
    private static final Random intGenerator = new Random(System.currentTimeMillis());

    @Test
    public void testCreateFrame_shouldSucceed() throws Exception {
        final int frameId = intGenerator.nextInt(10);
        final Frame frame = new Frame(frameId);

        assertThat(frame.getFrameId()).as("Frame id should be expected").isEqualTo(frameId);
        assertThat(frame.getScore()).as("Frame score should be 0").isEqualTo(0);
        assertThat(frame.hasFinished()).as("The frame hasn't started yet").isFalse();
        assertThat(frame.getFrameScore()).as("The frame cannot be scored yet").isEqualTo(Optional.empty());
        assertThat(frame.isReadyToTally()).as("The game is not ready to be scored yet").isFalse();
    }

    @Test
    public void testRecordScore_shouldAddScoreSimple() throws Exception {
        final int frameId = intGenerator.nextInt(9);
        final Frame frame = new Frame(frameId);

        frame.recordScore(4);
        assertThat(frame.getScore()).as("Frame should have a running score of 4").isEqualTo(4);

        frame.recordScore(5);
        assertThat(frame.getScore()).as("Frame score should increment to 9").isEqualTo(9);
        assertThat(frame.isReadyToTally()).as("The frame is ready to be scored as it is complete").isTrue();

        assertThat(frame.isSpare()).as("This frame is not a spare").isFalse();
        assertThat(frame.isStrike()).as("This frame is not a strike").isFalse();
    }

    @Test
    public void testRecordScore_shouldSucceedWithSpare() throws Exception {
        final int frameId = intGenerator.nextInt(9);
        final Frame frame = new Frame(frameId);

        frame.recordScore(1);
        assertThat(frame.getScore()).isEqualTo(1).as("Frame should have a running score of 1");
        assertThat(frame.hasFinished()).isFalse().as("The frame is not done yet");

        frame.recordScore(9);
        assertThat(frame.getScore()).as("Frame score should increment to 10").isEqualTo(10);
        assertThat(frame.isReadyToTally()).as("The frame is NOT ready to be scored as it has a spare").isFalse();

        assertThat(frame.isSpare()).as("This frame has a spare").isTrue();
        assertThat(frame.isStrike()).as("This frame is not a strike").isFalse();
    }

    @Test
    public void testRecordScore_shouldSucceedWithStrike() throws Exception {
        final int frameId = intGenerator.nextInt(9);
        final Frame frame = new Frame(frameId);

        frame.recordScore(10);
        assertThat(frame.getScore()).as("Frame should have a running score of 10").isEqualTo(10);
        assertThat(frame.hasFinished()).as("The frame is finished").isTrue();

        assertThat(frame.isReadyToTally()).as("The frame is NOT ready to be scored as it has a strike").isFalse();
        assertThat(frame.isSpare()).as("This frame is not a spare").isFalse();
        assertThat(frame.isStrike()).as("This frame is a strike").isTrue();
    }

    @Test
    public void testRecordScore_shouldFailWithTooManyScores() throws Exception {
        final int frameId = intGenerator.nextInt(9);
        final Frame frame = new Frame(frameId);

        frame.recordScore(1);
        frame.recordScore(8);
        assertThatThrownBy(() -> frame.recordScore(3))
                .as("Should receive FrameIsFullException")
                .isInstanceOf(FrameIsFullException.class);
    }

    @Test
    public void testRecordScore_shouldFailWithInvalidScores() throws Exception {
        final int frameId = intGenerator.nextInt(9);
        final Frame frame = new Frame(frameId);

        frame.recordScore(5);
        assertThatThrownBy(() -> frame.recordScore(6))
                .as("Should receive InvalidScoreException for scores over 10")
                .isInstanceOf(InvalidScoreException.class);
    }

    @Test
    public void testRecordScore_shouldSucceedForLastFrame() throws Exception {
        // There is a spare in the first two balls of last frame
        final Frame frame = new Frame(9);

        frame.recordScore(1);
        frame.recordScore(9);
        assertThat(frame.hasFinished()).as("When there is a spare in the last frame, it is not finished").isFalse();

        frame.recordScore(6);
        assertThat(frame.getFrameScore().get()).as("The frame score should be 16").isEqualTo(16);
        assertThat(frame.hasFinished()).as("All balls played for the last frame").isTrue();
    }

    @Test
    public void testRecordScore_shouldSucceedForLastFrameWithStrike() throws Exception {
        // There is a strike in the first ball
        final Frame frame = new Frame(9);
        frame.recordScore(10);
        frame.recordScore(1);
        assertThat(frame.hasFinished()).as("There are more balls for the last frame").isFalse();

        frame.recordScore(9);
        assertThat(frame.hasFinished()).as("All 3 balls are played in the last frame").isTrue();
        assertThat(frame.getFrameScore().get()).as("Should tally all 3 balls in the last frame").isEqualTo(20);
        assertThat(frame.isSpare()).as("The frame has a spare").isTrue();
        assertThat(frame.isStrike()).as("The last frame has a strike").isTrue();
    }

    @Test
    public void testRecordScore_shouldSucceedForLastFrameWithNoStrike() throws Exception {
        final Frame frame = new Frame(9);

        frame.recordScore(1);
        frame.recordScore(7);
        assertThat(frame.hasFinished()).as("The last frame with no spare or strike in the first two falls finishes here").isTrue();

        assertThatThrownBy(() -> frame.recordScore(6))
                .as("Should not be able to record another score when the last frame is finished")
                .isInstanceOf(FrameIsFullException.class);
    }

    @Test
    public void testSetBonus() throws Exception {

    }
}
