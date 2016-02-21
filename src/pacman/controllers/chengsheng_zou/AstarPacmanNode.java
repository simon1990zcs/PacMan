package pacman.controllers.chengsheng_zou;

import java.util.ArrayList;

import pacman.game.Constants.DM;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class AstarPacmanNode extends PacManNode {
	
	int destination;

	AstarPacmanNode(Game g, int d, ArrayList<MOVE> list, int des) {
		super(g, d, list);
		destination = des;
	}
	
	public double getEvaluatedCost(){
		//current cost 
		int curCost = super.moveList.size();
		//heuristic cost, using manhattan cost
		int curIndex = gameState.getPacmanCurrentNodeIndex();
		double heuriCost = gameState.getDistance(curIndex, destination, DM.MANHATTAN);
		// return the sum of current cost and heuristic cost
		return heuriCost + curCost;
	}
	
	
	
}
