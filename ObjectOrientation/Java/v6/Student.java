/*
 * CLASS: Student
 *
 * Author: Xian Mardiros
 * 				 7862786
 *
 * REMARKS: only exists to have a more simplified constructor
 * 					than Person, and for conceptual clarity, all of
 * 					the other data a Student needs is contained
 * 					within Person
 */
public class Student extends Person{

  public Student( String userid ){
		super( userid, 0 );
  }
}

