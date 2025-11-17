
import java.util.List;

class Diagnostico {
    private String enfermedad;
    private String explicacion;
    private List<String> sintomasRelevantes;
    
    public Diagnostico(String enfermedad, String explicacion, List<String> sintomasRelevantes) {
        this.enfermedad = enfermedad;
        this.explicacion = explicacion;
        this.sintomasRelevantes = sintomasRelevantes;
    }
    
    public String getEnfermedad() { return enfermedad; }
    public String getExplicacion() { return explicacion; }
    public List<String> getSintomasRelevantes() { return sintomasRelevantes; }
}