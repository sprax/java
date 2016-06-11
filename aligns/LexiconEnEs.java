package sprax.aligns;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;



import sprax.sprout.Spaces;
import sprax.sprout.Sx;
import sprax.files.TextFileReader;

public class LexiconEnEs extends Lexicon
{
    int     mDbgCount;            // TODO
    int     mNumBadDefs;
    int     mNumDuplicateTerms;
    int     mNumDefinedTerms;
    int     mNumDefinitions;    
    int     mNumDuplicateDefs;
    int     mNumPastTenseRefs;
    Pattern mPatternSrcDst  = Pattern.compile("[!?]*: *");
    Pattern mPatternSource  = Pattern.compile(", ");
    Pattern mPatternDestin  = Pattern.compile("; ");
    Pattern mPatternWords   = Pattern.compile(" +");    
    Pattern mPatternSrcWrds = Pattern.compile(",* +");    
    String  mFsBilinDictionary = "En/EnEsDict222.txt";
    String  mFsDictReWrite     = "En/EnEsDict444.txt";
    String  mFsDictOutput1     = "En/EnEsDict1.txt";
    String  mFsDictOutput2     = "En/EnEsDict2.txt";
    String  mFsDictOutput3     = "En/EnEsDict3.txt";
    
    WordSet mUnknownWords;
    
    /** Constructor ********************************************************/
    LexiconEnEs()
    {
        super("En", "Es");
        mTitle = "TranslationInSpanish101";
        ArrayList<String> inputs = null;
        if (sDbg < 3)
            inputs = TextFileReader.readFileIntoArrayList(mFsBilinDictionary);
        else
            inputs = new ArrayList<String>(Arrays.asList(mMockInput));
        
        mUnknownWords = new WordSet();
        
        int size = inputs.size();
        parseInput(inputs, size, 2);
        if (sDbg > 6) {
            reWriteInput(inputs, size, mFsDictReWrite);
            //        writeDictionary(mDict_1_1, mFsDictOutput1);
            //        writeDictionary(mDict_2_1, mFsDictOutput2);
            //        writeDictionary(mDict_3_1, mFsDictOutput3);
        }
    }
    
    
    /**
     * If the argument is a known past tense word,      * return the present tense form; 
     * otherwise, return null.
     */
    @Override
    String targetUnPast(String word) 
    {
        int lastIdx = word.length() - 1;
        if (lastIdx < 3)
            return null;
        char lastChar = word.charAt(lastIdx);
        if (lastChar == 'é') {
            int  punultIdx = lastIdx - 1;
            char penultChar = word.charAt(punultIdx);
            if (penultChar == 'r') {
                String sansE  = word.substring(0, lastIdx);
                String plusAR = sansE.concat("ar");
                if (isTargetWord(plusAR))
                    return plusAR;
            }
        } else if (word.endsWith("aba")) {
            int  punultIdx = lastIdx - 1;
            String sansBA = word.substring(0, punultIdx);
            String plusR = sansBA.concat("r");
            if (isTargetWord(plusR)) {
                return plusR;
            }
        }
        return null;
    }
    
