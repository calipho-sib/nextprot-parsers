package org.nextprot.parser.bed.service

import java.sql.DriverManager
import scala.Array.canBuildFrom
import org.nextprot.commons.statements.RawStatement
import oracle.jdbc.pool.OracleConnectionPoolDataSource
import oracle.jdbc.driver.OraclePreparedStatement

object StatementLoader {

  val ocpds = new OracleConnectionPoolDataSource();

  def init = {
    
	ocpds.setDriverType("thin");
	ocpds.setServerName("fou");
	ocpds.setNetworkProtocol("tcp");
	ocpds.setDatabaseName("SIBTEST3");
	ocpds.setPortNumber(1526);
	ocpds.setUser("nxbed"); 
	ocpds.setPassword("juventus");
	
/*	ocpds.setConnectionCachingEnabled(true);*/
	ocpds.setImplicitCachingEnabled(true);

	
	val conn = ocpds.getPooledConnection().getConnection();
	val statement = conn.createStatement();
	statement.executeQuery("DELETE FROM MAPPED_STATEMENTS");
	statement.close();
    
  }

  def loadStatements(statements: List[RawStatement]) = {

    val conn = ocpds.getPooledConnection().getConnection();
    val statement = conn.createStatement();
    
    val columnNames = RawStatement.getFieldNames(null).map(f => { "" + f + "" }).mkString(",");
    val bindVariableNames = RawStatement.getFieldNames(null).map(f => { ":" + f + "" }).mkString(",");

    statements.foreach(s => {
      val fieldValues = RawStatement.getFieldValues(s).map(v => {
        if (v != null) {
          "'" + v.replaceAll("'", "''") + "'" //This done because of single quotes in the text
        } else null
      }).mkString(",");
      val sqlStatement = "INSERT INTO mapped_statements (" + columnNames + ") VALUES ( " + fieldValues + ")";
      statement.addBatch(sqlStatement);
    });
    statement.executeBatch();
  }

  def close = {
    ocpds.close;
  }

}