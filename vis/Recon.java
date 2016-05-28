package sprax.vis;

import sprax.Sx;

/*************************************
 * Reconstruction/Localization of sensor network nodes
 */

public class Recon 
{
    /**
     * Given N observers of each other, estimate actual places of all.
     * Averaging each observer's observations of the others gives an O(N^2) algorithm.
     * 
     * @param observers
     * @param numObservers
     * @return
     */
    static Observer averageFromKnownOrder3d(Observer[] observers, int numObservers)
    {
        return null;
    }
    
    public static int unit_test(int numObs)
    {
    	String testLabel = Recon.class.getName() + ".unit_test";
    	Sx.puts(testLabel + " BEGIN");

    	test_2d0a(7);
    	
    	Sx.puts(testLabel + " END");
        return 0;
    }

    /**
     * Two spatial dimensions, 0 anchors, N sensor nodes
     * @param numObs
     * @return
     */
    public static int test_2d0a(int numObs)
    {
    	int numDim  = 2;
    	int numSeen = numObs - 1;	// each observer sees all others, not itself
    	int maxIter = 12;			// iteration limit
    	boolean bUpdateObservations = true;
    	boolean bNewObservations = true;
    	
    	String testLabel = Recon.class.getName() + ".test_2d2s0a";
    	Sx.puts(testLabel + " BEGIN");
    	ArrayFactory arrayFactory = new ArrayFactory();
    	Observer[] observers = new Observer[numObs];
    	for (int j = 0; j < numObs; j++)
    		observers[j] = new Observer();

    	// Initialize observers' own positions
    	for (int n = 0; n < numObs; n++) {
    		Observer obs = observers[n];

    		// Real position is on a parabola.
    		float[] realPos = { n, n*n };
    		obs.mRealPos    = new Observation(null, 1, 0, realPos);
    		float[] randVec = arrayFactory.randomVectorInOriginSphere(numDim, 0.1f);

    		// Initial (fake) computed pos = real pos + noise
    		obs.mAutoPos    = new Observation(obs.mRealPos);    		
    		obs.mAutoPos.addS(randVec);
    	}

    	Recon.addAllObservations(observers, numObs, arrayFactory, numDim);

    	// Compute candidate updated position of Observer[n] 
    	// as some combination of its own self-observed position 
    	// with a position obtained by averaging (or otherwise
    	// merging) the position(s) of Observer[n] as observed by
    	// all other (nearby) observers.
    	// 
    	for (int iter = 0; iter < maxIter; iter++) {
    		for (int nob = 0; nob < numObs; nob++) {
    			Observer obs = observers[nob];
    			int numOthersThatSeeMe = 0;
    			float c = 0, t = 0, x = 0, y = 0;
    			for (Observer other : obs.mObservations.keySet()) {
    				// skip self observation
    				if (other == obs)
    					continue;

    				Observation reflexObs = other.mObservations.get(obs);
    				if (reflexObs != null) {
    					numOthersThatSeeMe++;
    					c += reflexObs.mC;
    					t += reflexObs.mT;
    					x += reflexObs.mS[0];
    					y += reflexObs.mS[1];
    				}
    			}
    			if (numOthersThatSeeMe > 0) {
    				c /= numOthersThatSeeMe;
    				t /= numOthersThatSeeMe; 
    				float[] s = { x/numOthersThatSeeMe, y/numOthersThatSeeMe };            
    				obs.mSeenPos = new Observation(null, c, t, s);
    				Sx.format("%2d auto(% 7.4f % 8.4f)  seen(% 7.4f % 8.4f)", nob
    						, obs.mAutoPos.mS[0], obs.mAutoPos.mS[1]
    						, obs.mSeenPos.mS[0], obs.mSeenPos.mS[1]    						                                         
    				);
    				x = (obs.mSeenPos.mS[0] - obs.mAutoPos.mS[0])*0.5f;
    				y = (obs.mSeenPos.mS[1] - obs.mAutoPos.mS[1])*0.5f;
    				c = x*x + y*y;
    				obs.mAutoPos.mS[0] += x;
    				obs.mAutoPos.mS[1] += y;
    				Sx.format("  updt(% 8.5f % 9.5f) sqerr %g\n"
    						, obs.mAutoPos.mS[0], obs.mAutoPos.mS[1], c
    				);
    				
    				if (bNewObservations)
      		    	    Recon.addObservations(obs, observers, numObs, arrayFactory, numDim);

    				if (bUpdateObservations) {
    					for (Observation o : obs.mObservations.values()) {
    						o.mS[0] += x;
    						o.mS[1] += y;
    					}
    				}
 						                                         
    			}
    		}
    	}

    	// Observer aveObs = averageFromKnownOrder3d(observers, numObs);
    //    	int k = 0;
    //    	for (Observation o : aveObs.mObservations) {
    //    		Sx.format("% 4d:", k++);
    //    		Sx.putsArray(o.mS);
    //    	}

    Sx.puts(testLabel + " END");
    return 0;
    }
    
    public static void addAllObservations(Observer observers[], int numObs, ArrayFactory arrayFactory, int numDim)
    {
    	// Add fake observations of all nodes, including of this one by itself
    	for (int n = 0; n < numObs; n++) {
    		Observer obs = observers[n];
    		addObservations(obs, observers, numObs, arrayFactory, numDim);
    	}
    }
    
    public static void addObservations(Observer obs, Observer observers[], int numObs, ArrayFactory arrayFactory, int numDim)
    {
    	for (int j = 0; j < numObs; j++) {
    		// if (j == n)
    		//     continue;
    		Observer other = observers[j];
    		float[] randVec = arrayFactory.randomVectorInOriginSphere(numDim, 0.1f);
    		// Simulated obs of (other) nodes: their real pos + (different) noise
    		float[] pos = Observation.addS(other.mRealPos.mS, randVec);
    		obs.mObservations.put(other, new Observation(null, 1, 0, pos));
    	}
    }

    protected static int test_2()
    {
    	// ObserverA holds a position at 
    	return 0;
    }

    public static void main(String[] args) 
    {
        unit_test(100);
    }
    
}
