
public abstract class LinkedList{
  private Node top;
  private Node bottom;

  private class Node{
    public Node next;

    public Node getNext(){
      return next;
    }
  }

  public LinkedList(){
    top = null;
    bottom = null;
  }

  public boolean checkDuplicate( String userid ){
    boolean found = false;
    curr = top;
    while( curr != null && !found ){
      found = curr.item.userid.equals( userid );
    }

    return found;
  }

  public boolean isEmpty(){
    return top == null;
}

public class TutorList extends LinkedList{
  private class Node extends Node{
    public Tutor item;

    public Node( Tutor item ){
      this.item = item;
    }

    public Tutor getItem(){
      return item;
    }
    
  }

  public void decTutor( String userid, int hours ){
    Node curr = top;
    boolean found = false;
    while( curr != null && !found ){
      if( curr.getItem().getID().equals( userid ){
        curr.getItem().decHours( hours );
      }else{
        curr = curr.next;
      }
    }
  }

  public void add( String userid, int hours ){
    if( !checkDuplicate( userid ){
      Node newNode = new Node( new Tutor( userid, hours ) );
      if( isEmpty() ){
        top = newNode;
        bottom = top;
      }else{
        bottom.next = newNode;
        bottom = newNode;
      }
      System.out.printf( "Tutor with userid %s successfully created.\n", userid );
    }else{
      System.out.printf( "Duplicate Tutor with userid %s.\n", userid );
    }
  }

  public void topicAdd( String topic, String tutorid, int price ){
    Node curr = top;
    while( currTutor != null ){
      if( curr.item.userid.equals( tutorid ){
        curr.item.addTopic( topic, price );
      }else{
        curr = curr.next;
      }
    }
    if( curr == null ){
      System.out.printf( "Tutor %s not found.\n", tutorid );
    }
  }

  public AptList request( Student student, String topic, int hours ){

    // loop through tutor list to find tutors for the topic
    // which have the lowest price and hours remaining
    // if a full loop occurs with no change, then there is
    // no tutor available
    int hoursNeeded = hours;
    boolean anyChange = true;
    Tutor bestTutor = null;
    int bestPrice = Integer.MAX_VALUE;
    AptList reqApts = new AptList();
    // iterate until cannot find better tutor and all 
    // hours covered
    while( anyChange && hoursNeeded > 0 ){
      anyChange = false;
      Node curr = top;
      while( curr != null ){
        // check if current tutor teaches the topic
        Tutor currTutor = curr.item;
        int price = currTutor.getTopics().retrievePrice( topic );
        int hoursAvailable = currTutor.getHours();
        if( ( price != 0 && hoursAvailable != 0 ){
          // check if current tutor should replace current
          // bestTutor
          if( price < bestPrice || ( price == bestPrice && currTutor.getID().compareTo( bestTutor.getID() ) < 0){
            bestTutor = curr.item;
            bestPrice = price;
          }
        curr = curr.next;
      }
      curr = top;
      // check if a tutor in the subject was found, if so,
      // check if there are more hours to fill than the 
      // tutor has avaliable, if so, decrement tutors
      // hours to zero, otherwise decrement by the number
      // of hours to fill
      if( bestTutor != null ){
        if( hoursNeeded > bestTutor.getHours() ){
          reqApts.add( bestTutor, student, topic, bestTutor.getHours()*bestPrice );
          hoursNeeded -= bestTutor.getHours();
        }else{
          reqApts.add( bestTutor, student, topic, hoursNeeded, hoursNeeded*bestPrice );
          hoursNeeded = 0;
        } // end-if
        anyChange = true;
      } // end-if
      bestTutor = null;
      bestPrice = Integer.MAX_VALUE;
    } // end-while

    if( hoursNeeded != 0 ){
      System.out.printf( "No tutors available for Student %s for %d hours in topic %s.\n", studentid, hours, topic );
      reqApts = new AptList();
    }
    return reqApts;
  }
    
}

public class StudentList extends LinkedList{
  private class Node extends Node{
    public Student item;

    public Node( Student item ){
      this.item = item;
    }

  }

  public Student retrieve( String userid ){
    Student toReturn = null;
    Node curr = top;
    boolean found = false;
    while( curr != null && !found ){
      if( curr.getItem().getID().equals( userid ){
        toReturn = curr.getItem();
        found = true;
      }else{
        curr = curr.next;
      }
    }
    if( toReturn == null ){
      System.out.printf( "Student %s not found.\n", userid );
    }
    return toReturn;
  }

  public void add( String userid ){
    if( !checkDuplicate( userid ) ){
      Node newNode = new Node( new Student( userid ) );
      if( isEmpty() ){
        top = newNode;
        bottom = top;
      }else{
        bottom.next = newNode;
        bottom = newNode;
      }
      System.out.printf( "Student with userid %s successfully created.\n", userid );
    }else{
      System.out.printf( "Duplicate Student with userid %s.\n", userid );
    }
  }

}

public class TopicList extends LinkedList{

  private String userid;

  private class Node extends Node{
    public Topic item;

    public Node( Topic item ){
      this.item = item;
    }
  }

  public TopicList( String userid ){
    this.userid = userid;
  }

  // returns 0 if the topicList doesn't contain the topic
  public int retrievePrice( String topic ){
    int price = 0;
    Node curr = top;
    while( curr != null && price == 0 ){
      if( curr.item.getTopic().equals( topic ){
        price = curr.item.getPrice();
      }else{
        curr = curr.next;
      }
    }
    return price;
  }

  // returns false if the topicList doesn't contain the topic
  public boolean checkDuplicate( String topic ){
    boolean found = false;
    Node curr = top;
    while( curr != null && !found ){
      if( curr.item.getTopic().equals( topic ){
        found = true;
      }else{
        curr = curr.next;
      }
    }
    return found;
  }

  public void add( String topic, int price ){
    if( !checkDuplicate( topic ){
      Node newNode = new Node( topic, price ) );
      if( isEmpty() ){
        top = newNode;
        bottom = top;
      }else{
        bottom.next = newNode;
        bottom = newNode;
      }
      System.out.printf( "Topic %s added to Tutor %s with price %d.\n", topic, userid, price );
    }else{
      System.out.printf( "Duplicate topic %s for Tutor %s.\n", topic, userid );
    }
  }

}

public class AptList extends LinkedList{

  private class Node extends Node{
    public Tutor tutor;
    public Student student;
    public String topic;
    public int hours;
    public int price;

    public Node( Tutor tutor, Student student, String topic, int hours, int price ){
      this.tutor = tutor;
      this.Student = student;
      this.topic = topic;
      this.hours = hours;
      this.price = price;
    }
  }

  public void add( Tutor tutor, Student student, int hours, String topic){
    Node newNode = new Node( tutorid, studentid, topic, price ) );
    if( isEmpty() ){
      top = newNode;
      bottom = top;
    }else{
      bottom.next = newNode;
      bottom = newNode;
    }
  }

  public void append( AptList list ){
    Node curr = list.top;
    while( curr != null ){
      curr.tutor.decHours( curr.hours );
    }
    bottom.next = list.top;
    bottom = list.bottom;
  }

  // for use in requests only
  public decTutors(){


}
