package modelo.asistencia;

/**
 * Created by francis on 1/09/17.
 */

public class AsistenciaModificar
{
    private int id, idalumno;
    private String asignatura, tipoasistencia, fecha;

    public AsistenciaModificar(int id, int idalumno, String asignatura, String tipoasistencia, String fecha) {
        this.id = id;
        this.idalumno = idalumno;
        this.asignatura = asignatura;
        this.tipoasistencia = tipoasistencia;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdalumno() {
        return idalumno;
    }

    public void setIdalumno(int idalumno) {
        this.idalumno = idalumno;
    }

    public String getAsignatura() {
        return asignatura;
    }

    public void setAsignatura(String asignatura) {
        this.asignatura = asignatura;
    }

    public String getTipoasistencia() {
        return tipoasistencia;
    }

    public void setTipoasistencia(String tipoasistencia) {
        this.tipoasistencia = tipoasistencia;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "AsistenciaModificar{" +
                "id=" + id +
                ", idalumno=" + idalumno +
                ", asignatura='" + asignatura + '\'' +
                ", tipoasistencia='" + tipoasistencia + '\'' +
                ", fecha='" + fecha + '\'' +
                '}';
    }
}
