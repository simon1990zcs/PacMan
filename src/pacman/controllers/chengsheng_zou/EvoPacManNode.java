package pacman.controllers.chengsheng_zou;

import java.util.ArrayList;
import java.util.Random;

import pacman.game.Game;
import pacman.game.Constants.MOVE;

public class EvoPacManNode implements Comparable<EvoPacManNode> {
	Game game;
	ArrayList<MOVE> moves;
	private int score = -1;
	private static Random random = new Random();

	public EvoPacManNode(Game g){
		game = g.copy();
		//initialize a move sequence
		moves = new ArrayList<>(10);
		for(int i = 0; i < 10; i++){
			int ni = random.nextInt(MOVE.values().length);
			moves.add(MOVE.values()[ni]);
		}
	}
	public EvoPacManNode(Game g, ArrayList<MOVE> m) {
		game = g.copy();
		moves = new ArrayList<>(m);
	}
	
	public int getScore(){
		if (score != -1){
			return score;
		}
		Game copy = game.copy();
		for(MOVE m : moves){
			copy.advanceGame(m, EvoComp_Controller.ghosts.getMove(copy, 0));
		}
		score = copy.getScore();
		return score;
	}
	
	//based on itself, generate a new child, which one step different with itself
	public EvoPacManNode mutate(){
		EvoPacManNode child = new EvoPacManNode(game, moves);
		child.selfMutate();
		return child;
	}
	
	//one step different with itself.
	public void selfMutate(){
		//randomly pick one move in all moves, then randomly change to other
		int num = random.nextInt(10);
		//randomly change to other move, not the same move
		int ni = random.nextInt(MOVE.values().length - 1) + 1;
		ni %= MOVE.values().length;
		moves.set(num, MOVE.values()[ni]);
		//reset the score
		score = -1;
	}
	public EvoPacManNode reproduce(EvoPacManNode spouse) {
		// TODO Auto-generated method stub
		EvoPacManNode child = new EvoPacManNode(game);
		int len = random.nextInt(9) + 1;
		//pick self[0, len], and spouse[len, end]
		child.moves.clear();
		for(int i = 0; i < 10; i++){
			if (i < len){
				child.moves.add(this.moves.get(i));
			} else {
				child.moves.add(spouse.moves.get(i));
			}
		}
		return child;
	}
	
	@Override
	public int compareTo(EvoPacManNode o) {
		// TODO Auto-generated method stub
		return o.getScore() - this.getScore();
	}
}

