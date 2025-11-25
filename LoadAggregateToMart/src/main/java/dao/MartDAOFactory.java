package dao;

import org.jdbi.v3.core.Jdbi;

public class MartDAOFactory {

    private static MartDAO instance;

    public static void initialize(String url, String user, String pass) {
        Jdbi jdbi = Jdbi.create(url, user, pass);
        instance = new MartDAO(jdbi);
    }

    public static MartDAO getInstance() {
        if (instance == null) {
            throw new IllegalStateException("MartDAOFactory chưa được initialize()");
        }
        return instance;
    }
}
