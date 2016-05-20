package br.cefetmg.engcomp;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {

	    int adj[][] = {
	        {1, 2}, 
	        {-1, 2},
	        {1, -2},
	        {-1, -2},
	        {2, 1},
	        {2, -1},
	        {-2, 1},
	        {-2, -1}
	    };
        
        int n = Integer.parseInt(args[0]);
	    int N = n*n;

	    int G[][] = new int[N ][N];
        
        for (int i = 0; i < n; ++i){
            for (int j = 0; j < n; ++j){
                
                for(int k = 0; k < adj.length; ++k) {
                    int u = i + adj[k][0];
                    int v = j + adj[k][1];
                    
                    
                    if(u >= 0 && u < n && v >= 0 && v < n) {
                        
                        System.out.println((i*n + j) + ", " + (u*n + v));
                        G[i*n + j][u*n + v] = 1;
                        G[u*n + v][i*n + j] = 1;
                    }  
                    
                }   
            }
        }
        
        
        for (int i = 0; i < N; ++i) {
            for(int j = 0; j < N; ++j) {
                System.out.print(G[i][j] + " ");
            }
            
            System.out.println();
        }
    }
}
