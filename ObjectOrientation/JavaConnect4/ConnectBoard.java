// CLASS:   ConnectBoard
//
// Author:  Xian Mardiros, 7862786
//
// REMARKS: Contains a 2D array of type status and
//          the necessary methods for manipulating it
//          and getting info on its contents
//
public class ConnectBoard{
	private int size;
	private Status[][] board;

  /*
   * ConnectBoard constructor
   * PURPOSE: initializes all array elements to Status.NEITHER
   * PARAMETERS: board size
   */
	public ConnectBoard( int size ){
		this.size = size;
		board = new Status[size][size];
		for( int i = 0; i<size; i++ ){
			for( int j = 0; j<size; j++ ){
				board[i][j] = Status.NEITHER;
			}
		}
	}

  // check if a square is on the board
	public boolean validateSquare( int row, int col ){
		return row > -1 && row < size && col > -1 && col < size;
	}

  /*
   * validateMove
   * PURPOSE: check if the specified space is not occupied while
   *          the space below it is occupied 
   * PARAMETERS: coordinates of the board space 
   * RETURNS: boolean indicating whether a move is valid in the space 
   */
	public boolean validateMove( int row, int col ){
		return validateSquare( row, col ) && 
			board[row][col] == Status.NEITHER && 
			( !validateSquare( row+1, col ) || board[row+1][col] != Status.NEITHER );
	}

  /* 
   * getTopToken
   * PURPOSE: return a square representing the highest token
   *          placed in a given column 
   * PARAMETERS: the column to check 
   * RETURNS: the top token in the column or null if the column is empty
   */
	public Square getTopToken( int col ){
		int row = 0;
    Square retSquare = null;
    boolean colEmpty = false;
		while( !colEmpty && board[row][col] == Status.NEITHER ){
			row += 1;
      if( row == size ){ colEmpty = true; }
		}
    if( !colEmpty ){ retSquare = new Square( board[row][col], row, col ); }
		return retSquare; 
	}
	
  // return a square representing the top of the specified column
	public Square getTopSpace( int col ) {
		return new Square( board[0][col], 0, col );
	}

  /*
   * getBottomSpace
   * PURPOSE: get the bottommost empty space int the specified column 
   * PARAMETERS: the column to check
   * RETURNS: the bottom empty space, or null if the column is full
   */
	public Square getBottomSpace( int col ){
		int row = -1;
		Square retSquare = null;

		while( validateSquare(row+1,col) && board[row+1][col] == Status.NEITHER ){
			row += 1;
		}
		if( validateSquare(row,col) ) {
			retSquare = new Square( board[row][col], row, col );
		}
		return retSquare;
	}
	
  // set the bottom space of the column to be a token of 
  // the specified player, if the column isn't full 
	public void setBottomSpace( int col, Status player ) {
    Square toSet = getBottomSpace( col );
    if( toSet != null ){
      board[toSet.getRow()][toSet.getCol()] = player;
    }
	}

  /*
   * countAxisTokens
   * PURPOSE: counts the number of consecutive tokens of the specified player 
   *          on either side of the token at the specified coordinates
   *          along the specified axis
   * PARAMETERS:
   *          player - the player whose tokens we're checking for
   *          row - the row of the target token 
   *          col - the column of the target token 
   *          verticalOffset - the vertical component of the axis to check 
   *          horizontalOffset - the horizontal component of the axis to check 
   * RETURNS: the number of adjacent tokens of the same player in that axis 
   */
	public int countAxisTokens( Status player, int row, int col, int verticalOffset, int horizontalOffset ){
		return countTokens( player, row+verticalOffset, col+horizontalOffset, verticalOffset, horizontalOffset ) + 
      countTokens( player, row-verticalOffset, col-horizontalOffset, -verticalOffset, -horizontalOffset );
	}

  /*
   * countTokens
   * PURPOSE: recursive helper function to countAxisTokens, countTokens
   *          checks the current token, and if it a token of the target player,
   *          then checks the next token
   * PARAMETERS:
   *          player - the player whose tokens we're checking for
   *          row - the row of the target token 
   *          col - the column of the target token 
   *          verticalOffset - the vertical component of the axis to check 
   *          horizontalOffset - the horizontal component of the axis to check 
   * RETURNS: the number of tokens of the target player further along the specified axis,
   *          including the current token
   */
	private int countTokens( Status player, int currRow, int currCol, int verticalOffset, int horizontalOffset ){
		int tokens = 0;
		if( validateSquare( currRow, currCol ) && board[currRow][currCol] == player ){
			tokens += 1;
			tokens += countTokens( player, currRow + verticalOffset, currCol + horizontalOffset, verticalOffset, horizontalOffset );
		}
		return tokens;
	}

	public int getSize(){return size;}

  // checks if the specified token is the AIPlayer's token 
	public boolean AIToken( int row, int col ){
		boolean isAIToken = false;
		if( validateSquare( row, col ) ){
			isAIToken = board[row][col] == Status.TWO;
		}
		return isAIToken;
	}
}
