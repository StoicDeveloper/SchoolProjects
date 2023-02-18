// CLASS: AIPlayer
//
// Author: Xian Mardiros, 7862786
//
// REMARKS: This class controls the AI Connect player
//          and supplies the methods to decide on and
//          communicate their move in the game
//
//-----------------------------------------
public class AIPlayer implements Player{
	private Connect game;
	private ConnectBoard board;
  private int WIN_NUMBER;
  private Status thisPlayer;
  private Status otherPlayer;
	
  // constructor
	public AIPlayer( Connect game, ConnectBoard board, int WIN_NUMBER, Status thisPlayer ){
		this.board = board;
    this.WIN_NUMBER = WIN_NUMBER;
    this.thisPlayer = thisPlayer;
    this.otherPlayer = thisPlayer == Status.ONE ? Status.TWO : Status.ONE;
		setInfo( board.getSize(), game );
	}

	/*
	 * lastMove
	 * PURPOSE: tells player to move, determines best move,
	 * 					then sends that move to game logic. 
	 * 					First checks for possible wincons for the 
	 * 					opponent and disrupts them if found, if not 
	 * 					then checks for AIPlayer possible wincons and 
	 * 					executes them if found, if not, then finds 
	 * 					another move
	 * PARAMETERS: the previous move of the opponent
   */
	public void lastMove(int lastCol ){
		int nextMove = checkWinCons( otherPlayer );
		if( nextMove == -1 ){
			nextMove = checkWinCons( thisPlayer );
		}
		if( nextMove == -1 ){
			nextMove = findBest();
		}
		game.setAnswer( nextMove );
	}

	/*
	 * checkWinCons
	 * PURPOSE: check if the the specified player has a 
   *          winning move available to them
   * PARAMETERS: the player to check for
	 * RETURNS: the column to move into to prevent a loss
	 */
	private int checkWinCons( Status player ){
		// checks each possible axis for 3 consecutive tokens of the
		// same player, checking if the square beyond the last token 
		// of the current axis direction is a valid move. If 3 
		// consecutive tokens are found then the return value is
		// set to be the column of the square beyond the last 
		// consecutive token.
		int col = 0;
		boolean winConFound = false;

    // check each valid move until winCon or end of board found
		while( col < game.getSize() && !winConFound ){
			Square bottom = board.getBottomSpace( col );
			if( bottom != null ){
        int row = bottom.getRow();
        winConFound = hasWinCon( player, row, col );
			}
			col++;
		}
		if(!winConFound) {
			col = 0;
		}
		return col-1;
	}

  /*
   * hasWinCon
   * PURPOSE: checks if the specified player can win on the specified
   *          square
   * PARAMETERS: the player and the square coordinates
   * RETURNS: a boolean indicating whether a winCon was found
   */
  private boolean hasWinCon( Status player, int row, int col ){
    int num = WIN_NUMBER - 1;
    boolean winConFound = false;
    if( board.countAxisTokens( player, row, col, 1, 0 ) >= num || 
        board.countAxisTokens( player, row, col, 1, 1 ) >= num || 
        board.countAxisTokens( player, row, col, 0, 1 ) >= num || 
        board.countAxisTokens( player, row, col, -1, 1 ) >= num ){
      winConFound = true;
    }
    return winConFound;
  }

  /*
   * findbest
   * PURPOSE: Uses a scoring system to evaluate the best
   *          of all possible moves, based on centrality
   *          of the space being evaluated and the number
   *          of nearby tokens of the AI player
   * RETURNS: The column of the best move
   */
	private int findBest(){
		int currCol = 0;
		double currScore = 0;
		int bestCol = 0;
		double bestColScore = 0;
		int size = game.getSize();

    // iterate accross board columns
		while( currCol < size ){
			Square bottom = board.getBottomSpace(currCol);
      // skip filled rows
			if(bottom != null) {
				int row = bottom.getRow();

        // Each othogonal step towards the board centre is worth 1/2 point 
				int centralScore = 2*size - Math.abs( row - size/2 ) - Math.abs( currCol - size/2 );

        // Create array of booleans indicating whether each adjacent space contains a 
        // token of the same player
        // code is arranged in shape the relation of each space checked to the space bing evaluated
				boolean[] samePlayer = { 
					board.AIToken(row-1,currCol-1), 											board.AIToken(row-1,currCol+1),
					board.AIToken(row,currCol-1  ),												board.AIToken(row,currCol+1),
					board.AIToken(row+1,currCol-1),board.AIToken(row+1,currCol),board.AIToken(row+1,currCol+1) };

        // count up friendly tokens
				int nearbyTokenScore = 0;
				for( boolean token : samePlayer ){
					if( token ){ nearbyTokenScore++; }
				}

        // count total score for this space
				currScore = centralScore + nearbyTokenScore;

        // don't play on spaces that have a win above them
        if( hasWinCon( Status.ONE, row-1, currCol ) ||
            hasWinCon( Status.TWO, row-1, currCol ) ){
            currScore = 0;
        }

        // check if its better than the previous best
				if( currScore > bestColScore ){
					bestCol = currCol;
					bestColScore = currScore;
				}	
			}
			currCol++;
		}
		return bestCol;
	}


  // fulfills Player interface requirements
	public void gameOver(Status winner){
		//needn't do anything
    //AIPlayer doesn't need to respond, or be aware of, a loss
	}

  // provides the game logic to be used, and the board size which isn't needed
  // due to design of having references to a single game board from both
  // the game logic implementation and the AIPlayer
	public void setInfo(int size, GameLogic gl){
		this.game = (Connect) gl;
	}

}

	
