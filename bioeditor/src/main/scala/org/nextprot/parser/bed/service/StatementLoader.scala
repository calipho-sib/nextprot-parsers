package org.nextprot.parser.bed.service

import java.sql.DriverManager
import scala.Array.canBuildFrom
import org.nextprot.commons.statements.RawStatement
import oracle.jdbc.pool.OracleConnectionPoolDataSource
import oracle.jdbc.driver.OraclePreparedStatement
import org.nextprot.commons.statements.StatementField

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
    
    val columnNames = StatementField.values().map(f => { "" + f + "" }).mkString(",");
    val bindVariableNames = StatementField.values().map(f => { ":" + f + "" }).mkString(",");

    statements.foreach(s => {
      val fieldValues = StatementField.values().map(v => {
        val value : String = s.getValue(v);
        if (value != null) {
          "'" + value.replaceAll("'", "''") + "'" //This done because of single quotes in the text
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