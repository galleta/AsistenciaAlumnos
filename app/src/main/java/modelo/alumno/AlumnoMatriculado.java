package modelo.alumno;

import modelo.alumno.Alumno;

/**
 * Created by francis on 30/08/17.
 */

public class AlumnoMatriculado extends Alumno
{
    private String ciclo, curso;

    public AlumnoMatriculado(int id, String nombre, String apellidos, String ciclo, String curso)
    {
        super(id, nombre, apellidos);
        this.ciclo = ciclo;
        this.curso = curso;
    }

    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    @Override
    public String toString() {
        String mensaje = "Nombre: " + getNombre() + ", apellidos: " + getApellidos() + ", ciclo: " + ciclo + ", curso: " + curso;
        return mensaje;
    }
}
