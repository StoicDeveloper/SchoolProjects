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
  

  public TFDatabase(){
    tutorList = new LinkedList();
    studentList = new LinkedList();
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
	
		Student student = studentList.getStudent( studentid );
		if( student != null ){
			System.out.printf( "Attempting to fulfil request for " 
					+ "%s to receive %d hours of tutorin in topic "
					+ "%s.\n", studentid, hours, topic );

			int hoursRemaining = hours;
			LinkedList tempList = new LinkedList();
			
			Tutor bestTutor = tutorList.bestTutor( topic );
			while( hoursRemaining > 0 && bestTutor != null ){
				int aptHours = bestTutor.getData() < hoursRemaining 
					? bestTutor.getData() : hoursRemaining;

				tempList.insert( new Appointment( 
								bestTutor, student, topic, aptHours, 
								getTopic( bestTutor.getID(), topic )
								.getPrice()*aptHours ) );

				bestTutor.decHours( aptHours );
				hoursRemaining -= aptHours;
				bestTutor = bestTutor( topic );
			}

			if( hoursRemaining > 0 ){
				tempList.reincrementTutors();
				System.out.printf( "No tutors available for Student "
						+ "%s for %d hours in topic %s.\n", studentid, 
						hours, topic );
			}else{
				// append tempList node objects in new nodes to 
				// tutor apt lists and print details
				tutorList.appendApts( tempList);
				// append tempList to student apt list
				student.getAptList().append( tempList );
			}
		}
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

