package sprax.questions;


/**********************************************
AI has a lot of problems that involve searches.In this track you will learn most of the search techniques used in AI.

In this game, PacMan is positioned in a grid.PacMan has to find the food using Depth First Search(DFS).Assume the grid is completely observable, perform a DFS on the grid and then print the path obtained by DFS from the PacMan to the food.

Input Format

The first line contains 2 space separated integers which is the position of the PacMan.
The second line contains 2 space separated integers which is the position of the food.
The third line of the input contains 2 space separated integers indicating the size of the rows and columns respectively.The largest grid size is 30x30.

This is followed by row(r) lines each containing column(c) characters.
A wall is represented by the character '%' (ascii value 37), 
PacMan is represented by UpperCase alphabet 'P' (ascii value 80), 
empty spaces which can be used by PacMan for movement is represented by the character '-' (ascii value 45)
and food is represented by the character '.' (ascii value 46)

You have to mark the nodes explored while populating it into the stack and not when its expanded.

Note
+ The grid is indexed as per matrix convention
+ The evaluation process follows iterative - DFS and not recursive - DFS.

Populating Stack

In order to maintain uniformity across submissions, please follow the below mentioned order in pushing nodes to stack.
If a node has all the 4 adjacent neighbors.  Then,

UP is inserted first into the stack, followed by LEFT, followed by RIGHT and then by DOWN.

so, if (1, 1) has all its neighbors not visited, (0, 1), (1, 0), (1, 2), (2, 1) then,

(0, 1) - UP is inserted first
(1, 0) - LEFT is inserted second
(1, 2) - RIGHT is inserted third
(2, 1) - DOWN is inserted fourth(on top)
So, (2, 1) is the first to be popped from the stack.

Constraints

1 <= r, c <= 40

Output Format

Each cell in the grid is represented by its position in the grid(r, c).
PacMan can move only UP, DOWN, LEFT or RIGHT.
Your task is to print all the nodes that you encounter while searching the DFS tree.
While populating the stack, the following convention must be followed.

% 
%--
 -
In the above cell, LEFT and UP are invalid moves.You can either go RIGHT or DOWN.RIGHT is pushed first followed by DOWN.

Print the number of nodes explored.

A node is marked explored only when the node is popped out of the stack.
 (E)in the first line.Starting from the source node, 'P' (including it), 
 print all the nodes(r, c) expanded using DFS each node in a new line(r, c) until the food node is explored.

E 
r c 
r1 c1 
....

Then, print the distance 'D' between the source 'P' and the destination '.' 
calculated using DFS.

D + 1 lines follow, each line having a node encountered between 'P' and '.' both included.
D + 1 lines essentially representing the path between source and the destination.

Sample Input

3 9
5 1  
7 20 
%%%%%%%%%%%%%%%%%%%% 
%--------------%---%  
%-%%-%%-%%-%%-%%-%-%  
%--------P-------%-% 
%%%%%%%%%%%%%%%%%%-%  
%.-----------------%  
%%%%%%%%%%%%%%%%%%%%

Game 2:
25 13
3 1
27 28
%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%------------%%------------%
%-%%%%-%%%%%-%%-%%%%%-%%%%-%
%.%%%%-%%%%%-%%-%%%%%-%%%%-%
%-%%%%-%%%%%-%%-%%%%%-%%%%-%
%--------------------------%
%-%%%%-%%-%%%%%%%%-%%-%%%%-%
%-%%%%-%%-%%%%%%%%-%%-%%%%-%
%------%%----%%----%%------%
%%%%%%-%%%%%-%%-%%%%%-%%%%%%
%%%%%%-%%%%%-%%-%%%%%-%%%%%%
%%%%%%-%------------%-%%%%%%
%%%%%%-%-%%%%--%%%%-%-%%%%%%
%--------%--------%--------%
%%%%%%-%-%%%%%%%%%%-%-%%%%%%
%%%%%%-%------------%-%%%%%%
%%%%%%-%-%%%%%%%%%%-%-%%%%%%
%------------%%------------%
%-%%%%-%%%%%-%%-%%%%%-%%%%-%
%-%%%%-%%%%%-%%-%%%%%-%%%%-%
%---%%----------------%%---%
%%%-%%-%%-%%%%%%%%-%%-%%-%%%
%%%-%%-%%-%%%%%%%%-%%-%%-%%%
%------%%----%%----%%------%
%-%%%%%%%%%%-%%-%%%%%%%%%%-%
%------------P-------------%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%

Game 3:
25 13
3 1
27 28
%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%------------%%------------%
%-%%%%-%%%%%-%%-%%%%%-%%%%-%
%.%%%%-%%%%%-%%-%%%%%-%%%%-%
%-%%%%-%%%%%-%%-%%%%%-%%%%-%
%--------------------------%
%-%%%%-%%-%%%%%%%%-%%-%%%%-%
%-%%%%-%%-%%%%%%%%-%%-%%%%-%
%------%%----%%----%%------%
%%%%%%-%%%%%-%%-%%%%%-%%%%%%
%%%%%%-%%%%%-%%-%%%%%-%%%%%%
%%%%%%-%------------%-%%%%%%
%%%%%%-%-%%%%--%%%%-%-%%%%%%
%--------%--------%--------%
%%%%%%-%-%%%%%%%%%%-%-%%%%%%
%%%%%%-%------------%-%%%%%%
%%%%%%-%-%%%%%%%%%%-%-%%%%%%
%------------%%------------%
%-%%%%-%%%%%-%%-%%%%%-%%%%-%
%-%%%%-%%%%%-%%-%%%%%-%%%%-%
%---%%----------------%%---%
%%%-%%-%%-%%%%%%%%-%%-%%-%%%
%%%-%%-%%-%%%%%%%%-%%-%%-%%%
%------%%----%%----%%------%
%-%%%%%%%%%%-%%-%%%%%%%%%%-%
%------------P-------------%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%


Sample Output

In this example, PacMan is at the position(3, 9) and the food is at the position(5, 1).
DFS tree is printed starting from(3, 9) until the food node is expanded.
The DFS path length between(3, 9) and(5, 1) is 32. 
All the nodes encountered between(3, 9) and(5, 1)
both included is printed in the next 33 lines.
*******************************************************/

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Stack;

