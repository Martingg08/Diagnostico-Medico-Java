// ============================================================
// SISTEMA EXPERTO DE DIAGNÓSTICO MÉDICO - VERSIÓN POO
// Implementación en Java para comparación con Prolog
// ============================================================

import java.util.*;
import java.util.stream.Collectors;

// ============================================================
// CLASES DE REGLAS DE DIAGNÓSTICO
// ============================================================

abstract class ReglaDiagnostico {
    protected String nombreEnfermedad;
    
    public ReglaDiagnostico(String nombreEnfermedad) {
        this.nombreEnfermedad = nombreEnfermedad;
    }
    
    public abstract boolean evaluar(Paciente paciente);
    
    public abstract String obtenerExplicacion(Paciente paciente);
    
    public String getNombreEnfermedad() {
        return nombreEnfermedad;
    }
}

class ReglaCovid19 extends ReglaDiagnostico {
    public ReglaCovid19() {
        super("COVID-19");
    }
    
    @Override
    public boolean evaluar(Paciente p) {
        return p.tieneSintoma("fiebre_alta") &&
               p.tieneSintoma("tos_seca") &&
               (p.tieneSintoma("perdida_olfato") || p.tieneSintoma("perdida_gusto")) &&
               p.getDuracionSintomas() <= 7;
    }
    
    @Override
    public String obtenerExplicacion(Paciente p) {
        StringBuilder sb = new StringBuilder();
        sb.append("El paciente presenta:\n");
        if (p.tieneSintoma("fiebre_alta"))
            sb.append("  ✓ Fiebre alta (síntoma clave de COVID-19)\n");
        if (p.tieneSintoma("tos_seca"))
            sb.append("  ✓ Tos seca (común en COVID-19)\n");
        if (p.tieneSintoma("perdida_olfato") || p.tieneSintoma("perdida_gusto"))
            sb.append("  ✓ Pérdida de olfato/gusto (síntoma característico)\n");
        sb.append("  ✓ Duración: ").append(p.getDuracionSintomas())
          .append(" días (típico de fase inicial)\n");
        return sb.toString();
    }
}

class ReglaNeumonia extends ReglaDiagnostico {
    public ReglaNeumonia() {
        super("Neumonía");
    }
    
    @Override
    public boolean evaluar(Paciente p) {
        return p.tieneSintoma("fiebre_alta") &&
               p.tieneSintoma("dificultad_respirar") &&
               p.tieneSintoma("dolor_pecho") &&
               (p.tieneSintoma("tos_productiva") || p.tieneSintoma("tos_seca"));
    }
    
    @Override
    public String obtenerExplicacion(Paciente p) {
        StringBuilder sb = new StringBuilder();
        sb.append("El paciente presenta:\n");
        if (p.tieneSintoma("fiebre_alta"))
            sb.append("  ✓ Fiebre alta\n");
        if (p.tieneSintoma("dificultad_respirar"))
            sb.append("  ✓ Dificultad respiratoria (síntoma grave)\n");
        if (p.tieneSintoma("dolor_pecho"))
            sb.append("  ✓ Dolor en el pecho\n");
        return sb.toString();
    }
}

class ReglaBronquitis extends ReglaDiagnostico {
    public ReglaBronquitis() {
        super("Bronquitis");
    }
    
    @Override
    public boolean evaluar(Paciente p) {
        return p.tieneSintoma("tos_productiva") &&
               p.tieneSintoma("mucosidad") &&
               p.getDuracionSintomas() > 10 &&
               !p.tieneSintoma("dificultad_respirar");
    }
    
    @Override
    public String obtenerExplicacion(Paciente p) {
        return "El paciente presenta:\n" +
               "  ✓ Tos con mucosidad\n" +
               "  ✓ Duración prolongada: " + p.getDuracionSintomas() + " días\n";
    }
}

class ReglaGripe extends ReglaDiagnostico {
    public ReglaGripe() {
        super("Gripe");
    }
    
    @Override
    public boolean evaluar(Paciente p) {
        return p.tieneSintoma("fiebre_alta") &&
               p.tieneSintoma("dolor_muscular") &&
               (p.tieneSintoma("tos_seca") || p.tieneSintoma("tos_productiva")) &&
               !p.tieneSintoma("perdida_olfato") &&
               p.getDuracionSintomas() <= 10;
    }
    
