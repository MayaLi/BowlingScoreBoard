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
    public void testRecordScoreLastFrame_shouldSucceed() throws Exception {
        // There is no strike or spare in the first 2 balls of the last frame which should end the frame
        final Frame frame = new Frame(9);

        frame.recordScore(4);
        frame.recordScore(5);
        assertThat(frame.hasFinished()).as("When there is no more spare left").isTrue();

        assertThatThrownBy(() -> frame.recordScore(1))
                .as("Record score when the frame is finished should receive rexception")
                .isInstanceOf(FrameIsFullException.class);
    }

    @Test
    public void testRecordScoreLastFrame_shouldSucceedWithSpare() throws Exception {
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
    public void testRecordScoreLastFrame_shouldSucceedWithStrike() throws Exception {
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
    public void testRecordScoreLastFrame_shouldSucceedWithAllStrikes() throws Exception {
        final Frame frame = new Frame(9);
        frame.recordScore(10);
        assertThat(frame.hasFinished()).as("There are more balls for the last frame").isFalse();

        frame.recordScore(10);
        assertThat(frame.hasFinished()).as("There are more balls for the last frame").isFalse();

        frame.recordScore(10);
        assertThat(frame.hasFinished()).as("There are 3 balls for the final frame").isTrue();

        assertThat(frame.getFrameScore()).as("Total score should be 30")
                .isPresent()
                .isEqualTo(Optional.of(30));
    }


    @Test
    public void testSetBonus_shouldSucceedWithSpare() throws Exception {
        final Frame frame1 = new Frame(1);
        final Frame frame2 = new Frame(2);
        final Frame frame3 = new Frame(3);

        frame1.recordScore(4);
        frame1.recordScore(6);
        assertThat(frame1.hasFinished());
        assertThat(frame1.getFrameScore()).as("Cannot score a spare frame yet").isNotPresent();

        frame2.recordScore(7);
        assertThat(frame1.isReadyToTally()).isFalse();

        frame1.setBonus(frame2);
        assertThat(frame1.getFrameScore()).as("Frame1 is ready to score").isPresent()
                .isEqualTo(Optional.of(17));

        frame2.recordScore(3);
        assertThat(frame2.hasFinished());
        assertThat(frame2.getFrameScore()).as("Cannot score a spare frame yet").isNotPresent();

        frame1.setBonus(frame2, frame3);
        assertThat(frame1.getFrameScore()).as("Frame1 is ready to score").isPresent()
                .isEqualTo(Optional.of(17));

        frame2.setBonus(frame3);
        assertThat(frame2.isReadyToTally()).as("Frame2 cannot be tallied until first ball of frame 3 is rolled").isFalse();

        frame3.recordScore(10);
        frame2.setBonus(frame3);
        assertThat(frame2.getFrameScore()).as("Frame2 is ready to score").isPresent()
                .isEqualTo(Optional.of(20));
    }

    @Test
    public void testSetBonus_shouldSucceedWithStrike() throws Exception {
        final Frame frame1 = new Frame(1);
        final Frame frame2 = new Frame(2);

        frame1.recordScore(10);
        assertThat(frame1.hasFinished());
        assertThat(frame1.getFrameScore()).as("Cannot score a spare frame yet").isNotPresent();

        frame2.recordScore(7);
        assertThat(frame1.isReadyToTally()).isFalse();

        frame2.recordScore(2);
        assertThat(frame2.hasFinished());
        assertThat(frame1.isReadyToTally()).isFalse();
        assertThat(frame2.isReadyToTally()).isTrue();
        assertThat(frame2.getFrameScore()).as("Frame 2 is scored").isPresent().isEqualTo(Optional.of(9));

        frame1.setBonus(frame2);
        assertThat(frame1.isReadyToTally()).isTrue();
        assertThat(frame1.getFrameScore()).as("Frame1 is ready to score")
                .isPresent()
                .isEqualTo(Optional.of(19));
    }


    @Test
    public void testSetBonus_shouldSucceedWith2Strikes() throws Exception {
        final Frame frame1 = new Frame(1);
        final Frame frame2 = new Frame(2);
        final Frame frame3 = new Frame(3);

        frame1.recordScore(10);
        frame2.recordScore(10);
        frame1.setBonus(frame2);
        assertThat(frame1.hasFinished());
        assertThat(frame1.isReadyToTally()).isFalse();
        assertThat(frame2.isReadyToTally()).isFalse();

        frame3.recordScore(10);
        assertThat(frame1.isReadyToTally()).isFalse();

        frame1.setBonus(frame2);
        frame1.setBonus(frame2, frame3);
        frame2.setBonus(frame3);
        assertThat(frame2.isReadyToTally()).isFalse();
        assertThat(frame1.isReadyToTally())
                .as("First of 2 consecutive strikes is ready when the second frame first ball is rolled")
                .isTrue();

        assertThat(frame1.getFrameScore())
                .as("First of 2 consecutive strikes accumulate the next frame and the first ball of the 3rd frame")
                .isPresent()
                .isEqualTo(Optional.of(30));
    }
}
