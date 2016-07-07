package sprax.vis;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/*
 * TODO: new hierarchy:
 * 	StaticObservation (no time), DynamicObservation (record time)
 *  SelfObserver, OtherObserver, Observer has both
 * 
 * How to update auto-pos and observations of others:
 * A)  If positions of others were observed relative to self,
 *     as in using a laser range finder and compass, and
 *  1) then transformed into a global coordinate system, then after 
 *     updating auto-pos, an observer should also apply the same 
 *     transformation to its observations (in that coordinate global system).
 *     [This may tend to preserve measurement errors!]
 *  2) If those positions were kept in an observer-based local coord
 *     system, then they are still valid and should not be transformed.
 * B)  If the positions of others were observed in a global coord system,
 *     as in being registered against a known grid or using GPS, 
 *  1) and kept in that coord system, then correcting the observer's 
 *     position should not invalidate those observations of others. 
 *  2)* If they were observed in a global coord system, the positions of
 *     others should be left that way, not transformed into a local system.
 *     But if they were so transformed, then correcting their observer's 
 *     position should also trigger the corresponding (inverse) transform
 *     for the observed entities' positions.
 * 
 * TODO: Move "god's" observations to a "god" observer?
 * TODO: Model observed and/or real bias?
 * @author sprax
 *
 */

/** Social observer of point observations */
public class Observer 
{
	/** Latest observations of other observers */
	Map<Observer, Observation> 	mObservations = new HashMap<Observer, Observation>();
	
 	/** Latest self observation/estimates of this observer's own position */
    Observation 	mAutoPos;

 	/** Latest position as computed from its observations by others */
    Observation 	mSeenPos;
    
    /** Real position, as if observed by "god".  TODO: eliminate this. */
    Observation 	mRealPos;

    Set<Observer>   mObserversThatIHaveSeen;
    Set<Observer>   mObserversThatHaveSeenMe;
    
    Observer() {}					// default constructor

    public Observation meanObservationOfSeen()
    {
		float c = 0, t = 0, x = 0, y = 0;
		
		int numSeen = mObservations.size();
		for (Observation o : mObservations.values()) {
			c += o.mC;
			t += o.mT;
			x += o.mS[0];
			y += o.mS[1];
		}
		c /= numSeen;
		t /= numSeen; 
		float[] s = { x/numSeen, y/numSeen };            
		return new Observation(null, c, t, s);
	}

    public Observation meanSelfObservationOfSeen()
    {
		float c = 0, t = 0, x = 0, y = 0;
		
		int numSeen = mObservations.size();
		for (Observer o : mObservations.keySet()) {
			c += o.mAutoPos.mC;
			t += o.mAutoPos.mT;
			x += o.mAutoPos.mS[0];
			y += o.mAutoPos.mS[1];
		}
		c /= numSeen;
		t /= numSeen; 
		float[] s = { x/numSeen, y/numSeen };            
		return new Observation(null, c, t, s);
	}


    public Observation estDifFromMeanPos()
    {
		Observation meanOthers = meanObservationOfSeen();
		Observation othersMean = meanSelfObservationOfSeen();
		Observation diff       = Observation.subtract(othersMean, meanOthers);
		return diff;
	}
}
