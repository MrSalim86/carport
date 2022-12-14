package dat.backend.persistence;

import dat.backend.model.entities.User;
import dat.backend.model.exceptions.DatabaseException;
import dat.backend.model.persistence.ConnectionPool;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

 class UserMapper
{

    static ConnectionPool connectionPool;
    private UserMapper(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }
    static User login(String username, String password, ConnectionPool connectionPool) throws DatabaseException
    {
        Logger.getLogger("web").log(Level.INFO, "");

        User user = null;

        String sql = "SELECT * FROM user WHERE username = ? AND password = ?";

        try (Connection connection = connectionPool.getConnection())
        {
            try (PreparedStatement ps = connection.prepareStatement(sql))
            {
                ps.setString(1, username);
                ps.setString(2, password);
                ResultSet rs = ps.executeQuery();
                if (rs.next())
                {
                    String role = rs.getString("role");
                    String name = rs.getString("name");
                    String address = rs.getString("adresse");
                    String email = rs.getString("email");
                    int tlfNmr = rs.getInt("tlf");
                    user = new User(username, password, role, name, address, email, tlfNmr);
                } else
                {
                    throw new DatabaseException("Wrong username or password");
                }
            }
        } catch (SQLException ex)

        {
            throw new DatabaseException(ex, "Error logging in. Something went wrong with the database");
        }
        return user;
    }

    public static User createUser(String username, String password, String role, String name, String adresse, String email, int tlf) throws DatabaseException
    {
        Logger.getLogger("web").log(Level.INFO, "");
        User user = null;
        String sql = "insert into carport.user (username, password, role , name, adresse, email, tlf ) values (?,?,?,?,?,?,?)";
        try (Connection connection = connectionPool.getConnection())
        {
            try (PreparedStatement ps = connection.prepareStatement(sql))
            {
                ps.setString(1, username);
                ps.setString(2, password);
                ps.setString(3, role);
                ps.setString(4, name);
                ps.setString(5, adresse);
                ps.setString(6, email);
                ps.setInt(7, tlf);
                int rowsAffected = ps.executeUpdate();
                if(rowsAffected == 1){
                    user = new User(username, password, role, name, adresse, email,tlf);
                }else{
                    throw new DatabaseException("The user with username = " + username + " could not be inserted into the database");
                }


            }
        }
        catch (SQLException ex)
        {
            throw new DatabaseException(ex, "Could not insert username into database");
        }
        return user;
    }


}
