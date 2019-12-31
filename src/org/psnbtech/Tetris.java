package org.psnbtech;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;
import java.util.jar.JarException;

import javax.swing.JFrame;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.*;

public class Tetris extends JFrame {
	
	
	private static final long serialVersionUID = -4722429764792514382L;

	//The number of milliseconds per frame.
	private static final long FRAME_TIME = 1000L / 50L;
	
	//The number of pieces that exist.
	private static final int TYPE_COUNT = TileType.values().length;
		
	//The BoardPanel instance.
	private BoardPanel board;
	
	//The SidePanel instance.
	private SidePanel side;
    //Whether or not the game is paused.
	private boolean isPaused;
	
	//Whether or not we've played a game yet.
	private boolean isNewGame;
	
    //Whether or not the game is over.
	private boolean isGameOver;
	
	//The current level we're on.
	private int level;
	
	//The current score.
	private int score;
	
	//The random number generator.
	private Random random;
	
	//The clock that handles the update logic.
	private Clock logicTimer;
				
	//The current type of tile.
	private TileType currentType;
	//The next type of tile.
	private TileType nextType;
		
	//The current column of our tile.
	private int currentCol;
	
	//The current row of our tile.
	private int currentRow;
	
	//The current rotation of our tile.
	private int currentRotation;
		
	// Ensures that a certain amount of time passes after a piece is
	// spawned before we can drop it.
	private int dropCooldown;
	
