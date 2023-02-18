/*
 * CLASS: TestTutrfindr
 *
 * Author: Xian Mardiros
 * 				 7862786
 *
 * REMARKS: tests the methods of the Tutrfindr classes,
 * 					focusing on the data structure
 */

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestTutrfindr{

	// Start data structure testing 
	
	@Test
	public void testCreateList(){
		LinkedList list = new LinkedList();
		assertTrue( list.isEmpty() );
	}

	@Test
	public void testCreateDatabase(){
		TFDatabase db = new TFDatabase();
		assertTrue( db.isEmpty() );
	}


	@Test
	public void testListInsert(){
		LinkedList list = new LinkedList();
		Student test = new Student( "Test" );
		list.insert( test );
		assertFalse( list.isEmpty() );
	}

	@Test
	public void testListGet(){
		LinkedList list = new LinkedList();
		Student test = new Student( "Test" );
		list.insert( test );
		assertEquals( list.get( "Test" ), test );
	}

	@Test
	public void testAddTutor(){
		TFDatabase db = new TFDatabase();
		String tutor = "Test";
		db.addTutor( tutor, 10 );
		assertFalse( db.isEmpty() );
	}

	@Test
	public void testAddStudent(){
		TFDatabase db = new TFDatabase();
		String student = "Test";
		db.addStudent( student );
		assertFalse( db.isEmpty() );

	}

	// end data structure testing
	
	@Test
	// confirms that the tutor class's methods work
	public void testTutor(){
		String id = "Test";
		int data = 10;
		Tutor tutor = new Tutor( id, data );
		assertTrue( tutor.getID().equals( id ) );
		assertEquals( data, tutor.getData() );
		tutor.incHours( 1 );
		assertEquals( tutor.getData(), 11 );
		tutor.decHours( 2 );
		assertEquals( tutor.getData(), 9 );
		assertTrue( tutor.getTopics().isEmpty() );
	}

	@Test
	public void testStudent(){
		Student student = new Student( "Test" );
		assertTrue( student.getID().equals( "Test") );
		assertEquals( student.getData(), 0 );
	}

	@Test
	public void testTopic(){
		Topic topic = new Topic( "Test", 10 );
		assertTrue( topic.getTopic().equals( "Test" ) );
		assertEquals( topic.getPrice(), 10 );
	}

	@Test
	// confirms that the the appointment class 
	// works as intended
	public void testAppointment(){
		Student student = new Student( "testStudent" );
		Tutor tutor = new Tutor( "testTutor", 10 );
		Topic topic = new Topic( "testTopic", 10 );
		Appointment apt = new Appointment( tutor, student, topic, 10, 5 );
		assertEquals( apt.getPrice(), 5 );
		assertEquals( apt.getHours(), 10);
		assertEquals( apt.getStudent(), student );
		assertEquals( apt.getTopic(), topic );
		assertEquals( apt.getTutor(), tutor );
	}
}
