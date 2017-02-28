package project2;

//CS561 Project2
//Author: Siqi Dong

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;

public class mancala {
	
	public class Node{
		int value;
		int[] nextState;
		
		public Node(int val, int[] state) {
			value = val;
			nextState = state;
		}
		
		public int getValue() {
			return value;
		}
		
		public int[] getState() {
			return nextState;
		}
	}

	
	int method;
	int player;
	int cutoff;
	int size;
	int[] player1;
	int[] player2;
	ArrayList<String> minimaxLog = null;
	ArrayList<String> alphabetaLog = null;

	public static void main(String[] args) {
		mancala m= new mancala();
		
		try {
			m.readFile(args[3]);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (m.method == 1) {
			m.Greedy();
		}
		else if (m.method == 2) {
			m.MiniMax();
		}
		else if (m.method == 3) {
			m.AlphaBeta();
		}
		else if (m.method == 4) {
			m.Competition();
		} 
	}
	
	public void readFile(String file) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(file));
		method = Integer.parseInt(in.readLine());
		player = Integer.parseInt(in.readLine());
		cutoff = Integer.parseInt(in.readLine());
		if(method==1) { cutoff=1;}
		String[] board2 = in.readLine().split(" ");
		String[] board1 = in.readLine().split(" ");;
		int mancala2 = Integer.parseInt(in.readLine());
		int mancala1 = Integer.parseInt(in.readLine());
		size = board1.length;
		player1 = new int[size+1];
		player2 = new int[size+1];
				
		for (int i = 0; i < size; i++) {
			player1[i] = Integer.parseInt(board1[i]);
			player2[i] = Integer.parseInt(board2[i]);
		}
		player1[size] = mancala1;
		player2[size] = mancala2;
		
