
public class LinkedList{
	public Node top;

  // is it better to have one type of node, storing tutors
  // and students as persons, or have multiple types of node
  private class Node{
    public Node next;
    public Superclass item;
/*
		public Node( Superclass object ){
			this.next = null;
			item = object;
		}
*/
		public Node( Superclass object, Node next ){
			this.next = next;
			item = object;
  }

	public LinkedList(){
		top = null;
	}
/*
 * Better to just use the get() function and
 * check if it returns null than to have both
 *
	public boolean checkDuplicate( String id ){
		boolean found = false;
		Node curr = top;
		while( curr != null && !found ){
			found = curr.item.equals( id );
			curr = curr.next;
		}

		return found;
	}
*/

  public Superclass get( String id ){
    Superclass toReturn = null;
    Node node = top;
    while( node != null && toReturn == null ){
      if( node.item.equals( id ) ){
        toReturn = node.item;
      }else{
        node = node.next;
      }
    }

    return toReturn;
  }

	public void insert( Superclass object ){
		top = new Node( object, top );
		/* bad code
		Node newNode = new Node( object );
		if( top == null ){
			top = newNode;
		}else{
			newNode.next = top;
			top = newNode;
		}
		*/ // end bad code
	}

  public boolean insertObject( Superclass object ){

    //String userid = person.getID();
		//boolean isDuplicate = checkDuplicate( object.getID() );
		
    if( get( object.getID() ) != null ){
			insert( object );
		}
		return !isDuplicate;
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
/*
 * Better to have only one get function that retrieves
 * any instance of the Superclass, cast to the desired
 * subclass after retrieved, than to repeat all the
 * code unneccessarily
 *
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
	*/

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
			Tutor tutor = apt.getTutor();
			String student = apt.getStudent().getID();
			int hours = apt.getHours();
			Topic topic = apt.getTopic();
			int price = hours*topic.getPrice();
			tutor.getAptList().insert( apt );

			System.out.printf( "Tutor %s will tutor student %s for"
					+ " %d hours in topic %s at a rate of %d.\n", 
					tutor.getID(), student, hours, topic.getID(), 
					price );
			curr = curr.next;
		}
	}
	
	public Tutor bestTutor( String topic ){
		int bestPrice = Integer.MAX_VALUE;
		Tutor bestTutor = null;
		Node curr = top;
		while( curr != null ){
			Tutor currTutor = (Tutor) curr.item;
			Topic currTopic = (Topic) currTutor.getTopics().get( topic );
			int currPrice = currTopic.getPrice();
			if( currTopic != null && currTutor.getData() > 0 && 
					currPrice < bestPrice || 
					( currPrice == bestPrice &&
						currTutor.getID().compareTo( bestTutor.getID() ) < 0 ) ){
				bestTutor = currTutor;
				bestPrice = currPrice;
			}
			curr = curr.next;
		}
		return bestTutor;
	}

	public void print(){
		System.out.print( "[" );
		Node curr = top;
		while( curr != null ){
			System.out.print( curr.item.getID() );
			if( curr.next != null ){
				System.out.print( " " );
			}
			curr = curr.next;
		}
		System.out.println( "]" );
	}

	public void report( String personType ){

		String exchangeType = personType.equals( "Tutor" )
			? "revenue" : "cost";

		int totalHours = 0;
		int totalPrice = 0;
		Node curr = top;
		while( curr != null ){
			Appointment apt = (Appointment) curr.item;
			int hours = apt.getHours();
			int price = apt.getPrice();
			System.out.printf( "Appointment: %s: %s, topic: "
					+ "%s, hours: %d, total %s: %d", personType,
					apt.getTutor().getID(), apt.getTopic().getID(), hours, 
					exchangeType, price );
			totalHours += hours;
			totalPrice += price;
			curr = curr.next;
		}
		System.out.printf( "Total number of hours of tutoring: "
				+ "%d\nTotal %s tutoring: %d\n----------------"
				+ "---------\n", totalHours, exchangeType + 
				(personType.equals( "Tutor" ) ? " from" : " of"), totalPrice );
	}
}
