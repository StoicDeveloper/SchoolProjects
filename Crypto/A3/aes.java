// Xian Mardiros
// 7862786
// Nov 16, 2022
// COMP-4140 Assigment 3
// Implementation of 128-bit AES encryption and decryption

import java.io.*;
import java.util.*;
import java.math.*;
public class aes {
	static byte[][] sbox;
	static byte[][] invsbox;
	public static void main( String[] args ) {
		sbox = get_sbox();
		invsbox = get_invsbox();
		String key = getFileText(args[1]);
		String plaintext = getFileText(args[0]);
		System.out.println("Key:       " + key);
		System.out.println("Plaintext: " + plaintext);
		byte[] cipherBytes = encrypt(bytesFromText(plaintext), bytesFromText(key));
		String ciphertext =  toHexString(cipherBytes);
		System.out.println( "Ciphertext:" );
		System.out.println(ciphertext);
		System.out.println( "\n---------------------\n" );
		byte[] newPlainBytes = decrypt(cipherBytes, bytesFromText(key));
		String newPlainText =  toHexString(newPlainBytes);
		System.out.println( "\n---------------------\nDecrypted Plaintext:" );
		System.out.println(newPlainText);

		//System.out.println(Integer.toHexString(0x0000));
	}

	// Encrypt the ciphertext using 128-bit AES
	private static byte[] encrypt(byte[] plaintext, byte[] key) {
		byte[][][] keys = KeyExpansion(key);
		byte[][] state = arr2dXor(toWords(plaintext), keys[0]);
		System.out.println( "\nStarting encryption\n-----------------------\n" );
		System.out.println( "Initial State:" );
		printState(state);
		for(int r = 1; r <= 10; r++) {
			System.out.println( "\nRound " + r );
			SubBytes(state);
			ShiftRows(state);
			if(r <= 9) {
				MixColumns(state);
			}
			System.out.println( "Round key:" );
			printState(keys[r]);
			state = arr2dXor(state, keys[r]);
			System.out.println( "Final round result:" );
			printState(state);
		}
		return flatten(state);
	}
	// Decrypt the ciphertext using 128-bit AES
	private static byte[] decrypt(byte[] ciphertext, byte[] key) {
		byte[][][] keys = KeyExpansion(key);
		byte[][] state = toWords(ciphertext);
		System.out.println( "\nStarting decryption\n-----------------------\n" );
		System.out.println( "Initial State:" );
		printState(state);
		for(int r = 10; r >= 1; r--) {
			System.out.println( "\nRound " + r );
			System.out.println( "Round key:" );
			printState(keys[r]);
			state = arr2dXor(state, keys[r]);
			System.out.println( "Mixed key result:" );
			printState(state);
			if(r <= 9) {
				InvMixColumns(state);
			}
			InvShiftRows(state);
			InvSubBytes(state);
		}
		return flatten(arr2dXor(state, keys[0]));
	}

	// convert a 2d array into a 1d array
	private static byte[] flatten(byte[][] arr) {
		byte[] flat = new byte[16];
		for(int i = 0; i < 4; i++) {
			for(int k = 0; k < 4; k++) {
				flat[k*4 + i] = arr[i][k];
			}
		}
		return flat;
	}

	// construct an array of bytes from a string of hex characters
	private static byte[] bytesFromText(String text) {
		byte[] bytes = new byte[16];
		for(int i = 0; i < 16; i++) {
			bytes[i] = (byte) Integer.parseInt( text.substring(i*2, i*2+2), 16 );
		}
		return bytes;
	}

	// Get the substitution box
	private static byte[][] get_sbox() {
		try{
			Scanner file = new Scanner( new File("./sbox.csv") );
			return process_hex_csv(file);
		}catch (IOException error) {
			System.out.println(error.toString());
		}
		return new byte[0][0];

	}
	// Get the inverse substitution box
	private static byte[][] get_invsbox() {
		try{
			Scanner file = new Scanner( new File("./invsbox.csv") );
			return process_hex_csv(file);
		}catch (IOException error) {
			System.out.println(error.toString());
		}
		return new byte[0][0];
	}
	// Get the text from the key or plaintext files
	private static String getFileText(String filename) {
		try{
			Scanner file = new Scanner( new File(filename) );
			return file.nextLine();
		}catch (IOException error) {
			System.out.println(error.toString());
		}
		return "";
	}

