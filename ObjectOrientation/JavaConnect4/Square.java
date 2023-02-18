// CLASS:   Square
//
// Author:  Xian Mardiros, 7862786
//
// REMARKS: This class bundles some info on a given
//          board square together so that it can be
//          easily passed as a return value.
//          Meaninless to modify squares, since it won't 
//          change the board that it describes
public class Square{
	private Status status;
	private int row;
	private int col;

	Square(Status status, int row, int col){
		this.status = status;
		this.row = row;
		this.col = col;
	}

	public Status getStatus(){return status;}
	public int getRow(){return row;}
	public int getCol(){return col;}
}

