package pacman.controllers.amy_hoover;

import pacman.controllers.*;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants.MOVE;
import pacman.game.*;
import pacman.game.Game;

import java.util.*;

public class BFS extends Controller<MOVE>{
	
	public static StarterGhosts ghosts = new StarterGhosts();
	
	public MOVE getMove (Game game , long timeDue ){
		//System.out.println(timeDue);
		Random rnd=new Random();
		MOVE[] allMoves=MOVE.values();
		int highScore = -1;
		MOVE highMove = null;
		for(MOVE m: allMoves){
			//System . out . p r i n t l n (¡± Trying Move : ¡± + m) ;
			Game gameCopy=game.copy();
			Game gameAtM = gameCopy;
			gameAtM.advanceGame(m,ghosts.getMove(gameAtM,timeDue));
			int tempHighScore = this.bfs_JiDai(new PacManNode(gameAtM, 0) , 13) ;
			if (highScore < tempHighScore){
				highScore = tempHighScore ;
				highMove = m;
			}
			
			System.out.println("Trying Move: " + m + ", Score: " + tempHighScore);
		}
		System.out.println("High Score: " + highScore + ", High move:" + highMove);
		return highMove;
	}
	
	public int bfs_JiDai(PacManNode rootGameState, int maxDepth){
		MOVE allMoves[] = Constants.MOVE.values();
		int depth = 0;
		int highScore = -1;
		
		Queue<PacManNode> queue = new LinkedList<PacManNode>();
		queue.add(rootGameState);
		HashSet hs = new HashSet();
		Game game = rootGameState.gameState;
		hs.add(game.getPacmanCurrentNodeIndex());
		
		
		while(!queue.isEmpty()){
			PacManNode pacManNode = queue.remove();
			if(pacManNode.depth >= maxDepth){
				int score = pacManNode.gameState.getScore();
				if (highScore < score){
					highScore = score;
				}
			}
			else{
				for (MOVE m : allMoves){
					Game gameCopy = pacManNode.gameState.copy();
					gameCopy.advanceGame(m,ghosts.getMove(gameCopy, 0));
					if(hs.add(gameCopy.getPacmanCurrentNodeIndex())){
						PacManNode node = new PacManNode(gameCopy, pacManNode.depth + 1);
						queue.add(node);
					}
					
				}
			}
		}
		return highScore;
	}
}

