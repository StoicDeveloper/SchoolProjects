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
      new Tutrfindr().processCmdFile( file );
    }catch( IOException error ){
			System.out.println( error.toString() );
    }

    System.out.println( "End processing." );
  }

  public void processCmdFile( Scanner file ){
		appData = new TFDatabase();

    boolean quitFound = false;
    while( !quitFound && file.hasNextLine() ){
      String[] cmd = file.nextLine().split(" ");
      if( cmd.length > 0 && cmd[0].charAt( 0 ) != '#' ){
				if( cmd.length == 1 && cmd[0].equals( "QUIT" ) ){
					quitFound = true;

				}else if( cmd.length == 2 ){
					if( cmd[0].equals( "STUDENT" ) ){
						appData.addStudent( cmd[1] );

					}else if( cmd[0].equals( "STUDENTREPORT" ) ){
						appData.studentReport( cmd[1] );

					}else if( cmd[0].equals( "TUTORREPORT" ) ){
						appData.tutorReport( cmd[1] );
					}

				}else if( cmd.length == 3 && isNumeric( cmd[2] ) && 
						cmd[0].equals( "TUTOR" ) ){
					int hours = Integer.parseInt( cmd[2] );
					appData.addTutor( cmd[1], hours );

				}else if( cmd.length == 4 && isNumeric( cmd[3] ) ){
					int num = Integer.parseInt( cmd[3] );
					if( cmd[0].equals( "TOPIC" ) ){
						appData.addTopic( cmd[1], cmd[2], num );

					}else if( cmd[0].equals("REQUEST" ) ){
						appData.request( cmd[1], cmd[2], num );
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

	public Superclass( String id, int data ){
		this.id = id;
		this.data = data;
	}

  public String getID(){
    return id;
  }

  public int getData(){
    return data;
  }

	public void setData( int newValue ){
		data = newValue;
	}
  
  public boolean equals( String string ){
    return string.equals( id );
  }
}

abstract class Person extends Superclass{
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

class Tutor extends Person{
  private LinkedList topics;

  public Tutor( String userid, int hours ){
		super( userid, hours );
		topics = new LinkedList();
  }
 /* 
  public void addTopic( String topic, int price ){
    topics.insert( new Topic( topic, price ) );
  }
*/
  public LinkedList getTopics(){
    return topics;
  }

  public void decHours( int hours ){
    super.setData( getData() - hours );
  }

  public void incHours( int hours ){
    super.setData( getData() + hours );
  }
}

class Student extends Person{

  public Student( String userid ){
		super( userid, 0 );
  }
}

class Topic extends Superclass{

  public Topic( String topic, int price ){
		super( topic, price );
  }

  public int getPrice(){
    return super.getData();
  }

  public String getTopic(){
    return super.getID();
  }
}

// super.id is the topic, super.data is the price
class Appointment extends Superclass{
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
