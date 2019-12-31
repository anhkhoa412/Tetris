package org.psnbtech;

import java.awt.Color;


// The PieceType enum describes the properties of the various pieces that can be used in the game.

public enum TileType {
	TypeI(new Color(BoardPanel.COLOR_MIN, BoardPanel.COLOR_MAX, BoardPanel.COLOR_MAX), 4, 4, 1, new boolean[][] {
		{
			false,	false,	false,	false,
			true,	true,	true,	true,
			false,	false,	false,	false,
			false,	false,	false,	false,
		},
		{
			false,	false,	true,	false,
			false,	false,	true,	false,
			false,	false,	true,	false,
			false,	false,	true,	false,
		},
		{
			false,	false,	false,	false,
			false,	false,	false,	false,
			true,	true,	true,	true,
			false,	false,	false,	false,
		},
		{
			false,	true,	false,	false,
			false,	true,	false,	false,
			false,	true,	false,	false,
			false,	true,	false,	false,
		}
	}),
	

	TypeJ(new Color(BoardPanel.COLOR_MIN, BoardPanel.COLOR_MIN, BoardPanel.COLOR_MAX), 3, 3, 2, new boolean[][] {
		{
			true,	false,	false,
			true,	true,	true,
			false,	false,	false,
		},
		{
			false,	true,	true,
			false,	true,	false,
			false,	true,	false,
		},
		{
			false,	false,	false,
			true,	true,	true,
			false,	false,	true,
		},
		{
			false,	true,	false,
			false,	true,	false,
			true,	true,	false,
		}
	}),
	

	TypeL(new Color(BoardPanel.COLOR_MAX, 127, BoardPanel.COLOR_MIN), 3, 3, 2, new boolean[][] {
		{
			false,	false,	true,
			true,	true,	true,
			false,	false,	false,
		},
		{
			false,	true,	false,
			false,	true,	false,
			false,	true,	true,
		},
		{
			false,	false,	false,
			true,	true,	true,
			true,	false,	false,
		},
		{
			true,	true,	false,
			false,	true,	false,
			false,	true,	false,
		}
	}),
	
	
	TypeO(new Color(BoardPanel.COLOR_MAX, BoardPanel.COLOR_MAX, BoardPanel.COLOR_MIN), 2, 2, 2, new boolean[][] {
		{
			true,	true,
			true,	true,
		},
		{
			true,	true,
			true,	true,
		},
		{	
			true,	true,
			true,	true,
		},
		{
			true,	true,
			true,	true,
		}
	}),
	
	
	TypeS(new Color(BoardPanel.COLOR_MIN, BoardPanel.COLOR_MAX, BoardPanel.COLOR_MIN), 3, 3, 2, new boolean[][] {
		{
			false,	true,	true,
			true,	true,	false,
			false,	false,	false,
		},
		{
			false,	true,	false,
			false,	true,	true,
			false,	false,	true,
		},
		{
			false,	false,	false,
			false,	true,	true,
			true,	true,	false,
		},
		{
			true,	false,	false,
			true,	true,	false,
			false,	true,	false,
		}
	}),
	
	
	TypeT(new Color(128, BoardPanel.COLOR_MIN, 128), 3, 3, 2, new boolean[][] {
		{
			false,	true,	false,
			true,	true,	true,
			false,	false,	false,
		},
		{
			false,	true,	false,
			false,	true,	true,
			false,	true,	false,
		},
		{
			false,	false,	false,
			true,	true,	true,
			false,	true,	false,
		},
		{
			false,	true,	false,
			true,	true,	false,
			false,	true,	false,
		}
	}),
	
	
	TypeZ(new Color(BoardPanel.COLOR_MAX, BoardPanel.COLOR_MIN, BoardPanel.COLOR_MIN), 3, 3, 2, new boolean[][] {
		{
			true,	true,	false,
			false,	true,	true,
			false,	false,	false,
		},
		{
			false,	false,	true,
			false,	true,	true,
			false,	true,	false,
		},
		{
			false,	false,	false,
			true,	true,	false,
			false,	true,	true,
		},
		{
			false,	true,	false,
			true,	true,	false,
			true,	false,	false,
		}
	});
		

	private Color baseColor;
	private Color lightColor;
	private Color darkColor;
	
	private int spawnCol;
	private int spawnRow;
	private int dimension;
	private int rows;
	private int cols;
	private boolean[][] tiles;
	
	
	private TileType(Color color, int dimension, int cols, int rows, boolean[][] tiles) {
		this.baseColor = color;
		this.lightColor = color.brighter();
		this.darkColor = color.darker();
		this.dimension = dimension;
		this.tiles = tiles;
		this.cols = cols;
		this.rows = rows;
		
		this.spawnCol = 5 - (dimension >> 1);
		this.spawnRow = getTopInset(0);
	}
	
	
	public Color getBaseColor() {
		return baseColor;
	}

	public Color getLightColor() {
		return lightColor;
	}
	public Color getDarkColor() {
		return darkColor;
	}
	/**

	public Color getDarkColor() {
		return darkColor;
	}
	
	/**
	 * Gets the dimension of this type.
	 * @return The dimension.
	 */
	public int getDimension() {
		return dimension;
	}
	
	/**
	 * Gets the spawn column of this type.
	 * @return The spawn column.
	 */
	public int getSpawnColumn() {
		return spawnCol;
	}
	
	/**
	 * Gets the spawn row of this type.
	 * @return The spawn row.
	 */
	public int getSpawnRow() {
		return spawnRow;
	}
	
	/**
	 * Gets the number of rows in this piece. (Only valid when rotation is 0 or 2,
	 * but it's fine since this is only used for the preview which uses rotation 0).
	 * @return The number of rows.
	 */
	public int getRows() {
		return rows;
	}
	
	/**
	 * Gets the number of columns in this piece. (Only valid when rotation is 0 or 2,
	 * but it's fine since this is only used for the preview which uses rotation 0).
	 * @return The number of columns.
	 */
	public int getCols() {
		return cols;
	}
	
	
	public boolean isTile(int x, int y, int rotation) {
		return tiles[rotation][y * dimension + x];
	}
	
	
	public int getLeftInset(int rotation) {
	
		for(int x = 0; x < dimension; x++) {
			for(int y = 0; y < dimension; y++) {
				if(isTile(x, y, rotation)) {
					return x;
				}
			}
		}
		return -1;
	}
	
	
	public int getRightInset(int rotation) {
	
		for(int x = dimension - 1; x >= 0; x--) {
			for(int y = 0; y < dimension; y++) {
				if(isTile(x, y, rotation)) {
					return dimension - x;
				}
			}
		}
		return -1;
	}
	
	
	public int getTopInset(int rotation) {
		
		for(int y = 0; y < dimension; y++) {
			for(int x = 0; x < dimension; x++) {
				if(isTile(x, y, rotation)) {
					return y;
				}
			}
		}
		return -1;
	}
	

	public int getBottomInset(int rotation) {
	
		for(int y = dimension - 1; y >= 0; y--) {
			for(int x = 0; x < dimension; x++) {
				if(isTile(x, y, rotation)) {
					return dimension - y;
				}
			}
		}
		return -1;
	}


	
}
