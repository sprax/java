package sprax.aligns;

public class RunDiffs
{    
    public static float df(char[] sA, char[] sB, int len, int t0, int t1, int t2, float f0, float f1, float f2)
    {
        int distance = 0;
        int difPrv = 0, difNow, difNowAbs, difNowPrv, difNowPrvAbs;
        for (int j = 0; j < len; j++) {
            difNow = sA[j] - sB[j];
            difNowAbs    = difNow < 0 ? -difNow : difNow;
            difNowPrv    = difNow - difPrv;
            difNowPrvAbs = difNowPrv < 0 ? -difNowPrv : difNowPrv;
            
            if (difNowPrvAbs <= t0)
                distance += difNowAbs * f0;
            else if (difNowPrvAbs <= t1)
                distance += difNowAbs * f1;
            else if (difNowPrvAbs <= t2)
                distance += difNowAbs * f2;
            else
                distance += difNowAbs;
            
            difPrv = difNow;
        }
        return distance;
    }
}
