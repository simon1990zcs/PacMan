package pacman.controllers.chengsheng_zou;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants.MOVE;
import pacman.game.internal.Maze;
import pacman.game.internal.Node;
import pacman.game.Game;


public class BFS_Controller extends Controller<MOVE>{

	public static StarterGhosts ghosts = new StarterGhosts();
	public ArrayList<MOVE> savedMove = new ArrayList<>();
	
	@Override
	public MOVE getMove(Game game, long timeDue) {
		// TODO Auto-generated method stub
		if(savedMove.size() == 0){
			savedMove = bfs_simon(game, 500);
		}
		MOVE m = savedMove.get(0);
		savedMove.remove(0);
		return m;
	}
	
	public ArrayList<MOVE> bfs_simon(Game game, int maxDepth){
		
		Queue<PacManNode> queue = new LinkedList<>();
		Set<Integer> moveSet = new HashSet<>();
		Maze curMaze = game.getCurrentMaze();
		ArrayList<MOVE> result = new ArrayList<>();
		
		//initial PacManState
		queue.offer(new PacManNode(game, 0, new ArrayList<MOVE>()));
		moveSet.add(game.getPacmanCurrentNodeIndex());
		
		// search with BFS
		while(!queue.isEmpty()){
			PacManNode peek = queue.poll();
			
			if(peek.gameState.getScore() > game.getScore() || peek.depth > maxDepth){
				//find a score, not continue search
				result = peek.moveList;
				break;
				
			} else {
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
						queue.offer(temp);
					}
				}
			}
		}
		
		return result;
	}
	

}
