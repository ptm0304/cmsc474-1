import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


/**
 * This project performs Iterated Elimination of Strictly Dominated Strategies (IESDS).
 * The number of players should be less than or equal to five.
 * Each player should have at most 10 actions.
 * 2D ArrayList is used to store data of matrix for easing the removing process.
 * 
 * @author Taemin
 *
 */
public class IESDS {
	static int[] ACTION;
	static List<List<Integer>> REMAIN = new ArrayList<List<Integer>>();
	
	/**
	 * This recursively checks the matrix to see if there is any strategy that is strictly dominated.
	 * If none is found using both pure and mixed, print output and terminate.
	 * 
	 * @param yes - if not 1, print output and terminate
	 * @param n - number of players
	 * @param matrix - input matrix
	 * @param pure - if true, copmute single pure strategy. Else, copmute mixture of two pure strategies.
	 */
	public static void compute(int yes, int n, List<List<Integer>> matrix, boolean pure){
		//System.out.println(yes + " " + pure);
		if (yes == 1){
			
			if (pure == true){
				List<List<Integer>> new_matrix = computePure(n, matrix);
				if (new_matrix != null){
					if (new_matrix.size() > 1 || new_matrix.get(0).size() > 1){
						compute(1, n, new_matrix, true);
					}
					else{
						compute(0, n, new_matrix, true);	
					}
				}
				else{
					compute(1, n, matrix, false);
				}
			}
			else{
				List<List<Integer>> new_matrix = computeMixed(n, matrix);
				if (new_matrix != null){
					if (new_matrix.size() > 1 || new_matrix.get(0).size() > 1){
						compute(1, n, new_matrix, true);
					}
					else{
						compute(0, n, new_matrix, true);	
					}
				}
				else{
					compute(0, n, matrix, false);
				}
			}
		}
		else{
			printOut(n, matrix);
		}
	}
	
