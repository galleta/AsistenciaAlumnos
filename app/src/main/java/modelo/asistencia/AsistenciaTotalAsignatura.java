package modelo.asistencia;

/**
 * Created by francis on 27/09/16.
 */

public class AsistenciaTotalAsignatura
{
    private String nombrealumno, apellidosalumno, asignatura;
    private int totalasiste, totalfalta, totalfaltajustificada, totalretraso, totalhoras;
    private double porcentajeasistencia, notadeasistencia;

    public AsistenciaTotalAsignatura(String nombrealumno, String apellidosalumno, String asignatura, int totalasiste, int totalfalta, int totalfaltajustificada, int totalretraso, int totalhoras, double porcentajeasistencia, double notaasistencia)
    {
        this.nombrealumno = nombrealumno;
        this.apellidosalumno = apellidosalumno;
        this.asignatura = asignatura;
        this.totalasiste = totalasiste;
        this.totalfalta = totalfalta;
        this.totalfaltajustificada = totalfaltajustificada;
        this.totalretraso = totalretraso;
        this.totalhoras = totalhoras;
        this.notadeasistencia = notaasistencia;
        this.porcentajeasistencia = porcentajeasistencia;
    }

    public String getNombreAlumno() {
        return nombrealumno;
    }

    public void setNombreAlumno(String nombrealumno) {
        this.nombrealumno = nombrealumno;
    }

    public String getApellidosAlumno() {
        return apellidosalumno;
    }

    public void setApellidosAlumno(String apellidosalumno) {
        this.apellidosalumno = apellidosalumno;
    }

    public String getAsignatura() {
        return asignatura;
    }

    public void setAsignatura(String asignatura) {
        this.asignatura = asignatura;
    }

    public int getTotalAsiste() {
        return totalasiste;
    }

    public void setTotalAsiste(int totalasiste) {
        this.totalasiste = totalasiste;
    }

    public int getTotalFalta() {
        return totalfalta;
    }

    public void setTotalFalta(int totalfalta) {
        this.totalfalta = totalfalta;
    }

    public int getTotalFaltaJustificada() {
        return totalfaltajustificada;
    }

    public void setTotalFaltaJustificada(int totalfaltajustificada) {
        this.totalfaltajustificada = totalfaltajustificada;
    }

    public int getTotalRetraso() {
        return totalretraso;
    }

    public void setTotalRetraso(int totalretraso) {
        this.totalretraso = totalretraso;
    }

    public int getTotalHoras() {
        return totalhoras;
    }

    public void setTotalHoras(int totalhoras) {
        this.totalhoras = totalhoras;
    }

    public double getNotaAsistencia() {
        return notadeasistencia;
    }

    public void setNotaAsistencia(double notadeasistencia) {
        this.notadeasistencia = notadeasistencia;
    }

    public double getPorcentajeAsistencia() {
        return porcentajeasistencia;
    }

    public void setPorcentajeAsistencia(double porcentajeasistencia) {
        this.porcentajeasistencia = porcentajeasistencia;
    }

    @Override
    public String toString() {
        return "AsistenciaTotalAsignatura{" +
                "nombrealumno='" + nombrealumno + '\'' +
                ", apellidosalumno='" + apellidosalumno + '\'' +
                ", asignatura='" + asignatura + '\'' +
                ", totalasiste=" + totalasiste +
                ", totalfalta=" + totalfalta +
                ", totalfaltajustificada=" + totalfaltajustificada +
                ", totalretraso=" + totalretraso +
                ", totalhoras=" + totalhoras +
                ", porcentajeasistencia=" + porcentajeasistencia +
                ", notadeasistencia=" + notadeasistencia +
                '}';
    }
}
