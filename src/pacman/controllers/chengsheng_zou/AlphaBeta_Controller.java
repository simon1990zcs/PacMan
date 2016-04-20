package pacman.controllers.chengsheng_zou;


import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import pacman.controllers.Controller;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.internal.Maze;
import pacman.game.internal.Node;
import pacman.game.Game;

public class AlphaBeta_Controller extends Controller<MOVE> {

	Maze curMaze = null;
	
	@Override
	public MOVE getMove(Game game, long timeDue) {
		// TODO Auto-generated method stub
		if (curMaze == null){
			curMaze = game.getCurrentMaze();
		}
		MOVE res = MOVE.DOWN;
		double a = Integer.MIN_VALUE;
		double b = Integer.MAX_VALUE;
		double value = Integer.MIN_VALUE;
		Node node = curMaze.graph[game.getPacmanCurrentNodeIndex()];
		for(MOVE m : node.neighbourhood.keySet()){
			Game copy = game.copy();
			copy.advanceGame(m, null);
			double score = alphaBeta_simon(copy, a, b, 5, false);
			System.out.println("Move: " + m + ", score: " + score);
			if (score > value){
				value = score;
				res = m;
			}
			a = Math.max(value, a);
		}
		System.out.println(res);
		return res;
	}

	
	private double alphaBeta_simon(Game game, double a, double b, int depth, boolean pacManMove){
		System.out.println("current depth: " + depth);
		if (depth == 0 || game.wasPacManEaten()){
			return getEstimatedScore(game);
		}
		
		if (pacManMove){  // maximize the score
			double value = Integer.MIN_VALUE;
			Node node = curMaze.graph[game.getPacmanCurrentNodeIndex()];
			for(MOVE m : node.neighbourhood.keySet()){
				Game copy = game.copy();
				copy.advanceGame(m, null);
				value = Math.max(value, alphaBeta_simon(copy, a, b, depth - 1, false));
				a = Math.max(value, a);
				if (b <= a){
					break;
				}
			}
			return value;
		} else { // minimize the score
			double value = Integer.MAX_VALUE;
			for(EnumMap<GHOST,MOVE> map : getGhostsAllPossibleMoves(game)){
				Game copy = game.copy();
				copy.advanceGame(null, map);
				value = Math.min(value, alphaBeta_simon(copy, a, b, depth - 1, true));
				b = Math.min(value, b);
				if (b <= a){
					break;
				}
			}
			return value;
		}
		
	}
	
	private List<EnumMap<GHOST,MOVE>> getGhostsAllPossibleMoves(final Game game){
		List<EnumMap<GHOST,MOVE>> result = new ArrayList<>();
		EnumMap<GHOST,MOVE> initialMove = new EnumMap<GHOST,MOVE>(GHOST.class);
		helper(result, initialMove, game, 0);
		return result;
	}
	
	private void helper(List<EnumMap<GHOST,MOVE>> res, EnumMap<GHOST,MOVE> moves, final Game game, int pos){
		if (pos == GHOST.values().length){
			res.add(new EnumMap<>(moves));
			return;
		}
		GHOST ghost = GHOST.values()[pos];
		//if the ghost doesn't need to move, then goes to next ghost
		if (!game.doesGhostRequireAction(ghost)){
			helper(res, moves, game, pos + 1);
		}
		//add all possible moves for this ghosts
		Node node = curMaze.graph[game.getGhostCurrentNodeIndex(ghost)];
		for(MOVE m : node.neighbourhood.keySet()){
			moves.put(ghost, m);
			helper(res, moves, game, pos + 1);
			moves.remove(ghost);
		}
		
	}
	
	private double getEstimatedScore(Game game){
		//1. total game score get so far
		int gameScore = game.getScore();
		//2. the distance away from other ghosts, this is negative normally, ghostScore decrease when ghost come close
		double ghostScore = 0;
		for (GHOST ghost: GHOST.values()){
			double ghostDistance = game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(ghost));
			int sign = game.isGhostEdible(ghost) == true ? 1: -1;
			ghostScore += sign * (50 / ghostDistance);
		}
		//3. the distance with closest pills, pillScore proportional to distance
		int pillScore = -getDistanceToClosestPill(game);
		
		double total = gameScore + ghostScore + pillScore;
		//System.out.println(total);
		return total;
	}
	
	private int getDistanceToClosestPill(Game game){
		int currentNodeIndex=game.getPacmanCurrentNodeIndex();	
		int[] activePills=game.getActivePillsIndices();
		int[] activePowerPills=game.getActivePowerPillsIndices();
		int[] targetNodeIndices=new int[activePills.length+activePowerPills.length];
		
		for(int i=0;i<activePills.length;i++)
			targetNodeIndices[i]=activePills[i];
		
		for(int i=0;i<activePowerPills.length;i++)
			targetNodeIndices[activePills.length+i]=activePowerPills[i];		
		
		int nearest = game.getClosestNodeIndexFromNodeIndex(currentNodeIndex,targetNodeIndices,DM.PATH);
		return nearest;
	}
	
	class alphaBetaPacManNode{
		Game gmaeState;
		
	}
}



