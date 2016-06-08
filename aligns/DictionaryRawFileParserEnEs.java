package sprax.aligns;

import java.util.ArrayList;
import java.util.Arrays;

import sprax.Sx;
import sprax.files.FileUtil;
import sprax.files.TextFileReader;

/**
 * Inputs raw dictionary file and outputs cleaned-up dictionary file 
 * to be used as the input file for LexiconEnEs 
 * @author sprax
 *
 */
public class DictionaryRawFileParserEnEs extends LexiconEnEs
{
    
    /** Constructor ********************************************************/
    DictionaryRawFileParserEnEs()
    {
        super();
        mFsBilinDictionary = FileUtil.getTextFilePath("En/DictEngEsp101.txt");
        mFsDictReWrite     = FileUtil.getTextFilePath("En/EnEsDict222.txt");
        mTitle = "TranslationInSpanish101";
        ArrayList<String> inputs = null;
        if (sDbg < 3)
            inputs = TextFileReader.readFileIntoArrayList(mFsBilinDictionary);
        else
            inputs = new ArrayList<String>(Arrays.asList(mMockInput));
        
        mUnknownWords = new WordSet();
        
        int size = cleanupInput(inputs);
        size = separateEntries(inputs, size);
        parseInput(inputs, size, 2);
        if (sDbg > 1) {
            reWriteInput(inputs, size, mFsDictReWrite);
            writeDictionary(mDict_1_1, mFsDictOutput1);
            writeDictionary(mDict_2_1, mFsDictOutput2);
            writeDictionary(mDict_3_1, mFsDictOutput3);
        }
    }


    /**
     * Cleans up input by trimming and unfolding lines (removing arbitrary
     * line breaks), etc.
     * @param inputs
     * @return
     */
    static int cleanupInput(ArrayList<String> inputs) 
    {
        int sizeIn = inputs.size();
        int sizeOut = 0;
        boolean isPrevGood = false;
        for (int jSrc = 0; jSrc < sizeIn; jSrc++) {
            String line = inputs.get(jSrc).trim();
            int len = line.length();
            if (len < 4) {
                isPrevGood = false;
                continue;
            }
                        
            int idxTermSep = line.indexOf(sSepTermsDefns);
            int idxDefsSep = line.indexOf(sSepDefinitions);
            if (idxTermSep < 0 || 0 <= idxDefsSep && idxDefsSep < idxTermSep ) {
                if (isPrevGood) {
                    String prevLine = inputs.get(sizeOut-1);
                    line = prevLine.concat(" ").concat(line);
                    inputs.set(sizeOut-1, line);
                }
                continue;
            }
            inputs.set(sizeOut++, line);
            isPrevGood = true;
        }
        return sizeOut;
    }
    