	/**
	 * This checks if there is any strictly dominant pure strategy
	 * 
	 * @param n - number of players
	 * @param matrix - input matrix
	 * @return New matrix with strictly dominated strategy removed. Null if no strictly dominated strategy is found.
	 */
	public static List<List<Integer>> computePure(int n, List<List<Integer>> matrix){
		List<List<Integer>> temp = new ArrayList<List<Integer>>();
		int nRows = matrix.size();
		int nCols = matrix.get(0).size();
		
		// for last player
		for (int x = 0; x < ACTION[n-1]; x++){
			temp.add(new ArrayList<Integer>());
		}
		for (int i = 0; i < nRows; i++){
			for (int j = 0; j < nCols; j++){
				int ind = j / n;
				if (j%n == n-1){
					temp.get(ind).add(matrix.get(i).get(j));
				}
			}
		}
		int action = checkDominant(temp);
		if (action != -1){
			// store data for output
			REMAIN.get(n-1).remove((action));
			// remove temp(action, cols)... this action of player i is strictly dominated.
			for (int i = 0; i < nRows; i ++){
				int x = 0;
				for (int j = 0; j < nCols; j++){
					int index = j / n;
					if (index == action){
						
						matrix.get(i).remove(j-x);
						x += 1;
						
					}
				}
			}
			// decrease number of actions for player i
			ACTION[n-1] -= 1;
			// return matrix with removed action
			return matrix;
		}
		
		else{
			temp = new ArrayList<List<Integer>>();

			// for each player except last
			for (int i = 0; i < n-1; i++){ 
				temp = new ArrayList<List<Integer>>();

				int divider = 1;
				for (int x = 0; x <= i; x++){
					divider = divider*ACTION[x];
				}
				int pattern = nRows/divider; // pattern number used to compare

				for (int x = 0; x < ACTION[i]; x++){
					temp.add(new ArrayList<Integer>());

				}
				for (int j = 0; j < nRows; j ++){
					int index = (j/pattern) % ACTION[i];
					for (int m = 0; m < nCols; m++){
						if (m % n == i){
							temp.get(index).add(matrix.get(j).get(m));
						}
					}
				}

				action = checkDominant(temp);
				if (action != -1){
					REMAIN.get(i).remove((action));
					// remove temp(action, cols)... this action of player i is strictly dominated.
					int x = 0;
					for (int j = 0; j < nRows; j ++){
						int index = (j/pattern) % ACTION[i];
						if (index == action){
							matrix.remove(j-x);
							x += 1;
						}
					}
					// decrease number of actions for player i
					ACTION[i] -= 1;
					// return matrix with removed action
					return matrix;
				}
			}
		}
		return null;
	}
	/**
	 * This finds which action is strictly dominated by other actions, if any.
	 * 
	 * @param temp - Temporal matrix that is ordered by actions of a specific player.
	 * @return integer i which indicates ith action is strictly dominated. 
	 */
	public static int checkDominant(List<List<Integer>> temp){
		int r = temp.size();
		int c = temp.get(0).size();
		boolean remove = false;
		for (int i = 0; i < r; i++){
			for (int j = 0; j < r; j++){
				remove = true;
				innerloop:
				for (int k = 0; k < c; k++){
					if (temp.get(i).get(k) >= temp.get(j).get(k)){
						// if i is not strictly dominated, do not remove row i
						remove = false;
						break innerloop;
					}
				}
				if (remove == true){
					return i;
				}
			}
		}
		return -1;
	}
	/**
	 * This checks if there is any strictly dominant strategies that are mixtures of two pure strategies.
	 * 
	 * @param n - number of players
	 * @param matrix - input matrix
	 * @return New matrix with strictly dominated strategy removed. Null if no strictly dominated strategy is found.
	 */
	public static List<List<Integer>> computeMixed(int n, List<List<Integer>> matrix){
		List<List<Integer>> temp = new ArrayList<List<Integer>>();
		int nRows = matrix.size();
		int nCols = matrix.get(0).size();
		
		// for last player
		for (int x = 0; x < ACTION[n-1]; x++){
			temp.add(new ArrayList<Integer>());
		}
		for (int i = 0; i < nRows; i++){
			for (int j = 0; j < nCols; j++){
				int ind = j / n;
				if (j%n == n-1){
					temp.get(ind).add(matrix.get(i).get(j));
				}
			}
		}
		
		int action = checkDominantMixed(temp);
		if (action != -1){
			REMAIN.get(n-1).remove((action));
			// remove temp(action, cols)... this action of player i is strictly dominated.
			for (int i = 0; i < nRows; i ++){
				int x = 0;
				for (int j = 0; j < nCols; j++){
					int index = j / n;
					if (index == action){
						
						matrix.get(i).remove(j-x);
						x += 1;
						
					}
				}
			}
			// decrease number of actions for player i
			ACTION[n-1] -= 1;
			// return matrix with removed action
			return matrix;
		}
		
		// for each player except last
		
		for (int i = 0; i < n-1; i++){ 
			temp = new ArrayList<List<Integer>>();
			int divider = 1;
			for (int x = 0; x <= i; x++){
				divider = divider*ACTION[x];
			}
			int pattern = nRows/divider; // pattern number used to compare
			for (int x = 0; x < ACTION[i]; x++){
				temp.add(new ArrayList<Integer>());
			}
			for (int j = 0; j < nRows; j ++){
				int index = (j/pattern) % ACTION[i];
				for (int m = 0; m < nCols; m++){
					if (m % n == i){
						temp.get(index).add(matrix.get(j).get(m));
					}
				}
			}
		
			action = checkDominantMixed(temp);
			if (action != -1){
				
				REMAIN.get(i).remove((action));
				int x = 0;
				// remove temp(action, cols)... this action of player i is strictly dominated.
				for (int j = 0; j < nRows; j ++){
					int index = (j/pattern) % ACTION[i];
					if (index == action){
						matrix.remove(j-x);
						x += 1;
					}
				}
				// decrease number of actions for player i
				ACTION[i] -= 1;
				// return matrix with removed action

				return matrix;
			}
		}
		return null;
	}
	/**
	 * This finds which action is strictly dominated by other actions, if any.
	 * 
	 * @param temp - Temporal matrix that is ordered by actions of a specific player.
	 * @return integer i which indicates ith action is strictly dominated. 
	 */
	public static int checkDominantMixed(List<List<Integer>> temp){
		int r = temp.size();
		int c = temp.get(0).size();
		boolean remove = false;
		if (r < 3){
			return -1;
		}
		else if (r == 3){ // no mixtures of three actions
			if (isStrictlyDominated(temp.get(0), temp.get(1), temp.get(2))){
				return 0;
			}
			else if (isStrictlyDominated(temp.get(1), temp.get(0), temp.get(2))){
				return 1;
			}
			else if (isStrictlyDominated(temp.get(2), temp.get(0), temp.get(1))){
				return 2;
			}
		}
		else{
			for (int i = 0; i < r; i++){
				for (int j = 0; j < r; j++){
					for (int k = 0; k < r; k++){
						if (isStrictlyDominated(temp.get(i), temp.get(j), temp.get(k))){
							return i;
						}
						else if (isStrictlyDominated(temp.get(j), temp.get(i), temp.get(k))){
							return j;
						}
						else if (isStrictlyDominated(temp.get(k), temp.get(i), temp.get(j))){
							return k;
						}
					}
				}
			}
		}
		return -1;
	}
	/**
	 * This checks if aciton one is strictly dominated by mixture of action two and action three.
	 * @param one
	 * @param two
	 * @param three
	 * @return true if one is strictly dominated by mixture of two and three. false, otherwise.
	 */
	public static boolean isStrictlyDominated(List<Integer> one, List<Integer> two, List<Integer> three){
		List<Double> p_max = new ArrayList<Double>();
		List<Double> p_min = new ArrayList<Double>();
		for (int x = 0; x < one.size(); x++){
			if (one.get(x) >= two.get(x) && one.get(x) >= three.get(x)){
				return false;
			}
			else{
				/*
				 Solve equations: p+q = 1
				 				  two.get(x) * p + three.get(x) * q > one.get(x)
				 
				 */
				if (two.get(x) > three.get(x)){
					p_max.add(x, (double) 1);
					p_min.add(x, linearEq(1, 1, 1, two.get(x), three.get(x), one.get(x)));
				}
				else{
					p_min.add(x, (double) 0);
					p_max.add(x, linearEq(1, 1, 1, two.get(x), three.get(x), one.get(x)));
				}
			}
		}
		double max = 1;
		double min = 0;
		for (int x = 0; x < one.size(); x++){
			if (p_max.get(x) < max){
				max = p_max.get(x);
			}
			if (p_min.get(x) > min){
				min = p_min.get(x);
			}
		}
		if (max >= min){
			return true;
		}
		return false;
	}
	/**
	 * Solves equations for x : 
	 * 		  
	 * 		  ax + by = e
	 * 		  bx + cy = f
	 * 
	 * 
	 * @param a
	 * @param b
	 * @param e
	 * @param c
	 * @param d
	 * @param f
	 * @return x
	 */
	public static double linearEq(int a, int b, int e, int c, int d, int f){
		return (((double) e* (double) d) - ((double) b* (double) f))/(((double) a*(double) d)-((double) b* (double) c));
	}
	