		in.close();
	}
	
	public void writeNextState(int[] nextstate) throws IOException{
		File outputFile = new File("Z_next_state.txt");
		FileOutputStream fos = new FileOutputStream(outputFile);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		
		for (int i = 0; i < size-1 ; i++) {
			bw.write(Integer.toString(nextstate[i]));
			bw.write(" ");
		}
		bw.write(Integer.toString(nextstate[size-1]));
		bw.newLine();
		
		for (int i = 0; i < size-1 ; i++) {
			bw.write(Integer.toString(nextstate[i+size]));
			bw.write(" ");
		}
		bw.write(Integer.toString(nextstate[2*size-1]));
		bw.newLine();
			
		bw.write(Integer.toString(nextstate[2*size]));
		bw.newLine();
		bw.write(Integer.toString(nextstate[2*size+1]));
		bw.newLine();
		
		bw.close();
	}
	
	public void writeLog(ArrayList<String> log) throws IOException{
		File outputFile = new File("Z_traverse_log.txt");
		FileOutputStream fos = new FileOutputStream(outputFile);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		
		Iterator<String> iterator = log.iterator();
		while (iterator.hasNext()) {
			bw.write(iterator.next());
			bw.newLine();
		}
		bw.close();
	}
	
	public int[] saveBoard(int[] board2, int[] board1) {
		
		int[] theBoard = new int[2*size+2];
		for (int i = 0; i < size; i++) {
			theBoard[i] = board2[i];
			theBoard[i+size] = board1[i];
		}
		theBoard[2*size] = board2[size];
		theBoard[2*size+1] = board1[size];
		return theBoard;
	}
	
	public void restoreBoard(int[] oriBoard) {
		for (int i = 0; i < size; i++) {
			player2[i] = oriBoard[i];
			player1[i] = oriBoard[i+size];
		}
		player2[size] = oriBoard[2*size];
		player1[size] = oriBoard[2*size+1];
	}

	public void getActionSet(ArrayList<Node> q) {
		int rootVal = Integer.MIN_VALUE;
		int rootAlpha = Integer.MIN_VALUE;
		int rootBeta = Integer.MAX_VALUE;
		if (method==2) { pathMiniMax("root",0,rootVal); }
		if (method==3) { pathAlphaBeta("root",0,rootVal, rootAlpha, rootBeta); }
		
		int[] originalBoard = saveBoard(player2, player1);
		
		if (player==1) {
			for (int i = 0; i < size; i++) {
				if (player1[i]!=0) {
					int newValue = playerOneMove(player1[i], i, q, 1, rootAlpha, rootBeta);
					if (newValue>rootVal) { rootVal = newValue; }
					if (method==2) { pathMiniMax("root",0,rootVal); }
					if (method==3) {
						if(rootVal>=rootBeta) {
							pathAlphaBeta("root",0,rootVal, rootAlpha, rootBeta);
							break;
						}
						else {
							if(rootVal>rootAlpha) { rootAlpha=rootVal; }
							pathAlphaBeta("root",0,rootVal, rootAlpha, rootBeta);
						}
					}
					restoreBoard(originalBoard);
				}
			}
		}
		else if (player==2) {
			for (int i = 0; i < size; i++) {
				if (player2[i]!=0) {
					int newValue = playerTwoMove(player2[i], i, q, 1, rootAlpha, rootBeta);
					if (newValue>rootVal) { rootVal = newValue; }
					if (method==2) { pathMiniMax("root",0,rootVal); }
					if (method==3) {
						if(rootVal>=rootBeta) {
							pathAlphaBeta("root",0,rootVal, rootAlpha, rootBeta);
							break;
						}
						else {
							if(rootVal>rootAlpha) { rootAlpha=rootVal; }
							pathAlphaBeta("root",0,rootVal, rootAlpha, rootBeta);
						}
					}
					restoreBoard(originalBoard);
				}
			}
		}
	}
	
	public Node findNode(ArrayList<Node> q) {
		Iterator<Node> iterator = q.iterator();
		Node biggest = iterator.next();
		while (iterator.hasNext()) {
			Node current = iterator.next();
			if(biggest.getValue()<current.getValue()) {
				biggest = current;
			}
		}	
		return biggest;
	}
	
	public void Greedy() {
		ArrayList<Node> q = new ArrayList<Node>();
		getActionSet(q);
		Node chooseNode = findNode(q);
		try {
			writeNextState(chooseNode.getState());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void MiniMax() {
		minimaxLog = new ArrayList<String>();
		minimaxLog.add("Node,Depth,Value");
		ArrayList<Node> q = new ArrayList<Node>();
		getActionSet(q);
		Node chooseNode = findNode(q);
		try {
			writeNextState(chooseNode.getState());
			writeLog(minimaxLog);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void pathMiniMax(String s, int dep, int val) {
		if (val==Integer.MAX_VALUE) {
			minimaxLog.add(s+","+dep+","+"Infinity");
		}
		else if (val==Integer.MIN_VALUE) {
			minimaxLog.add(s+","+dep+","+"-Infinity");
		}
		else {
			minimaxLog.add(s+","+dep+","+val);
		}
	}
	
	public void AlphaBeta() {
		alphabetaLog = new ArrayList<String>();
		alphabetaLog.add("Node,Depth,Value,Alpha,Beta");
		ArrayList<Node> q = new ArrayList<Node>();
		getActionSet(q);
		Node chooseNode = findNode(q);
		
		try {
			writeNextState(chooseNode.getState());
			writeLog(alphabetaLog);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void pathAlphaBeta(String s, int dep, int val, int alpha, int beta) {
		String v;
		String a;
		String b;
		if(val==Integer.MIN_VALUE) { v="-Infinity"; }
		else if(val==Integer.MAX_VALUE) { v="Infinity"; }
		else { v=Integer.toString(val); }
		
		if(alpha==Integer.MIN_VALUE) { a="-Infinity"; }
		else if(alpha==Integer.MAX_VALUE) { a="Infinity"; }
		else { a=Integer.toString(alpha); }
		
		if(beta==Integer.MIN_VALUE) { b="-Infinity"; }
		else if(beta==Integer.MAX_VALUE) { b="Infinity"; }
		else { b=Integer.toString(beta); }
		
		alphabetaLog.add(s+","+dep+","+v+","+a+","+b);
	}
	
	public void Competition() {
		
	}
	
	//ASSUME THE PLAYER IS PLAYER1(BOTTOM)
	public int playerOneMove(int node, int index, ArrayList<Node> queue, int depth, int alpha, int beta) {
		
		String nodeName = "B"+Integer.toString(index+2);
		int currentVal;
		int currentAlpha = alpha;
		int currentBeta = beta;
		if (player==1) { currentVal = Integer.MIN_VALUE; }
		else { currentVal = Integer.MAX_VALUE; }
		int[] currentBoard;
		
		int q = node / (2*size + 1);
		int r = node % (2*size + 1);
		
		player1[index] = 0;
		for (int i = 0; i < size; i++) {
			player1[i] += q;
			player2[i] += q;
		}
		player1[size] += q;
		
		if (r < (size-index)) {
			for (int i = 1; i <= r; i++) {
				player1[index + i]++;
			}
			if (player1[index+r]==1) {
				player1[index+r]=0;
				player1[size]+=player2[index+r]+1;
				player2[index+r]=0;
			}
		}
		else if (r == (size-index)) {
			for (int i = 1; i <= r; i++) {
				player1[index + i]++;
			}

			currentBoard = saveBoard(player2, player1);
			boolean endGame = true;
			for (int i = 0; i<size; i++) {
				if (player1[i]!=0) {
					endGame = false;
					break;
				}
			}
			
			if(!endGame) {
				if(method==2) {
					pathMiniMax(nodeName, depth, currentVal);
				}
				if(method==3) {
					pathAlphaBeta(nodeName, depth, currentVal, currentAlpha, currentBeta);
				}
				for (int i = 0; i<size; i++) {
					if (player1[i]!=0) {
						int v = playerOneMove(player1[i], i, queue, depth, currentAlpha, currentBeta);
						if (player==1) { if(v>currentVal) {currentVal = v;} }
						else { if(v<currentVal) {currentVal = v;} }
						
						if (method==2) { pathMiniMax(nodeName, depth, currentVal); }
						if (method==3) {
							if(player==1) {
								if(currentVal>=currentBeta) {
									pathAlphaBeta(nodeName, depth,currentVal, currentAlpha, currentBeta);
									break;
								}
								else {
									if(currentVal>currentAlpha) { currentAlpha=currentVal; }
									pathAlphaBeta(nodeName, depth,currentVal, currentAlpha, currentBeta);
								}
							}
							else {
								if(currentVal<=currentAlpha) {
									pathAlphaBeta(nodeName, depth,currentVal, currentAlpha, currentBeta);
									break;
								}
								else {
									if(currentVal<currentBeta) { currentBeta=currentVal; }
									pathAlphaBeta(nodeName, depth,currentVal, currentAlpha, currentBeta);
								}
							}
						}
					}
					restoreBoard(currentBoard);
				}
			}
			else {
				currentVal = playerOneEnd();
				currentBoard = saveBoard(player2, player1);
				if (method==2) { pathMiniMax(nodeName, depth, currentVal); }
				if (method==3) { pathAlphaBeta(nodeName, depth, currentVal, currentAlpha, currentBeta); }
				if (depth==1) {
					Node n = new Node(currentVal, currentBoard);	
					queue.add(n);
				}
			}
			return currentVal;
			
		}
		else if ((size-index) < r && r < (2*size-index+1)) {
			for (int i = 1; i <= (size-index); i++) {
				player1[index + i]++;
			}
			for (int i = 1; i <= r-(size-index); i++) {
				player2[size - i]++;
			}
		}
		else {
			for (int i = 1; i <= (size-index); i++) {
				player1[index + i]++;
			}
			for (int i = 0; i < size; i++) {
				player2[i]++;
			}
			for (int i = 0; i < r-(2*size-index); i++) {
				player1[i]++;
			}
			if (player1[r-(2*size-index)-1]==1) {
				player1[r-(2*size-index)-1]=0;
				player1[size]+=player2[r-(2*size-index)-1]+1;
				player2[r-(2*size-index)-1]=0;
			}
		}
		
		currentBoard = saveBoard(player2, player1);
		
		boolean endGameOne = true;
		for (int i = 0;i < size;i++) {
			if (player1[i]!=0) {
				endGameOne = false;
				break;
			}
		}
		boolean endGameTwo = true;
		for (int i = 0;i < size;i++) {
			if (player2[i]!=0) {
				endGameTwo = false;
				break;
			}
		}
		
		if(player==1) { currentVal=Integer.MAX_VALUE;}
		else { currentVal=Integer.MIN_VALUE;}
		
		if(endGameOne==true && endGameTwo==false) {
			currentVal = playerOneEnd();
			currentBoard = saveBoard(player2, player1);
			if (method==2) { pathMiniMax(nodeName, depth, currentVal); }
			if (method==3) { pathAlphaBeta(nodeName, depth, currentVal, currentAlpha, currentBeta); }
		}
		if(endGameOne==false && endGameTwo==true) {
			currentVal = playerTwoEnd();
			currentBoard = saveBoard(player2, player1);
			if (method==2) { pathMiniMax(nodeName, depth, currentVal); }
			if (method==3) { pathAlphaBeta(nodeName, depth, currentVal, currentAlpha, currentBeta); }
		}
		if(endGameOne==true && endGameTwo==true) {
			if (player==1) { currentVal = player1[size]-player2[size];}
			else { currentVal = player2[size]-player1[size]; }
			currentBoard = saveBoard(player2, player1);
			if (method==2) { pathMiniMax(nodeName, depth, currentVal); }
			if (method==3) { pathAlphaBeta(nodeName, depth, currentVal, currentAlpha, currentBeta); }
		}
		
		if(endGameOne==false && endGameTwo==false) {
			if(depth<cutoff) {
				if(method==2) { pathMiniMax(nodeName, depth, currentVal); }
				if (method==3) { pathAlphaBeta(nodeName, depth, currentVal, currentAlpha, currentBeta); }
				
				for (int i = 0;i < size;i++) {
					if (player2[i]!=0) {
						int v = playerTwoMove(player2[i], i, queue, depth+1, currentAlpha, currentBeta);
						if (player==1) { if (v<currentVal) { currentVal = v; } }
						else { if (v>currentVal) { currentVal = v; } }
						
						if (method==2) { pathMiniMax(nodeName, depth, currentVal); }
						if (method==3) {
							if (player==1) {
								if(currentVal<=currentAlpha) {
									pathAlphaBeta(nodeName, depth, currentVal, currentAlpha, currentBeta);
									break;
								}
								else {
									if(currentVal<currentBeta) { currentBeta=currentVal; }
									pathAlphaBeta(nodeName, depth, currentVal, currentAlpha, currentBeta);
								}
							}
							else {
								if(currentVal>=currentBeta) {
									pathAlphaBeta(nodeName, depth, currentVal, currentAlpha, currentBeta);
									break;
								}
								else {
									if(currentVal>currentAlpha) { currentAlpha=currentVal; }
									pathAlphaBeta(nodeName, depth, currentVal, currentAlpha, currentBeta);
								}
							}
						}
					}
					restoreBoard(currentBoard);
				}
			}
			else {
				if (player==1) { currentVal = player1[size]-player2[size]; }
				else { currentVal = player2[size]-player1[size]; }
				if (method==2) { pathMiniMax(nodeName, depth, currentVal); }
				if (method==3) { pathAlphaBeta(nodeName, depth, currentVal, currentAlpha, currentBeta); }
			}
		}

		if (depth==1) {
			Node n = new Node(currentVal, currentBoard);	
			queue.add(n);
		}

		return currentVal;
	}
	
	public int playerOneEnd() {
		int total = 0;
		for (int i = 0; i < size; i++) {
			total += player2[i];
			player2[i] = 0;
		}
		player2[size] += total;
		
		if (player==1) { return player1[size]-player2[size];}
		else { return player2[size]-player1[size]; }

	}
	
	//ASSUME THE PLAYER IS PLAYER2(TOP)
	public int playerTwoMove(int node, int index, ArrayList<Node> queue, int depth, int alpha, int beta) {
		
		String nodeName = "A"+Integer.toString(index+2);
		int currentVal;
		int currentAlpha = alpha;
		int currentBeta = beta;
		if (player==1) { currentVal = Integer.MAX_VALUE; }
		else { currentVal = Integer.MIN_VALUE; }
		int[] currentBoard;
		
		int q = node / (2*size + 1);
		int r = node % (2*size + 1);
		
		player2[index] = 0;
		for (int i = 0; i < size; i++) {
			player1[i] += q;
			player2[i] += q;
		}
		player2[size] += q;
		
		if (r <= index) {
			for (int i = 1; i <= r; i++) {
				player2[index - i]++;
			}
			if (player2[index-r]==1) {
				player2[index-r]=0;
				player2[size]+=player1[index-r]+1;
				player1[index-r]=0;
			}
		}
		else if (r == (index+1)) {
			for (int i = 1; i < r; i++) {
				player2[index - i]++;
			}
			player2[size]++;
			
			currentBoard = saveBoard(player2, player1);
			boolean endGame = true;
			for (int i = 0; i<size; i++) {
				if (player2[i]!=0) {
					endGame = false;
					break;
				}
			}
			
			if(!endGame) {
				if(method==2) {
					pathMiniMax(nodeName, depth, currentVal);
				}
				if(method==3) {
					pathAlphaBeta(nodeName, depth, currentVal, currentAlpha, currentBeta);
				}
				for(int i = 0; i<size; i++) {
					if (player2[i]!=0) {
						int v = playerTwoMove(player2[i], i, queue, depth, currentAlpha, currentBeta);
						if (player==1) { if(v<currentVal) {currentVal = v;} }
						else { if(v>currentVal) {currentVal = v;} }
						
						if (method==2) { pathMiniMax(nodeName, depth, currentVal); }
						if (method==3) {
							if(player==1) {
								if(currentVal<=currentAlpha) {
									pathAlphaBeta(nodeName, depth, currentVal, currentAlpha, currentBeta);
									break;
								}
								else {
									if(currentVal<currentBeta) { currentBeta=currentVal; }
									pathAlphaBeta(nodeName, depth, currentVal, currentAlpha, currentBeta);
								}
							}
							else {
								if(currentVal>=currentBeta) {
									pathAlphaBeta(nodeName, depth, currentVal, currentAlpha, currentBeta);
									break;
								}
								else {
									if(currentVal>currentAlpha) { currentAlpha=currentVal; }
									pathAlphaBeta(nodeName, depth, currentVal, currentAlpha, currentBeta);
								}
							}
						}
					}
					restoreBoard(currentBoard);
				}
			}
			else {
				currentVal = playerTwoEnd();
				currentBoard = saveBoard(player2, player1);
				if (method==2) { pathMiniMax(nodeName, depth, currentVal); }
				if(method==3) { pathAlphaBeta(nodeName, depth, currentVal, currentAlpha, currentBeta); }
				if (depth==1) {
					Node n = new Node(currentVal, currentBoard);	
					queue.add(n);
				}
			}
			
			return currentVal;
		}
		else if (r > (index+1) && r < (size+index+2)) {
			for (int i = 1; i <=index ; i++) {
				player2[index - i]++;
			}
			player2[size]++;
			for (int i = 0; i < (r-index-1); i++) {
				player1[i]++;
			}
		}
		else {
			for (int i = 1; i <=index ; i++) {
				player2[index - i]++;
			}
			for (int i = 0; i < size; i++) {
				player1[i]++;
			}
			for (int i = 0; i < (r-index-size) ; i++) {
				player2[size - i]++;
			}
			if (player2[2*size+index-r+1]==1) {
				player2[2*size+index-r+1]=0;
				player2[size]+=player1[2*size+index-r+1]+1;
				player1[2*size+index-r+1]=0;
			}
		}
		
		currentBoard = saveBoard(player2, player1);
		
		boolean endGameTwo = true;
		for (int i = 0;i < size;i++) {
			if (player2[i]!=0) {
				endGameTwo = false;
				break;
			}
		}
		boolean endGameOne = true;
		for (int i = 0;i < size;i++) {
			if (player1[i]!=0) {
				endGameOne = false;
				break;
			}
		}
		
		if(player==1) { currentVal=Integer.MIN_VALUE;}
		else { currentVal=Integer.MAX_VALUE;}
		
		if(endGameTwo==true && endGameOne==false) {
			currentVal = playerTwoEnd();
			currentBoard = saveBoard(player2, player1);
			if (method==2) { pathMiniMax(nodeName, depth, currentVal); }
			if (method==3) { pathAlphaBeta(nodeName, depth, currentVal, currentAlpha, currentBeta); }
		}
		if(endGameTwo==false && endGameOne==true) {
			currentVal = playerOneEnd();
			currentBoard = saveBoard(player2, player1);
			if (method==2) { pathMiniMax(nodeName, depth, currentVal); }
			if (method==3) { pathAlphaBeta(nodeName, depth, currentVal, currentAlpha, currentBeta); }
		}
		if(endGameTwo==true && endGameOne==true) {
			if (player==1) { currentVal = player1[size]-player2[size];}
			else { currentVal = player2[size]-player1[size]; }
			currentBoard = saveBoard(player2, player1);
			if (method==2) { pathMiniMax(nodeName, depth, currentVal); }
			if (method==3) { pathAlphaBeta(nodeName, depth, currentVal, currentAlpha, currentBeta); }
		}
		
		if(endGameOne==false && endGameTwo==false) {
			if(depth<cutoff) {
				if(method==2) { pathMiniMax(nodeName, depth, currentVal); }
				if (method==3) { pathAlphaBeta(nodeName, depth, currentVal, currentAlpha, currentBeta); }
				
				for (int i = 0;i < size;i++) {
					if (player1[i]!=0) {
						int v = playerOneMove(player1[i], i, queue, depth+1, currentAlpha, currentBeta);
						if (player==1) { if (v>currentVal) { currentVal = v; } }
						else { if (v<currentVal) { currentVal = v; } }
						
						if (method==2) { pathMiniMax(nodeName, depth, currentVal); }
						if (method==3) {
							if(player==1) {
								if(currentVal>=currentBeta) {
									pathAlphaBeta(nodeName, depth, currentVal, currentAlpha, currentBeta);
									break;
								}
								else {
									if(currentVal>currentAlpha) { currentAlpha=currentVal; }
									pathAlphaBeta(nodeName, depth, currentVal, currentAlpha, currentBeta);
								}
							}
							else {
								if(currentVal<=currentAlpha) {
									pathAlphaBeta(nodeName, depth, currentVal, currentAlpha, currentBeta);
									break;
								}
								else {
									if(currentVal<currentBeta) { currentBeta=currentVal; }
									pathAlphaBeta(nodeName, depth, currentVal, currentAlpha, currentBeta);
								}
							}
						}	
					}
					restoreBoard(currentBoard);
				}
			}
			else {
				if (player==1) { currentVal = player1[size]-player2[size]; }
				else { currentVal = player2[size]-player1[size]; }
				if (method==2) { pathMiniMax(nodeName, depth, currentVal); }
				if (method==3) { pathAlphaBeta(nodeName, depth, currentVal, currentAlpha, currentBeta); }
			}
		}
		
		if (depth==1) {
			Node n = new Node(currentVal, currentBoard);	
			queue.add(n);
		}
		return currentVal;
	}
	
	public int playerTwoEnd() {
		int total = 0;
		for (int i = 0; i < size; i++) {
			total += player1[i];
			player1[i] = 0;
		}
		player1[size] += total;
		
		if (player==1) { return player1[size]-player2[size];}
		else { return player2[size]-player1[size]; }
	}

}
