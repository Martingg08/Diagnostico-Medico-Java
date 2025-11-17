import java.util.ArrayList;
import java.util.List;

class Paciente {
    private String id;
    private List<Sintoma> sintomas;
    private int duracionSintomas; // en días
    private List<String> factoresRiesgo;
    
    public Paciente(String id, int duracionSintomas) {
        this.id = id;
        this.sintomas = new ArrayList<>();
        this.duracionSintomas = duracionSintomas;
        this.factoresRiesgo = new ArrayList<>();
    }
    
    public void agregarSintoma(Sintoma s) {
        if (!sintomas.contains(s)) {
            sintomas.add(s);
        }
    }
    
    public void agregarFactorRiesgo(String factor) {
        factoresRiesgo.add(factor);
    }
    
    public boolean tieneSintoma(String nombre) {
        return sintomas.stream()
            .anyMatch(s -> s.getNombre().equals(nombre));
    }
    
    public String getId() { return id; }
    public List<Sintoma> getSintomas() { return sintomas; }
    public int getDuracionSintomas() { return duracionSintomas; }
    public List<String> getFactoresRiesgo() { return factoresRiesgo; }
}