package pacman.controllers.chengsheng_zou;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

//genetic algorithm -- crossover 
public class Genetic_Controller extends Controller<MOVE> {

	public static StarterGhosts ghosts = new StarterGhosts();
	public ArrayList<MOVE> savedMove = new ArrayList<>();
	
	@Override
	public MOVE getMove(Game game, long timeDue) {
		if(savedMove.size() == 0){
			savedMove = geneticAlgorithm_simon(game, 100);
		}
		MOVE m = savedMove.get(0);
		savedMove.remove(0);
		return m;
	}

	private ArrayList<MOVE> geneticAlgorithm_simon(Game game, int maxTimes) {
		List<EvoPacManNode> parents = new ArrayList<>();
		
		//initialize 10 parents randomly at the first time
		for(int i = 0; i < 10; i++){
			parents.add(new EvoPacManNode(game));
		}
		
		//crossover multiple times
		Random random = new Random();
		for(int i = 0; i < maxTimes; i++){
			List<EvoPacManNode> children = new ArrayList<>(10);
			for(int j = 0; j < 10; j++){
				//randomly pick two different parents
				int n1 = random.nextInt(10);
				int n2 = random.nextInt(9) + 1;
				n2 %= 10;
				EvoPacManNode child = parents.get(n1).reproduce(parents.get(n2));
				//40% chance self mutation
				if (random.nextInt(10) < 4){
					child.selfMutate();
				}
				children.add(child);
			}
			parents = children;
		}
		
		//return the highest score children;
		EvoPacManNode best = parents.get(0);
		for(int i = 1; i < 10; i++){
			if (parents.get(i).getScore() > best.getScore()){
				best = parents.get(i);
			}
		}
		
		return best.moves;
		
	}

}
