package pacman.controllers.chengsheng_zou;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Game;
import pacman.game.Constants.MOVE;
import pacman.game.internal.Maze;
import pacman.game.internal.Node;

public class DFS_Controller extends Controller<MOVE>{

	public static StarterGhosts ghosts = new StarterGhosts();
	public ArrayList<MOVE> savedMove = new ArrayList<>();
	
	@Override
	public MOVE getMove(Game game, long timeDue) {
		// TODO Auto-generated method stub
		if(savedMove.size() == 0){
			savedMove = dfs_simon(game, 15);
		}
		MOVE m = savedMove.get(0);
		savedMove.remove(0);
		return m;
	}
	
	private ArrayList<MOVE> dfs_simon(Game game, int maxDepth){
		Stack<PacManNode> stack = new Stack<>();
		Set<Integer> moveSet = new HashSet<>();
		int highScore = game.getScore();
		Maze curMaze = game.getCurrentMaze();
		ArrayList<MOVE> result = new ArrayList<>();
		
		//initial PacManState
		stack.push(new PacManNode(game, 0, new ArrayList<MOVE>()));
		moveSet.add(game.getPacmanCurrentNodeIndex());
		
		// search with BFS
		while(!stack.isEmpty()){
			PacManNode peek = stack.pop();
			// a path is longer than maxPath
			if (peek.gameState.getScore() > highScore){
				result = peek.moveList;
				highScore = peek.gameState.getScore();
			}
			if (result.size() == 0 || peek.depth <= maxDepth) {
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
						//*one more step in same direction, since there are 4 distance between 2 pills
						// so two step as one depth is safe
						copy.advanceGame(m, ghosts.getMove(copy, 0));
						moveSet.add(copy.getPacmanCurrentNodeIndex());
						moveList.add(m);
						//*end of one more step
						moveList.add(m);
						PacManNode temp = new PacManNode(copy, peek.depth + 1, moveList);
						stack.push(temp);
					}
				}
			}
		}
		
		// in case result is empty
		if(result.size() == 0){
			result.add(MOVE.LEFT);
		}
		return result;
	}

}