public class LeastGreaterString 
{    
    static void leastGreaterStringStdin()
    {
        try (Scanner in = new Scanner(System.in)) 
        {    
            int numCases = in.nextInt();
            String cases[] = new String[numCases];
            for (int j = 0; j < numCases; j++) {
            	cases[j] = in.next();
            }
            leastGreaterStringArray(cases);
        }
    }
    
	static void leastGreaterStringArray(String stray[])
	{
		for (String strin : stray) {
			String stout = leastGreaterString(strin);
			System.out.println(stout);
		}
	}

	public static String leastGreaterString(String strin) {
		String stout = "no answer";
		if (strin != null || strin.length() > 1) {
			int length = strin.length();
			int maxIdx = length - 1;
			int maxVal = strin.charAt(maxIdx);
			char chray[] = strin.toCharArray();
			for (int idx = maxIdx; --idx >= 0; ) {
				char val = chray[idx];
				if (val < maxVal) {
					// Now we know that swapping val and maxVal (the characters at idx and maxIdx)
					// would make a lexicographically greater string, but we want the *least* one 
					// greater.  So find the index of the least char in the tail greater than val.
					// We can re-use maxIdx and maxVal for this, since we won't need them anymore;
					// we'll return directly out of this loop.
					int rstIdx = idx;
					while (++rstIdx < length) {
						char rstVal = chray[rstIdx];
						if (val < rstVal && rstVal < maxVal) {
							maxIdx = rstIdx;
							maxVal = rstVal;
						}
					}
					arraySwap(chray, idx, maxIdx);
					Arrays.sort(chray, idx+1, length);
					return new String(chray);
				} else if (maxVal < val) {
					maxVal = val;
					maxIdx = idx;
				}
			}
		}
		return stout;
	}
	

	private static void arraySwap(char[] arr, int beg, int end) 
	{
		char tmp = arr[beg];
		arr[beg] = arr[end];
		arr[end] = tmp;
	}
	
	public static void unit_test()
	{
		String stray[] = { "ab", "bb", "hefg", "dhck", "dkhc" };
		leastGreaterStringArray(stray);
	}

    public static void main(String[] args) {
    	//leastGreaterStringStdin();
    	unit_test();
    }
}

