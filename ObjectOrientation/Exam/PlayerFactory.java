// COMP2150 take home exam
// Question 2
// Class: PlayerFactory
// Author: Xian Mardiros, 7862786
class PlayerFactory{
  public static Player makePlayer( string type, GameLogic gl, int size ){
    Player newPlayer;

    if( type.equals( "computer" ) ){
      newPlayer = new AIPlayer( gl, size );
    }else if( type.equals( "human" ) ){
      newPlayer = new HumanPlayer( gl, size );
    }
    return newPlayer;
  }
}
