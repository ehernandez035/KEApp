package es.ehu.ehernandez035.kea;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.TokenStream;

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

        ANTLRErrorListener listener = new MyErrorListener(erroreak);

        CharStream input = CharStreams.fromString(progText);
        WhileLexer lexer = new WhileLexer(input);

        lexer.addErrorListener(listener);

        TokenStream tokens = new CommonTokenStream(lexer);
        WhileParser parser = new WhileParser(tokens);
        parser.addErrorListener(listener);

        if (parser.getNumberOfSyntaxErrors() > 0) {
            return "";
        }

        WhileParser.ProgContext prog = parser.prog();

        if (!erroreak.isEmpty()) {
            return "";
        }

        MyWhileVisitor visitor = new MyWhileVisitor();
        Programa programa = (Programa) visitor.visitProg(prog);

        if (!erroreak.isEmpty()) {
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

    public static String exekutatuMakro(String progText, List<Character> alfabetoa, List<String> parametroak, List<Errorea> erroreak) {
        if (progText.trim().length() == 0) {
            erroreak.add(new Errorea(new Posizioa(0, 0, 0, 0), "Programa hutsa da"));
            return "";
        }
        CharStream input = CharStreams.fromString(progText);
        MakroprogramaLexer lexer = new MakroprogramaLexer(input);

        MyErrorListener listener = new MyErrorListener(erroreak);
        lexer.addErrorListener(listener);

        TokenStream tokens = new CommonTokenStream(lexer);
        MakroprogramaParser parser = new MakroprogramaParser(tokens);
        parser.addErrorListener(listener);

        if (parser.getNumberOfSyntaxErrors() > 0) {
            return "";
        }

        MakroprogramaParser.ProgContext prog = parser.prog();

        if (!erroreak.isEmpty()) {
            return "";
        }

        MyMakroVisitor visitor = new MyMakroVisitor();
        Programa programa = (Programa) visitor.visitProg(prog);

        if (!erroreak.isEmpty()) {
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

    static class MyErrorListener extends BaseErrorListener {
        private final List<Errorea> erroreLista;

        public MyErrorListener(List<Errorea> erroreLista) {
            this.erroreLista = erroreLista;
        }

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
            erroreLista.add(new Errorea(new Posizioa(line, line, charPositionInLine, charPositionInLine), "Sintaxi errorea"));
        }
    }
}
