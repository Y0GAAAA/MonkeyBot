package storage;

import discord4j.common.util.Snowflake;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;

public class DbUser {

    private final long id;
    private long credits;
    private boolean isAdmin;
    
    public long getCredits() {
        return credits;
    }
    public long getId() {
        return id;
    }
    public boolean getIsAdmin() {
        return isAdmin;
    }
    
    public DbUser setCredits(long credits) {
        this.credits = credits;
        return this;
    }
    public DbUser setIsAdmin(boolean b) {
        this.isAdmin = b;
        return this;
    }
    
    DbUser(Snowflake id) {
        this.id = id.asLong();
    }
    public DbUser(ResultSet result) throws SQLException {
        final long id = result.getBigDecimal("id").longValue();
        final long credits = result.getBigDecimal("credits").longValue();
        final boolean admin = result.getBoolean("admin");
        
        this.id = id;
        this.credits = credits;
        this.isAdmin = admin;
    }
    
    public void commit() {
        Database.withStatement((statement) -> {
            try {
                statement.executeUpdate(
                        MessageFormat.format(
                                "UPDATE user_table SET credits={0}, admin={1} WHERE id={2}",
                                String.valueOf(this.getCredits()),
                                getBit(this.getIsAdmin()),
                                String.valueOf(this.getId())
                        )
                );
            } catch (SQLException ex) {
                System.out.println("Error occurred when updating user data; id " + this.getId());
                ex.printStackTrace();
            }
            return null;
        });
    }
    
    public static String getBit(boolean b) {
        return b ? "1" : "0";
    }
    
    @Override
    public String toString() {
        return "\"id\":" + id +
               ", \"credits\":" + credits +
               ", \"isAdmin\":" + isAdmin;
    }
}
