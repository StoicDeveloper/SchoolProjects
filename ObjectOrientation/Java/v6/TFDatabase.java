/*
 * CLASS: TFDatabase
 *
 * AUTHOR: Xian Mardiros
 * 				 7862786
 * 
 * REMARKS: Provides an ADT for the app to use for 
 * 					storing all of the app's
 * 					tutorsm, students, and the tutor's topics.
 * 					All of the behaviour that the app needs 
 * 					from its various classes is provided by the
 * 					interface implemented in this class, such that
 * 					the app needn't refer to any class but this one.
 * 					
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

	public boolean isEmpty(){
		return tutorList.isEmpty() && studentList.isEmpty();
	}

	/*
	 * addTutor
	 *
	 * PURPOSE: calls insertObject on tutorList and prints
	 * 					output on success or if the tutor was a 
	 * 					duplicate
	 * PARAMETERS: The userid and hours of the desired tutor
	 */
  public void addTutor( String userid, int hours ){
    if( tutorList.insertObject( new Tutor( userid, hours ) ) ){
			System.out.printf( "Tutor with userid %s successfully" 
					+ " created.\n", userid );
		}else{
			System.out.printf( "Duplicate Tutor with userid %s.\n",
					userid );
		}
		//tutorList.print();
  }

	/*
	 * addStudent
	 *
	 * PURPOSE: calls insertObject on studentList and prints
	 * 					output on success or if the student was a 
	 * 					duplicate
	 * PARAMETERS: The userid of the desired student
	 */
  public void addStudent( String userid ){
    if( studentList.insertObject( new Student( userid ) ) ){
			System.out.printf( "Student with userid %s "
					+ "successfully created.\n", userid );
		}else{
			System.out.printf( "Duplicate Student with userid "
					+ "%s.\n", userid );
		}
		//studentList.print();
  }

	/*
	 * addTopic
	 *
	 * PURPOSE: adds the specified topic to the correct tutor
	 * 					and prints output on success or if the tutor 
	 * 					was a duplicate or if the tutor wasn't found
	 * PARAMETERS: The topic name and the userid and price
	 * 						 of the desired tutor
	 */
	public void addTopic( String topic, String tutorid, int price ){
		Tutor tutor = (Tutor) tutorList.get( tutorid );
		if( tutor != null ){
			if( tutor.getTopics().insertObject( 
						new Topic( topic, price ) ) ){
				System.out.printf( "Topic %s added to Tutor %s with" 
						+ " price %d.\n", topic, tutorid, price );
			}else{
				System.out.printf( "Duplicate topic %s for Tutor"
						+ "%s.\n", topic, tutorid );
			}
		}else{
			System.out.printf( "Tutor %s not found.\n", tutorid );
		}
	}

	/*
	 * request
	 *
	 * PURPOSE: try to create appointments for a given student
	 * 					to have tutoring for the parameter topic,
	 * 					print output on the success or failure of the
	 * 					attempt
	 * PARAMETERS: The userid of the requesting student, 
	 * 						 a string of the desired topic, and how 
	 * 						 many hours the tutoring will be for
	 */
	public void request( String studentid, String topicid, 
			int hours ){
	// 1) check if student present
	// 2) create temp aptList
	// 3) find best tutor for the topic
	// 4) add tutor to temp list
	// 5) if hours remain to be filled, repeat 3&4 until no
	// 		hours remain or no more tutors teach that topic
	// 6) if no hours remain, iterate temp list
	// 		adding nodes to the tutor aptLists, then append list
	// 		to the student's list, else reincrement tutors and 
	// 		discard the temp list
	
		Student student = (Student) studentList.get( studentid );
		if( student != null ){
			System.out.printf( "Attempting to fulfil request for " 
					+ "%s to receive %d hours of tutoring in topic "
					+ "%s.\n", studentid, hours, topicid );

			int hoursRemaining = hours;
			LinkedList tempList = new LinkedList();
			
			Tutor bestTutor = tutorList.bestTutor( topicid );
			while( hoursRemaining > 0 && bestTutor != null ){
				//System.out.println( bestTutor.getID() );
				//
				int aptHours = bestTutor.getData() < hoursRemaining 
					? bestTutor.getData() : hoursRemaining;
				Topic topic = (Topic)bestTutor.getTopics().
					get( topicid );

				//System.out.println( bestTutor.getData() );

				tempList.insert( new Appointment( 
								bestTutor, student, topic, aptHours, 
								((Topic)bestTutor.getTopics().get( topicid ))
								.getPrice()*aptHours ) );

				bestTutor.decHours( aptHours );
				hoursRemaining -= aptHours;
				bestTutor = tutorList.bestTutor( topicid );
			}

			if( hoursRemaining > 0 ){
				tempList.reincrementTutors();

				System.out.printf( "No tutors available for Student "
						+ "%s for %d hours in topic %s.\n", studentid, 
						hours, topicid );
			}else{
				// append tempList's appointments to 
				// tutor apt lists and print details
				tutorList.appendApts( tempList);

				// append tempList to student apt list
				student.getAptList().appendList( tempList );
			}
		}
	}

	/*
	 * studentReport
	 *
	 * PURPOSE: calls report on the specified student if they
	 * 					exist
	 * PARAMETERS: The userid of the student to report on
	 */
	public void studentReport( String userid ){
		Student student = (Student) studentList.get( userid );
		if( student != null ){
			student.report();
		}
	}

	/*
	 * tutorReport
	 *
	 * PURPOSE: calls report on the specified tutor if they 
	 * 					exist.
	 * PARAMETERS: The userid of the tutor to report on
	 */
	public void tutorReport( String userid ){
		Tutor tutor = (Tutor) tutorList.get( userid );
		if( tutor != null ){
			tutor.report();
		}
	}
}

