package org.tool.astar;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * A星寻路算法
 */
public class AStarPath {
	
    private byte[][] map;  	// 地图矩阵，0表示能通过，1表示不能通过
    
    private int map_w;    	// 地图宽度
    private int map_h;    	// 地图高度
    
    private int start_x;  		// 起点坐标X
    private int start_y;  		// 起点坐标Y
    private int goal_x;   		// 终点坐标X
    private int goal_y;   		// 终点坐标Y


    private boolean closeList[][];     	// 关闭列表
    public  int openList[][][];            // 打开列表
    private int openListLength;


    private static final int EXIST 			= 1;
    private static final int NOT_EXIST 	= 0;


    private static final int ISEXIST 			= 0;  
    private static final int EXPENSE 		= 1;     	// 自身的代价
    private static final int DISTANCE 		= 2;    	// 距离的代价
    private static final int COST 				= 3;  		// 消耗的总代价
    private static final int FATHER_DIR 	= 4;  		// 父节点的方向


    public static final int DIR_NULL 					= 0;
    public static final int DIR_DOWN 					= 1;     	// 方向：下
    public static final int DIR_UP 						= 2;      	// 方向：上
    public static final int DIR_LEFT 					= 3;     	// 方向：左
    public static final int DIR_RIGHT 					= 4;    	// 方向：右
    public static final int DIR_UP_LEFT 				= 5;
    public static final int DIR_UP_RIGHT 				= 6;
    public static final int DIR_DOWN_LEFT 				= 7;
    public static final int DIR_DOWN_RIGHT 				= 8;


    private int depth;                				// 算法嵌套深度
    private boolean isFound;                  		// 是否找到路径


	public AStarPath(byte[][] map) {
		this.map 		= map;
		this.map_w 	= map.length;
		this.map_h 	= map[0].length;
		this.closeList = new boolean[map_w][map_h];
		this.openList = new int[map_w][map_h][5];
	}

	private void init(int sx, int sy, int gx, int gy) {
		this.start_x = sx;
		this.start_y = sy;
		this.goal_x = gx;
		this.goal_y = gy;
		this.depth = 5000;
		this.initCloseList();
		this.initOpenList(goal_x, goal_y);
	}

	// 得到地图上这一点的消耗值
	private int getMapExpense(int x, int y, int dir) {
		if (dir < 5) {
			return 10;
		} else {
			return 14;
		}
	}

	// 得到距离的消耗值
	private int getDistance(int x, int y, int ex, int ey) {
		return 10 * (Math.abs(x - ex) + Math.abs(y - ey));
	}

	// 得到给定坐标格子此时的总消耗值
	private int getCost(int x, int y) {
		return openList[x][y][COST];
	}

	// 开始寻路
	private void searchPath() {

		addOpenList(start_x, start_y);
		aStar(start_x, start_y);
	}

	// 寻路
	private void aStar(int x, int y) {
		// 控制算法深度
		for (int t = 0; t < depth; t++) {
			if (((x == goal_x) && (y == goal_y))) {
				isFound = true;
				return;
			} else if ((openListLength == 0)) {
				isFound = false;
				return;
			}

			removeOpenList(x, y);
			addCloseList(x, y);

			// 该点周围能够行走的点
			addNewOpenList(x, y, x, y + 1, 		DIR_UP);
			addNewOpenList(x, y, x, y - 1, 			DIR_DOWN);
			addNewOpenList(x, y, x - 1, y, 			DIR_RIGHT);
			addNewOpenList(x, y, x + 1, y, 		DIR_LEFT);
			addNewOpenList(x, y, x + 1, y + 1, 	DIR_UP_LEFT);
			addNewOpenList(x, y, x - 1, y + 1, 	DIR_UP_RIGHT);
			addNewOpenList(x, y, x + 1, y - 1, 	DIR_DOWN_LEFT);
			addNewOpenList(x, y, x - 1, y - 1, 	DIR_DOWN_RIGHT);

			// 找到估值最小的点，进行下一轮算法
			int cost = 0x7fffffff;
			for (int i = 0; i < map_w; i++) {
				for (int j = 0; j < map_h; j++) {
					if (openList[i][j][ISEXIST] == EXIST) {
						if (cost > getCost(i, j)) {
							cost = getCost(i, j);
							x = i;
							y = j;
						}
					}
				}
			}
		}
		// 算法超深
		isFound = false;
		return;
	}

	// 添加一个新的节点
	private void addNewOpenList(int x, int y, int newX, int newY, int dir) {
		if (isCanPass(newX, newY)) {
			if (openList[newX][newY][ISEXIST] == EXIST) {
				if (openList[x][y][EXPENSE] + getMapExpense(newX, newY, dir) < openList[newX][newY][EXPENSE]) {
					setFatherDir(newX, newY, dir);
					setCost(newX, newY, x, y, dir);
				}
			} else {
				addOpenList(newX, newY);
				setFatherDir(newX, newY, dir);
				setCost(newX, newY, x, y, dir);
			}
		}
	}

