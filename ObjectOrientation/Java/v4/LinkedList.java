
public class LinkedList{
	public Node top;

	public LinkedList(){
		top = null;
	}

	public boolean checkDuplicate( String id ){
		boolean found = false;
		Node curr = top;
		while( curr != null && !found ){
			found = curr.item.equals( id );
			curr = curr.next;
		}

		return found;
	}

	public void insert( Superclass object ){
		Node newNode = new Node( object );
		if( top == null ){
			top = newNode;
		}else{
			newNode.next = top;
			top = newNode;
		}
	}

	public void append( LinkedList list ){
		if( top == null ){
			top = list.top;
		}else{
			Node curr = top;
			while( curr.next != null ){
				curr = curr.next;
			}
			curr.next = list.top;
		}
	}

  public Tutor getTutor( String userid ){
		Tutor tutor = (Tutor) getObject( userid, tutorList );
		if( tutor == null ){
			System.out.printf( "Tutor %s not found.\n", 
					userid );
		}

    return tutor;
  }

  public Student getStudent( String userid ){
		Student student = (Student) getObject( userid, 
				studentList );
		if( student == null ){
			System.out.printf( "Student %s not found.\n", 
					userid );
		}

    return student;
  }

  public Topic getTopic( String tutorid, String topic ){
    return (Topic) getObject( topic, getTutor( tutorid ).
				getTopics() );
  }
  public Superclass getObject( String id ){
    Superclass toReturn = null;
    Node node = top;
    while( node != null && toReturn != null ){
      if( node.item.getID().equals( id ) ){
        toReturn = node.item;
      }else{
        node = node.next;
      }
    }

    return toReturn;
  }

  // is it better to have one type of node, storing tutors
  // and students as persons, or have multiple types of node
  private class Node{
    public Node next;
    public Superclass item;

		public Node( Superclass object ){
			this.next = null;
			item = object;
		}
  }

  private boolean insertObject( Superclass object ){

    //String userid = person.getID();
		boolean isDuplicate = checkDuplicate( object.getID() );
    if( !isDuplicate ){
			insert( object );
		}
		return isDuplicate;
  }

	public void reincrementTutors(){
		Node curr = top;
		// reincrement tutors and print fail message
		while( curr != null ){
			Appointment apt = (Appointment) curr.item;
			apt.getTutor().incHours( apt.getHours() );
			curr = curr.next;
		}
	}

	public void appendApts( LinkedList tempList ){
		Node curr = tempList.top;
		while( curr != null ){
			Appointment apt = (Appointment) curr.item;
			apt.getTutor().getAptList().insert( apt );
			System.out.printf( "Tutor %s will tutor student %s for %d hours in topic %s at a rate of %d.\n", apt.getTutor().getID(), apt.getStudent().getID(), apt.getHours(), apt.getTopic(), getTopic( apt.getTutor().getID(), apt.getTopic() ).getPrice() );
			curr = curr.next;
		}
	}
	
	public Tutor bestTutor( String topic ){
		Tutor toReturn = null;
		int bestPrice = Integer.MAX_VALUE;
		Node curr = top;
		while( curr != null ){
			Tutor currTutor = curr.getItem();
			Topic currTopic = getTopic( currTutor.getID(), topic );
			int currPrice = currTopic.getPice();
			if( currTopic != null && currTutor.getHours() > 0 && 
					currPrice < bestPrice || 
					( currPrice == bestPrice &&
						currTutor.getID().compareTo( bestTutor ) < 0 ) ){
				bestTutor = currTutor;
				bestPrice = currPrice;
			}
			curr = curr.next;
		}
		return toReturn;
	}
}
