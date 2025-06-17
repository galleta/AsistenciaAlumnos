package modelo.asistencia;

import modelo.alumno.Alumno;

/**
 * Created by francis on 24/09/16.
 */

public class Asistencia
{
    private Alumno alumno;
    private String tipoasistencia, fecha, asignatura, ciclo, curso;
    private int id;

    public Asistencia(int id, Alumno alumno, String tipoasistencia, String fecha, String asignatura, String ciclo, String curso)
    {
        this.id = id;
        this.alumno = alumno;
        this.tipoasistencia = tipoasistencia;
        this.fecha = fecha;
        this.asignatura = asignatura;
        this.ciclo = ciclo;
        this.curso = curso;
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }

    public String getTipoAsistencia() {
        return tipoasistencia;
    }

    public void setTipoAsistencia(String tipoasistencia) {
        this.tipoasistencia = tipoasistencia;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getAsignatura() {
        return asignatura;
    }

    public void setAsignatura(String asignatura) {
        this.asignatura = asignatura;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTipoasistencia() {
        return tipoasistencia;
    }

    public void setTipoasistencia(String tipoasistencia) {
        this.tipoasistencia = tipoasistencia;
    }

    @Override
    public String toString() {
        return "Asistencia{" +
                "\n\talumno=" + alumno.getNombre() + " " + alumno.getApellidos() +
                "\n\ttipoasistencia='" + tipoasistencia + '\'' +
                "\n\tfecha='" + fecha + '\'' +
                "\n\tasignatura='" + asignatura + '\'' +
                "\n\tciclo='" + ciclo + '\'' +
                "\n\tcurso='" + curso + '\'' +
                "\n\tid=" + id +
                '}';
    }
}