	//The speed of the game.
	private float gameSpeed;
		
	
	private Tetris() {
		//Set the basic properties of the window.
		super("Tetris");
		setLayout(new BorderLayout());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		
		//Initialize the BoardPanel and SidePanel instances.
		this.board = new BoardPanel(this);
		this.side = new SidePanel(this);
		
		//Add the BoardPanel and SidePanel instances to the window.
		add(board, BorderLayout.CENTER);
		add(side, BorderLayout.EAST);
		
		//Adds a custom anonymous KeyListener to the frame.
		addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyPressed(KeyEvent e) {
								
				switch(e.getKeyCode()) {
				
			//Drop - When pressed, we check to see that the game is not
			//paused and that there is no drop cooldown, then set the
			// logic timer to run at a speed of 25 cycles per second.
				case KeyEvent.VK_S:
					if(!isPaused && dropCooldown == 0) {
						logicTimer.setCyclesPerSecond(25.0f);
					}
					break;
				//Move Left - When pressed, we check to see that the game is
				// not paused and that the position to the left of the current
				//position is valid. If so, we decrement the current column by 1.	
				case KeyEvent.VK_A:
					if(!isPaused && board.isValidAndEmpty(currentType, currentCol - 1, currentRow, currentRotation)) {
						currentCol--;
					}
					break;
				//Move Right
				case KeyEvent.VK_D:
					if(!isPaused && board.isValidAndEmpty(currentType, currentCol + 1, currentRow, currentRotation)) {
						currentCol++;
					}
					break;
					
				//Rotate Anticlockwise 	
				case KeyEvent.VK_Q:
					if(!isPaused) {
						rotatePiece((currentRotation == 0) ? 3 : currentRotation - 1);
					}
					break;
				//Rotate Clockwise 
				case KeyEvent.VK_E:
					if(!isPaused) {
						rotatePiece((currentRotation == 3) ? 0 : currentRotation + 1);
					}
					break;
				case KeyEvent.VK_M:
					if(!isPaused) {
					try {
						FileInputStream fileInputStream = new FileInputStream("/Users/AnhKhoa/eclipse-workspace/New folder/Sound/src/music.mp3");
						Player player = new Player (fileInputStream);
						player.play();
						}catch (FileNotFoundException e1) {
							e1.printStackTrace();
							
						}catch (JavaLayerException ex){
							ex.printStackTrace();
						}
					}
					break;
				//Pause Game 
				case KeyEvent.VK_P:
					if(!isGameOver && !isNewGame) {
						isPaused = !isPaused;
						logicTimer.setPaused(isPaused);
					}
					break;
				//Start Game
				case KeyEvent.VK_ENTER:
					if(isGameOver || isNewGame) {
						resetGame();
					}
					break;
				
					}
				}
			
		
			@Override
			public void keyReleased(KeyEvent e) {
				
				switch(e.getKeyCode()) {
				
				case KeyEvent.VK_S:
					logicTimer.setCyclesPerSecond(gameSpeed);
					logicTimer.reset();
					break;
				}
				
			}
			
		});
	//Here we resize the frame to hold the BoardPanel and SidePanel instances,
	//center the window on the screen, and show it to the user.
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	//Starts the game running. Initializes everything and enters the game loop.
	private void startGame() {
		//Initialize our random number generator, logic timer, and new game variables
		this.random = new Random();
		this.isNewGame = true;
		this.gameSpeed = 1.0f;
		
	// Setup the timer to keep the game from running before the user presses enter
	//to start it.
		this.logicTimer = new Clock(gameSpeed);
		logicTimer.setPaused(true);
		
		while(true) {
	//Get the time that the frame started.
			long start = System.nanoTime();
			
			//Update the logic timer.
			logicTimer.update();
			
			if(logicTimer.hasElapsedCycle()) {
				updateGame();
			}
		
			//Decrement the drop cool down if necessary.
			if(dropCooldown > 0) {
				dropCooldown--;
			}
			
			//Display the window to the user.
			renderGame();
			// Sleep to cap the framerate.
			long delta = (System.nanoTime() - start) / 1000000L;
			if(delta < FRAME_TIME) {
				try {
					Thread.sleep(FRAME_TIME - delta);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
//Updates the game and handles the bulk of it's logic.
	private void updateGame() {
	//Check to see if the piece's position can move down to the next row.
		if(board.isValidAndEmpty(currentType, currentCol, currentRow + 1, currentRotation)) {
		////Increment the current row if it's safe to do so.
			currentRow++;
		} else {
			// We've either reached the bottom of the board, or landed on another piece, so
			// we need to add the piece to the board.
			board.addPiece(currentType, currentCol, currentRow, currentRotation);
			//Check to see if adding the new piece resulted in any cleared lines. If so,
			// increase the player's score. (Up to 4 lines can be cleared in a single go;
			// [1 = 100pts, 2 = 200pts, 3 = 400pts, 4 = 800pts]).
			int cleared = board.checkLines();
			if(cleared > 0) {
				score += 50 << cleared;
			}
			//Increase the speed slightly for the next piece and update the game's timer
			// to reflect the increase.
			gameSpeed += 0.035f;
			logicTimer.setCyclesPerSecond(gameSpeed);
			logicTimer.reset();
			
	
		
			dropCooldown = 25;
			
			//Update the difficulty level. This has no effect on the game, and is only
			 //used in the "Level" string in the SidePanel.
			level = (int)(gameSpeed * 1.70f);
			
	//Spawn a new piece to control.
			spawnPiece();
		}		
	}
	
	//Forces the BoardPanel and SidePanel to repaint.
	private void renderGame() {
		board.repaint();
		side.repaint();
	}
	//Resets the game variables to their default values at the start
	//of a new game.
	private void resetGame() {
		this.level = 1;
		this.score = 0;
		this.gameSpeed = 1.0f;
		this.nextType = TileType.values()[random.nextInt(TYPE_COUNT)];
		this.isNewGame = false;
		this.isGameOver = false;		
		board.clear();
		logicTimer.reset();
		logicTimer.setCyclesPerSecond(gameSpeed);
		spawnPiece();
	}
		
//Spawns a new piece and resets our piece's variables to their default values.
	private void spawnPiece() {
		//Poll the last piece and reset our position and rotation to
		 //their default variables, then pick the next piece to use.
		this.currentType = nextType;
		this.currentCol = currentType.getSpawnColumn();
		this.currentRow = currentType.getSpawnRow();
		this.currentRotation = 0;
		this.nextType = TileType.values()[random.nextInt(TYPE_COUNT)];
		// If the spawn point is invalid, we need to pause the game and flag that we've lost
	 // because it means that the pieces on the board have gotten too high.
		if(!board.isValidAndEmpty(currentType, currentCol, currentRow, currentRotation)) {
			this.isGameOver = true;
			logicTimer.setPaused(true);
		}		
	}
	//Attempts to set the rotation of the current piece to newRotation.
	private void rotatePiece(int newRotation) {
	//Sometimes pieces will need to be moved when rotated to avoid clipping
	//out of the board
		int newColumn = currentCol;
		int newRow = currentRow;
		
	//Get the insets for each of the sides. These are used to determine how
	// many empty rows or columns there are on a given side.
		int left = currentType.getLeftInset(newRotation);
		int right = currentType.getRightInset(newRotation);
		int top = currentType.getTopInset(newRotation);
		int bottom = currentType.getBottomInset(newRotation);
		
	
		if(currentCol < -left) {
			newColumn -= currentCol - left;
		} else if(currentCol + currentType.getDimension() - right >= BoardPanel.COL_COUNT) {
			newColumn -= (currentCol + currentType.getDimension() - right) - BoardPanel.COL_COUNT + 1;
		}
		
	
		if(currentRow < -top) {
			newRow -= currentRow - top;
		} else if(currentRow + currentType.getDimension() - bottom >= BoardPanel.ROW_COUNT) {
			newRow -= (currentRow + currentType.getDimension() - bottom) - BoardPanel.ROW_COUNT + 1;
		}
		//Check to see if the new position is acceptable. If it is, update the rotation and
		//position of the piece.
		if(board.isValidAndEmpty(currentType, newColumn, newRow, newRotation)) {
			currentRotation = newRotation;
			currentRow = newRow;
			currentCol = newColumn;
		}
	}
	

	public boolean isPaused() {
		return isPaused;
	}
	public boolean isGameOver() {
		return isGameOver;
	}
	

	public boolean isNewGame() {
		return isNewGame;
	}
	

	public int getScore() {
		return score;
	}
	

	public int getLevel() {
		return level;
	}
	
	public TileType getPieceType() {
		return currentType;
	}
	

	public TileType getNextPieceType() {
		return nextType;
	}
	
	public int getPieceCol() {
		return currentCol;
	}
	

	public int getPieceRow() {
		return currentRow;
	}
	
	
	public int getPieceRotation() {
		return currentRotation;
	}

	
	public static void main(String[] args) {
		Tetris tetris = new Tetris();
		tetris.startGame();
		

	
}}
