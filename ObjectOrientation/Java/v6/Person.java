/*
 * CLASS: Person
 *
 * Author: Xian Mardiros
 * 				 7862786
 *
 * REMARKS: an abstract class that contains data and 
 * 					behaviour useful to both tutors and students
 */
public abstract class Person extends Superclass{
	private LinkedList aptList;

	public Person( String id, int data ){
		super( id, data );
		aptList = new LinkedList();
	}
	
	public LinkedList getAptList(){
		return aptList;
	}

	public void report(){
		String personType = this instanceof Tutor 
			? "Tutor" : "Student";
		System.out.printf( 
				"\nReport for %s %s\n" + 
				"--------------------------\n", personType, getID() );
		aptList.report( personType );
	}
}

