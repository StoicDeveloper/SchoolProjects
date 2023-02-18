/*
The TutrFindr Database is structured as a bipartite graph.
Each disjoint set of the graph is composed of a linked list.
The linked lists are the lists of Tutors and Students,
which are the vertices of the graph.
The edges between the vertices are the appointments between
the Tutors and Students, and contain the length and price of 
the appointment, as well as references to the corresponding
Tutor and Student. The edges (appointments) intersecting each
Tutor or Student are contained within each Tutor and Student
as linked lists
*/

public class TFDatabase{
  private LinkedList tutorList;
  private LinkedList studentList;
  
  private class LinkedList{
    public Node top;

    public LinkedList(){
      top = null;
    }
  }

  // is it better to have one type of node, storing tutors
  // and students as persons, or have multiple types of node
  private class Node{
    public Node next;
    public Superclass item;

    public Node getNext(){
      return next;
    }

    public Superclass getItem(){
      return item;
    }

  }
/*
  private class TutorNode extends Node{
    public Tutor item;

    public Node( Tutor item ){
      this.item = item;
    }

    public Tutor getItem(){
      return item;
    }
  }

  private class StudentNode extends Node{
    public Student item;

    public Node( Student item ){
      this.item = item;
    }

    public Student getItem(){
      return item;
    }
  }
*/
  public TFDatabase(){
    tutorList = new LinkedList();
    studentList = new LinkedList();
  }

  public Tutor getTutor( String userid ){
    return (Tutor) getObject( userid, tutorList );
  }

  public Student getStudent( String userid ){
    return (Student) getObject( userid, studentList );
  }

  public Topic getTopic( String tutorid, String topic ){
    return (Topic) getObject( topic, getTutor( tutorid ).getTopics() );
  }

  public Superclass getObject( String id, LinkedList list ){
    Superclass toReturn = null;
    Node node = list.top;
    while( node != null && toReturn != null ){
      if( node.item.getID().equals( id ){
        toReturn = node.item;
      }else{
        node = node.next;
      }
    }

    return toReturn;
  }

  public void addTutor( String userid, int hours ){
    insertPerson( new Tutor( userid, hours ), tutorList );
  }

  public void addStudent( String userid ){
    insertPerson( new Student( userid ), studentList );
  }

  private void insertPerson( Person person, LinkedList list ){
    String personType = person instanceof Tutor ? "Tutor" : "Student";
    String userid = person.getID();

    if( !checkDuplicate( userid ){
      Node newNode = new Node( person );

      // 4 cases: 1) empty list, 2) non-empty insert at top
      //          3) non-empty insert at end, 4) 2 or more
      //          elements in lest, insert in middle
      if( list.top == null){ // case 1
        list.top = newNode;
      }else if( list.top.getItem().getID().compareTo( userid ) > 0 ){ // case 2
        newNode.next = list.top;
        list.top = newNode;
      }else{
        Node curr = list.top;
        while( node != null ){ // iterate until case 3 and 4
          if( curr.next == null ){ // case 4
            curr.next = newNode;
          }else if( curr.next.item.getID().compareTo( userid ) > 0 ){ // case 3
          }else{
            curr = curr.next;
          }
        }
      }

      System.out.printf( "%s with userid %s successfully created.\n", personType, userid );
    }else{
      System.out.printf( "Duplicate %s with userid %s.\n", personType, userid );
    }
  }


  private void insertPerson( Person person, LinkedList list ){
    String personType = person instanceof Tutor ? "Tutor" : "Student";
    if( !checkDuplicate( userid ){
      Node newNode = new Node( new Tutor( userid, hours ) );
      if( node == null ){
        person instanceof Tutor ? topTutor = (Tutor)person :
                                topStudent = (Student)person;
      }else{
        while( node != null ){
          
      }
      System.out.printf( "%s with userid %s successfully created.\n", personType, userid );
    }else{
      System.out.printf( "Duplicate %s with userid %s.\n", personType, userid );
    }
  }
      
  public boolean checkDuplicate( String userid ){
    boolean found = false;
    Node curr = tutorList.top;
    while( curr != null && !found ){
      found = curr.item.userid.equals( userid );
      if( curr == null && curr instanceof Tutor ){
        curr = studentList.top;
      }
    }

    return found;
  }

  
