package learning;


import com.acuo.algo.MapConverter;
import com.acuo.algo.Renjin;
import org.junit.Test;
import org.renjin.primitives.vector.RowNamesVector;
import org.renjin.repackaged.guava.collect.ImmutableList;
import org.renjin.repackaged.guava.collect.ImmutableMap;
import org.renjin.script.RenjinScriptEngineFactory;
import org.renjin.sexp.DoubleArrayVector;
import org.renjin.sexp.DoubleVector;
import org.renjin.sexp.ListVector;
import org.renjin.sexp.SEXP;
import org.renjin.sexp.StringArrayVector;
import org.renjin.sexp.Symbols;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.InputStreamReader;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class RenjinGradleTest {

    private Renjin renjin = new Renjin();

    @Test
    public void test() throws ScriptException {
        RenjinScriptEngineFactory factory = new RenjinScriptEngineFactory();
        // create a Renjin engine:
        ScriptEngine engine = factory.getScriptEngine();

        // Sanity check
        DoubleVector result = (DoubleVector) engine.eval("1+1");
        assertThat(result.length()).isEqualTo(1);
        assertThat(result.getElementAsDouble(0)).isEqualTo(2.0);

        // Produce some output
        engine.eval("print(rnorm(10))");
    }

    @Test
    public void testExcecutionOfRCode() throws ScriptException {
        // create a script engine manager
        ScriptEngineManager factory = new ScriptEngineManager();

        // create an R engine
        ScriptEngine engine = factory.getEngineByName("Renjin");

        // evaluate R code from String
        engine.eval("print('Hello, World')");

        // evaluate R script from classpath
        engine.eval(new InputStreamReader(RenjinGradleTest.class.getResourceAsStream("/test.r")));
    }

    @Test
    public void testPutDataFrameFromListOfObject() throws ScriptException {
        StringArrayVector.Builder id = new StringArrayVector.Builder();
        StringArrayVector.Builder country = new StringArrayVector.Builder();
        DoubleArrayVector.Builder revenue = new DoubleArrayVector.Builder();
        List<Record> records = ImmutableList.of(new Record("1", "FR", 2d));
        for(Record record : records) {
            id.add(record.getId());
            country.add(record.getCountry());
            revenue.add(record.getRevenue());
        }

        ListVector.NamedBuilder myDf = new ListVector.NamedBuilder();
        myDf.setAttribute(Symbols.CLASS, new StringArrayVector("data.frame"));
        myDf.setAttribute(Symbols.ROW_NAMES, new RowNamesVector(records.size()));
        myDf.add("id", id.build());
        myDf.add("country", country.build());
        myDf.add("revenue", revenue.build());
        renjin.put("callInfo", myDf.build());
        renjin.eval("print(callInfo)");
    }

    @Test
    public void testMapConverter() throws ScriptException {
        SEXP test = new MapConverter().convertToR(ImmutableMap.of("callId", ImmutableList.of(1 , 2, 3), "currency", ImmutableList.of("USD" , "EUR", "GBP")), 3);
        renjin.put("test", test);
        renjin.eval("print(test)");
    }

    @Test
    public void testLpSolveAPI() throws ScriptException {
        renjin.eval("import(lpsolve.LpSolve)");
        renjin.eval("my.lp <- LpSolve$makeLp(0L, 4L)");
        renjin.eval("my.lp$setVerbose(1L)");
        renjin.eval("my.lp$strAddConstraint(\"3 2 2 1\", 1L, 4)");
        renjin.eval("my.lp$strAddConstraint(\"0 4 3 1\", 2L, 3)");
        renjin.eval("my.lp$strSetObjFn(\"2 3 -2 3\")");
        renjin.eval("my.lp$solve()");
        renjin.eval("obj <- my.lp$getObjective()");
        SEXP res = (SEXP) renjin.eval("res <- my.lp$getPtrVariables()");
        renjin.eval("my.lp$deleteLp()");
        assertThat(res).isNotNull();
    }

    class Record {

        private final String id;
        private final String country;
        private final Double revenue;

        Record(String id, String country, Double revenue) {
            this.id = id;
            this.country = country;
            this.revenue = revenue;
        }

      public String getId() {
        return id;
      }

      public String getCountry() {
        return country;
      }

      public Double getRevenue() {
        return revenue;
      }
    }

}
