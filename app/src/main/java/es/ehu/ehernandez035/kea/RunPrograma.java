package es.ehu.ehernandez035.kea;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

import es.ehu.ikasle.ehernandez035.gramatika.WhileLexer;
import es.ehu.ikasle.ehernandez035.gramatika.WhileParser;
import es.ehu.ikasle.ehernandez035.gramatika.makro.MakroprogramaLexer;
import es.ehu.ikasle.ehernandez035.gramatika.makro.MakroprogramaParser;
import es.ehu.ikasle.ehernandez035.makroprograma.MyMakroVisitor;
import es.ehu.ikasle.ehernandez035.makroprograma.SZA.Errorea;
import es.ehu.ikasle.ehernandez035.makroprograma.SZA.Liburutegia;
import es.ehu.ikasle.ehernandez035.makroprograma.SZA.Posizioa;
import es.ehu.ikasle.ehernandez035.makroprograma.SZA.Programa;
import es.ehu.ikasle.ehernandez035.makroprograma.SZA.SinboloTaula;
import es.ehu.ikasle.ehernandez035.whileprograma.MyWhileVisitor;

public class RunPrograma {

    public static String exekutatuWhile(String progText, List<Character> alfabetoa, List<String> parametroak, List<Errorea> erroreak) {
        if (progText.trim().length() == 0) {
            erroreak.add(new Errorea(new Posizioa(0, 0, 0, 0), "Programa hutsa da"));
            return "";
        }
        CharStream input = CharStreams.fromString(progText);
        WhileLexer lexer = new WhileLexer(input);

        TokenStream tokens = new CommonTokenStream(lexer);
        WhileParser parser = new WhileParser(tokens);
        ANTLRErrorListener listener = new ANTLRErrorListener() {
            // List<Whatever> errors;
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                erroreak.add(new Errorea(new Posizioa(line, line, charPositionInLine, charPositionInLine), "Sintaxi errorea"));
            }

            @Override
            public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact, BitSet ambigAlts, ATNConfigSet configs) {

            }

            @Override
            public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex, BitSet conflictingAlts, ATNConfigSet configs) {

            }

            @Override
            public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction, ATNConfigSet configs) {

            }
        };
        parser.addErrorListener(listener);

        if (parser.getNumberOfSyntaxErrors() > 0) {
            return "";
        }

        WhileParser.ProgContext prog = parser.prog();

        MyWhileVisitor visitor = new MyWhileVisitor();
        es.ehu.ikasle.ehernandez035.whileprograma.Programa programa = (es.ehu.ikasle.ehernandez035.whileprograma.Programa) visitor.visitProg(prog);

        if (parser.getNumberOfSyntaxErrors() > 0) {
            return "";
        }

        // TODO Implement verify
//        programa.verify(st, erroreak);


//        programa.verifyAlf(st, erroreak);

        List<String> parametroakKopia = new ArrayList<>();
        parametroakKopia.add(""); // X0
        parametroakKopia.addAll(parametroak);

        if (!erroreak.isEmpty()) {
            return "";
        } else {
            return programa.execute(parametroakKopia);
        }
    }

    public static String exekutatuMakro(String progText, List<Character> alfabetoa, List<String> parametroak, List<Errorea> erroreak) {
        if (progText.trim().length() == 0) {
            erroreak.add(new Errorea(new Posizioa(0, 0, 0, 0), "Programa hutsa da"));
            return "";
        }
        CharStream input = CharStreams.fromString(progText);
        MakroprogramaLexer lexer = new MakroprogramaLexer(input);

        TokenStream tokens = new CommonTokenStream(lexer);
        MakroprogramaParser parser = new MakroprogramaParser(tokens);
        ANTLRErrorListener listener = new ANTLRErrorListener() {
            // List<Whatever> errors;
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                erroreak.add(new Errorea(new Posizioa(line, line, charPositionInLine, charPositionInLine), "Sintaxi errorea"));
            }

            @Override
            public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact, BitSet ambigAlts, ATNConfigSet configs) {

            }

            @Override
            public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex, BitSet conflictingAlts, ATNConfigSet configs) {

            }

            @Override
            public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction, ATNConfigSet configs) {

            }
        };
        parser.addErrorListener(listener);

        if (parser.getNumberOfSyntaxErrors() > 0) {
            return "";
        }

        MakroprogramaParser.ProgContext prog = parser.prog();

        MyMakroVisitor visitor = new MyMakroVisitor();
        Programa programa = (Programa) visitor.visitProg(prog);

        if (parser.getNumberOfSyntaxErrors() > 0) {
            return "";
        }

        SinboloTaula st = new SinboloTaula(alfabetoa);
        for (int i = 0; i < parametroak.size(); i++) {
            st.gordeAldagaia("X" + Integer.toString(i + 1), parametroak.get(i));
        }
        Liburutegia.gehituFuntzioak(st);
        programa.verify(st, erroreak);


        programa.verifyAlf(st, erroreak);

        if (!erroreak.isEmpty()) {
            return "";
        } else {
            return programa.execute(st);
        }
    }
}