/*	*******************************************************************
Input:
100
imllmmcslslkyoegymoa
fvincndjrurfh
rtglgzzqxnuflitnlyit
mhtvaqofxtyrz
zalqxykemvzzgaka
wjjulziszbqqdcpdnhdo
japjbvjlxzkgietkm
jqczvgqywydkunmjw
ehdegnmorgafrjxvksc
tydwixlwghlmqo
wddnwjneaxbwhwamr
pnimbesirfbivxl
mijamkzpiiniveik
qxtwpdpwexuej
qtcshorwyck
xoojiggdcyjrupr
vcjmvngcdyabcmjz
xildrrhpca
rrcntnbqchsfhvijh
kmotatmrabtcomu
bnfcejmyotvw
dnppdkpywiaxddoqx
tmowsxkrodmkkra
jfkaehlegohwggf
ttylsiegnttymtyx
kyetllczuyibdkwyihrq
xdhqbvlbtmmtshefjf
kpdpzzohihzwgdfzgb
kuywptftapaa
qfqpegznnyludrv
ufwogufbzaboaepslikq
jfejqapjvbdcxtkry
sypjbvatgidyxodd
wdpfyqjcpcn
baabpjckkytudr
uvwurzjyzbhcqmrypraq
kvtwtmqygksbim
ivsjycnooeodwpt
zqyxjnnitzawipqsm
blmrzavodtfzyepz
bmqlhqndacv
phvauobwkrcfwdecsd
vpygyqubqywkndhpzw
yikanhdrjxw
vnpblfxmvwkflqobrk
pserilwzxwyorldsxksl
qymbqaehnyzhfqpqprpl
fcakwzuqlzglnibqmkd
jkscckttaeifiksgkmxx
dkbllravwnhhfjjrce
imzsyrykfvjt
tvogoocldlukwfcajvix
cvnagtypozljpragvlj
hwcmacxvmus
rhrzcpprqccf
clppxvwtaktchqrdif
qwusnlldnolhq
yitveovrja
arciyxaxtvmfgquwb
pzbxvxdjuuvuv
nxfowilpdxwlpt
swzsaynxbytytqtq
qyrogefleeyt
iotjgthvslvmjpcchhuf
knfpyjtzfq
tmtbfayantmwk
asxwzygngwn
rmwiwrurubt
bhmpfwhgqfcqfldlhs
yhbidtewpgp
jwwbeuiklpodvzii
anjhprmkwibe
lpwhqaebmr
dunecynelymcpyonjq
hblfldireuivzekegit
uryygzpwifrricwvge
kzuhaysegaxtwqtvx
kvarmrbpoxxujhvgpw
hanhaggqzdpunkugzmhq
gnwqwsylqeuqr
qzkjbnyvclrkmdtc
argsnaqbquv
obbnlkoaklcx
ojiilqieycsasvqosycu
qhlgiwsmtxbffjsxt
vvrvnmndeogyp
ibeqzyeuvfzb
sajpyegttujxyx
zmdjphzogfldlkgbchnt
tbanvjmwirxx
gmdhdlmopzyvddeqyjja
yxvmvedubzcpd
soygdzhbckfuk
gkbekyrhcwc
wevzqpnqwtpfu
rbobquotbysufwqjeo
bpgqfwoyntuhkvwo
schtabphairewhfmp
rlmrahlisggguykeu
fjtfrmlqvsekq

Expected Output:

imllmmcslslkyoegyoam
fvincndjrurhf
rtglgzzqxnuflitnlyti
mhtvaqofxtyzr
zalqxykemvzzgkaa
wjjulziszbqqdcpdnhod
japjbvjlxzkgietmk
jqczvgqywydkunmwj
ehdegnmorgafrjxvsck
tydwixlwghlomq
wddnwjneaxbwhwarm
pnimbesirfbixlv
mijamkzpiiniveki
qxtwpdpwexuje
qtcshorwykc
xoojiggdcyjrurp
vcjmvngcdyabcmzj
xildrrpach
rrcntnbqchsfhvjhi
kmotatmrabtcoum
bnfcejmyotwv
dnppdkpywiaxddoxq
tmowsxkrodmkrak
jfkaehlegowfggh
ttylsiegnttymxty
kyetllczuyibdkwyiqhr
xdhqbvlbtmmtshejff
kpdpzzohihzwgdgbfz
kuywptftpaaa
qfqpegznnyludvr
ufwogufbzaboaepsliqk
jfejqapjvbdcxtkyr
sypjbvatgiodddxy
wdpfyqjcpnc
baabpjckkyturd
uvwurzjyzbhcqmryprqa
kvtwtmqygksbmi
ivsjycnooeodwtp
zqyxjnnitzawipsmq
blmrzavodtfzyezp
bmqlhqndavc
phvauobwkrcfwdedcs
vpygyqubqywkndhwpz
yikanhdrwjx
vnpblfxmvwkflqokbr
pserilwzxwyorldsxlks
qymbqaehnyzhfqpqrlpp
fcakwzuqlzglnidbkmq
jkscckttaeifiksgkxmx
dkbllravwnhhfjjrec
imzsyrykfvtj
tvogoocldlukwfcajvxi
cvnagtypozljprajglv
hwcmacxvsmu
rhrzcpprqcfc
clppxvwtaktchqrfdi
qwusnlldnolqh
yitverajov
arciyxaxtvmfgqwbu
pzbxvxdjuuvvu
nxfowilpdxwltp
swzsaynxbytyttqq
qyrogefletey
iotjgthvslvmjpcchufh
knfpyjtzqf
tmtbfayantwkm
asxwzygnngw
rmwiwrurutb
bhmpfwhgqfcqfldlsh
yhbidtewppg
jwwbeuiklpodziiv
anjhprmkwieb
lpwhqaebrm
dunecynelymcpyonqj
hblfldireuivzekegti
uryygzpwifrriecgvw
kzuhaysegaxtwqtxv
kvarmrbpoxxujhvgwp
hanhaggqzdpunkugzmqh
gnwqwsylqeurq
qzkjbnyvclrkmtcd
argsnaqbqvu
obbnlkoaklxc
ojiilqieycsasvqosyuc
qhlgiwsmtxbffjtsx
vvrvnmndeopgy
ibeqzyeuvzbf
sajpyegttujyxx
zmdjphzogfldlkgbchtn
tbanvjmwixrx
gmdhdlmopzyvddeyajjq
yxvmvedubzdcp
soygdzhbckkfu
gkbekyrhwcc
wevzqpnqwtpuf
rbobquotbysufwqjoe
bpgqfwoyntuhkwov
schtabphairewhfpm
rlmrahlisggguykue
fjtfrmlqvseqk
*************************************************************/

