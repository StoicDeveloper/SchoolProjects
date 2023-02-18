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

		public boolean checkDuplicate( String id ){
			boolean found = false;
			Node curr = top;
			while( curr != null && !found ){
				found = curr.item.equals( id );
				curr = curr.next;
			}

			return found;
		}

		public void insertNode( Node newNode ){
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

  public TFDatabase(){
    tutorList = new LinkedList();
    studentList = new LinkedList();
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

  public Superclass getObject( String id, LinkedList list ){
    Superclass toReturn = null;
    Node node = list.top;
    while( node != null && toReturn != null ){
      if( node.item.getID().equals( id ) ){
        toReturn = node.item;
      }else{
        node = node.next;
      }
    }

    return toReturn;
  }

  public void addTutor( String userid, int hours ){
    if( insertObject( new Tutor( userid, hours ), 
					tutorList )){
			System.out.printf( "Tutor with userid %s successfully" 
					+ "created.\n", userid );
		}else{
			System.out.printf( "Duplicate Tutor with userid %s.\n",
					userid );
		}
  }

  public void addStudent( String userid ){
    if( insertObject( new Student( userid ), studentList ) ){
			System.out.printf( "Student with userid %s "
					+ "successfully created.\n", userid );
		}else{
			System.out.printf( "Duplicate Student with userid "
					+ "%s.\n", userid );
		}
  }

	public void addTopic( String topic, String tutorid, int price ){
		Tutor tutor = getTutor( tutorid );
		if( tutor != null ){
			if( insertObject( new Topic( topic, price ), 
						tutor.getTopics() ) ){
				System.out.printf( "Topic %s added to Tutor %s with" 
						+ "price %d.\n", topic, tutorid, price );
			}else{
				System.out.printf( "Duplicate topic %s for Tutor"
						+ "%s.\n", topic, tutorid );
			}
		}else{
			System.out.printf( "Tutor %s not found.\n", tutorid );
		}
	}

  private boolean insertObject( Superclass object, 
			LinkedList list ){

    //String userid = person.getID();
		boolean isDuplicate = 
			list.checkDuplicate( object.getID() );
    if( !isDuplicate ){
      Node newNode = new Node( object );

			list.insertNode( newNode );
		}
		return isDuplicate;
  }

	public void request( String studentid, String topic, 
			int hours ){
	// 1) check if student present
	// 2) create temp aptList
	// 3) find best tutor for the topic
	// 4) add tutor to temp list
	// 5) if hours remain to be filled, repeat 3&4 until no
	// 		hours remain or no more tutors teach that topic
	// 6) if no hours remain, append iterate temp list
	// 		adding nodes to the tutor aptLists, then append list
	// 		to the student's list
	
		Student student = getStudent( studentid );
		if( student != null ){
			System.out.printf( "Attempting to fulfil request for " 
					+ "%s to receive %d hours of tutorin in topic "
					+ "%s.\n", studentid, hours, topic );

			int hoursRemaining = hours;
			LinkedList tempList = new LinkedList();
			
			Tutor bestTutor = bestTutor( topic );
			while( hoursRemaining > 0 && bestTutor != null ){
				int aptHours = bestTutor.getData() < hoursRemaining 
					? bestTutor.getData() : hoursRemaining;

				tempList.insertNode( new Node( new Appointment( 
								bestTutor, student, topic, aptHours, 
								getTopic( bestTutor.getID(), topic )
								.getPrice()*aptHours ) ) );

				bestTutor.decHours( aptHours );
				hoursRemaining -= aptHours;
				bestTutor = bestTutor( topic );
			}

			Node curr = tempList.top;
			if( hoursRemaining > 0 ){
				// reincrement tutors and print fail message
				while( curr != null ){
					Appointment apt = curr.item;
					apt.getTutor().incHours( apt.getHours() );
					curr = curr.next;
				}
				System.out.printf( "No tutors available for Student "
						+ "%s for %d hours in topic %s.\n", studentid, 
						hours, topic );
			}else{
				// append tempList node objects in new nodes to 
				// tutor apt lists
				while( curr != null ){
					Appointment apt = curr.item;
					apt.getTutor().getAptList().insertNode( 
							new Node( apt ) );
					curr = curr.next;
				}
				// append tempList to student apt list
				student.getAptList().append( tempList );
				// print success message
				//
				curr = tempList.top;
				while( curr != null ){
					Appointment currApt = curr.item;
					System.out.printf( "Tutor %s will tutor student %s for %d hours in topic %s at a rate of %d.\n", apt.getTutor().getID(), apt.getStudent().getID(), apt.getHours(), apt.getTopic(), getTopic( apt.getTutor().getID(), apt.getTopic() ).getPrice() );
					curr = curr.next;
				}
			}
		}
	}
	
	public Tutor bestTutor( String topic ){
		Tutor toReturn = null;
		int bestPrice = Integer.MAX_VALUE;
		Node curr = tutorList.top;
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

	public void studentReport( String userid ){
		Student student = getStudent( userid );
		if( student != null ){
			student.report();
		}
	}

	public void tutorReport( String userid ){
		Tutor tutor = getTutor( userid );
		if( tutor != null ){
			tutor.report();
		}
	}
}
