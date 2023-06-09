package com.tads.dsw3prova1mateusferraz;


import jakarta.inject.Singleton;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


@Singleton
public class BoardService {

    private static final int BOARD_SIZE = 4;
    private static final int MAXIMUM_NUMBER = 2048;
    private static final int[] POSSIBILITES = {2, 2, 2, 2, 4, 2, 2, 2, 2, 2};

    private int[][] board;
    private List<int[]> emptyTilesCoordinates;
    private int score = 0;

    private boolean isGameRunning;
    private GameStatus gameStatus;

    public BoardService() {
        initializeCleanGame();
    }

    private void initializeCleanGame() {
        this.board = new int[BOARD_SIZE][BOARD_SIZE];
        this.emptyTilesCoordinates = new ArrayList<>();
        this.score = 0;
        this.gameStatus = GameStatus.RUNNING;
        this.isGameRunning = true;
        initializeEmptyTiles();
    }

    private void initializeEmptyTiles() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int column = 0; column < BOARD_SIZE; column++) {
                emptyTilesCoordinates.add(new int[]{row, column});
            }
        }
    }

    private String formatJsonResponseWithGameDetails(int[][] matrix) {
        JSONObject gameDetailsResponse = new JSONObject();
        gameDetailsResponse.put("score", this.score);
        gameDetailsResponse.put("status", this.gameStatus);
        JSONArray matrixJson = new JSONArray();
        for (int i = 0; i < BOARD_SIZE; i++) {
            JSONObject rowJson = new JSONObject();
            rowJson.put("row", i + 1);
            JSONObject squaresValues = new JSONObject();
            for (int j = 0; j < BOARD_SIZE; j++) {
                squaresValues.put(String.valueOf(j + 1), matrix[i][j]);
            }
            rowJson.put("values", squaresValues);
            matrixJson.put(rowJson);
        }
        gameDetailsResponse.put("board", matrixJson);
        return gameDetailsResponse.toString();
    }

    public String startGame() {
        if (isGameRunning) {
            initializeCleanGame();
        }
        insertRandomNumberToBoard(2);
        return formatJsonResponseWithGameDetails(board);
    }

    public void insertRandomNumberToBoard() {
        final Random random = new Random();
        final int nextCoordinateIndex = random.nextInt(emptyTilesCoordinates.size());
        final int[] nextNumberCoordinate = emptyTilesCoordinates.get(nextCoordinateIndex);
        board[nextNumberCoordinate[0]][nextNumberCoordinate[1]] = POSSIBILITES[random.nextInt(BOARD_SIZE)];
        emptyTilesCoordinates.remove(nextCoordinateIndex);
    }

    public void insertRandomNumberToBoard(int quantity) {
        for (int i = 0; i < quantity; i++) {
            insertRandomNumberToBoard();
        }
    }

    private boolean checkIfGameIsLost() {
        final int[][] boardCopy = Arrays.copyOf(this.board, this.board.length);
        int numberOfTilesAffected = (
                moveBoardUp(boardCopy) +
                moveBoardDown(boardCopy) +
                moveBoardLeft(boardCopy) +
                moveBoardRight(boardCopy)
        );
        return numberOfTilesAffected == 0;
    }

    public String execute(BoardMove boardMove) {
        // Check if there's free space on board
        if (emptyTilesCoordinates.size() == 0 && checkIfGameIsLost()) {
            this.gameStatus = GameStatus.LOST;
        }
        int numberOfTilesAffected = switch (boardMove) {
            case UP -> moveBoardUp(this.board);
            case RIGHT -> moveBoardRight(this.board);
            case DOWN -> moveBoardDown(this.board);
            case LEFT -> moveBoardLeft(this.board);
        };
        // Check if any move was performed, otherwise do not insert random number to board
        if (numberOfTilesAffected > 0 && this.gameStatus == GameStatus.RUNNING) {
            insertRandomNumberToBoard();
        }
        return formatJsonResponseWithGameDetails(board);
    }

    private int moveBoardUp(int[][] board) {
        int numberOfTilesAffected = 0;
        for (int column = 0; column < BOARD_SIZE; column++) {
            int numberOfFreeTilesOnTop = 0;
            boolean sumAlreadyPerformedOnTopTile = false;
            for (int row = 0; row < BOARD_SIZE; row++) {
                int currentNumber = board[row][column];
                if (currentNumber == 0) {
                    numberOfFreeTilesOnTop++;
                    continue;
                }
                int newRowPosition = row;
                if (numberOfFreeTilesOnTop > 0) {
                    newRowPosition = row - numberOfFreeTilesOnTop;
                    board[newRowPosition][column] = currentNumber;
                    numberOfTilesAffected++;
                    final int[] newPositionCoordinate = new int[]{newRowPosition, column};
                    emptyTilesCoordinates.removeIf(ints -> Arrays.equals(ints, newPositionCoordinate));
                    board[row][column] = 0;
                    emptyTilesCoordinates.add(new int[]{row, column});
                }
                if (newRowPosition > 0 && !sumAlreadyPerformedOnTopTile) {
                    final int numberOnTop = board[newRowPosition - 1][column];
                    if (currentNumber == numberOnTop) {
                        int sum = currentNumber + numberOnTop;
                        board[newRowPosition - 1][column] = sum;
                        numberOfTilesAffected++;
                        this.score += sum;
                        if (sum == MAXIMUM_NUMBER) {
                            this.gameStatus = GameStatus.WIN;
                        }
                        board[newRowPosition][column] = 0;
                        emptyTilesCoordinates.add(new int[]{newRowPosition, column});
                        numberOfFreeTilesOnTop++;
                        sumAlreadyPerformedOnTopTile = true;
                    }
                } else {
                    sumAlreadyPerformedOnTopTile = false;
                }
            }
        }
        return numberOfTilesAffected;
    }

    private int moveBoardRight(int[][] board) {
        int numberOfTilesAffected = 0;
        for (int row = 0; row < BOARD_SIZE; row++) {
            int numberOfFreeTilesOnRight = 0;
            boolean sumAlreadyPerformedOnRight = false;
            for (int column = BOARD_SIZE - 1; column >= 0; column--) {
                int currentNumber = board[row][column];
                if (currentNumber == 0) {
                    numberOfFreeTilesOnRight++;
                    continue;
                }
                int newColumnIndex = column;
                if (numberOfFreeTilesOnRight > 0) {
                    newColumnIndex = column + numberOfFreeTilesOnRight;
                    board[row][newColumnIndex] = currentNumber;
                    numberOfTilesAffected++;
                    final int[] newPositionCoordinate = new int[]{row, newColumnIndex};
                    emptyTilesCoordinates.removeIf(ints -> Arrays.equals(ints, newPositionCoordinate));
                    board[row][column] = 0;
                    emptyTilesCoordinates.add(new int[]{row, column});
                }
                if (newColumnIndex < BOARD_SIZE - 1 && !sumAlreadyPerformedOnRight) {
                    int numberOnRight = board[row][newColumnIndex + 1];
                    if (currentNumber == numberOnRight) {
                        final int sum = currentNumber + numberOnRight;
                        board[row][newColumnIndex + 1] = sum;
                        numberOfTilesAffected++;
                        this.score += sum;
                        if (sum == MAXIMUM_NUMBER) {
                            this.gameStatus = GameStatus.WIN;
                        }
                        board[row][newColumnIndex] = 0;
                        emptyTilesCoordinates.add(new int[]{row, newColumnIndex});
                        numberOfFreeTilesOnRight++;
                        sumAlreadyPerformedOnRight = true;
                    }
                } else {
                    sumAlreadyPerformedOnRight = false;
                }
            }
        }
        return numberOfTilesAffected;
    }

    private int moveBoardLeft(int[][] board) {
        int numberOfTilesAffected = 0;
        for (int row = 0; row < BOARD_SIZE; row++) {
            int numberOfFreeTilesOnLeft = 0;
            boolean sumAlreadyPerformedOnLeft = false;
            for (int column = 0; column < BOARD_SIZE; column++) {
                int currentNumber = board[row][column];
                if (currentNumber == 0) {
                    numberOfFreeTilesOnLeft++;
                    continue;
                }
                int newColumnIndex = column;
                if (numberOfFreeTilesOnLeft > 0) {
                    newColumnIndex = column - numberOfFreeTilesOnLeft;
                    board[row][newColumnIndex] = currentNumber;
                    numberOfTilesAffected++;
                    final int[] newCoordinate = new int[]{row, newColumnIndex};
                    emptyTilesCoordinates.removeIf(coordinate -> Arrays.equals(coordinate, newCoordinate));
                    board[row][column] = 0;
                    emptyTilesCoordinates.add(new int[]{row, column});
                }
                if (newColumnIndex > 0 && !sumAlreadyPerformedOnLeft) {
                    int numberOnLeft = board[row][newColumnIndex - 1];
                    if (currentNumber == numberOnLeft) {
                        final int sum = currentNumber + numberOnLeft;
                        board[row][newColumnIndex - 1] = sum;
                        numberOfTilesAffected++;
                        this.score += sum;
                        if (sum == MAXIMUM_NUMBER) {
                            this.gameStatus = GameStatus.WIN;
                        }
                        board[row][newColumnIndex] = 0;
                        emptyTilesCoordinates.add(new int[]{row, newColumnIndex});
                        numberOfFreeTilesOnLeft++;
                        sumAlreadyPerformedOnLeft = true;
                    }
                } else {
                    sumAlreadyPerformedOnLeft = false;
                }
            }
        }
        return numberOfTilesAffected;
    }

    private int moveBoardDown(int[][] board) {
        int numberOfTilesAffected = 0;
        for (int column = 0; column < BOARD_SIZE; column++) {
            int numberOfFreeTilesOnBottom = 0;
            boolean sumAlreadyPerformedOnBottom = false;
            for (int row = BOARD_SIZE - 1; row >= 0; row--) {
                int currentNumber = board[row][column];
                if (currentNumber == 0) {
                    numberOfFreeTilesOnBottom++;
                    continue;
                }
                int newRowIndex = row;
                if (numberOfFreeTilesOnBottom > 0) {
                    newRowIndex = row + numberOfFreeTilesOnBottom;
                    board[newRowIndex][column] = currentNumber;
                    numberOfTilesAffected++;
                    final int[] newCoordinate = new int[]{newRowIndex, column};
                    emptyTilesCoordinates.removeIf(coordinate -> Arrays.equals(coordinate, newCoordinate));
                    board[row][column] = 0;
                    emptyTilesCoordinates.add(new int[]{row, column});
                }
                if (newRowIndex < BOARD_SIZE - 1 && !sumAlreadyPerformedOnBottom) {
                    final int numberOnBottom = board[newRowIndex + 1][column];
                    if (currentNumber == numberOnBottom) {
                        final int sum = currentNumber + numberOnBottom;
                        board[newRowIndex + 1][column] = sum;
                        numberOfTilesAffected++;
                        this.score += sum;
                        if (sum == MAXIMUM_NUMBER) {
                            this.gameStatus = GameStatus.WIN;
                        }
                        board[newRowIndex][column] = 0;
                        emptyTilesCoordinates.add(new int[]{newRowIndex, column});
                        numberOfFreeTilesOnBottom++;
                        sumAlreadyPerformedOnBottom = true;
                    }
                } else {
                    sumAlreadyPerformedOnBottom = false;
                }
            }
        }
        return numberOfTilesAffected;
    }

}
