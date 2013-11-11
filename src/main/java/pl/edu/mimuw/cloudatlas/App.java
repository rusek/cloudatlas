package pl.edu.mimuw.cloudatlas;

import pl.edu.mimuw.cloudatlas.query.Env;
import pl.edu.mimuw.cloudatlas.query.EvaluationException;
import pl.edu.mimuw.cloudatlas.query.ParseException;
import pl.edu.mimuw.cloudatlas.query.Parsers;
import pl.edu.mimuw.cloudatlas.query.SelectStmt;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        try {
			SelectStmt select = (SelectStmt) Parsers.parseQuery("SELECT 1 + 1").get(0);
			Env env = new Env();
			System.out.println(select.evaluate(env));
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (EvaluationException e) {
			e.printStackTrace();
		}
    }
}
