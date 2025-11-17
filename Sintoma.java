import java.util.Objects;

class Sintoma {
    private String nombre;
    
    public Sintoma(String nombre) {
        this.nombre = nombre;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sintoma sintoma = (Sintoma) o;
        return nombre.equals(sintoma.nombre);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(nombre);
    }
    
    @Override
    public String toString() {
        return nombre;
    }
}