	// 设置消耗值
	private void setCost(int x, int y, int ex, int ey, int dir) {
		openList[x][y][EXPENSE] = openList[ex][ey][EXPENSE] + getMapExpense(x, y, dir);
		openList[x][y][DISTANCE] = getDistance(x, y, ex, ey);
		openList[x][y][COST] = openList[x][y][EXPENSE] + openList[x][y][DISTANCE];
	}

	// 设置父节点方向
	private void setFatherDir(int x, int y, int dir) {
		openList[x][y][FATHER_DIR] = dir;
	}

	// 判断一个点是否可以通过
	private boolean isCanPass(int x, int y) {
		// 超出边界
		if (x < 0 || x >= map_w || y < 0 || y >= map_h) {
			return false;
		}
		// 地图不通
		if (map[x][y] != 0) {
			return false;
		}
		// 在关闭列表中
		if (isInCloseList(x, y)) {
			return false;
		}
		return true;
	}

	// 移除打开列表的一个元素
	private void removeOpenList(int x, int y) {
		if (openList[x][y][ISEXIST] == EXIST) {
			openList[x][y][ISEXIST] = NOT_EXIST;
			openListLength--;
		}
	}

	// 判断一点是否在关闭列表中
	private boolean isInCloseList(int x, int y) {
		return closeList[x][y];
	}

	// 添加关闭列表
	private void addCloseList(int x, int y) {
		closeList[x][y] = true;
	}

	// 添加打开列表
	private void addOpenList(int x, int y) {
		if (openList[x][y][ISEXIST] == NOT_EXIST) {
			openList[x][y][ISEXIST] = EXIST;
			openListLength++;
		}
	}

	// 初始化关闭列表
	private void initCloseList() {
		for (int x = 0; x < map_w; x++) {
			for (int y = 0; y < map_h; y++) {
				closeList[x][y] = false;
			}
		}
	}

	// 初始化打开列表
	private void initOpenList(int ex, int ey) {
		for (int x = 0; x < map_w; x++) {
			for (int y = 0; y < map_h; y++) {
				openList[x][y][ISEXIST] 		= NOT_EXIST;
				openList[x][y][EXPENSE] 		= getMapExpense(x, y, DIR_NULL);
				openList[x][y][DISTANCE] 		= getDistance(x, y, ex, ey);
				openList[x][y][COST] 			= openList[x][y][EXPENSE] + openList[x][y][DISTANCE];
				openList[x][y][FATHER_DIR] 		= DIR_NULL;
			}
		}
		openListLength = 0;
	}


    /**
     *  寻路 - 获得结果
     * @param sx 起点X
     * @param sy 起点Y
     * @param gx 终点X
     * @param gy 终点Y
     * @return
     */
    public LinkedList<Tile> findPath(int sx, int sy, int gx, int gy){
		if ((sx >= map.length || sy >= map[0].length) || (gx >= map.length || gy >= map[0].length)
				|| (sx < 0 || sy < 0) || (gx < 0 || gy < 0)) {
			return null;
		}
    	byte t1 = map[sx][sy];
		byte t2 = map[gx][gy];
		// 强制通行（演示用）
    	map[sx][sy] = 0;
		map[gx][gy] = 0;
    	init(sx, sy, gx, gy);
        searchPath();
		map[sx][sy] = t1;
		map[gx][gy] = t2;
        if(!isFound){
            return null;
        }
    	LinkedList<Tile> route = new LinkedList<Tile>();
        // openList是从目标点向起始点倒推的。
        int iX = goal_x;
        int iY = goal_y;
        while((iX != start_x || iY != start_y)){
            route.add(0, new Tile(iX, iY));
            switch(openList[iX][iY][FATHER_DIR]){
            case DIR_DOWN:          iY++;            break;
            case DIR_UP:            iY--;            break;
            case DIR_LEFT:          iX--;            break;
            case DIR_RIGHT:         iX++;            break;
            case DIR_UP_LEFT:       iX--;   iY--;    break;
            case DIR_UP_RIGHT:      iX++;   iY--;    break;
            case DIR_DOWN_LEFT:     iX--;   iY++;    break;
            case DIR_DOWN_RIGHT:    iX++;   iY++;    break;
            }
        }
        return route;
    }
    
    public static void main(String[] args) {
		
    	Random random = new Random();
		byte[][] map = new byte[100][100];
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				map[i][j] = (byte) (random.nextInt(2));
			}
		}
		System.out.println(map[1][3] + " -> " + map[0][0]);
		AStarPath asr = new AStarPath(map);
		List<Tile> result = asr.findPath(3, 1, 0, 0);
		if(result != null) {
			for (Tile p : result) {
				System.out.println(p.x + "," + p.y);
			}
		} else {
			System.err.println("走不通！");
		}

	}
    
}