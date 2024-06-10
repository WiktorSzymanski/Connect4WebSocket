package pl.szymanski.wiktor.connect4websocket.Game;

import lombok.extern.slf4j.Slf4j;
import pl.szymanski.wiktor.connect4websocket.exceptions.ColumnFullException;
import pl.szymanski.wiktor.connect4websocket.exceptions.NotYourMoveException;

import java.util.Objects;

import static java.util.Collections.fill;

@Slf4j
public class GameState {
    private final Integer[][] gameBoard = new Integer[7][6];
    private int currentMovePlayerIndex;

    public GameState() {
        currentMovePlayerIndex = 0;
    }

    private boolean checkWinCondition(Integer playerIndex,int col, int row) {
        return checkDirection(playerIndex, row, col, 1, 0) || // Horizontal
                checkDirection(playerIndex, row, col, 0, 1) || // Vertical
                checkDirection(playerIndex, row, col, 1, 1) || // Diagonal /
                checkDirection(playerIndex, row, col, 1, -1);  // Diagonal \
    }

    private boolean checkDirection(Integer token, int row, int col, int dRow, int dCol) {
        int count = 0;

        // Check in the positive direction
        for (int i = 0; i < 4; i++) {
            int newRow = row + i * dRow;
            int newCol = col + i * dCol;
            if (newRow < 0 || newRow >= gameBoard[0].length || newCol < 0 || newCol >= gameBoard.length || !Objects.equals(gameBoard[newCol][newRow], token)) {
                break;
            }
            count++;
        }

        // Check in the negative direction
        for (int i = 1; i < 4; i++) {
            int newRow = row - i * dRow;
            int newCol = col - i * dCol;
            if (newRow < 0 || newRow >= gameBoard[0].length || newCol < 0 || newCol >= gameBoard.length || !Objects.equals(gameBoard[newCol][newRow], token)) {
                break;
            }
            count++;
        }

        return count >= 4;
    }

    public int placeToken(int col, int playerIndex) {
        if (playerIndex != currentMovePlayerIndex) {
            throw new NotYourMoveException();
        }

        for (int row = 0; row < 6; row++) {
            if (gameBoard[col][row] == null) {
                gameBoard[col][row] = playerIndex;

                log.info("Player {} placed his token in position col {} row {}", playerIndex, col, row);
                boolean won = checkWinCondition(currentMovePlayerIndex, col, row);
                log.info("Player {} won {}", playerIndex, won);
                if (won) return playerIndex;
                currentMovePlayerIndex = (currentMovePlayerIndex + 1) % 2;
                return -1;
            }
        }
        throw new ColumnFullException();
    }
}
