/*
 * NAME						: Xian Mardiros
 * STUDENT NUMBER	: 7862786
 * COURSE					: COMP2160
 * INSTRUCTOR			: Mike Domaratski
 * ASSIGNMENT			: 1
 *
 * REMARKS: This program is meant to simulate a
 * 					gig-economy app that connects
 * 					tutors to students, and handles requests
 * 					by students for appointments with tutors
 * 					for a given subject at the lowest price.
*/

import java.io.*;
import java.util.Scanner;

public class Tutrfindr{
  TFDatabase appData;
  
  public static void main( String[] args ){
    
    System.out.println( "Tutrfindr begin processing\nInput text file name:" );

    try{
			// ask for the input file name
      Scanner file = new Scanner( new File( (new Scanner( 
								System.in ) ).nextLine() ) );

			// process the command file
      new Tutrfindr().processCmdFile( file );

    }catch( IOException error ){
			System.out.println( error.toString() );
    }

    System.out.println( "End processing." );
  }

	/*
	 * processCmdFile
	 *
	 * PURPOSE: go through each line of the file and process
	 * 					its command, if the command matches the 
	 * 					predetermined command format, otherwise the
	 * 					line is ignored or if it is a comment
	 * PARAMETERS: a scanner object of the file to be parsed
	 */
  public void processCmdFile( Scanner file ){
		// create database for app instance
		appData = new TFDatabase();

    boolean endFound = false;
    while( !endFound && file.hasNextLine() ){
			// checks the content and format of each line, and
			// calls the database methods with the line's 
			// containing parameters if appropriate
      String[] cmd = file.nextLine().split(" ");

			// is the line blank or a comment?
      if( cmd.length > 0 && cmd[0].charAt( 0 ) != '#' ){
				// does the line signal the program's end?
				if( cmd.length == 1 && cmd[0].equals( "END" ) ){
					endFound = true;

				// commands with one parameter
				}else if( cmd.length == 2 ){
					if( cmd[0].equals( "STUDENT" ) ){
						appData.addStudent( cmd[1] );

					}else if( cmd[0].equals( "STUDENTREPORT" ) ){
						appData.studentReport( cmd[1] );

					}else if( cmd[0].equals( "TUTORREPORT" ) ){
						appData.tutorReport( cmd[1] );
					}

				// commands with two parameters
				}else if( cmd.length == 3 && isNumeric( cmd[2] ) && 
						cmd[0].equals( "TUTOR" ) ){
					int hours = Integer.parseInt( cmd[2] );
					appData.addTutor( cmd[1], hours );

				// commands with three parameters
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
    if( endFound ){
      System.out.println( "BYE");
    }else{
      System.out.println( "END command missing");
    }
  }

	/*
	 * isNumeric
	 *
	 * PURPOSE: checks if a string is compose of numbers
	 * PARAMETERS: the string to check
	 * Returns: a boolean of whether the string is composed
	 * 					entirely of numbers or not
	 */
	public boolean isNumeric( String num ){
		boolean toReturn = true;
		try{
			// throws exception if number isn't numeric
			int i = Integer.parseInt( num );
		}catch( NumberFormatException error ){
			toReturn = false;
		}
		return toReturn;
	}
}


