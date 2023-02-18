// CLASS:   Connect
//
// Author:  Xian Mardiros, 7862786
//
// REMARKS: Implementation of GameLogic interface, provides
//          connection between both players, and controls 
//          flow of the game
import java.util.Random;

public class Connect implements GameLogic{
    private ConnectBoard board;
		private int size; //doesn't really need this, can access board
    private Status playerTurn;
    private HumanPlayer player1;
    private AIPlayer player2;
    private int WIN_NUMBER = 4;

    /*
     * Connect constructor
     * PURPOSE: Execute tasks needed to prepare to start game:
     *            choose random size, random first player,
     *            create game board and player objects
     */
    public Connect(){
        Random generator = new Random();
        size = 6 + generator.nextInt(7);
        System.out.printf("The board size is %dx%d\n", size, size);
        int firstTurn = generator.nextInt(2);
				if( firstTurn == 0 ){
					playerTurn = Status.ONE;
				}else{
					playerTurn = Status.TWO;
				}

        board = new ConnectBoard(size);
        player1 = new HumanPlayer(this);
        player2 = new AIPlayer(this, board, WIN_NUMBER, Status.TWO );
    }

    /*
     * setAnswer
     * PURPOSE: Accomplish after-move tasks:
     *            tell board to place token,
     *            check endgame conditions,
     *            change player turn,
     *            tell next player the last move.
     * PARAMETERS: The column to place the token in
     */
    public void setAnswer( int col ){
    	board.setBottomSpace(col, playerTurn);
       if(checkWinCondition( col )){
					player1.gameOver( playerTurn );
        }else if( checkStalemate() ){
					player1.gameOver(Status.NEITHER);
        }else{
            if( playerTurn == Status.ONE ){
                playerTurn = Status.TWO;
                player2.lastMove(col);
            }else{
                playerTurn = Status.ONE;
                player1.lastMove(col);
            }
        }
    }

    /*
     * checkWinCondition
     * PURPOSE: check if the last move has resulted in 
     *          that player winning, by counting tokens
     *          of the same player on either side on each
     *          axis of the concerning token 
     * PARAMETERS: The column to check
     * RETURNS: A boolean indicating whether a win was found
     */
    private boolean checkWinCondition( int col ){
			boolean winFound = false;
			Square square = board.getTopToken(col);
			Status player = square.getStatus();
			int row = square.getRow();
      int num = WIN_NUMBER-1; // its WIN_NUMBER-1 since the current token isn't counted

			// check all possible win directions, diagonal-left, 
			// diagonal-right, horizontal, vertical
			//
			// vertical
			if( !winFound ){
				winFound = board.countAxisTokens( player, row, col,  1, 0 ) >= num;
			}
			//horizontal
			if( !winFound ){
				winFound = board.countAxisTokens( player, row, col, 0, 1 ) >= num;
			}
			// diagonal-left
			if( !winFound ){
				winFound = board.countAxisTokens( player, row, col, 1, 1 ) >= num;
			}
			// diagonal-right
			if( !winFound ){
				winFound = board.countAxisTokens( player, row, col, -1, 1 ) >= num;
			}
			
			return winFound;
    }

    /*
     * checkStalemate
     * PURPOSE: check whether the game has reached a tie,
     *          i.e. that the top row is filled
     */
    private boolean checkStalemate(){
			int col = 0;
			boolean boardFilled = true;
			while( boardFilled && col < size ){
				boardFilled = board.getTopSpace(col).getStatus() != Status.NEITHER;
				col += 1;
			}
			return boardFilled;
    }

    // tell one player to move or the other
    void run(){
			if( playerTurn == Status.ONE ){
				player1.lastMove( -1 );
			}else{
				player2.lastMove( -1 );
			}
    }

    // return the board size
		public int getSize(){
			return size;
		}
}
