package storage;

import discord4j.common.util.Snowflake;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class UserFactory {
    
    private static final long DEFAULT_BALANCE = 5000;
    private static final boolean DEFAULT_ADMIN = false;
    
    public static DbUser fromId(Snowflake userId) {
    
        return Database.withStatement((statement) -> {
        
            try {
            
                ResultSet result = statement.executeQuery(
                        "SELECT * FROM user_table WHERE id=" + userId.asString()
                );
                
                if (!result.next()) {
                
                    statement.execute(
                            MessageFormat.format(
                                    "INSERT INTO user_table(id, credits, admin) VALUES({0}, {1}, {2})",
                                    userId.asString(),
                                    String.valueOf(DEFAULT_BALANCE),
                                    DbUser.getBit(DEFAULT_ADMIN)
                            ));
                
                    return new DbUser(userId).setCredits(DEFAULT_BALANCE)
                                             .setIsAdmin(DEFAULT_ADMIN);
                
                }
                
                return new DbUser(result);
            
            } catch (SQLException ex) {
                ex.printStackTrace();
                return null;
            }
        
        });
    
    }

    public static DbUser[] fromWhereClause(String whereClauseContent) {
    
        return Database.withStatement(s -> {
            try {
                DbUser[] users = new DbUser[getUserCount(Optional.of(whereClauseContent))];
                ResultSet result = s.executeQuery("SELECT * FROM user_table WHERE " + whereClauseContent);
                
                int i = 0;
                while (result.next()) {
                    users[i] = new DbUser(result);
                    i++;
                }
                return users;
            } catch (SQLException ex) {
                System.err.println("Error occurred when querying user_table with the following where clause : " + whereClauseContent);
                ex.printStackTrace();
                return null;
            }
            
        });
    }
    
    private static int getUserCount(Optional<String> whereClause) {
        return Database.withStatement(statement -> {
            try {
                AtomicReference<String> queryString = new AtomicReference<>("SELECT COUNT(*) FROM user_table");
                whereClause.ifPresent(where -> queryString.set(queryString + " WHERE " + where));
                ResultSet result = statement.executeQuery(queryString.get());
            
                if (result.next()) {
                    return result.getInt(1);
                } else {
                    throw new SQLException("user_table entry count should always be queryable");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                return 0;
            }
        });
    }
    
}