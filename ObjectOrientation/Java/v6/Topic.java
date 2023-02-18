
/*
 * CLASS: Topic
 *
 * Author: Xian Mardiros
 * 				 7862786
 *
 * REMARKS: this class has no behaviour that would be 
 * 					different from a non-abstract Superclass, 
 * 					but it seems a neater solution to have this 
 * 					be a separate class, to keep the various 
 * 					classes conceptually separate
 */
public class Topic extends Superclass{

  public Topic( String topic, int price ){
		super( topic, price );
  }

	// to make it easier to know what it is you're getting
  public int getPrice(){
    return super.getData();
  }

  public String getTopic(){
    return super.getID();
  }
}

