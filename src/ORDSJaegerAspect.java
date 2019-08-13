import java.util.logging.Level;

import com.google.common.collect.ImmutableMap;

import io.jaegertracing.internal.JaegerTracer;
import io.opentracing.Scope;
import io.opentracing.Tracer;
//import Tracing;
import lib1.Tracing;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.CodeSignature;
aspect   ORDSJaegerAspect {
	 
	// private final Tracer tracer;
	 
	 //JaegerTracer tracer = Tracing.init("ords_trace");
         
	  JaegerTracer tracer = null;
	  private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(ORDSJaegerAspect.class.getName());
	  
	  pointcut topLevelServlet(): execution(* oracle.dbtools.entrypoint.WebApplicationRequestEntryPoint.service(..)) &&
	                             !within(ORDSJaegerAspect); 
	  
	  pointcut securityCheckServlet(): execution(* oracle.dbtools.rest.resource.jdbc.JDBCResourceDispatcher.service(..)) &&
      !within(ORDSJaegerAspect); 
	  
	  pointcut dbCallServlet(): execution(* oracle.dbtools.rest.resource.generator.json.JSONResourceGeneratorBase.service(..)) &&
      !within(ORDSJaegerAspect); 
	  
	  //oracle.dbtools.common.stmt.jdbc.JDBCStatementsProvider.prepareStatement(final Connection conn, final Statement stmt)
	  pointcut jdbcStatement(): execution(* oracle.dbtools.common.stmt.jdbc.JDBCStatementsProvider.prepareStatement(..)) &&
      !within(ORDSJaegerAspect);
	  
	  pointcut jdbcCall(): execution(* oracle.dbtools.common.stmt.jdbc.BindableQueryImpl.executeQuery(..)) &&
      !within(ORDSJaegerAspect);
	  
	  private int top_level_servlet_nesting = 0;
	  private int security_check_servlet = 0;
	  private int db_call_servlet = 0;
	  private int jdbc_call = 0;
	  private int jdbc_statement = 0;

	  Object around(): topLevelServlet() {
		tracer = Tracing.init("ords_trace");  
		LOGGER.log(Level.INFO, "Entering topLevelServlet");
		Object o = null;
		try (Scope scope = tracer.buildSpan("topLevelServlet").startActive(true)) {
            scope.span().setTag("topLevelServlet", "topLevelServlet");
			top_level_servlet_nesting++;
		    long stime=System.currentTimeMillis();
		    //Object o = proceed();
		    o = proceed();
		    long etime=System.currentTimeMillis();
		    top_level_servlet_nesting--;
		    StringBuilder info = new StringBuilder();
		    for (int i=0;i<top_level_servlet_nesting;i++) {
		      info.append("  ");
		    }
		    info.append(thisJoinPoint+" took "+(etime-stime)+"ms");
		    System.out.println(info.toString());
		}
	    //System.out.println(sentence.toString());
	    LOGGER.log(Level.INFO, "Exiting topLevel servlet");
	    return o;
	  }
	  Object around(): securityCheckServlet() {
		LOGGER.log(Level.INFO, "Entering securityCheckServlet");  
		Object o = null;
		try (Scope scope = tracer.buildSpan("securityCheckServlet").startActive(true)) {
            scope.span().setTag("securityCheckServlet", "securityCheckServlet");
			security_check_servlet++;
		    long stime=System.currentTimeMillis();
		    //Object o = proceed();
		    o = proceed();
		    long etime=System.currentTimeMillis();
		    security_check_servlet--;
		    StringBuilder info = new StringBuilder();
		    for (int i=0;i<security_check_servlet;i++) {
		      info.append("  ");
		    }
		    info.append(thisJoinPoint+" took "+(etime-stime)+"ms");
		    System.out.println(info.toString());
		}
	    //System.out.println(sentence.toString());
	    LOGGER.log(Level.INFO, "Exiting securityCheckServlet");
	    return o;
	  }
	  Object around(): dbCallServlet() {
		LOGGER.log(Level.INFO, "Entering dbCallServlet");
		Object o = null;
		try (Scope scope = tracer.buildSpan("dbCallServlet").startActive(true)) {
            scope.span().setTag("dbCallServlet", "dbCallServlet");
		db_call_servlet++;
	    long stime=System.currentTimeMillis();
	    //Object o = proceed();
	    o = proceed();
	    long etime=System.currentTimeMillis();
	    db_call_servlet--;
	    StringBuilder info = new StringBuilder();
	    for (int i=0;i<db_call_servlet;i++) {
	      info.append("  ");
	    }
	    info.append(thisJoinPoint+" took "+(etime-stime)+"ms");
	    System.out.println(info.toString());
		}
	    //System.out.println(sentence.toString());
	    LOGGER.log(Level.INFO, "Exiting dbCallServlet");
	    return o;
	  }
	  
	  Object around(): jdbcCall() {
		  LOGGER.log(Level.INFO, "Entering jdbcCall");
		  Object o = null;
			try (Scope scope = tracer.buildSpan("jdbcCall").startActive(true)) {
	            scope.span().setTag("jdbcCall", "jdbcCall");
		    jdbc_call++;
		    long stime=System.currentTimeMillis();
		   //Object o = proceed();
		    o = proceed();
		    long etime=System.currentTimeMillis();
		    jdbc_call--;
		    StringBuilder info = new StringBuilder();
		    for (int i=0;i<jdbc_call;i++) {
		      info.append("  ");
		    }
		    info.append(thisJoinPoint+" took "+(etime-stime)+"ms");
		    System.out.println(info.toString());
			}
		   // System.out.println(statement.toString());
		    LOGGER.log(Level.INFO, "Exiting jdbcCall");
		    return o;
		  }
	  Object around(): jdbcStatement() {
		    LOGGER.log(Level.INFO, "Entering jdbcStatement init");
		    printParameters(thisJoinPoint);
		    jdbc_statement++;
		    long stime=System.currentTimeMillis();
		    Object o = proceed();
		    long etime=System.currentTimeMillis();
		    jdbc_statement--;
		    StringBuilder info = new StringBuilder();
		    for (int i=0;i<jdbc_statement;i++) {
		      info.append("  ");
		    }
		    info.append(thisJoinPoint+" took "+(etime-stime)+"ms");
		    System.out.println(info.toString());
		    //System.out.println(sentence.toString());
		    LOGGER.log(Level.INFO, "Exiting jdbcStatement init");
		    return o;
		  }
	  static private void printParameters(JoinPoint jp) {
	      System.out.println("Arguments: " );
	      Object[] args = jp.getArgs();
	      String[] names = ((CodeSignature)jp.getSignature()).getParameterNames();
	      Class[] types = ((CodeSignature)jp.getSignature()).getParameterTypes();
	      for (int i = 0; i < args.length; i++) {
	         System.out.println("  "  + i + ". " + names[i] +
	             " : " +            types[i].getName() +
	             " = " +            args[i]);
	      }
	   }
	}