    @Override
    public String obtenerExplicacion(Paciente p) {
        return "El paciente presenta:\n" +
               "  ✓ Fiebre alta\n" +
               "  ✓ Dolor muscular (muy común en gripe)\n" +
               "  ✓ Ausencia de pérdida de olfato (descarta COVID-19)\n";
    }
}

class ReglaAlergia extends ReglaDiagnostico {
    public ReglaAlergia() {
        super("Alergia Respiratoria");
    }
    
    @Override
    public boolean evaluar(Paciente p) {
        return p.tieneSintoma("estornudos") &&
               p.tieneSintoma("congestion_nasal") &&
               !p.tieneSintoma("fiebre_alta") &&
               !p.tieneSintoma("fiebre_moderada");
    }
    
    @Override
    public String obtenerExplicacion(Paciente p) {
        return "El paciente presenta:\n" +
               "  ✓ Estornudos frecuentes\n" +
               "  ✓ Congestión nasal\n" +
               "  ✓ Ausencia de fiebre (descarta infección)\n";
    }
}

// ============================================================
// MOTOR DE DIAGNÓSTICO
// ============================================================

class MotorDiagnostico {
    private List<ReglaDiagnostico> reglas;
    
    public MotorDiagnostico() {
        reglas = new ArrayList<>();
        inicializarReglas();
    }
    
    private void inicializarReglas() {
        reglas.add(new ReglaCovid19());
        reglas.add(new ReglaNeumonia());
        reglas.add(new ReglaBronquitis());
        reglas.add(new ReglaGripe());
        reglas.add(new ReglaAlergia());
    }
    
    public List<Diagnostico> diagnosticar(Paciente paciente) {
        List<Diagnostico> diagnosticos = new ArrayList<>();
        
        for (ReglaDiagnostico regla : reglas) {
            if (regla.evaluar(paciente)) {
                List<String> sintomasRelevantes = obtenerSintomasRelevantes(paciente, regla);
                Diagnostico diag = new Diagnostico(
                    regla.getNombreEnfermedad(),
                    regla.obtenerExplicacion(paciente),
                    sintomasRelevantes
                );
                diagnosticos.add(diag);
            }
        }
        
        return diagnosticos;
    }
    
    private List<String> obtenerSintomasRelevantes(Paciente p, ReglaDiagnostico regla) {
        return p.getSintomas().stream()
                .map(Sintoma::getNombre)
                .collect(Collectors.toList());
    }
    
    public Severidad calcularSeveridad(Paciente paciente) {
        if (paciente.tieneSintoma("saturacion_baja") && 
            paciente.tieneSintoma("dificultad_respirar")) {
            return Severidad.CRITICA;
        }
        
        List<Diagnostico> diagnosticos = diagnosticar(paciente);
        if (diagnosticos.stream().anyMatch(d -> d.getEnfermedad().equals("Neumonía"))) {
            return Severidad.ALTA;
        }
        
        if (paciente.tieneSintoma("dificultad_respirar") && 
            paciente.tieneSintoma("fiebre_alta")) {
            return Severidad.ALTA;
        }
        
        if (paciente.tieneSintoma("fiebre_alta")) {
            return Severidad.MEDIA;
        }
        
        return Severidad.BAJA;
    }
}

// ============================================================
// SISTEMA DE RECOMENDACIONES
// ============================================================

class SistemaRecomendaciones {
    
    public List<String> recomendarExamenes(Paciente paciente, List<Diagnostico> diagnosticos) {
        List<String> examenes = new ArrayList<>();
        
        for (Diagnostico diag : diagnosticos) {
            switch (diag.getEnfermedad()) {
                case "COVID-19":
                    examenes.add("PCR COVID-19");
                    break;
                case "Neumonía":
                    examenes.add("Rayos X de Tórax");
                    examenes.add("Hemograma Completo");
                    break;
            }
        }
        
        if (paciente.tieneSintoma("dificultad_respirar")) {
            examenes.add("Oximetría");
            if (!examenes.contains("Rayos X de Tórax")) {
                examenes.add("Rayos X de Tórax");
            }
        }
        
        if (paciente.tieneSintoma("fiebre_alta") && paciente.getDuracionSintomas() > 5) {
            if (!examenes.contains("Hemograma Completo")) {
                examenes.add("Hemograma Completo");
            }
        }
        
        return examenes.stream().distinct().collect(Collectors.toList());
    }
    
