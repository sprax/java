package sprax.vis;

import java.util.Comparator;

/*
 * TODO: class set:
 * interface:   ObservationInterface
 * catch-all:   Observation: indefinite dimensions, time, confidence, parent observer
 * abstract:    Ors - Observation relative to self
 * 2d static:   Ors2d - Ors with 2 spatial dimensions, no time, no confidence
 * 2d dynamic:  Ors2dt  - Ors2d plus time
 * 2d general:  Ors2dtc - Orst2dt plus confidence
 * 3d static:   Ors3d - "   "    3  "
 * etc...
 * 
 * @author sprax
 *
 */

public class Observation implements ObservationInterface
{
    Observer mSpotter;
  //Observer mSpotted;
    float    mC;      // confidence
    float    mT;      // time, units unspecified
    float    mS[];    // spatial coords, units unspecified

    public Observation(Observer o, float c, float t, float[] s) {
        mSpotter = o;
        mC = c;
        mT = t;
        mS = new float[s.length];
        for (int j = 0; j < s.length; j++)
            mS[j] = s[j];
    }
    
    Observation(Observation o) {			// copy constructor
    	this(o.mSpotter, o.mC, o.mT, o.mS);
    }

    
    public void addS(float[] s) {
    	for (int j = 0; j < s.length; j++) {
    		mS[j] += s[j];
    	}
    }    
    public void subS(float[] s) {
    	for (int j = 0; j < s.length; j++) {
    		mS[j] -= s[j];
    	}
    }
    /** return observation derived by subtracting A - B */
    public static Observation subtract(Observation oA, Observation oB) {
    	int len = oB.mS.length;
    	float dif[] = new float[len];
    	for (int j = 0; j < len; j++) {
    		dif[j] = oA.mS[j] - oB.mS[j];
    	}
    	float c = 0.5f * (oA.mC + oB.mC);
    	float t = 0.5f * (oA.mT + oB.mT);
    	return new Observation(null, c, t, dif);
    }
    
    
    public static float[] addS(float[] v0, float[] v1) {
    	float[] sum = new float[v1.length];
    	for (int j = 0; j < v1.length; j++) {
    		sum[j] = v0[j] + v1[0];
    	}
    	return sum;
    }

    @Override
    public float[] getS() {
        return mS;
    }

    @Override
    public float getT() {
        return mT;
    }

    @Override
    public float getC() {
        return mC;
    }

    @Override
    public Observer getO() {
        return mSpotter;
    }
    

}



class CompareByTime implements Comparator<Observation>
{
    
    @Override
    public int compare(Observation o1, Observation o2) {
        float timeDif = (o1.mT - o2.mT);
        if (timeDif < 0.0)
            return -1;
        else if (timeDif > 0.0)
            return 1; 
        return 0;    
    }
}