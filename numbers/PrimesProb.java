
import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;

public class Solution {

   public static void main(String[] args) {
      Scanner in = new Scanner(System.in);
      BigInteger n = in.nextBigInteger();
      in.close();
      // Probability of ≈ 97%
      System.out.println(n.isProbablePrime(5) ? "prime" : "not prime");
   }
}

