import java.io.*;
import java.util.Scanner;

/* 

*/
public class Tutrfindr{
  TFDatabase appData;
  
  public static void main( String[] args ){
    
    System.out.println( "Tutrfindr begin processing\nInput text file name:" );

    try{
      Scanner file = new Scanner( new File( (new Scanner( System.in ) ).nextLine() ) );
      processCmdFile( file );
    }catch( IOException error ){
      error.print();
    }

    System.out.println( "End processing." );
  }

  public void processCmdFile( Scanner file ){
		appData = new TFDatabase();

    boolean quitFound = false;
    while( !quitFound && file.hasNextLine() ){
      String[] cmd = split( file.nextLine() );
      if( cmd.length() > 0 && cmd[0].charAt( 0 ) != '#' ){
				if( cmd.length == 1 && cmd[0].equals( "QUIT" ) ){
					quitFound = true;

				}else if( cmd.length == 2 ){
					if( cmd[0].equals( "STUDENT" ) ){
						addData.addStudent( cmd[1] );

					}else if( cmd[0].equals( "STUDENTREPORT" ) ){
						appData.studentReport( cmd[1] );

					}else if( cmd[0].equals( "TUTORREPORT" ) ){
						appData.tutorReport( cmd[1] );
					}

				}else if( cmd.length == 3 && isNumeric( cmd[2] ) && 
						cmd[0].equals( "TUTOR" ) ){
					appData.addTutor( cmd[1], cmd[2] );

				}else if( cmd.length == 4 && isNumeric( cmd[3] ) ){
					if( cmd[0].equals( "TOPIC" ) ){
						appData.addTopic( cmd[1], cmd[2], cmd[3] );

					}else if( cmd[0].equals("REQUEST" ) ){
						appData.request( cmd[1], cmd[2], cmd[3] );
					}
				}
			}
    }
    if( quitFound ){
      System.out.println( "BYE");
    }else{
      System.out.println( "QUIT command missing");
    }
  }

	public boolean isNumeric( String num ){
		boolean toReturn = true;
		try{
			int i = Integer.parseInt( num );
		}catch( NumberFormatException error ){
			toReturn = false;
		}
		return toReturn;
	}
}

abstract class Superclass{
  private String id;
  private int data;

  public String getID(){
    return userid;
  }

  public int getData(){
    return data;
  }
  
  public boolean equals( String string ){
    return string.equals( id );
  }
}

abstract class Person extends Superclass{
	private LinkedList aptList;

	public Person(){
		aptList = new LinkedList();
	}
	
	public LinkedList getAptList(){
		return aptList;
	}

	public void report(){
		String userid = getID();
		String personType = this instanceof Tutor 
			? "Tutor" : "Student";
		String exchangeType = this instanceof Tutor
			? "revenue" : "cost";

		System.out.printf( 
				"Report for %s %s\n" + 
				"--------------------------", personType, userid );
		int totalHours = 0;
		int totalPrice = 0;
		Node curr = aptList.top;
		while( curr != null ){
			Appointment apt = curr.item;
			int hours = apt.getHours();
			int price = apt.getPrice();
			System.out.printf( "Appointment: %s: %s, topic: "
					+ "%s, hours: %d, total %s: %d", personType,
					apt.getTutor().getID(), apt.getTopic(), hours, 
					exchangeType, price );
			totalHours += hours;
			totalPrice += price;
		}
		System.out.printf( "Total number of hours of tutoring: "
				+ "%d\nTotal %s tutoring: %d\n----------------"
				+ "---------", totalHours, exchangeType + this 
				instanceof Tutor ? " from" : " of", totalPrice );
	}
}

class Tutor extends Person{
  private LinkedList topics;

  public Tutor( String userid, int hours ){
    super.id = userid;
    super.data = hours;
  }
  
  public void addTopic( String topic, int price ){
    topics.add( topic, price );
  }

  public LinkedList getTopics(){
    return topics;
  }

  public void decHours( int hours ){
    this.data -= hours;
  }

  public void incHours( int hours ){
    this.data += hours;
  }
}

class Student extends Person{

  public Student( String userid ){
    super.id = userid;
    super.data = 0;
  }
}

class Topic extends Superclass{

  public Topic( String topic, int price ){
    super.id = topic;
    super.data = price;
  }

  public int getPrice(){
    return price;
  }

  public String getTopic(){
    return topic;
  }
}

// super.id is the topic, super.data is the price
class Appointment extends Superclass{
	private Tutor tutor;
	private Student student;
	private int hours;

	public Appointment( Tutor tutor, Student student, 
			String topic, int hours, int price ){
		this.tutor = tutor;
		this.student = student;
		super.id = topic;
		this.hours = hours;
		super.data = price;
	}

	public Tutor getTutor(){
		return tutor();
	}

	public Student getStudent(){
		return student();
	}

	public String getTopic(){
		return id;
	}

	public int getHours(){
		return hours;
	}

	public int getPrice(){
		return data;
	}
}
