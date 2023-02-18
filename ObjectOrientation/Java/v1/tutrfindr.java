import java.io.*
import java.util.*
public class Tutrfindr{
  private TutorList tutorList;
  private StudentList studentList;
  private AptList aptList;
  
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
    boolean quitFound = false;
    tutorList = new TutorList();
    studentList = new studentList();

    while( !quitFound && file.hasNextLine() ){
      String[] cmd = split( file.nextLine() );
      if( cmd[0].charAt( 0 ) == '#' ){
      }else if( cmd[0].equals( "TUTOR" ) ){
        tutorList.add( cmd[1], cmd[2] );

      }else if( cmd[0].equals( "STUDENT" ) ){
        studentList.add( cmd[1] );

      }else if( cmd[0].equals( "TOPIC" ) ){
        tutorList.topicAdd( cmd[1], cmd[2], cmd[3] );

      }else if( cmd[0].equals("REQUEST" ) ){
        Student student = studentList.retrieve( cmd[1] );
        if( student != null ){
          aptList.append( tutorList.request( student, cmd[2], cmd[3] ) );
        }

      }else if( cmd[0].equals( "STUDENTREPORT" ) ){
        studentList.report( cmd[1] );

      }else if( cmd[0].equals( "TUTORREPORT" ) ){
        tutorList.report( cmd[1] );

      }else if( cmd[0].equals( "QUIT" ) ){
        quitFound = true;
      }
    }
    if( quitFound ){
      System.out.println( "BYE");
    }else{
      System.out.println( "QUIT command missing");
    }
  }


public abstract class Person{
  private String userid;
  private int hours;
  
  public String getID(){
    return userid;
  }

  public int getHours(){
    return hours;
  }
}

public class Tutor extends Person{
  private TopicList topics;

  public Tutor( String userid, int hours ){
    super.userid = userid;
    super.hours = hours;
  }
  
  public void addTopic( String topic, int price ){
    topics.add( topic, price );
  }

  public TopicList getTopics(){
    return topics;
  }

  public void decHours( int hours ){
    this.hours -= hours;
  }

  public int retrievePrice( String topic ){
    return topics.retrievePrice( String topic );
  }
}

public class Student{

  public Student( String userid ){
    super.userid = userid;
    super.hours = 0;
  }
}

public class Topic{
  private String topic;
  private int price;

  public boolean equals( Topic toCompare ){
    return topic.equals( toCompare.topic );
  }

  public int getPrice(){
    return price;
  }

  public String getTopic(){
    return topic;
  }
}