    /**
     * Separates entries found on the same line, each marked by a colon
     */
    protected int separateEntries(ArrayList<String> inputs, int sizeIn) 
    {
        int last, sizeOut = 0;
        for (int jSrc = 0; jSrc < sizeIn; jSrc++) {
            String line = inputs.get(jSrc);                               
            String terms[] = mPatternSrcDst.split(line);
            int numTerms   = terms.length;
            switch(numTerms) {
                case 0:
                    throw new IllegalStateException("0 terms parsed from line: " + line);
                case 1:
                    throw new IllegalStateException("1 term parsed from line: " + line);
                case 2:
                    inputs.set(sizeOut, terms[0].trim().concat(": ").concat(terms[1].trim()));
                    sizeOut++;
                    break;
                default:
                    String word, wordStr = terms[0].trim();
                    for (int j = 1; j < numTerms-1; j++) {
                        String defns[]  = mPatternDestin.split(terms[j]);
                        int numDefns    = defns.length;
                        String words[]  = mPatternWords.split(defns[numDefns-1].trim());
                        int numWords    = words.length;
                        int idxWords    = numWords;
                        while (idxWords > 1) {
                            word = words[idxWords-1];
                            last = word.length() - 1;
                            if (word.charAt(last) == ',')
                                word = word.substring(0, last);
                            if (isSourceWord(word))
                                idxWords = idxWords-1;
                            else
                                break;
                        } 
                        // Try to detect English terms with no Spanish definitions...
                        if (idxWords == 1 && isSourceWord(words[0])) {
                            // If we got multiple definitions, the last one 
                            // might actually be nothing but another English
                            // term, even its first word.  That's OK.
                            // But 
                            if (numDefns > 1)
                                idxWords = 0;
                            else { // Only one definition and it appears to be
                                // all in English...
                                // This might be English defining or 
                                // clarifying English, which is not OK.
                                // But it could also be a Spanish word that is
                                // also an English word, as in a loan word,
                                // a bilingual (non-translated) name, 
                                // or a proper noun:
                                // amigo: amigo
                                // Emilio : Emilio (contra John: Juan or William: Guillermo)
                                // Corcovado: Corcovado (contra Germany: Alemania)
                                if ( ! isTargetWord(words[0]) ) {
                                    Sx.putsArray(mNumBadDefs + "   ", words);
                                    mNumBadDefs++;
                                }
                            }
                        }
                        // If we got multiple definitions, the last one might actually
                        // be nothing but another English term, even its first word.
                        if (idxWords == 1 && numDefns > 1 && isSourceWord(words[0]))
                            idxWords = 0;
                        if (idxWords < numWords) {
                            String defnStr;
                            if (numDefns > 1) {
                                defnStr = defns[0];
                                for (int k = 1; k < numDefns-1; k++)
                                    defnStr = defnStr.concat("; ").concat(defns[k]);
                                for (int k = 0; k < idxWords; k++)
                                    defnStr = defnStr.concat("; ").concat(words[k]);
                            } else {
                                defnStr = words[0];
                                for (int k = 1; k < idxWords; k++)
                                    defnStr = defnStr.concat("; ").concat(words[k]);
                            }
                            String entryStr = wordStr.concat(": ").concat(defnStr);
                            inputs.add(sizeOut, entryStr);
                            sizeOut++;
                            sizeIn++;
                            jSrc++;
                            wordStr = words[idxWords];
                            for (int k = idxWords+1; k < numWords; k++) {
                                wordStr = wordStr.concat(" ").concat(words[k]);
                            }
                        } else if (numWords == 1 && numDefns > 1) {
                            wordStr = words[0];
                        } else {
                            mDbgCount++;
                            word = words[numWords-1];
                            if (word.equals("maravilloso"))
                                mDbgCount++;
                            Sx.format("%4d    %s\n", mDbgCount, word);
                        }
                    }
                    String lastEntryStr = wordStr.concat(": ").concat(terms[numTerms-1]);
                    inputs.set(sizeOut, lastEntryStr);
                    sizeOut++;
                    break;
            }
        }
        return sizeOut;
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
        String  testName = DictionaryRawFileParserEnEs.class.getName() + ".test_parse";
        Sx.puts(testName + " BEGIN");
        
        DictionaryRawFileParserEnEs dict = new DictionaryRawFileParserEnEs();
        Sx.format("\nCodes and title: %s  %s    %s\n", dict.getSrcCode(), dict.getDstCode(), dict.getTitle());
                
        Sx.puts(testName + " END");    
        return 0;
    }
    
    public static int unit_test(int level) 
    {
        String  testName = DictionaryRawFileParserEnEs.class.getName() + ".unit_test";
        Sx.puts(testName + " BEGIN");
        
//        String wordListFileA = FileUtil.getTextFilePath("Es/spanishAcute.txt"); 
//        String wordListFileB = FileUtil.getTextFilePath("Es/EsWords.txt"); 
//        compareWordListFiles(wordListFileA, wordListFileB);
  
        test_parse(2);

        Sx.puts(testName + " END");    
        return 0;
    }
    
    public static void main(String[] args) { unit_test(2); }
    
}