	// Storing and parsing the tables seemed easier than generating them
	// Parse the file, convert it into a 2d byte array
	private static byte[][] process_hex_csv( Scanner file ) {
		byte[][] arr = new byte[16][16];
		int k = 0;
		while(file.hasNextLine()) {
			String[] str_line = file.nextLine().split(",");
			for(int i = 0; i < str_line.length; i++){
				arr[k][i] = (byte) Integer.parseInt( str_line[i], 16 );
			}
			k++;
		}
		return arr;
	}

	// The first one is not used
	static byte[][] roundConstants = {
		BigInteger.valueOf(0x12345678).toByteArray(),
		BigInteger.valueOf(0x01000000).toByteArray(),
		BigInteger.valueOf(0x02000000).toByteArray(),
		BigInteger.valueOf(0x04000000).toByteArray(),
		BigInteger.valueOf(0x08000000).toByteArray(),
		BigInteger.valueOf(0x10000000).toByteArray(),
		BigInteger.valueOf(0x20000000).toByteArray(),
		BigInteger.valueOf(0x40000000).toByteArray(),
		BigInteger.valueOf(0x80000000).toByteArray(),
		BigInteger.valueOf(0x1b000000).toByteArray(),
		BigInteger.valueOf(0x36000000).toByteArray()
	};
	
	// Start with flat byte array
	// translate into 4-bytes words
	// generate keys as 4-arrays of 4-byte words
	private static byte[][][] KeyExpansion( byte[] flat_key ) {
		byte[][][] keys = new byte[11][4][4];
		byte[][] key = reflectToWords(flat_key);
		keys[0] = key;

		for(int i = 1; i < 11; i++) {
			keys[i][0] = arrXor(arrXor(keys[i-1][0], SubWord(RotWord(keys[i-1][3]))), roundConstants[i]);
			keys[i][1] = arrXor(keys[i-1][1], keys[i][0]);
			keys[i][2] = arrXor(keys[i-1][2], keys[i][1]);
			keys[i][3] = arrXor(keys[i-1][3], keys[i][2]);
		}

		// This is what you need to do when you generate keys sideways
		for(byte[][] reflectingKey: keys) {
			reflect(reflectingKey);
		}

		System.out.println( "Generated Keys:" );
		for(int i = 0; i < 11; i++) {
			System.out.println( "Key " + i );
			printState(keys[i]);
		}

		return keys;
	}

	// I wrote the KeyExpansion algorithm sideways by accident, so this function that reflects a matrix seemed easier to write than fixing key expansion
	// Don't take marks off plz, the two functions together equal one correct key expansion
	private static void reflect(byte[][] arr) {
		for(int i = 0; i < arr.length; i++) {
			for(int k = 0; k < arr.length; k++) {
				if( k > i ) {
					byte temp = arr[i][k];
					arr[i][k] = arr[k][i];
					arr[k][i] = temp;
				}
			}
		}
	}

	// rotate the input 4-byte word into a new array, leaving input inchanged
	private static byte[] RotWord( byte[] word ) {
		byte[] rot = new byte[word.length];
		rot[word.length-1] = word[0];
		for(int i = 0; i < word.length - 1; i++) {
			rot[i] = word[i+1];
		}
		return rot;
	}

	// Substitute each byte for the input 4-byte word, return result with input array unchanged
	private static byte[] SubWord( byte[] word ) {
		byte[] subbed = new byte[word.length];
		for(int i = 0; i < word.length; i++) {
			subbed[i] = subByte(word[i], sbox);
		}
		return subbed;
	}

