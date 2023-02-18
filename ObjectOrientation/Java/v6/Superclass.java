/*
 * CLASS: Superclass
 *
 * Author: Xian Mardiros
 * 				 7862786
 *
 * REMARKS: An abstract class that contains behaviour and 
 * 					data useful for Students, Tutors, and Topics
 */
public abstract class Superclass{
  private String id;
  private int data;

	public Superclass( String id, int data ){
		this.id = id;
		this.data = data;
	}

  public String getID(){
    return id;
  }

  public int getData(){
    return data;
  }

	public void setData( int newValue ){
		data = newValue;
	}
  
  public boolean equals( String string ){
    return string.equals( id );
  }
}