    public List<String> recomendarTratamientos(Paciente paciente, 
                                                 List<Diagnostico> diagnosticos,
                                                 Severidad severidad) {
        List<String> tratamientos = new ArrayList<>();
        
        if (severidad == Severidad.CRITICA) {
            tratamientos.add("Hospitalización Urgente");
            return tratamientos;
        }
        
        for (Diagnostico diag : diagnosticos) {
            switch (diag.getEnfermedad()) {
                case "Neumonía":
                    tratamientos.add("Antibióticos");
                    break;
                case "COVID-19":
                    if (severidad == Severidad.ALTA) {
                        tratamientos.add("Antivirales");
                    }
                    break;
                case "Gripe":
                    if (!paciente.getFactoresRiesgo().isEmpty() && 
                        paciente.getDuracionSintomas() <= 2) {
                        tratamientos.add("Antivirales");
                    }
                    break;
                case "Bronquitis":
                    tratamientos.add("Antitusivos");
                    break;
                case "Alergia Respiratoria":
                    tratamientos.add("Antihistamínicos");
                    break;
            }
        }
        
        if (severidad == Severidad.BAJA) {
            tratamientos.add("Reposo e Hidratación");
        }
        
        if (paciente.tieneSintoma("fiebre_alta")) {
            tratamientos.add("Antiinflamatorios");
        }
        
        return tratamientos.stream().distinct().collect(Collectors.toList());
    }
}

// ============================================================
// GENERADOR DE REPORTES
// ============================================================

class GeneradorReportes {
    private MotorDiagnostico motorDiagnostico;
    private SistemaRecomendaciones sistemaRecomendaciones;
    
    public GeneradorReportes() {
        this.motorDiagnostico = new MotorDiagnostico();
        this.sistemaRecomendaciones = new SistemaRecomendaciones();
    }
    
    public void generarReporteCompleto(Paciente paciente) {
        System.out.println("=== DIAGNÓSTICO PARA " + paciente.getId() + " ===\n");
        
        System.out.println("SÍNTOMAS REPORTADOS:");
        for (Sintoma s : paciente.getSintomas()) {
            System.out.println("  - " + s.getNombre());
        }
        
        System.out.println("\nDURACIÓN: " + paciente.getDuracionSintomas() + " días");
        
        if (!paciente.getFactoresRiesgo().isEmpty()) {
            System.out.println("\nFACTORES DE RIESGO:");
            for (String factor : paciente.getFactoresRiesgo()) {
                System.out.println("  - " + factor);
            }
        }
        
        List<Diagnostico> diagnosticos = motorDiagnostico.diagnosticar(paciente);
        System.out.println("\nPOSIBLES ENFERMEDADES:");
        if (diagnosticos.isEmpty()) {
            System.out.println("  No se puede determinar con los síntomas actuales");
        } else {
            for (Diagnostico diag : diagnosticos) {
                System.out.println("  * " + diag.getEnfermedad());
            }
        }
        
        Severidad severidad = motorDiagnostico.calcularSeveridad(paciente);
        System.out.println("\nSEVERIDAD: " + severidad);
        
        List<String> examenes = sistemaRecomendaciones.recomendarExamenes(paciente, diagnosticos);
        System.out.println("\nEXÁMENES RECOMENDADOS:");
        if (examenes.isEmpty()) {
            System.out.println("  Ninguno específico por el momento");
        } else {
            for (String examen : examenes) {
                System.out.println("  - " + examen);
            }
        }
        
        List<String> tratamientos = sistemaRecomendaciones.recomendarTratamientos(
            paciente, diagnosticos, severidad
        );
        System.out.println("\nTRATAMIENTOS SUGERIDOS:");
        if (tratamientos.isEmpty()) {
            System.out.println("  Consultar con médico");
        } else {
            for (String tratamiento : tratamientos) {
                System.out.println("  - " + tratamiento);
            }
        }
        
        System.out.println("\n" + "=".repeat(50) + "\n");
    }
    