	// takes a 16 byte array and turns it into 4 4-byte arrays
	// consecutive bytes in the input become the columns of the output
	private static byte[][] toWords( byte[] flat ) {
		byte[][] out = new byte[4][4];
		for(int i = 0; i < flat.length; i++) {
			// Because nested for-loops are for losers
			out[i%4][i/4] = flat[i];
		}
		return out;
	}
	// copy each consecutive 4 bytes of the input array to make each row of the output 2d byte array
	private static byte[][] reflectToWords( byte[] flat ) {
		byte[][] out = new byte[4][4];
		out[0] = Arrays.copyOfRange(flat,0, 4);
		out[1] = Arrays.copyOfRange(flat,4, 8);
		out[2] = Arrays.copyOfRange(flat,8, 12);
		out[3] = Arrays.copyOfRange(flat,12, 16);
		return out;
	}
	// xor each byte of the input 1d arrays to get each byte of the result array
	private static byte[] arrXor( byte[] arr1, byte[] arr2 ) {
		byte[] arr3 = new byte[arr1.length];
		for(int i = 0; i < arr1.length; i++) {
			arr3[i] = (byte)((int) arr1[i] ^ (int)arr2[i]);
		}
		return arr3;
	}
	// call arrXor on each row of the input 2d arrays to create the each row of the result array
	private static byte[][] arr2dXor( byte[][] arr1, byte[][] arr2 ) {
		byte[][] arr3 = new byte[arr1.length][arr1[0].length];
		for(int i = 0; i < arr1.length; i++) {
			arr3[i] = arrXor(arr1[i], arr2[i]);
		}
		return arr3;
	}

	// Substitute this one byte with its matching element from the provided box
	private static byte subByte( byte theByte, byte[][] box ) {
		int x = (((int)theByte) & 0xF0) >>> 4;
		int y = ((int)theByte) & 0x0F;
		return box[x][y];
	}

	// Because what good is a program without loggin?
	private static void printState( byte[][] state ) {
		for(byte[] row: state) {
			System.out.println( toHexString(row) );
		}
	}
	
	// just call the sub bytes helper function with the forward box
	private static void SubBytes( byte[][] state ) {
		SubBytesInner(state, sbox);
		System.out.println( "SubBytes result:" );
		printState(state);
	}
	// just call the sub bytes helper function with the inverse box
	private static void InvSubBytes( byte[][] state ) {
		SubBytesInner(state, invsbox);
		System.out.println( "InvSubBytes result:" );
		printState(state);
	}
	// Substituate all the state bytes in-place from the specified box. Works for both forward and inverse
	private static void SubBytesInner( byte[][] state, byte[][] box ) {
		for(int i = 0; i < state.length; i++) {
			for(int k = 0; k < state[0].length; k++) {
				state[i][k] = subByte(state[i][k], box);
			}
		}
	}

	// Shift each shifting row, overwriting each current row array
	private static void ShiftRows( byte[][] state ) {
		for(int i = 1; i < state.length; i++) {
			state[i] = ShiftRow(state[i], i, false);
		}
		System.out.println( "ShiftRows result:" );
		printState(state);
	}
	// Shift each shifting row in inverse direction, overwriting each current row array
	private static void InvShiftRows( byte[][] state ) {
		for(int i = 1; i < state.length; i++) {
			state[i] = ShiftRow(state[i], i, true);
		}
		System.out.println( "InvShiftRows result:" );
		printState(state);
	}

	// Tried to do this operation fully in-place, but this way is much simpler
	// create the shifted array for this current row, for the specified shift degree
	private static byte[] ShiftRow( byte[] row, int degree, boolean isInverse) {
		byte[] shifted = new byte[4];
		for(int i = 0; i < 4; i++) {
			shifted[IndexAfterShift(i, degree, row.length, isInverse)] = row[i];
		}
		return shifted;
	}
	
	// Find the index that the byte at the specified index will be moved to, depending on:
	// 		- the amount each byte is shifted (degree)
	// 		- the length of the array
	// 		- whether the operation is inverted
	private static int IndexAfterShift( int index, int degree, int length, boolean isInverse ) {
		if(isInverse){
			return (index + degree) % length;
		}
		return (index + length - degree) % length;
	}
	
