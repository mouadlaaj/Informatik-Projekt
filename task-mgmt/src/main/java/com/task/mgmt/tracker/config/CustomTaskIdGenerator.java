package com.task.mgmt.tracker.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.jdbc.ReturningWork;

public class CustomTaskIdGenerator implements IdentifierGenerator{
	private static final long serialVersionUID = 1L;

	@Override
    public Object generate(SharedSessionContractImplementor session, Object object) {
        String prefix = "TSK-";
        
        return session.doReturningWork(new ReturningWork<String>() {

            @Override
            public String execute(Connection con) throws SQLException {
                // do something useful
            	String value = null;
                try (PreparedStatement stmt = con.prepareStatement("select Id as Id from task ORDER BY ID DESC LIMIT 1")) {
                    ResultSet rs = stmt.executeQuery();
                    if(rs.next())
                    {
                    	String idVal = rs.getString(1);
                    	int id= 10000;
                    	if(idVal != null) {
                    		String[] split = idVal.split("-");
                            id=(Integer.parseInt(split[1])) + 1;
                    	}
                    	
                        String generatedId = prefix + new Integer(id).toString();
                        value=generatedId;
                        return generatedId;
                    } else {
                    	String generatedId = prefix + new Integer(10000).toString();
                        value=generatedId;
                        return generatedId;
                    }
                }
            }
        });
    }

	
}