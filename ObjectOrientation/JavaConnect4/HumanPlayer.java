// CLASS:   HumanPlayer
//
// Author:  Xian Mardiros, 7862786
//
// REMARKS: Implements interfaces for Player and Human,
//          serves to convey info between the text interface 
//          and the game logic
public class HumanPlayer implements Player, Human{
	private Connect game;
	private SwingGUI userInterface;

	public HumanPlayer( Connect game ){
		userInterface = new SwingGUI();
		setInfo( game.getSize(), game );
	}

	public void lastMove( int lastCol ){
		userInterface.lastMove( lastCol );
	}

	public void gameOver( Status winner ){
		userInterface.gameOver( winner );
	}

	public void setInfo( int size, GameLogic gl ){
		this.game = (Connect) gl;
		userInterface.setInfo( this, size );
	}

	public void setAnswer( int col ){
		game.setAnswer( col );
	}

}