	/**
	 * Print out the output
	 * 
	 * @param n
	 * @param matrix
	 */
	public static void printOut(int n, List<List<Integer>> matrix){
		System.out.println(n);
		for (int i = 0; i < REMAIN.size(); i++){
			for (Integer e: REMAIN.get(i)){
				System.out.print(e + " ");
			}
			System.out.println();
		}
		for (int i = 0; i < matrix.size(); i++){
			for (Integer e: matrix.get(i)){
				System.out.print(e + " ");
			}
			System.out.println();
		}
	}
	
	public static void main(String[] args){
		Scanner sc = new Scanner(System.in);
		int yes = sc.nextInt();
		int n = sc.nextInt();
		ACTION = new int[n];
		for (int i = 0; i < n; i++){
			REMAIN.add(new ArrayList<Integer>());
			ACTION[i] = sc.nextInt();
			for (int j = 1; j <= ACTION[i]; j++){
				REMAIN.get(i).add(j);
			}
		}
		
		int nRows = 1;
		for (int i = 0; i < n-1; i++){
			nRows = nRows*ACTION[i];
		}
		int nCols = n*ACTION[n-1];
		List<List<Integer>> matrix = new ArrayList<List<Integer>>();
		for (int i = 0; i < nRows; i++){
			matrix.add(new ArrayList<Integer>());
			for (int j = 0; j< nCols; j++){
				matrix.get(i).add(sc.nextInt());
			}
		}
		sc.close();
		compute(yes, n, matrix, true);
	}
}
