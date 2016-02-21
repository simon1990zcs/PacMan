package pacman.controllers.chengsheng_zou;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.internal.Maze;
import pacman.game.internal.Node;
import pacman.game.Game;

public class Astar_Controller extends Controller<MOVE> {

	public static StarterGhosts ghosts = new StarterGhosts();
	public ArrayList<MOVE> savedMove = new ArrayList<>();
	// comparator for Priority queue used in A star search
	private static Comparator<AstarPacmanNode> CPT = new Comparator<AstarPacmanNode>() {
		@Override
		public int compare(AstarPacmanNode o1, AstarPacmanNode o2) {
			return (int) (o1.getEvaluatedCost() - o2.getEvaluatedCost());
		}
	};
	
	@Override
	public MOVE getMove(Game game, long timeDue) {
		// TODO Auto-generated method stub
		if(game.wasPacManEaten()){
			savedMove.clear();
		}
		if(savedMove.size() == 0){
			int des = getObjective(game);
			savedMove = Astar_simon(game, des);
		}
		MOVE m = savedMove.get(0);
		savedMove.remove(0);
		System.out.println("the next move : " + m);
		return m;
	}
	
	private int getObjective(Game game){
		int[] dest;
		int curIndex = game.getPacmanCurrentNodeIndex();
		// if there is any ghost is edible, then go to eat them
		ArrayList<Integer> edibleGhost = new ArrayList<>();
		for(GHOST ghost: GHOST.values()){
			if (game.isGhostEdible(ghost)){
				edibleGhost.add(game.getGhostCurrentNodeIndex(ghost));
			}
		}
		
		if (edibleGhost.size() != 0){
			 dest = new int[edibleGhost.size()];
			 for(int i = 0; i < dest.length; i++){
				 dest[i] = edibleGhost.get(i);
			 }
			 System.out.println("go after a ghost");
			
		} else {
			//if no edible ghost is available, then search for a power pill if there is any
			dest = game.getActivePowerPillsIndices();
			
			if (dest.length == 0){
				// if no more power pills, then go after normal pills
				dest = game.getActivePillsIndices();
			} else {
				System.out.println("go after a power pills");
			}
		} 

		return game.getClosestNodeIndexFromNodeIndex(curIndex,dest,DM.MANHATTAN);
	}
	
	private ArrayList<MOVE> Astar_simon(Game game, int destination){
		
		Queue<AstarPacmanNode> queue = new PriorityQueue<>(10, CPT);
		Set<Integer> moveSet = new HashSet<>();
		Maze curMaze = game.getCurrentMaze();
		ArrayList<MOVE> result = new ArrayList<>();
		
		//initial PacManState
		queue.offer(new AstarPacmanNode(game, 0, new ArrayList<MOVE>(), destination));
		moveSet.add(game.getPacmanCurrentNodeIndex());
		
		// search with BFS
		while(!queue.isEmpty()){
			// get the min estimated cost node
			AstarPacmanNode peek = queue.poll();
			// if the node is already in destination, then return result
			if (peek.gameState.getPacmanCurrentNodeIndex() == destination){
				result = peek.moveList;
				break;
			}
			
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
					AstarPacmanNode temp = new AstarPacmanNode(copy, peek.depth + 1, moveList, peek.destination);
					queue.offer(temp);
				}
			}
			
		}
		
		//limit the step to 10 step
		if(result.size() > 10) {
			ArrayList<MOVE> res = new ArrayList<>(10);
			for(int i = 0; i < 10; i++){
				res.add(result.get(i));
			}
			result = res;
		}
		return result;
	}

}