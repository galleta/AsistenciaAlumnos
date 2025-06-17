package modelo.faltaasistencia;

import modelo.alumno.Alumno;
import modelo.asignatura.Asignatura;

public class FaltaAsistencia
{
    private Alumno alumno;
    private Asignatura asignatura;
    private String tipoasistencia, fecha;
    private int cantidad;

    public FaltaAsistencia(Alumno alumno, Asignatura asignatura, String tipoasistencia, String fecha, int cantidad) {
        this.alumno = alumno;
        this.asignatura = asignatura;
        this.tipoasistencia = tipoasistencia;
        this.fecha = fecha;
        this.cantidad = cantidad;
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }

    public Asignatura getAsignatura() {
        return asignatura;
    }

    public void setAsignatura(Asignatura asignatura) {
        this.asignatura = asignatura;
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

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    @Override
    public String toString() {
        return "FaltaAsistencia{" +
                "alumno=" + alumno +
                ", asignatura=" + asignatura +
                ", tipoasistencia='" + tipoasistencia + '\'' +
                ", fecha='" + fecha + '\'' +
                ", cantidad=" + cantidad +
                '}';
    }
}