    /**
     * If the argument is a known present participle, 
     * return the present tense form; 
     * otherwise, return null.
     */
    @Override
    String targetUnPresentParticiple(String word) 
    {
        int lastIdx = word.length() - 1;
        if (lastIdx < 5)
            return null;
        char lastChar = word.charAt(lastIdx);
        if (lastChar == 'o' && word.endsWith("ndo")) {
            String sansNDO = word.substring(0, lastIdx-2);
            String plusR = sansNDO.concat("r");
            if (isTargetWord(plusR))
                return plusR;
        }
        return null;
    }
    
    
    /**
     * Write cleaned-up, re-formatted input into a new file.
     * line breaks), etc.
     * @param inputs
     * @return
     */
    protected int reWriteInput(ArrayList<String> inputs, int sizeIn, String outFileSpec) 
    {
        int sizeOut = 0;
        try {
            // Create the file stream and its writer
            FileWriter fstream = new FileWriter(outFileSpec);
            BufferedWriter out = new BufferedWriter(fstream);
            for (int jSrc = 0; jSrc < sizeIn; jSrc++) {
                String line = inputs.get(jSrc);
                out.write(line.concat("\n"));
                sizeOut++;
            }
            // Close the output stream
            out.close();
        } catch (Exception e){//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
        return sizeOut;
    }    
    
    /**
     * Write cleaned-up, re-formatted input into a new file.
     * line breaks), etc.
     * @param inputs
     * @return
     */
    protected int writeDictionary(Map<String, Set<String>> dict, String outFileSpec)
    {
        int sizeOut = 0;
        try {
            // Create the file stream and its writer
            FileWriter fstream = new FileWriter(outFileSpec);
            BufferedWriter out = new BufferedWriter(fstream);
            for (Map.Entry<String, Set<String>> entry : dict.entrySet()) {
                String line = entryString(entry.getKey(), entry.getValue());
                out.write(line.concat("\n"));
                sizeOut++;
            }
            // Close the output stream
            out.close();
        } catch (Exception e){//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
        return sizeOut;
    }    
    
    
    
    String entryString(String key, Collection<String> defns)
    {
        String line = key;
        Iterator<String> defIter = defns.iterator();
        line = line.concat(": ").concat(defIter.next());
        while ( defIter.hasNext() ) {
            line = line.concat("; ").concat(defIter.next());
        }
        return line;
    }        
    
    protected void parseInput(List<String> inputs, int size, int verbose) 
    {
        mDict_1_1 = new TreeMap<String, Set<String>>();
        mDict_2_1 = new TreeMap<String, Set<String>>();
        mDict_3_1 = new TreeMap<String, Set<String>>();
        
        int totEntries = 0;
        for (int j = 0; j < size; j++) {
            String line = inputs.get(j);
            
            String[] entries = mPatternSrcDst.split(line);
            int numEntries = entries.length;
            if (numEntries < 2)
                continue;         // not a dictionary entry?
            
            int srcLen = entries[0].length();
            if (srcLen < 1)
                continue;
            int dstLen = entries[1].length();
            if (dstLen < 1)
                continue;
            
            String sources[] = mPatternSource.split(entries[0]);
            
            if (line.startsWith("marvel"))
                mDbgCount++;
            
            String definitions[] = null;
            ArrayList<String> singleDef = new ArrayList<String>(1);
            singleDef.add("");
            for (int kLast = numEntries-1, kTerm = 1; kTerm <= kLast; kTerm++) {
                definitions = mPatternDestin.split(entries[kTerm]);
                if (kTerm == kLast) {
                    parseEntry(sources, Arrays.asList(definitions), verbose);
                } else {
                    int numDef = definitions.length;  
                    int lastSpace = definitions[numDef-1].lastIndexOf(' ');
                    if (lastSpace > 0) {
                        String lastDef = definitions[numDef-1].substring(0, lastSpace);
                        String nextSrc = definitions[numDef-1].substring(lastSpace+1);
                        definitions[numDef-1] = lastDef;
                        parseEntry(sources, Arrays.asList(definitions), verbose);
                        sources = mPatternSource.split(nextSrc);
                    } else if (numDef > 1) {
                        parseEntry(sources, Arrays.asList(definitions).subList(0, numDef-1), verbose);
                        sources = mPatternSource.split(definitions[numDef-1]);
                    } else {
                        //                  String words[] = mPatternWords.split(entries[kTerm]);
                        //                  int numWords = words.length;
                        //                  if (numWords > 1) {
                        //                    String lastDef = definitions[numDef-1].substring(0, lastSpace);
                        //                    definitions[numDef-1] = lastDef;
                        //                    parseEntry(sources, Arrays.asList(definitions));
                        //                    sources = mPatternSource.split(words[numWords-1]);
                        //                  }
                        throw new IllegalStateException("Ill-formed entry: " + line);
                    }
                    
                    ////              , "abortifacient: abortivo abortion: aborto; fracaso abortionist: abortista"
                    
                    //            if (numDef > 1) { // last definition is next term to be defined
                    //               parseEntry(sources, Arrays.asList(definitions).subList(0, numDef-1));
                    //               sources = mPatternSource.split(definitions[numDef-1]);
                    //            } else {    // last word is next term to be defined
                }
            }
            
            //        int srcNum = sources.length;
            //        int defNum = definitions.length;
            //
            //        Sx.format("%2d  %2d  %2d  %2d  <%s>\n", numEntries, len, srcNum, defNum, line);
            //        for (int jSrc = 0; jSrc < srcNum; jSrc++)
            //          Sx.format("                <%s>\n", sources[jSrc]);
            //
            //        String padding = StringOfSpaces.get(srcLen + 18);
            //        //Sx.format("%s<%s>\n", padding, terms[1]);
            //        for (int jDst = 0; jDst < defNum; jDst++) {
            //          Sx.format("%s<%s>\n", padding, definitions[jDst]);
            //        }
            
            
            if (++totEntries > 999999)
                // if (++totEntries > 99999)
                break;
        }
        if (sDbg > 1) {
            Sx.format("tot %d   sizes 1, 2, 3:  %d  %d  %d\n"
                    , totEntries, mDict_1_1.size(), mDict_2_1.size(), mDict_3_1.size());
            
            Sx.format("%d Unknown Words . . . . . . . . . . . .\n", mUnknownWords.size());   
            for (String word : mUnknownWords.getCollector())
                Sx.puts(word);
        }
    }
    
    protected Map<String, Set<String>> getSubDictionary(int numWords, int numDefWords)
    {
        switch(numWords) {
            case 1: 
                return mDict_1_1;
            case 2: 
                return mDict_2_1;
            default:
                return mDict_3_1;
        }
    }
    
    protected void parseEntry(String phrases[], Collection<String> defns, int verbose) 
    {
        
        for (String phrase : phrases) {
            String words[] = mPatternWords.split(phrase);
            int numWords = words.length;
            Map<String, Set<String>> subDict = getSubDictionary(numWords, 0);
            
            boolean bDuplicateTerm = false;
            Set<String> defSet = subDict.get(phrase);
            if (defSet == null) {
                defSet = new HashSet<String>();
                mNumDefinedTerms++;
            } else {
                bDuplicateTerm = true;
                mNumDuplicateTerms++;
            }
            
            if (phrase.contains("flashpan")) {
                bDuplicateTerm = ! bDuplicateTerm;
                bDuplicateTerm = ! bDuplicateTerm;
            }
            
            // Single addition point for definitions
            int oldNumDefs = mNumDefinitions;
            for (String def : defns) {
                if (def.contains("pasado de")) {
                    String defWords[] = mPatternWords.split(def);
                    int numDefWords   = defWords.length;
                    String lastWord   = defWords[numDefWords - 1];
                    if (verbose > 2)
                        Sx.format("%3d  %12s  <  %s\n", mNumPastTenseRefs, lastWord, def);
                    mPastTenseToRoot.put(phrase, lastWord);
                    mNumPastTenseRefs++;
                } else {
                    boolean bAdded = defSet.add(def);
                    if (bAdded) {
                        //  Sx.format("%s %s\n", StringOfSpaces.get(phrase.length()), def);
                        mNumDefinitions++;
                    } else {
                        mNumDuplicateDefs++;
                    }
                }
            }
            
            if ( ! bDuplicateTerm)
                subDict.put(phrase, defSet);
            
            if (verbose > 2 && bDuplicateTerm) {
                int difNumDefs = mNumDefinitions - oldNumDefs;
                if (difNumDefs > 0) {
                    Sx.format("%3d Added %d new defs for phrase: %s\n", mNumDuplicateTerms, difNumDefs, phrase);
                } else {
                    Sx.format("%3d %d old defs contain all %d new defs for phrase: %s\n"
                            , mNumDuplicateTerms, defSet.size(), defSet.size(), phrase);
                }
                Sx.puts(entryString(phrase, defSet) + "\n");
            }
            
            
            if (sDbg > 0) {
                for (String word : words) {
                    if ( ! isSourceWord(word) ) {
                        mUnknownWords.addString(word);
                    }
                }
            }
            
            if (verbose > 2) {
                Sx.format( "%4d  <%s>%s%d / %d\n", mNumDefinedTerms, phrase
                        , Spaces.get(28 - phrase.length()), defns.size(), mNumDefinitions);
            }
            if (verbose > 2) {
                for (String def : defSet)
                    Sx.format("                <%s>\n", def);
            }       
        }
    }
    
    static final String mMockInput[] = { "A"
        , ""
        , "    a: un; una    "
        , "  "
        , "appointment: cita; nombramiento; puesto; appointments: muebles; mobiliario"
        , "luce: lucio; flower of luce: flor de lis"
        , "men: plural of man: hombres; humanos"
        , "lucent: claro; transparente lucern, lucerne: alfalfa lucid: lúcido"
        , "lucidity: lucidez"
        , "abortifacient: abortivo abortion: aborto; fracaso abortionist: abortista"
        , "aardvark: oso hormiguero"
        , "aardwolf: carnívoro sudafricano parecido "
        , "abaca: planta que crece en Filipinas cuyas fibras reciben el nombre de cáñamo de Manila"
        , "aback: atrás; hacia atrás abactinal: sin rayos abactor: ladrón de ganado abacus: ábaco"
        , "abaft: a popa; hacia popa"
        , "a la hiena"                                  // broken line
        , "aasvogel: buitre"                            
        , "sudafricano"                                 // broken line
        , "aba, abba, abaya: tela siria hecha de pelo de cabra o camello;"
        , "prenda exterior hecha de esta tela"          // broken line
        , "abaca: planta que crece en Filipinas"
        , "cuyas fibras reciben el"                     // broken line
        , "nombre de cáñamo de Manila"                  // broken line
        , "aback: atrás; hacia atrás "
        , "abactinal: sin rayos "
        , "abactor: ladrón de ganado "
        , "abacus: ábaco"
        , "abaft: a popa; hacia popa"
        , "abalone: oreja marina"
        , "abandon: abandono; naturalidad; desenvoltura; desenfreno; abandonar; entregarse"
        , "abandoned: abandonado; vicioso; depravado"
        , "abandonment: abandono; dejación; entrega de sí mismo; desamparo; desenfreno"
        , "abase: humillar; rebajar; envilecer"
        , "abasement: humillación; degradación; envilecimiento "
        , "abash: avergonzar; correr; confundir; desconcertar "
        , "abashed: avergonzado; confuso; desconcertado "
        , "abashment: vergüenza; confusión"
        , "abate: reducir; disminuir; rebajar; moderar; deducir; quitar; suprimir; anular; menguar; "
                + "amainar; ceder; remitir"
                , "abatement: disminución; mitigación; moderación; rebaja; deducción; supresión; anulación"
                , "abatis, abattis: muralla de árboles caídos con las ramas hacia afuera"
                , "abat-jour: pantalla o postigo "
                , "abattoir: matadero; picadero "
                , "abattu: deprimido"
                , "abature: rastro de un ciervo a través de la maleza"
                , "abat-voix: caja de resonancia"
                , "abaxial: lejos de los ejes"
                , "abb: hilo de la trama o de la urdimbre"
                , "abba: padre"
                , "abbacy: abadía; convento; monasterio"
                , "abbatial: abacial "
                , "abbé: abate "
                , "abbess: abadesa"
                , "abbey: abadía; convento; monasterio"
                , "abbot: abad "
                , "abbotship: abbacy "
                , "abbreviate: abreviar"
                , "abbreviation: abreviatura; abreviación"
                , ""
                , "www.TranslationInSpanish101.com"
                , " "
                , "abbreviature: compendio; resumen "
                , "abdicate: abdicar; renunciar "
                , "abdication: abdicación; renuncia "
                , "abdomen: abdomen"
                , "abdominal: abdominal "
                , "abdominous: panzudo "
                , "abduce: abduct "
                , "abducent: abductor"
                , "abduct: raptar; secuestrar; plagiar"
                , "abduction: secuestro; rapto; robo; plagio; abductor"
                , "abeam: por el través"
                , "abear: comportarse; tolerar; soportar"        
                , "amazing: asombroso; increíble "
                , "Amazon: Amazonas"
                , "Amazonian: amazónico"
                , "ambassador: embajador"
                , "amber: ámbar"
                , "ambergris: ámbar gris; sustancia que se encuentra flotando en algunos mares o en las vísceras del cachalote y que se usa en perfumería"
                , "ambiance: ambiente "
                , "ambidextrous: ambidiestro "
                , "ambience: ambiente "
                , "ambiguity: ambigüedad"
                , "answer: respuesta; contestador; solución; responder; contestar; abrir; corresponder a"
                , "answer back: contestar con impertinencia"
                , "answer for: responder de"
                , "answerable: responsable"
                , "answering machine: contestador automático"
                , "ant: hormiga"
                , "antacid: antiácido"
                , "arch: arco; bóveda; empeine; astuto; malicioso; arquear; formar un arco; fallen arches: pies planos"
                , "archaeological: arqueológico "
                , "bachelor's button: nombre del azulejo, la centaura y otras plantas"
                , "bacillus: bacilo"
                , "back: espalda; lomo; espinazo; dorso; envés; revés; trasera; fondo; respaldo; canto; parte convexa; defensa; "
                        + "foro; en minería, parte de una veta o filón que se encuentra más cerca de la superficie; posterior; dorsal; "
                        + "trasero; interior; apartado; atrasado; que vuelve; atrás; hacia atrás; de vuelta; de regreso; retroceder; girar; "
                        + "apoyar; sostener; respaldar; secundar; empujar; formar; montar; apostar"
                        , "back away: retroceder "
                        , "back down: ceder "
                        , "back off: retroceder"
                        , "back out: echarse atrás"
                        , "back up: apoyar; retrasar; dar marcha atrás; hacer una copia de seguridad; to get somebody's back up: picar a uno"
                        , "backache: dolor de espalda"
                        , "about: alrededor de; acerca de; junto a; por; en; encima; sobre; hacia; "
                        , "aproximadamente; casi; about time: ya es hora; about-face: media vuelta; "
                        , "to be all about: tratarse; to be about: estar a punto"
                        , "above: encima; sobre; arriba; antes; anterior; to be not above: ser capaz"
                        , "above-board: abiertamente; franco; honrado; sincero"
                        , "chuck: bistec; tirar; lanzar; golpear; chuck it!: déjalo; basta ya"
                        , "marvel: maravilla; prodigio; maravillarse; "
                        , "admirarse; preguntarse marvellous, marvelous: maravilloso; prodigioso; "
                                + "asombroso Marvel-of-Perú: dondiego de noch"
    };
    
    public static int test_parse(int dbgLevel) 
    {
        String  testName = LexiconEnEs.class.getName() + ".test_parse";
        Sx.puts(testName + " BEGIN");
        
        LexiconEnEs dict = new LexiconEnEs();
        Sx.format("\nCodes and title: %s  %s    %s\n", dict.getSrcCode(), dict.getDstCode(), dict.getTitle());
        
        Sx.puts(testName + " END");    
        return 0;
    }
    
    public static int unit_test(int level) 
    {
        String  testName = LexiconEnEs.class.getName() + ".unit_test";
        Sx.puts(testName + " BEGIN");
        
        //        String wordListFileA = "text/Es/spanishAcute.txt"; 
        //        String wordListFileB = "text/Es/EsWords.txt"; 
        //        compareWordListFiles(wordListFileA, wordListFileB);
        
        test_parse(2);
        SentencesAlignment.unit_test(3);
        
        Sx.puts(testName + " END");    
        return 0;
    }
    
    public static void main(String[] args) { unit_test(2); }
    
}
