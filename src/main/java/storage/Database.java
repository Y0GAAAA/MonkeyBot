package storage;

import panic.ExitCodes;
import panic.Panicker;

import java.sql.*;
import java.util.function.Function;

public class Database {
    
    private static final SynchronizedLock DATABASE_LOCK = new SynchronizedLock();
    
    private static Connection sqlConnection;
    
    static {
        try {
            String connectionString = System.getenv("MONKEY_BOT_JDBC_SQLSERVER_CONNECTION_STRING");
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            sqlConnection = DriverManager.getConnection(connectionString);
        } catch (SQLException ignored) {
            new Panicker("Could not connect to database.", ExitCodes.DATABASE_CONNECTION_FAILED).panic();
        } catch (ClassNotFoundException ignored) {
            new Panicker("Could not find JDBC SQLServerDriver class.", ExitCodes.DRIVER_CLASS_NOT_FOUND).panic();
        }  catch (Exception ex) {
            new Panicker(ex, -1).panic();
        }
    }
    
    public static <T> T withStatement(Function<Statement, T> f) {
        try {
            final T result;
            synchronized (DATABASE_LOCK) {
                Statement statement = sqlConnection.createStatement();
                result = f.apply(statement);
                statement.close();
            }
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
}