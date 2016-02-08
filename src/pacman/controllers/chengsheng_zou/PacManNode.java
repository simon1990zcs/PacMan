package pacman.controllers.chengsheng_zou;

import pacman.game.Constants.MOVE;

import java.util.ArrayList;

import pacman.game.Game;

public class PacManNode {
	Game gameState;
	int depth;
	ArrayList<MOVE> moveList;
	PacManNode(Game g, int d, ArrayList<MOVE> list){
		this.gameState = g;
		this.depth = d;
		this.moveList = list;
	}
}
