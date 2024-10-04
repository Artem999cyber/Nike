import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Admin {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty role;
    private final SimpleStringProperty nickname;
    private final SimpleStringProperty login;
    private final SimpleStringProperty password;

    public Admin(int id, String role, String nickname, String login, String password) {
        this.id = new SimpleIntegerProperty(id);
        this.role = new SimpleStringProperty(role);
        this.nickname = new SimpleStringProperty(nickname);
        this.login = new SimpleStringProperty(login);
        this.password = new SimpleStringProperty(password);
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public String getRole() {
        return role.get();
    }

    public void setRole(String role) {
        this.role.set(role);
    }

    public SimpleStringProperty roleProperty() {
        return role;
    }

    public String getNickname() {
        return nickname.get();
    }

    public void setNickname(String nickname) {
        this.nickname.set(nickname);
    }

    public SimpleStringProperty nicknameProperty() {
        return nickname;
    }

    public String getLogin() {
        return login.get();
    }

    public void setLogin(String login) {
        this.login.set(login);
    }

    public SimpleStringProperty loginProperty() {
        return login;
    }

    public String getPassword() {
        return password.get();
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public SimpleStringProperty passwordProperty() {
        return password;
    }
}