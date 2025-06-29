package Util;
import java.awt.Color;
import java.io.*;
import java.util.List;

import GameObjects.Tile;
import Panel.GamePanel;

/**
 * TileMap
 */
public class TileMap {

    BufferedReader br;
    int c = 0;
    // by default is 40x40
    public int row = 70;          
    public int column = 155;          
    public char[][] charArr = new char[row][column];
    public int[][] intArr = new int[row][column];
    public Tile[][] tileArr = new Tile[row][column];
    public Tile[][] tileArrOverlay = new Tile[row][column];
    public Tile[][]  pathHelper = new Tile[row][column];
    

    public TileMap(){
    	fileReader();
    }

    public void fileReader() {
        try {
        	String filePath = "./res/tileMap2.txt";
            br = new BufferedReader( new FileReader(filePath));

            for (int i = 0; i < row; i++) {
                for (int j = 0; j < column; j++) {
                    c = br.read();
                    char character = (char) c;
                    if ( character == '\n' ||  character == '\r'){   // String.valueOf(character) == "\r\n"
                        j--;
                    }else{
                        charArr[i][j] = character;
                        intArr[i][j] = Character.getNumericValue(c);
                    }
                }
            }

            //System.out.println(Arrays.deepToString(charArr));
 
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void printer() {
		for (int i = 0; i < row-1; i++) {
			for (int j = 0; j < column; j++) {
				System.out.print(intArr[i][j]);
			}
			System.out.println();
		}
	}
    
    
	public void generateTileMap() {
		  float posX = 0;
		  float posY = 0;
		  for (int i = 0; i < row; i++) {
		    for (int j = 0; j < column; j++) {
		        if (charArr[i][j] == '1') {
		        	tileArr[i][j] = new Tile(posX, posY, (int)GamePanel.TILE_SIZE, (int)GamePanel.TILE_SIZE, Color.RED);
		        } else {
		        	tileArr[i][j] = null;
		        }
		        tileArrOverlay[i][j] = null;
		        posX += GamePanel.TILE_SIZE;
		    }
		    posY += GamePanel.TILE_SIZE;
		    posX = 0;
		  }
	}
	
	public void highlightPath(List<Vector2Int> path) {
		clearPath(path);
		for (Vector2Int v : path) {
			Vector2 worldPos =  GamePanel.convertCellToWorld(v.x, v.y);
			pathHelper[v.y][v.x] = new Tile(worldPos.x, worldPos.y, (int)GamePanel.TILE_SIZE, (int)GamePanel.TILE_SIZE, Color.BLUE);
		}
	}
	
	public void clearPath(List<Vector2Int> path){
		/*
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < column; j++) {
				pathHelper[i][j] = null;
			}
		}
		*/
		
		for (int i = 0; i < path.size(); i++) {
			pathHelper[path.get(i).y][path.get(i).x] = null;
		}
	}
	
	public boolean isWalkable(int x, int y) {
		return intArr[y][x] == 0; 
	}


}