    public void generarExplicacion(Paciente paciente, String enfermedad) {
        List<Diagnostico> diagnosticos = motorDiagnostico.diagnosticar(paciente);
        
        for (Diagnostico diag : diagnosticos) {
            if (diag.getEnfermedad().equals(enfermedad)) {
                System.out.println("EXPLICACIÓN: ¿Por qué se diagnosticó " + enfermedad + "?\n");
                System.out.println(diag.getExplicacion());
                return;
            }
        }
        
        System.out.println("No se diagnosticó " + enfermedad + " para este paciente.");
    }
}

// ============================================================
// PROGRAMA PRINCIPAL
// ============================================================

public class SistemaExpertoDiagnostico {
    
    public static void main(String[] args) {
        GeneradorReportes generador = new GeneradorReportes();
        
        // Crear pacientes de ejemplo
        Paciente paciente1 = crearPaciente1();
        Paciente paciente2 = crearPaciente2();
        Paciente paciente3 = crearPaciente3();
        
        // Generar reportes
        generador.generarReporteCompleto(paciente1);
        generador.generarExplicacion(paciente1, "COVID-19");
        
        generador.generarReporteCompleto(paciente2);
        
        generador.generarReporteCompleto(paciente3);
    }
    
    private static Paciente crearPaciente1() {
        Paciente p = new Paciente("paciente1", 3);
        p.agregarSintoma(new Sintoma("fiebre_alta"));
        p.agregarSintoma(new Sintoma("tos_seca"));
        p.agregarSintoma(new Sintoma("dolor_muscular"));
        p.agregarSintoma(new Sintoma("perdida_olfato"));
        p.agregarSintoma(new Sintoma("perdida_gusto"));
        p.agregarSintoma(new Sintoma("fatiga"));
        return p;
    }
    
    private static Paciente crearPaciente2() {
        Paciente p = new Paciente("paciente2", 5);
        p.agregarSintoma(new Sintoma("fiebre_alta"));
        p.agregarSintoma(new Sintoma("tos_productiva"));
        p.agregarSintoma(new Sintoma("dificultad_respirar"));
        p.agregarSintoma(new Sintoma("dolor_pecho"));
        p.agregarSintoma(new Sintoma("saturacion_baja"));
        p.agregarFactorRiesgo("edad_avanzada");
        p.agregarFactorRiesgo("fumador");
        return p;
    }
    
    private static Paciente crearPaciente3() {
        Paciente p = new Paciente("paciente3", 7);
        p.agregarSintoma(new Sintoma("estornudos"));
        p.agregarSintoma(new Sintoma("congestion_nasal"));
        p.agregarSintoma(new Sintoma("mucosidad"));
        p.agregarSintoma(new Sintoma("dolor_cabeza"));
        return p;
    }
}

/*
ANÁLISIS COMPARATIVO CON PROLOG:

DESVENTAJAS DE ESTE ENFOQUE POO:

1. VERBOSIDAD: 
   - ~450 líneas vs ~200 en Prolog
   - Mucho código boilerplate (getters, setters, constructores)

2. RIGIDEZ:
   - Las reglas están "hardcodeadas" en clases
   - Agregar una nueva enfermedad requiere crear una clase completa
   - Modificar reglas requiere recompilar

3. FALTA DE BIDIRECCIONALIDAD:
   - Solo funciona en dirección: síntomas → enfermedad
   - La inversa (enfermedad → síntomas) requiere código adicional
   - No hay "backtracking" natural para explorar alternativas

4. DIFICULTAD DE MANTENIMIENTO:
   - Conocimiento médico disperso en múltiples clases
   - Cambiar una regla puede requerir modificar varias clases
   - Mayor acoplamiento entre componentes

5. MENOR TRANSPARENCIA:
   - El razonamiento no es evidente del código
   - Difícil de auditar y validar por expertos médicos
   - La lógica está oculta en estructuras de control

6. COMPLEJIDAD INNECESARIA:
   - Motor de inferencia manual
   - Sistema de reglas debe implementarse desde cero
   - Gestión manual del flujo de razonamiento

VENTAJAS DEL PROLOG SOBRE ESTE ENFOQUE:
   - Declarativo: describe QUÉ, no CÓMO
   - Bidireccional: las mismas reglas funcionan en ambas direcciones
   - Conciso: menos código, más expresivo
   - Mantenible: agregar conocimiento es trivial
   - Transparente: el razonamiento es auditable
*/
