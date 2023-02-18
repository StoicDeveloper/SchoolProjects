/*
 * CLASS: Tutor
 *
 * Author: Xian Mardiros
 * 				 7862786
 *
 * REMARKS: Represents the Tutors of the app, and contains 
 * 					behaviours and data specific to their need,
 * 					particularly a list of Topics, and methods
 * 					to increment or decrement the number of
 * 					hours available for tutoring
 */
public class Tutor extends Person{
  private LinkedList topics;

  public Tutor( String userid, int hours ){
		super( userid, hours );
		topics = new LinkedList();
  }
 /* 
  public void addTopic( String topic, int price ){
    topics.insert( new Topic( topic, price ) );
  }
*/
  public LinkedList getTopics(){
    return topics;
  }

  public void decHours( int hours ){
    super.setData( getData() - hours );
  }

  public void incHours( int hours ){
    super.setData( getData() + hours );
  }
}
