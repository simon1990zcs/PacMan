package pacman.controllers.chengsheng_zou;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants.MOVE;
import pacman.game.internal.Maze;
import pacman.game.internal.Node;
import pacman.game.Game;

public class IDDFS_Controller extends Controller<MOVE> {

	public static StarterGhosts ghosts = new StarterGhosts();
	//instead of return one move, save the whole path to the pills. 
	public ArrayList<MOVE> savedMove = new ArrayList<>();
	
	@Override
	public MOVE getMove(Game game, long timeDue) {
		//if the path is non-empty, then take from list, otherwise search for the path
		if(savedMove.size() == 0){
			savedMove = iddfs_simon(game, 500);
		}
		MOVE m = savedMove.get(0);
		savedMove.remove(0);
		return m;
	}
	
	private ArrayList<MOVE> iddfs_simon(Game game, int maxDepth){
		
		int curScore = game.getScore();
		Maze curMaze = game.getCurrentMaze();
		ArrayList<MOVE> result = new ArrayList<>();
		
		//iterative deepening the depth, from 1 to maxDepth, 
		// it's supposed to break during loop once a result is found
		for(int depth = 1; depth < maxDepth; depth++){
			Stack<PacManNode> stack = new Stack<>();
			Set<Integer> moveSet = new HashSet<>();
			
			//initial PacManState
			stack.push(new PacManNode(game, 0, new ArrayList<MOVE>()));
			moveSet.add(game.getPacmanCurrentNodeIndex());
			
			// search with DFS
			while(!stack.isEmpty()){
				PacManNode peek = stack.pop();
				// record the highest score path
				if (peek.gameState.getScore() > curScore){
					//find a closet pill, then return
					return peek.moveList;
				}
				//skip those path longer than depth
				if (peek.depth <= depth) {
					//keep adding children
					Node node = curMaze.graph[peek.gameState.getPacmanCurrentNodeIndex()];
					//loop of adding children in possible directions
					for(MOVE m : node.neighbourhood.keySet()){
						Game copy = peek.gameState.copy();
						copy.advanceGame(m, ghosts.getMove(copy, 0));
						if (!moveSet.contains(copy.getPacmanCurrentNodeIndex())){
							//move discovered node index into set
							moveSet.add(copy.getPacmanCurrentNodeIndex());
							ArrayList<MOVE> moveList = new ArrayList<>(peek.moveList);
							moveList.add(m);
							PacManNode temp = new PacManNode(copy, peek.depth + 1, moveList);
							stack.push(temp);
						}
					}
				}
			}
			
		}
		
		// in case result is not found, beyong maxDepth, then adding left instead
		result.add(MOVE.LEFT);
		return result;
	}

}
