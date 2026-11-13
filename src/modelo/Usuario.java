package modelo;
 
public class Usuario {
 
    private int id;
    private String username;
    private String password;
    private Rol rol;   
 
    public Usuario() {
    }
 
    public Usuario(int id, String username, String password, Rol rol) {
        this.id = id;
        setUsername(username);  
        setPassword(password);
        this.rol = rol;
    }
 
    public int getId() {
        return id;
    }
 
    public void setId(int id) {
        this.id = id;
    }
 
    public String getUsername() {
        return username;
    }
 
    public void setUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("El usuario no puede estar vacio");
        }
        this.username = username.trim();
    }
 
    public String getPassword() {
        return password;
    }
 
    public void setPassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("La contrasena no puede estar vacia");
        }
        this.password = password;
    }
 
    public Rol getRol() {
        return rol;
    }
 
    public void setRol(Rol rol) {
        this.rol = rol;
    }
 
    @Override
    public String toString() {
        return username + " (" + rol + ")";
    }
}
