package com.marchex.bowling;

import org.junit.Test;

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

        // frame 3: score 55 - strike score
        board.recordPoints(10);
        assertThat(board.scoreBoardTotal()).as("Need to wait until the full next frame is played to score a strike").isEqualTo(29);

        // frame 4: score 75 - > 2consecutive strike score
        board.recordPoints(10);

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

        // frame 0: score 9
        board.recordPoints(4);
        board.recordPoints(5);

        // frame 1: score 29 - spare score
        board.recordPoints(1);
        board.recordPoints(9);

        // frame 2: score 46 - strike score
        board.recordPoints(10);

        // frame 3: score 53
        board.recordPoints(3);
        board.recordPoints(4);

        // frame 4: score 83 - > 2consecutive strike score
        board.recordPoints(10);

        // frame 5: score 110
        board.recordPoints(10);

        // frame 6: score 128
        board.recordPoints(10);

        // frame 7: score 136
        board.recordPoints(7);
        board.recordPoints(1);

        // frame 8: score 166
        board.recordPoints(10);

        // frame 9: score 192
        board.recordPoints(10);
        board.recordPoints(10);
    }
}
