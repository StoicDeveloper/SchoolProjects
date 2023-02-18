import java.io.*
import java.util.*

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
    boolean quitFound = false;
    tutorList = new TutorList();
    studentList = new studentList();

    while( !quitFound && file.hasNextLine() ){
      String[] cmd = split( file.nextLine() );
      if( cmd[0].charAt( 0 ) == '#' ){
      }else if( cmd[0].equals( "TUTOR" ) ){
        appData.addTutor( cmd[1], cmd[2] );

      }else if( cmd[0].equals( "STUDENT" ) ){
        studentList.addStudent( cmd[1] );

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


public abstract class Superclass{
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

public class Tutor extends Superclass{
  private TopicList topics;

  public Tutor( String userid, int hours ){
    super.id = userid;
    super.data = hours;
  }
  
  public void addTopic( String topic, int price ){
    topics.add( topic, price );
  }

  public TopicList getTopics(){
    return topics;
  }

  public void decHours( int hours ){
    this.data -= hours;
  }

  public int retrievePrice( String topic ){
    return topics.retrievePrice( String topic );
  }
}

public class Student extends Superclass{

  public Student( String userid ){
    super.id = userid;
    super.data = 0;
  }
}

public class Topic extends Superclass{

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
