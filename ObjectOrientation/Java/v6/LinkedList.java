/*
 * CLASS: LinkedList
 *
 * Author: Xian Mardiros
 * 				 7862786
 *
 * REMARKS: A standard linked list, with some behaviour
 * 					specific to the app's needs.
*/

public class LinkedList{
	public Node top;

  private class Node{
    public Node next;
    public Superclass item;

		public Node( Superclass object, Node next ){
			this.next = next;
			item = object;
		}
  }

	public LinkedList(){
		top = null;
	}

	public boolean isEmpty(){
		return top == null;
	}

	/*
	 * get
	 *
	 * PURPOSE:	iterate through list until matching object 
	 * 					is found
	 * PARAMETERS: takes a string of the id of the object
	 * 						 you're looking for
	 * Returns: returns a Superclass object that will need
	 * 					to be casted, or null if the object isn't found
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

	/*
	 * insertObject
	 *
	 * PURPOSE: checks of the object is a duplicate and inserts
	 * 					the object into the list if it wasn't
	 * PARAMETERS: The Superclass object to be inserted
	 * Returns: True if successful, False if duplicate
	 */
  public boolean insertObject( Superclass object ){

    //String userid = person.getID();
		//boolean isDuplicate = checkDuplicate( object.getID() );
		
		boolean isDuplicate = get( object.getID() )!= null;
    if( !isDuplicate ){
			insert( object );
		}
		return !isDuplicate;
  }

	/*
	 * appendList
	 *
	 * PURPOSE: appends the parameter list onto the 
	 * 					calling list, used during requests
	 * 					once the viability of a set of appointments
	 * 					have been confirmed
	 * PARAMETERS: a linked list of appointments which have 
	 * 						 been confirmed to completely fill the need
	 * 						 for a student's request for tutoring
	 */
	public void appendList( LinkedList list ){
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
	 * appendApts
	 *
	 * PURPOSE: this has a similar purpose to appendList, 
	 * 					but since the appointments will always be 
	 * 					added to the same student's appointment list
	 * 					in the appendList method, but not 
	 * 					necessarily the same tutors' lists, this method
	 * 					takes the containing appointments from each 
	 * 					tempList node and inserts them into the aptList
	 * 					of the correct tempList tutor.
	 * 					This method also prints information on each
	 * 					successful appointment.
	 * Parameters: the tempList whose containing appointments
	 * 						 will be added to new nodes and inserted into
	 * 						 tutors' appointment lists
	 */
	public void appendApts( LinkedList tempList ){
		Appointment apt; // the apt of the current node
		Tutor tutor;		 // its tutor
		Topic topic;		 // its topic
		int hours;

		// loop through the tempList and insert and print output
		// on each apt of each node in the list
		Node curr = tempList.top;
		while( curr != null ){
			apt = (Appointment) curr.item;
			tutor = apt.getTutor();
			topic = apt.getTopic();
			hours = apt.getHours();
			
			// insert the apt into the tutor's list
			tutor.getAptList().insert( apt );

			System.out.printf( "Tutor %s will tutor student %s for"
					+ " %d hours in topic %s at a rate of %d.\n", 
					tutor.getID(), apt.getStudent().getID(), 
					hours, topic.getID(), 
					hours*topic.getPrice() );

			curr = curr.next;
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

	/*
	 * reincrementTutors
	 *
	 * PURPOSE: Since a tutor's recorded number of available
	 * 					hours is decremented when an appointment is
	 * 					created, when a list of proposed appointments 
	 * 					are found to not completely fill the needs of 
	 * 					the given request, then all of the tutor's
	 * 					record of available hours must be 
	 * 					reincremented. This method does so.
	 */
	public void reincrementTutors(){
		// loop through appointment list, and increment the
		// tutor's hours by the length of the appointment
		Node curr = top;
		while( curr != null ){
			Appointment apt = (Appointment) curr.item;
			apt.getTutor().incHours( apt.getHours() );
			curr = curr.next;
		}
	}

	/*
	 * bestTutor
	 *
	 * PURPOSE: finds the best tutor in a tutorList to teach
	 * 					the parameter topic, based on price, 
	 * 					or alphabetically if prices were tied.
	 * PARAMETERS: a string representing the topic to be taught
	 * Returns: the best tutor that was found, or null if no
	 * 					tutors teach the specified topic
	 */
	public Tutor bestTutor( String topic ){
		int bestPrice = Integer.MAX_VALUE;	// best price so far
		Tutor bestTutor = null; // the best tutor so far
		Tutor currTutor;	// the current tutor in the list
		Topic currTopic;	// the topic & price of that tutor

		// loop through list checking for the best tutor
		Node curr = top;
		while( curr != null ){
			currTutor = (Tutor) curr.item;
			currTopic = (Topic) currTutor.getTopics().get( topic );

			if( currTopic != null ){
				int currPrice = currTopic.getPrice();

				if( currTutor.getData() > 0 && 
						( currPrice < bestPrice || 
							( currPrice == bestPrice && 
								currTutor.getID().compareTo( 
									bestTutor.getID() ) < 0 ) ) ){ 
					// I'm sorry for the long if condition, it just 
					// checks that the tutor has hours left, whether
					// their price is better, or if tied, whether their
					// name is first alphabetically to the current best
					bestTutor = currTutor;
					bestPrice = currPrice;
				}
			}
			curr = curr.next;
		}
		return bestTutor;
	}

	/*
	 * print
	 *
	 * PURPOSE: mainly used for debugging, prints the ids of 
	 * 					the contents of the calling list
	 */
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

	/*
	 * report
	 *
	 * PURPOSE: prints the the overall information and 
	 * 					appointment details of the parameter person
	 * PARAMETERS: the id of the person to search and report on
	 */
	public void report( String personType ){

		// for help with printing info specific 
		// to the person type
		String exchangeType = personType.equals( "Tutor" )
			? "revenue" : "cost";

		int totalHours = 0; // hours in apts of the person
		int totalPrice = 0; // money exchanged in apts
		Appointment apt; // the current apt
		int hours; // its length
		int price; // its price

		Node curr = top;
		while( curr != null ){
			apt = (Appointment) curr.item;
			hours = apt.getHours();
			price = apt.getPrice();

			System.out.printf( "Appointment: %s: %s, topic: "
					+ "%s, hours: %d, total %s: %d\n", personType,
					apt.getTutor().getID(), apt.getTopic().getID(), 
					hours, exchangeType, price );

			totalHours += hours;
			totalPrice += price;
			curr = curr.next;
		}

		System.out.printf( "Total number of hours of tutoring: "
				+ "%d\nTotal %s tutoring: %d\n----------------"
				+ "---------\n", totalHours, exchangeType + 
				(personType.equals( "Tutor" ) ? " from" : " of"), 
				totalPrice );
	}
}
