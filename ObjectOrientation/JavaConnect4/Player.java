// CLASS:   Player
//
// Author:  UM CS Department
//
// REMARKS: Unchanged from original
public interface Player {
    void lastMove(int lastCol);
    void gameOver(Status winner);
    void setInfo(int size, GameLogic gl);
}
