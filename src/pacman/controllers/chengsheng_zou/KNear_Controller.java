package pacman.controllers.chengsheng_zou;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import pacman.controllers.Controller;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class KNear_Controller extends Controller<MOVE> {
	
	Set<trainData> datas = new HashSet<>();
	
	public KNear_Controller() {
		//load data into memory
		BufferedReader br = null;

		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader("/Users/simon/Documents/data.txt"));
			while ((sCurrentLine = br.readLine()) != null) {
				datas.add(new trainData(sCurrentLine));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public MOVE getMove(Game game, long timeDue) {
		int curIndex = game.getPacmanCurrentNodeIndex();
		//closest distance to pill
    	int[] activePills=game.getActivePillsIndices();
    	int pill = game.getClosestNodeIndexFromNodeIndex(curIndex,activePills,DM.PATH);
    	int pillDis = game.getShortestPathDistance(curIndex, pill);
    	MOVE pillDir = game.getNextMoveTowardsTarget(curIndex, pill, DM.PATH);
    	
    	//closet ghost
    	int ghostDis = Integer.MAX_VALUE;
    	boolean edible = false;
    	MOVE ghostDir = MOVE.NEUTRAL;
    	for(GHOST ghost : GHOST.values()){
    		if (ghostDis > game.getShortestPathDistance(curIndex, game.getGhostCurrentNodeIndex(ghost))){
    			ghostDis = game.getShortestPathDistance(curIndex, game.getGhostCurrentNodeIndex(ghost));
    			ghostDir = game.getNextMoveTowardsTarget(curIndex, game.getGhostCurrentNodeIndex(ghost), DM.PATH);
    			edible = game.isGhostEdible(ghost);
    		}
    	}
    	
    	//search in datas, looking for 3 nearest points
    	Queue<trainData> que = new PriorityQueue<>();
    	for(trainData data : datas){
    		//calculate the compareData in trainData
    		int val = 0;
    		val += Math.abs(pillDis - data.closestPill);
    		val += Math.abs(ghostDis - data.closestGhost) * 10;
    		val += (edible == data.edible ? 0 : 100);
    		data.compareValue = val;
    		que.offer(data);
    		if(que.size() > 3){
    			que.poll();
    		}
    	}
    	
    	//in these 3 points, find the desired move
    	MOVE res = MOVE.NEUTRAL;
    	while(!que.isEmpty()){
    		trainData data = que.poll();
    		if (data.realMove == MOVE.NEUTRAL){
    			continue;
    		} else if (data.realMove == data.pillDir){
    			return pillDir;
    		} else if (data.edible && data.realMove == data.ghostDir){
    			return ghostDir;
    		} else if (!data.edible && data.realMove == data.ghostDir.opposite()){
    			return ghostDir.opposite();
    		}
    	}
    	
		return res;
	}

}

class trainData implements Comparable<trainData>{
	int closestPill;
	MOVE pillDir;
	int closestGhost;
	boolean edible;
	MOVE ghostDir;
	MOVE realMove;
	//used for pick nearest training data
	int compareValue = 0;
	
	public trainData(String line) {
		// TODO Auto-generated constructor stub
		String[] strs = line.split(" ");
		closestPill = Integer.parseInt(strs[0]);
		pillDir = MOVE.valueOf(strs[1]);
		closestGhost = Integer.parseInt(strs[2]);
		edible = Boolean.parseBoolean(strs[3]);
		ghostDir = MOVE.valueOf(strs[4]);
		realMove = MOVE.valueOf(strs[5]);
	}

	@Override
	public int compareTo(trainData o) {
		// TODO Auto-generated method stub
		return this.compareValue - o.compareValue;
	}
	
	
}

