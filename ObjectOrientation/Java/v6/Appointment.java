/*
 * CLASS: Appointment
 *
 * Author: Xian Mardiros
 * 				 7862786
 *
 * REMARKS: this class contains all of the data
 * 					relevant to a particular appointment, and the
 * 					methods for retreiving that data
 */
public class Appointment extends Superclass{
	// super.id is the topic, super.data is the price
	private Tutor tutor;
	private Student student;
	private Topic topic;
	private int hours;

	public Appointment( Tutor tutor, Student student, 
			Topic topic, int hours, int price ){
		super( topic.getID(), price );
		this.tutor = tutor;
		this.student = student;
		//super.id = topic;
		this.topic = topic;
		this.hours = hours;
	}

	public Tutor getTutor(){
		return tutor;
	}

	public Student getStudent(){
		return student;
	}
/*
	public String getTopic(){
		return id;
	}
*/
	public Topic getTopic(){
		return topic;
	}
	
	public int getHours(){
		return hours;
	}

	public int getPrice(){
		return getData();
	}
}
