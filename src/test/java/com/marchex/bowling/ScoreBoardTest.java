package com.marchex.bowling;

import org.junit.Test;

import java.util.LinkedList;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class ScoreBoardTest {

    @Test
    public void testCreateScoreBoard_shouldSucceed() throws Exception {
        final ScoreBoard scoreBoard = new ScoreBoard();
        assertThat(scoreBoard.isComplete())
                .as("New score board should not be complete")
                .isFalse();
        assertThat(scoreBoard.scoreBoardTotal()).as("No points collected yet").isEqualTo(0);
    }

    @Test
    public void testRecordPoints_shouldSucceed() throws Exception {
        final ScoreBoard board = new ScoreBoard();

        // frame 0: score 9
        board.recordPoints(4);
        board.recordPoints(5);
        assertThat(board.scoreBoardTotal()).as("Total all balls").isEqualTo(9);

        // frame 1: score 22 - spare score
        board.recordPoints(1);
        board.recordPoints(9);
        assertThat(board.scoreBoardTotal()).as("Need to wait until the next ball played to score").isEqualTo(9);

        // frame 2: score 29
        board.recordPoints(3);
        assertThat(board.scoreBoardTotal()).as("Need to wait until the next ball played to score").isEqualTo(22);
        board.recordPoints(4);
        assertThat(board.scoreBoardTotal()).isEqualTo(29);
        assertThat(board.getCurrentFrameId()).as("The frame has advanced automatically to 3").isEqualTo(3);

        // frame 3: score 55 - strike score
        board.recordPoints(10);
        assertThat(board.scoreBoardTotal()).as("Need to wait until the full next frame is played to score a strike").isEqualTo(29);

        // frame 4: score 75 - > 2consecutive strike score
        board.recordPoints(10);
        assertThat(board.getCurrentFrameId()).as("The frame has advanced automatically.").isEqualTo(5);

        // frame 5: score 75
        board.recordPoints(6);
        assertThat(board.scoreBoardTotal()).as("Can tally scores up to frame 3").isEqualTo(71);
        board.recordPoints(4);
        assertThat(board.scoreBoardTotal()).as("Can tally scores up to frame 4").isEqualTo(75);

        // frame 6: score 113
        board.recordPoints(10);
        assertThat(board.scoreBoardTotal()).as("Can tally scores up to frame 5").isEqualTo(95);

        // frame 7: score 121
        board.recordPoints(7);
        board.recordPoints(1);
        assertThat(board.scoreBoardTotal()).as("Can tally scores up to frame 7").isEqualTo(121);

        // frame 8: score 151
        board.recordPoints(10);
        assertThat(board.scoreBoardTotal()).as("Can tally scores up to frame 7").isEqualTo(121);

        // frame 9: score 177
        board.recordPoints(10);
        board.recordPoints(10);
        assertThat(board.isComplete()).isFalse();
        assertThat(board.scoreBoardTotal()).as("Total score board is 151 summed up to frame 8").isEqualTo(151);

        board.recordPoints(6);
        assertThat(board.isComplete()).isTrue();
        assertThat(board.scoreBoardTotal()).as("Total score board is 157 and game is done").isEqualTo(177);

    }

    @Test
    public void testRecordPoints_perfectGame() throws Exception {
        final ScoreBoard board = new ScoreBoard();

        // A perfect game requires rolling 12 strikes
        while (!board.isComplete()) {
            board.recordPoints(10);
        }

        final LinkedList<Frame> frames = board.getFrames();
        for (int i = 0; i < 9; i++) {
            assertThat(frames.get(i).getScore()).as("Score for frame " + i + " should be 10").isEqualTo(10);
            assertThat(frames.get(i).getFrameScore().get()).as("Score with bonus for frame " + i + " should be 30").isEqualTo(30);
        }

        assertThat(frames.get(9).getScore()).as("Last frame score should be 30").isEqualTo(30);
        assertThat(frames.get(9).getFrameScore().get()).isEqualTo(30);

        assertThat(board.scoreBoardTotal()).as("Total score board for a perfect game is 300").isEqualTo(300);
    }
}
