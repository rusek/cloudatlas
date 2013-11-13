package pl.edu.mimuw.cloudatlas;

import java.util.ArrayList;
import java.util.List;

import pl.edu.mimuw.cloudatlas.attributes.DoubleValue;
import pl.edu.mimuw.cloudatlas.query.Env;
import pl.edu.mimuw.cloudatlas.query.EvaluationException;
import pl.edu.mimuw.cloudatlas.query.ParseException;
import pl.edu.mimuw.cloudatlas.query.Parsers;
import pl.edu.mimuw.cloudatlas.query.SelectStmt;
import pl.edu.mimuw.cloudatlas.zones.ZMI;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	List<ZMI> infos = new ArrayList<ZMI>();
    	ZMI info = new ZMI();
    	info.addAttribute("cpu_usage", new DoubleValue(0.5));
    	infos.add(info);
    	info = new ZMI();
    	info.addAttribute("cpu_usage", new DoubleValue(0.1));
    	infos.add(info);
    	
        try {
			SelectStmt select = (SelectStmt) Parsers.parseQuery("SELECT count(cpu_usage) WHERE cpu_usage > (SELECT 0.3)").get(0);
			Env env = Env.createFromZMIs(infos);
			System.out.println(select.evaluate(env));
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (EvaluationException e) {
			e.printStackTrace();
		}
    }
}
