// https://stackoverflow.com/questions/24840667/what-is-the-regex-to-extract-all-the-emojis-from-a-string
//
    
Had a similar problem. The following served me well and matches surrogate pairs

public class SplitByUnicode {
    public static void main(String[] argv) throws Exception {
        String string = "Thats a nice joke 😆😆😆 😛";
        System.out.println("Original String:"+string);
        String regexPattern = "[\uD83C-\uDBFF\uDC00-\uDFFF]+";
        byte[] utf8 = string.getBytes("UTF-8");

        String string1 = new String(utf8, "UTF-8");

        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(string1);
        List<String> matchList = new ArrayList<String>();

        while (matcher.find()) {
            matchList.add(matcher.group());
        }

        for(int i=0;i<matchList.size();i++){
            System.out.println(i+":"+matchList.get(i));

        }
    }
}
Output is:

Original String:Thats a nice joke 😆😆😆 😛
0:😆😆😆
1:😛


ou can do it like this

    String s="Thats a nice joke 😆😆😆 😛";
    Pattern pattern = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                                      Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(s);
    List<String> matchList = new ArrayList<String>();

    while (matcher.find()) {
        matchList.add(matcher.group());
    }

    for(int i=0;i<matchList.size();i++){
        System.out.println(matchList.get(i));
    }


(?:[\u2700-\u27bf]|(?:\ud83c[\udde6-\uddff]){2}|[\ud800-\udbff][\udc00-\udfff]|[\u0023-\u0039]\ufe0f?\u20e3|\u3299|\u3297|\u303d|\u3030|\u24c2|\ud83c[\udd70-\udd71]|\ud83c[\udd7e-\udd7f]|\ud83c\udd8e|\ud83c[\udd91-\udd9a]|\ud83c[\udde6-\uddff]|[\ud83c[\ude01-\ude02]|\ud83c\ude1a|\ud83c\ude2f|[\ud83c[\ude32-\ude3a]|[\ud83c[\ude50-\ude51]|\u203c|\u2049|[\u25aa-\u25ab]|\u25b6|\u25c0|[\u25fb-\u25fe]|\u00a9|\u00ae|\u2122|\u2139|\ud83c\udc04|[\u2600-\u26FF]|\u2b05|\u2b06|\u2b07|\u2b1b|\u2b1c|\u2b50|\u2b55|\u231a|\u231b|\u2328|\u23cf|[\u23e9-\u23f3]|[\u23f8-\u23fa]|\ud83c\udccf|\u2934|\u2935|[\u2190-\u21ff])
It identifies many single-char emoji that the other answers do not account for. For more information about how this regex works, take a look at this post. https://medium.com/@thekevinscott/emojis-in-javascript-f693d0eb79fb#.enomgcu63

    
This worked for me in java 8:

public static String mysqlSafe(String input) {
  if (input == null) return null;
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < input.length(); i++) {
      if (i < (input.length() - 1)) { // Emojis are two characters long in java, e.g. a rocket emoji is "\uD83D\uDE80";
        if (Character.isSurrogatePair(input.charAt(i), input.charAt(i + 1))) {
          i += 1; //also skip the second character of the emoji
          continue;
        }
      }
      sb.append(input.charAt(i));
    }

  return sb.toString();
}

You may also use emoji4j library.

String emojiText = "A 🐱, 🐱 and a 🐭 became friends. For 🐶's birthday party, they all had 🍔s, 🍟s, 🍪s and 🍰.";

EmojiUtils.removeAllEmojis(emojiText);//returns "A ,  and a  became friends. For 's birthday party, they all had s, s, s and .



