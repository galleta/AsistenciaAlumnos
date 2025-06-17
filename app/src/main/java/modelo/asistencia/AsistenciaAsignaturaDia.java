package modelo.asistencia;

/**
 * Created by francis on 30/09/16.
 */

public class AsistenciaAsignaturaDia
{
    private String nombrealumno, apellidosalumno, asignatura, fecha;
    private int totalasiste, totalfalta, totalfaltajustificada, totalretraso;


    public AsistenciaAsignaturaDia(String nombrealumno, String apellidosalumno, String asignatura, String fecha, int totalasiste, int totalfalta, int totalfaltajustificada, int totalretraso) {
        this.nombrealumno = nombrealumno;
        this.apellidosalumno = apellidosalumno;
        this.asignatura = asignatura;
        this.fecha = fecha;
        this.totalasiste = totalasiste;
        this.totalfalta = totalfalta;
        this.totalfaltajustificada = totalfaltajustificada;
        this.totalretraso = totalretraso;
    }

    public String getNombrealumno() {
        return nombrealumno;
    }

    public void setNombrealumno(String nombrealumno) {
        this.nombrealumno = nombrealumno;
    }

    public String getApellidosalumno() {
        return apellidosalumno;
    }

    public void setApellidosalumno(String apellidosalumno) {
        this.apellidosalumno = apellidosalumno;
    }

    public String getAsignatura() {
        return asignatura;
    }

    public void setAsignatura(String asignatura) {
        this.asignatura = asignatura;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getTotalasiste() {
        return totalasiste;
    }

    public void setTotalasiste(int totalasiste) {
        this.totalasiste = totalasiste;
    }

    public int getTotalfalta() {
        return totalfalta;
    }

    public void setTotalfalta(int totalfalta) {
        this.totalfalta = totalfalta;
    }

    public int getTotalfaltajustificada() {
        return totalfaltajustificada;
    }

    public void setTotalfaltajustificada(int totalfaltajustificada) {
        this.totalfaltajustificada = totalfaltajustificada;
    }

    public int getTotalretraso() {
        return totalretraso;
    }

    public void setTotalretraso(int totalretraso) {
        this.totalretraso = totalretraso;
    }

    @Override
    public String toString() {
        return "AsistenciaAsignaturaDia{" +
                "nombrealumno='" + nombrealumno + '\'' +
                ", apellidosalumno='" + apellidosalumno + '\'' +
                ", asignatura='" + asignatura + '\'' +
                ", fecha='" + fecha + '\'' +
                ", totalasiste=" + totalasiste +
                ", totalfalta=" + totalfalta +
                ", totalfaltajustificada=" + totalfaltajustificada +
                ", totalretraso=" + totalretraso +
                '}';
    }
}
