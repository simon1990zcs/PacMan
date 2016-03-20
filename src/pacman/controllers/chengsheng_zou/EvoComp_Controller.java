package pacman.controllers.chengsheng_zou;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

//Evolution Computation algorithms
public class EvoComp_Controller extends Controller<MOVE> {

	public static StarterGhosts ghosts = new StarterGhosts();
	public ArrayList<MOVE> savedMove = new ArrayList<>();
	private static Comparator<EvoPacManNode> cmp = new Comparator<EvoPacManNode>(){
		@Override
		public int compare(EvoPacManNode o1, EvoPacManNode o2) {
			// descending order
			return o2.getScore() - o1.getScore();
		}
	};
	
	@Override
	public MOVE getMove(Game game, long timeDue) {
		// TODO Auto-generated method stub
		if(savedMove.size() == 0){
			savedMove = evolutionComputation_simon(game, 100);
		}
		MOVE m = savedMove.get(0);
		savedMove.remove(0);
		return m;
	}
	
	private ArrayList<MOVE> evolutionComputation_simon(Game game, int maxTimes){
		Queue<EvoPacManNode> parents = new PriorityQueue<>(10, cmp);
		
		//initialize 10 parents randomly at the first time
		for(int i = 0; i < 10; i++){
			parents.add(new EvoPacManNode(game));
		}
		
		//evolution computation
		for(int i = 0; i < maxTimes; i++){
			Queue<EvoPacManNode> children = new PriorityQueue<>(15, cmp);
			//pick top five, then mutate twice
			for(int j = 0; j < 5; j++){
				EvoPacManNode temp = parents.poll();
				children.offer(temp.mutate());
				children.offer(temp.mutate());
			}
			//add left over 5 children into queue
			for(int j = 0; j < 5; j++){
				children.add(parents.poll());
			}
			parents = children;
		}
		
		//return the highest score moves
		return parents.poll().moves;
	}

}
