import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class checker {
	
	
	public static void main(String args[]) {
		
		Scanner sc = new Scanner(System.in);
		//ask if user wants to play with AI or player
		System.out.println("\nWelcome to Checkers :)");
		System.out.print("Key in '1' for multiplayer or '2' for single player: ");
		int mode = sc.nextInt();
		
		
		//set up board
		//Board game = new Board("test");
		Board game = new Board();
		game.printBoard();
		int[] score = game.updateScore();
		System.out.printf("Initial score: {%d,%d}\n ",score[0],score[1]);
		
//MULTIPLAYER = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 
		if(mode==1) {
			//give instructions and stuff
			String piecepos;
			String move;
			int player = 1;
			
			System.out.println("\nYou are Player 1 (x).\nPlease key in the piece that you want to move by its position "
					+ "(eg: 50 will be the piece at row 5 column 0)\nMove can be one move to a position (eg:43) or jumps (eg:30 12)\n"
					+ "Key in 'exit' when asked for piece to move to stop the game.");
			while(score[0]!=0 && score[1]!=0) {
				
				System.out.printf("\nPlayer %d piece to move: ",player);
				piecepos = sc.next();
				if(piecepos.equalsIgnoreCase("exit"))break;
				
				//check if valid piece
				int pos[] = new int[2];
				pos[0] = Character.getNumericValue(piecepos.charAt(0));
				pos[1] = Character.getNumericValue(piecepos.charAt(1));
				if(game.isOccupied(pos)) {
					if((game.board[pos[0]][pos[1]].color == 'x' || game.board[pos[0]][pos[1]].color == 'K') && player==1) {player=2;}
					else if((game.board[pos[0]][pos[1]].color == 'o' || game.board[pos[0]][pos[1]].color == 'Q') && player==2) {player=1;}
					else {
						System.out.print("It's the other player's turn.\n");
						player=player*3;
					}
					if(player<3) {
						sc.nextLine();
						System.out.print("Move: ");
						move = sc.nextLine();
						if(game.board[pos[0]][pos[1]].Move(move, game)) {
							game.printBoard();
							score = game.updateScore();
							System.out.printf("Score: {%d,%d}\n ",score[0],score[1]);
						}
						else {
							if(player==1)player=2;
							else player=1;
						}
						
					}
					else {player=player/3;}
				}
				else {System.out.println("Invalid position.");}
			
			}
		}
//SINGLE PLAYER = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 
		else if(mode==2) {
			//give instructions and stuff
			String piecepos = "";
			String move;
			int player = 1;
			
			System.out.println("\nYou are Player 1 (x).\nPlease key in the piece that you want to move by its position "
					+ "(eg: 50 will be the piece at row 5 column 0)\nMove can be one move to a position (eg:43) or jumps (eg:30 12)\n"
					+ "Key in 'exit' when asked for piece to move to stop the game.");
			
			while(score[0]!=0 && score[1]!=0) {	
				
				while(player==1) {
					System.out.printf("\nPlayer %d piece to move: ",player);
					piecepos = sc.next();
					if(piecepos.equalsIgnoreCase("exit"))break;
					
					//check if valid piece
					int pos[] = new int[2];
					pos[0] = Character.getNumericValue(piecepos.charAt(0));
					pos[1] = Character.getNumericValue(piecepos.charAt(1));
					if(game.isOccupied(pos)) {
						if((game.board[pos[0]][pos[1]].color == 'x' || game.board[pos[0]][pos[1]].color == 'K') && player==1) {player=2;}
						else {
							System.out.printf("%d%d is not yours to move.\n",pos[0],pos[1]);
							player=player*3;
						}
						if(player<3) {
							sc.nextLine();
							System.out.print("Move: ");
							move = sc.nextLine();
							if(game.board[pos[0]][pos[1]].Move(move, game)) {
								game.printBoard();
								score = game.updateScore();
								System.out.printf("Score: {%d,%d}\n ",score[0],score[1]);
							}
							else {player=1;}
							
						}
						else {player=player/3;}
					}
					else {System.out.println("Invalid position.");}
				}
				
				if(piecepos.equalsIgnoreCase("exit"))break;
				
				//run AI to find best move
				AI notSoClever = new AI(game);
				int n = 3; //change this for search depth
				String[] best = notSoClever.alphabeta(notSoClever.tree.root, n, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
				//move and update board
				String bestmove = best[1];
				int peepee = Character.getNumericValue(bestmove.charAt(0));
				int poopoo = Character.getNumericValue(bestmove.charAt(1));
				String moo = bestmove.substring(3);
				

				System.out.printf("\nPlayer 2 moves piece %d%d to "+moo+".\n",peepee,poopoo);
				game.board[peepee][poopoo].Move(moo, game);
				game.printBoard();
				score = game.updateScore();
				System.out.printf("Score: {%d,%d}\n ",score[0],score[1]);
				
				player=1;
			}
		}
		sc.close();
	}	

}


class Piece{
	
	int val;
	char color;
	int[] pos;
	
	public Piece() {
		
	}
	
	/**
	 * Constructor
	 * @param color = char piece's color (pawn: o or x; king: Q or K)
	 * @param pos = int array piece's position 
	 */
	public Piece(char color, int[] pos) {
		this.color = color;
		this.pos = pos;
		
		if(color=='o'||color=='x') {this.val = 1;}
		else if(color=='Q'||color=='K') {this.val = 2;}
		
	}
	
	public Piece(Piece p) {
		this.val = p.val;
		this.color = p.color;
		this.pos = p.pos;
	}
	
	
	public Boolean Move(String move, Board game) {
		
		//take input and store in int array
		StringTokenizer st = new StringTokenizer(move);
		int i=0;
		boolean validmove = true;
		boolean jump = true;
		
		boolean crowned;
		if(this.color=='o' || this.color=='x') crowned = false;
		else crowned = true;
		
		ArrayList<int[]> delete = new ArrayList<int[]>();
		if(!st.hasMoreTokens()) return false;
		while(st.hasMoreTokens()) {
			String post = st.nextToken();
			int posA[] = new int[2];
			posA[0] = Character.getNumericValue(post.charAt(0));
			posA[1] = Character.getNumericValue(post.charAt(1));
			
			//check if move is legal
			if(post.length()>2) {
				System.out.printf("invalid move: "+post+" \n\n");
				validmove = false;
				break;
			}
			if((posA[0])%2 == (posA[1])%2) {
				System.out.printf("invalid move: %d%d \n\n",posA[0],posA[1]);
				validmove = false;
				break;
			}
			//check if position is occupied
			if(game.isOccupied(posA)) {
				System.out.printf("position %d%d is occupied\n\n",posA[0],posA[1]);
				validmove=false;
				break;
			}
			//pawns can only move forward, unless capturing opponent
			//for x
			if(this.color=='x') {
				if(pos[0]>posA[0] || (i%2==1 && jump)) {
					//move forward once
					if(pos[0]-posA[0]==1 && (Math.abs(pos[1]-posA[1])==1)) {
						if(i>0) {
							System.out.printf("invalid 2 move: %d%d \n\n",posA[0],posA[1]);
							validmove = false;
							break;
						}
						jump=false;
					}
					//jump/attack
					else if((pos[0]-posA[0]==2||(i>0 && Math.abs(pos[0]-posA[0])==2))&& (Math.abs(pos[1]-posA[1])==2)) {
						if(jump==false) {
							validmove = false;
							System.out.printf("invalid jump after move: %d%d \n\n",posA[0],posA[1]);
							break;
						}
						int check[] = new int[2];
						if(posA[0]<pos[0]) check[0]=pos[0]-1;
						else check[0]=pos[0]+1;
						if(posA[1]>pos[1]) check[1]=pos[1]+1;
						else check[1]=pos[1]-1;
						
						//check if jumped piece is in delete
						for(int k=0; k<delete.size(); k++) {
							int[] cdelete = delete.get(k);
							if(cdelete[0]==check[0] && cdelete[1]==check[1]) {
								validmove = false;
								System.out.printf("invalid jump: %d%d \n\n",posA[0],posA[1]);
								break;
							}
						}if(!validmove) {break;}
						if(!game.isOccupied(check) || game.board[check[0]][check[1]].color==this.color) {
							validmove = false;
							System.out.printf("invalid jump: %d%d \n\n",posA[0],posA[1]);
							break;
						}
						delete.add(check);
					}
					else {
						System.out.printf("invalid ? move: %d%d \n\n",posA[0],posA[1]);
						validmove = false;
						break;
					}	
				}
				else 
				{
					System.out.printf("invalid backward move: %d%d \n\n",posA[0],posA[1]);
					validmove = false;
					break;
				}
				if(validmove && posA[0]==0) {
					this.color='K'; 
					this.val++;
					this.pos=posA;
					game.updateBoard(this);
					if(jump) {
						for(int j =0; j<=i; j++) {
							int[] temp = delete.remove(0);
							game.board[temp[0]][temp[1]]=null;
						}
					}
					break;
				}
			}
			//for o
			else if(this.color=='o') {
				if(pos[0]<posA[0] || (i%2==1 && jump)) {
					//move forward once
					if(posA[0]-pos[0]==1 && (Math.abs(pos[1]-posA[1])==1)) {
						if(i>0) {
							System.out.printf("invalid 2 move: %d%d \n\n",posA[0],posA[1]);
							validmove = false;
							break;
						}
						jump=false;
					}
					//jump/attack
					else if((posA[0]-pos[0]==2||(i>0 && Math.abs(pos[0]-posA[0])==2))&& (Math.abs(pos[1]-posA[1])==2)) {
						if(jump==false) {
							validmove = false;
							System.out.printf("invalid jump after move: %d%d \n\n",posA[0],posA[1]);
							break;
						}
						int check[] = new int[2];
						if(posA[0]<pos[0]) check[0]=pos[0]-1;
						else check[0]=pos[0]+1;
						if(posA[1]>pos[1]) check[1]=pos[1]+1;
						else check[1]=pos[1]-1;
						
						//check if jumped piece is in delete
						for(int k=0; k<delete.size(); k++) {
							int[] cdelete = delete.get(k);
							if(cdelete[0]==check[0] && cdelete[1]==check[1]) {
								validmove = false;
								System.out.printf("invalid jump: %d%d \n\n",posA[0],posA[1]);
								break;
							}
						}if(!validmove) {break;}
						if(!game.isOccupied(check) || game.board[check[0]][check[1]].color==this.color) {
							validmove = false;
							System.out.printf("invalid jump: %d%d \n\n",posA[0],posA[1]);
							break;
						}
						delete.add(check);
					}
					else {
						System.out.printf("invalid ? move: %d%d \n\n",posA[0],posA[1]);
						validmove = false;
						break;
					}
				}
				else {
					System.out.printf("invalid backward move: %d%d \n\n",posA[0],posA[1]);
					validmove = false;
					break;
				}
				if(validmove && posA[0]==7) {
					this.color='Q'; 
					this.val++;
					this.pos = posA;
					game.updateBoard(this);
					if(jump) {
						for(int j =0; j<=i; j++) {
							int[] temp = delete.remove(0);
							game.board[temp[0]][temp[1]]=null;
						}
					}
					break;
				}
			}
			else if((this.color=='K' || this.color=='Q')&& crowned) {
				//move once
				if(Math.abs(pos[0]-posA[0])==1 && (Math.abs(pos[1]-posA[1])==1)) {
					if(i>0) {
						System.out.printf("invalid move: %d%d \n\n",posA[0],posA[1]);
						validmove = false;
						break;
					}
					jump=false;
				}
				//jump/attack
				else if((Math.abs(pos[0]-posA[0])==2||(i>0 && Math.abs(pos[0]-posA[0])==2))&& (Math.abs(pos[1]-posA[1])==2)) {
					if(jump==false) {
						validmove = false;
						System.out.printf("invalid jump after move: %d%d \n\n",posA[0],posA[1]);
						break;
					}
					int check[] = new int[2];
					if(posA[0]<pos[0]) check[0]=pos[0]-1;
					else check[0]=pos[0]+1;
					if(posA[1]>pos[1]) check[1]=pos[1]+1;
					else check[1]=pos[1]-1;
					
					//check if jumped piece is in delete
					for(int k=0; k<delete.size(); k++) {
						int[] cdelete = delete.get(k);
						if(cdelete[0]==check[0] && cdelete[1]==check[1]) {
							validmove = false;
							System.out.printf("invalid jump: %d%d \n\n",posA[0],posA[1]);
							break;
						}
					}if(!validmove) {break;}			
					if(!game.isOccupied(check) || game.board[check[0]][check[1]].color==this.color || (this.color=='K'&&game.board[check[0]][check[1]].color=='x') || (this.color=='Q'&&game.board[check[0]][check[1]].color=='o')) {
						validmove = false;
						System.out.printf("invalid jump: %d%d \n\n",posA[0],posA[1]);
						break;
					}
					delete.add(check);
				}
				else {
					System.out.printf("invalid ? move: %d%d \n\n",posA[0],posA[1]);
					validmove = false;
					break;
				}	
			}			
			
			//if legal, update position
			this.pos = posA;
			game.updateBoard(this);
			i++;
		}
		
		if(jump) {
			for(int j =0; j<i; j++) {
				int[] temp = delete.remove(0);
				game.board[temp[0]][temp[1]]=null;
			}
		}
		
		return (validmove || i>0);
		
	}
	
}



class Board{
	
	Piece[][] board = new Piece[8][8];
	
	//initialize board with all pawns
	public Board(){
		for(int i=0; i<8; i++) {
			if(i%2==0 && i<3) {
				for(int j=1; j<8; j=j+2) {
					int[] pos = {i,j};
					board[i][j] = new Piece('o',pos);
				}
			}
			else if(i%2==1 && i<3) {
				for(int j=0; j<8; j=j+2) {
					int[] pos = {i,j};
					board[i][j] = new Piece('o',pos);
				}
			}
			else if(i%2==0 && i>4) {
				for(int j=1; j<8; j=j+2) {
					int[] pos = {i,j};
					board[i][j] = new Piece('x',pos);
				}
			}
			else if(i%2==1 && i>4) {
				for(int j=0; j<8; j=j+2) {
					int[] pos = {i,j};
					board[i][j] = new Piece('x',pos);
				}
			}
		}
	}
	
	//for testing purpose
	public Board(String test){
		if(test.equalsIgnoreCase("mid")) {
			for(int j=1; j<8; j=j+2) {
				int[] poso = {3,j-1};
				int[] posx = {4,j};
				board[3][j-1] = new Piece('o',poso);
				board[4][j] = new Piece('x',posx);
			}		
		}
		if(test.equalsIgnoreCase("test")) {
			
			int[] poso1 = {1,2};
			int[] posx1 = {2,1};
			int[] poso2 = {5,0};
			int[] posx2 = {4,5};
			int[] poso3 = {7,4};
			board[1][2] = new Piece('o',poso1);
			board[2][1] = new Piece('x',posx1);
			board[5][0] = new Piece('o',poso2);
			board[4][5] = new Piece('x',posx2);
			board[7][4] = new Piece('Q',poso3);	
		}
	}
	
	//clone board 
	public Board(Board game) {
		for(int i=0; i<8; i++) {
			for(int j=0; j<8; j++) {
				int[] check = {i,j};
				if(game.isOccupied(check)) {
					Piece temp = new Piece(game.board[i][j]);
					board[i][j]=temp;
				}
			}
		}
	}
	
	
	//update board given p is moved
	public void updateBoard(Piece p) {
		boolean updated = false;
		for(int i=0; i<8; i++) {
			for(int j=0; j<8; j++) {
				int posA[] = new int[2];
				posA[0]=i;
				posA[1]=j;
				//if piece moved
				if(board[i][j]==p) {
					board[p.pos[0]][p.pos[1]]= p;
					board[i][j] = null;
					updated=true;
					break;
				}
			}
			if(updated)break;
		}
	}
	
	/*
	 * evaluate score 
	 * return {player1x, player2o} scores
	 * based on how many piece left on board
	 * starts with {75,75}
	 * 
	 */
	public int[] updateScore() {	
		int[] points = {0,0};
		for(int i=0; i<8; i++) {
			for(int j=0; j<8; j++) {
				if(board[i][j]!=null) {
					if(board[i][j].color=='x') {
						points[0] = points[0] + 5 + 2*(7-i);
						if(j==0 || j==7) {
							points[0]++;// = points[0]+5; //positions on side score higher
						}
					}
					else if(board[i][j].color=='o') {
						points[1] = points[1] + 5 + 2*i;
						if(j==0 || j==7) {
							points[1]++;// = points[1]+5;
						}
					}
					else if(board[i][j].color=='K') {
						points[0] = points[0] + 7 + 2*(7-i);
						if(j==0 || j==7) {
							points[0]++;// = points[0]+5;
						}
					}
					else if(board[i][j].color=='Q') {
						points[1] = points[1] + 7 + 2*i;
						if(j==0 || j==7) {
							points[1]++;// = points[1]+5;
						}
					}
				}
			}
		}
		return points;
	}
	
	//print current state
	public void printBoard() {
		System.out.print("   0  1  2  3  4  5  6  7");
		for(int i=0; i<8; i++) {
			System.out.print("\n"+i+"  ");
			for(int j=0; j<8; j++) {
				if(board[i][j]!=null) {System.out.print(board[i][j].color + "  ");}
				else {System.out.print("   ");}
			}
		}
		System.out.print("\n");		
	}
	
	//check if board position is occupied
	public boolean isOccupied(int[] pos) {
		return (board[pos[0]][pos[1]]!=null); 
	}
	
}


/*
 * let root node be the AI’s current value
 * each child be the value added based on the game state resulting from a move/action chosen.
 * repeated for n levels.
 * 
 * AI takes o or Q pieces
 */
class AI{
	int[] score = {0,0};
	SearchTree tree;
	Piece bestpiece;
	String[] bestmove;
	
	//constructor
	public AI(Board game) {
		score = game.updateScore();
		tree = new SearchTree(game);
	}
	
	//find all legal moves in this state
	//return arraylist where the first number is position of piece to move
	public ArrayList<String> findMove(Board game, char color) {
		
		Board cur = new Board(game);
		ArrayList<String> moves = new ArrayList<String>();
		
		//find all possible moves
		if(color=='o' || color=='Q') {
			for(int i=0; i<8; i++) {
				for(int j=0; j<8; j++) {
					int[] posthis = {i,j};
					if(cur.isOccupied(posthis) && cur.board[i][j].color=='o') {
						//find move, add new state to tree
						//move forward, or jump
						//every jump is a new state
						int[] checkr = {i+1,j+1};
						int[] checkl = {i+1,j-1};
						if(i!=7 && j!=0 && !cur.isOccupied(checkl)) {
							moves.add(Integer.toString(i)+Integer.toString(j)+" "+Integer.toString(checkl[0]) + Integer.toString(checkl[1]));
						}
						else if(i<6 && j>1){ //jump
							int[] pos = {i+2,j-2};
							if(!cur.isOccupied(pos) && cur.board[checkl[0]][checkl[1]].color!='o' && cur.board[checkl[0]][checkl[1]].color!='Q') moves.add(Integer.toString(i)+Integer.toString(j)+" "+Integer.toString(pos[0]) + Integer.toString(pos[1]));
						}
						if(i!=7 && j!=7 && !cur.isOccupied(checkr)) {
							moves.add(Integer.toString(i)+Integer.toString(j)+" "+Integer.toString(checkr[0]) + Integer.toString(checkr[1]));
						}
						else if (i<6 && j<6){ //jump
							int[] pos = {i+2,j+2};
							if(!cur.isOccupied(pos)&& cur.board[checkr[0]][checkr[1]].color!='o' && cur.board[checkr[0]][checkr[1]].color!='Q') moves.add(Integer.toString(i)+Integer.toString(j)+" "+Integer.toString(pos[0]) + Integer.toString(pos[1]));
						}
					}
					else if(cur.isOccupied(posthis) && cur.board[i][j].color=='Q') {
						//find move, add new state to tree
						//move forward, backward, or jump
						//every jump is a new state
						int[] checkrd = {i+1,j+1};
						int[] checkld = {i+1,j-1};
						int[] checkru = {i-1,j+1};
						int[] checklu = {i-1,j-1};
						if(i!=0 && j!=0 && !cur.isOccupied(checklu)) {
							moves.add(Integer.toString(i)+Integer.toString(j)+" "+Integer.toString(checklu[0]) + Integer.toString(checklu[1]));
						}
						else if(i>1 && j>1){ //jump
							int[] pos = {i-2,j-2};
							if(!cur.isOccupied(pos)&& cur.board[checklu[0]][checklu[1]].color!='o' && cur.board[checklu[0]][checklu[1]].color!='Q') moves.add(Integer.toString(i)+Integer.toString(j)+" "+Integer.toString(pos[0]) + Integer.toString(pos[1]));
						}
						if(i!=0 && j!=7 && !cur.isOccupied(checkru)) {
							moves.add(Integer.toString(i)+Integer.toString(j)+" "+Integer.toString(checkru[0]) + Integer.toString(checkru[1]));
						}
						else if(i>1 && j<6){ //jump
							int[] pos = {i-2,j+2};
							if(!cur.isOccupied(pos)&& cur.board[checkru[0]][checkru[1]].color!='o' && cur.board[checkru[0]][checkru[1]].color!='Q') moves.add(Integer.toString(i)+Integer.toString(j)+" "+Integer.toString(pos[0]) + Integer.toString(pos[1]));
						}
						if(i!=7 && j!=0 && !cur.isOccupied(checkld)) {
							moves.add(Integer.toString(i)+Integer.toString(j)+" "+Integer.toString(checkld[0]) + Integer.toString(checkld[1]));
						}
						else if(i<6 && j>1){ //jump
							int[] pos = {i+2,j-2};
							if(!cur.isOccupied(pos)&& cur.board[checkld[0]][checkld[1]].color!='o' && cur.board[checkld[0]][checkld[1]].color!='Q') moves.add(Integer.toString(i)+Integer.toString(j)+" "+Integer.toString(pos[0]) + Integer.toString(pos[1]));
						}
						if(i!=7 && j!=7 && !cur.isOccupied(checkrd)) {
							moves.add(Integer.toString(i)+Integer.toString(j)+" "+Integer.toString(checkrd[0]) + Integer.toString(checkrd[1]));
						}
						else if (i<6 && j<6) { //jump
							int[] pos = {i+2,j+2};
							if(!cur.isOccupied(pos)&& cur.board[checkrd[0]][checkrd[1]].color!='o' && cur.board[checkrd[0]][checkrd[1]].color!='Q') moves.add(Integer.toString(i)+Integer.toString(j)+" "+Integer.toString(pos[0]) + Integer.toString(pos[1]));
						}
					}
				}
			}
		}
		else if(color=='x' || color=='K') {
			for(int i=0; i<8; i++) {
				for(int j=0; j<8; j++) {
					int[] posthis = {i,j};
					if(cur.isOccupied(posthis) && cur.board[i][j].color=='x') {
						//find move, add new state to tree
						//move forward, or jump
						//every jump is a new state
						int[] checkr = {i-1,j+1};
						int[] checkl = {i-1,j-1};
						if(i!=0 && j!=0 && !cur.isOccupied(checkl)) {
							moves.add(Integer.toString(i)+Integer.toString(j)+" "+Integer.toString(checkl[0]) + Integer.toString(checkl[1]));
						}
						else if(i>1 && j>1){ //jump
							int[] pos = {i-2,j-2};
							if(!cur.isOccupied(pos)&& cur.board[checkl[0]][checkl[1]].color!='x' && cur.board[checkl[0]][checkl[1]].color!='K') moves.add(Integer.toString(i)+Integer.toString(j)+" "+Integer.toString(pos[0]) + Integer.toString(pos[1]));
						}
						if(i!=0 && j!=7 && !cur.isOccupied(checkr)) {
							moves.add(Integer.toString(i)+Integer.toString(j)+" "+Integer.toString(checkr[0]) + Integer.toString(checkr[1]));
						}
						else if(i>1 && j<6){ //jump
							int[] pos = {i-2,j+2};
							if(!cur.isOccupied(pos)&& cur.board[checkr[0]][checkr[1]].color!='x' && cur.board[checkr[0]][checkr[1]].color!='K') moves.add(Integer.toString(i)+Integer.toString(j)+" "+Integer.toString(pos[0]) + Integer.toString(pos[1]));
						}
					}
					else if(cur.isOccupied(posthis) && cur.board[i][j].color=='K') {
						//find move, add new state to tree
						//move forward, backward, or jump
						//every jump is a new state
						int[] checkrd = {i+1,j+1};
						int[] checkld = {i+1,j-1};
						int[] checkru = {i-1,j+1};
						int[] checklu = {i-1,j-1};
						if(i!=0 && j!=0 && !cur.isOccupied(checklu)) {
							moves.add(Integer.toString(i)+Integer.toString(j)+" "+Integer.toString(checklu[0]) + Integer.toString(checklu[1]));
						}
						else if(i>1 && j>1){ //jump
							int[] pos = {i-2,j-2};
							if(!cur.isOccupied(pos)&& cur.board[checklu[0]][checklu[1]].color!='x' && cur.board[checklu[0]][checklu[1]].color!='K') moves.add(Integer.toString(i)+Integer.toString(j)+" "+Integer.toString(pos[0]) + Integer.toString(pos[1]));
						}
						if(i!=0 && j!=7 && !cur.isOccupied(checkru)) {
							moves.add(Integer.toString(i)+Integer.toString(j)+" "+Integer.toString(checkru[0]) + Integer.toString(checkru[1]));
						}
						else if(i>1 && j<6){ //jump
							int[] pos = {i-2,j+2};
							if(!cur.isOccupied(pos)&& cur.board[checkru[0]][checkru[1]].color!='x' && cur.board[checkru[0]][checkru[1]].color!='K') moves.add(Integer.toString(i)+Integer.toString(j)+" "+Integer.toString(pos[0]) + Integer.toString(pos[1]));
						}
						if(i!=7 && j!=0 && !cur.isOccupied(checkld)) {
							moves.add(Integer.toString(i)+Integer.toString(j)+" "+Integer.toString(checkld[0]) + Integer.toString(checkld[1]));
						}
						else if(i<6 && j>1){ //jump
							int[] pos = {i+2,j-2};
							if(!cur.isOccupied(pos)&& cur.board[checkld[0]][checkld[1]].color!='x' && cur.board[checkld[0]][checkld[1]].color!='K') moves.add(Integer.toString(i)+Integer.toString(j)+" "+Integer.toString(pos[0]) + Integer.toString(pos[1]));
						}
						if(i!=7 && j!=7 && !cur.isOccupied(checkrd)) {
							moves.add(Integer.toString(i)+Integer.toString(j)+" "+Integer.toString(checkrd[0]) + Integer.toString(checkrd[1]));
						}
						else if (i<6 && j<6) { //jump
							int[] pos = {i+2,j+2};
							if(!cur.isOccupied(pos)&& cur.board[checkrd[0]][checkrd[1]].color!='x' && cur.board[checkrd[0]][checkrd[1]].color!='K') moves.add(Integer.toString(i)+Integer.toString(j)+" "+Integer.toString(pos[0]) + Integer.toString(pos[1]));
						}
					}
				}
			}
		}
		return moves;
	}
	
	
	public String[] alphabeta(SearchTree.Node no, int n, int alpha, int beta, boolean max) {
		String[] bestmove = new String[2]; //[0] is score [1] is moves
		
		if(n==0) {
			int[] scores = no.state.updateScore();
			bestmove[0] = Integer.toString(scores[0])+"  "+Integer.toString(scores[1]);
			bestmove[1] = "";
			return bestmove;
		}
		
		if(max) { //maximize o
			int val = Integer.MIN_VALUE;
			ArrayList<String> moves = findMove(no.state,'o');
			for(int i=0; i<moves.size(); i++) {
				Board temp = new Board(no.state);
				String[] thismove = moves.get(i).split(" ");
				int peepee = Character.getNumericValue(thismove[0].charAt(0));
				int poopoo = Character.getNumericValue(thismove[0].charAt(1));
				String moo = moves.get(i).substring(3);
				temp.board[peepee][poopoo].Move(moo, temp);
				SearchTree.Node childnode = no.addChild(temp);
				String[] childresult = alphabeta(childnode,n-1,alpha,beta,false);
				String[] childscore = childresult[0].split(" ");
				
				int diff = (Integer.parseInt(childscore[2])-Integer.parseInt(childscore[0]));
				val = Integer.max(val, diff);
				//val = Integer.max(val,Integer.parseInt(childscore[2]));
				//if(val==Integer.parseInt(childscore[2])) {
				if(val==diff) {
					bestmove = childresult;
					bestmove[1] = moves.get(i);
				}
				alpha = Integer.max(alpha,val);
				if(alpha>=beta) {
					break;
				}
			}
			return bestmove;
		}
		else { //minimize x
			int val = Integer.MAX_VALUE;
			ArrayList<String> moves = findMove(no.state,'x');
			for(int i=0; i<moves.size(); i++) {
				Board temp = new Board(no.state);
				String[] thismove = moves.get(i).split(" ");
				int peepee = Character.getNumericValue(thismove[0].charAt(0));
				int poopoo = Character.getNumericValue(thismove[0].charAt(1));
				String moo = moves.get(i).substring(3);
				temp.board[peepee][poopoo].Move(moo, temp);
				SearchTree.Node childnode = no.addChild(temp);
				String[] childresult = alphabeta(childnode,n-1,alpha,beta,true);
				String[] childscore = childresult[0].split(" ");
				
				int diff = Integer.parseInt(childscore[2])-Integer.parseInt(childscore[0]);
				val = Integer.min(val,diff);
				//val = Integer.min(val,Integer.parseInt(childscore[2]));
				//if(val==Integer.parseInt(childscore[2])) {
				if(val==diff) {
					bestmove = childresult;
					bestmove[1] = moves.get(i);
				}
				beta = Integer.min(beta,val);
				if(alpha>=beta) {
					break;
				}
			}
			return bestmove;
		}
	}
	
	
}


