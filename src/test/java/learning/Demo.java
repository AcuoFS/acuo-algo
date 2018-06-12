package learning;

import com.acuo.algo.Renjin;
import lpsolve.LpSolve;
import lpsolve.LpSolveException;
import org.renjin.sexp.SEXP;

import javax.script.ScriptException;

public class Demo {

    public static void main(String[] args) {
        testRenjin();
        testLpSolve();
    }

    private static void testRenjin() {
        try {
            Renjin renjin = new Renjin();

            renjin.eval("import(lpsolve.LpSolve)");
            renjin.eval("my.lp <- LpSolve$makeLp(0L, 4L)");
            renjin.eval("my.lp$strAddConstraint(\"3 2 2 1\", 1L, 4)");
            renjin.eval("my.lp$strAddConstraint(\"0 4 3 1\", 2L, 3)");
            renjin.eval("my.lp$strSetObjFn(\"2 3 -2 3\")");
            renjin.eval("my.lp$solve()");
            SEXP obj = (SEXP) renjin.eval("obj <- my.lp$getObjective()");
            SEXP res = (SEXP) renjin.eval("res <- my.lp$getPtrVariables()");
            renjin.eval("my.lp$deleteLp()");
            System.out.println(res);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }


    private static void testLpSolve() {
        try {
            // Create a problem with 4 variables and 0 constraints
            LpSolve solver = LpSolve.makeLp(0, 4);

            // add constraints
            solver.strAddConstraint("3 2 2 1", LpSolve.LE, 4);
            solver.strAddConstraint("0 4 3 1", LpSolve.GE, 3);

            // set objective function
            solver.strSetObjFn("2 3 -2 3");

            // solve the problem
            solver.solve();

            // print solution
            System.out.println("Value of objective function: " + solver.getObjective());
            double[] var = solver.getPtrVariables();
            for (int i = 0; i < var.length; i++) {
                System.out.println("Value of var[" + i + "] = " + var[i]);
            }

            // delete the problem and free memory
            solver.deleteLp();

        } catch (LpSolveException e) {
            e.printStackTrace();
        }
    }

}
