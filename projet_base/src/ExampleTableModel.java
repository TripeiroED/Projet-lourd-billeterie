import java.sql.SQLException;

public class ExampleTableModel implements Model {
    private Integer id;
    private string column1;
    private string column2;

    public static void getAll(){
        String sql = "SELECT * " +
                "FROM ExampleTable";

        try (var conn = MySQLConnection.connect();
             var stmt  = conn.createStatement();
             var rs    = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println(
                    rs.getString("column1") + "\t" +
                    rs.getString("column2")  + "\t" +
                    rs.getString("column3")
                );

            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void getId(Integer id){
        // Select row by id
    }

    public static void updateRowId(Integer id, String[] newValues){
        // Update
    }

    public static void deleteRowId(Integer id){
        // Delete
    }

    public static void insertNewRow(Integer id, String[] values){
        // Insert
    }

    // getter + setter for your attributs

}