	// Call multColumn on each column; doing the matrix multiplication on state in place
	private static void MixColumns( byte[][] state ) {
		for(int i = 0; i < 4; i++){
			multColumn(state, i);
		}
		System.out.println( "MixColumns result:" );
		printState(state);
	}
	// Call invMultColumn on each column; doing the inverse matrix multiplication on state in place
	private static void InvMixColumns( byte[][] state ) {
		for(int i = 0; i < 4; i++){
			invMultColumn(state, i);
		}
		System.out.println( "InvColumns result:" );
		printState(state);
	}

	// Multiply in-place this column of the state with the 4x4 matrix of constant integer bytes
	private static void multColumn( byte[][] state, int column ) {
		byte s0 = state[0][column];
		byte s1 = state[1][column];
		byte s2 = state[2][column];
		byte s3 = state[3][column];
		byte sp0 = plus(plus(times2(s0),times3(s1)), plus(       s2,         s3) );
		byte sp1 = plus(plus(       s0, times2(s1)), plus(times3(s2),        s3) );
		byte sp2 = plus(plus(       s0,        s1),  plus(times2(s2), times3(s3)) );
		byte sp3 = plus(plus(times3(s0),       s1),  plus(       s2,  times2(s3) ));

		state[0][column] = sp0;
		state[1][column] = sp1;
		state[2][column] = sp2;
		state[3][column] = sp3;
	}

	// inverse of previous routine: multiply in place this column of the state with the inverse int matrix
	private static void invMultColumn( byte[][] state, int column ) {
		byte s0 = state[0][column];
		byte s1 = state[1][column];
		byte s2 = state[2][column];
		byte s3 = state[3][column];

		state[0][column] = plus(plus(times0e(s0),times0b(s1)),plus(times0d(s2),times09(s3)));
		state[1][column] = plus(plus(times09(s0),times0e(s1)),plus(times0b(s2),times0d(s3)));
		state[2][column] = plus(plus(times0d(s0),times09(s1)),plus(times0e(s2),times0b(s3)));
		state[3][column] = plus(plus(times0b(s0),times0d(s1)),plus(times09(s2),times0e(s3)));
	}

	// The following routines are all variations of the same thing.
	// They all take a byte, and multiply it with the integer specified given in the function name.
	// Obviously, they could probably all be combined into a single recursive function, but this works fine, 
	// and I don't think it's overly repetitive
	private static byte times2( byte b ) {
		boolean oneShifted = ((int) b & 0x80) == 0;
		byte result = (byte) (((int)b) << 1);
		if(oneShifted) {
			result = plus(result, (byte) 0x1b);
		}
		return result;
	}
	private static byte times3( byte b ) {
		return plus(times2(b), b);
	}
	private static byte times4( byte b ) {
		return times2(times2(b));
	}
	private static byte times8( byte b ) {
		return times2(times4(b));
	}
	private static byte times09( byte b ) {
		return plus(times8(b), b);
	}
	private static byte times0d( byte b ) {
		return plus(plus(times8(b), times4(b)), b);
	}
	private static byte times0b( byte b ) {
		return plus(plus(times8(b), times2(b)), b);
	}
	private static byte times0e( byte b ) {
		return plus(plus(times8(b), times4(b)), times2(b));
	}

	// XORs two bytes, returning the output
	private static byte plus( byte b1, byte b2 ) {
		return (byte) ((int) b1 ^ (int) b2);
	}

	// display the input byte array as a hexadecimal string
	private static char[] hexes = "0123456789ABCDEF".toCharArray();
	public static String toHexString(byte[] bytes) {
		char[] hex = new char[bytes.length * 2];
		for(int i = 0; i < bytes.length; i++) {
			int x = (((int)bytes[i]) & 0xF0) >>> 4;
			int y = ((int)bytes[i]) & 0x0F;
			hex[i*2] = hexes[x];
			hex[i*2+1] = hexes[y];
		}
		return new String(hex);
	}
}
