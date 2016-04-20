package pacman.controllers;

import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import pacman.game.Game;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

/*
 * Allows a human player to play the game using the arrow key of the keyboard.
 */
public class HumanController extends Controller<MOVE>
{
	public final static String path = "/Users/simon/Documents/workspace/PacMan/src/pacman/controllers/chengsheng_zou/data.txt";

	
	public KeyBoardInput input;
    
    public HumanController(KeyBoardInput input)
    {
    	this.input=input;
    }
    
    public KeyBoardInput getKeyboardInput()
    {
    	return input;
    }

    public MOVE getMove(Game game,long dueTime)
    {
    	MOVE res = null;
    	switch(input.getKey())
    	{
	    	case KeyEvent.VK_UP: 	res = MOVE.UP; break;
	    	case KeyEvent.VK_RIGHT: res = MOVE.RIGHT; break;
	    	case KeyEvent.VK_DOWN: 	res = MOVE.DOWN; break;
	    	case KeyEvent.VK_LEFT: 	res = MOVE.LEFT; break;
	    	default: 				res = MOVE.NEUTRAL; break;
    	}

    	infoSave(res, game);
    	
    	return res;
    }
    
    private void infoSave(MOVE move, Game game){
    	//save to a document 
    	String output = "";
    	int curIndex = game.getPacmanCurrentNodeIndex();
    	
    	//1. distance to close pill
    	//2. direction to closet pill
    	int[] activePills=game.getActivePillsIndices();
    	int pill = game.getClosestNodeIndexFromNodeIndex(curIndex,activePills,DM.PATH);
    	int dis = game.getShortestPathDistance(curIndex, pill);
    	MOVE dir = game.getNextMoveTowardsTarget(curIndex, pill, DM.PATH);
    	output = output + dis + " " + dir.name() + " ";
    	
    	
    	//3. distance to closet ghosts
    	//4. is it edible
    	//5. direction to closet ghosts
    	dis = Integer.MAX_VALUE;
    	boolean edible = false;
    	for(GHOST ghost : GHOST.values()){
    		if (dis > game.getShortestPathDistance(curIndex, game.getGhostCurrentNodeIndex(ghost))){
    			dis = game.getShortestPathDistance(curIndex, game.getGhostCurrentNodeIndex(ghost));
    			dir = game.getNextMoveTowardsTarget(curIndex, game.getGhostCurrentNodeIndex(ghost), DM.PATH);
    			edible = game.isGhostEdible(ghost);
    		}
    	}
    	output = output + dis + " " + edible + " " + dir.name() + " ";
    	
    	
    	//6. the final move it made
    	output = output + move.name() + "\n";
    	
    	try {
    		FileWriter fileWritter = new FileWriter(path,true);
	        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
	        bufferWritter.write(output);
	        bufferWritter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}