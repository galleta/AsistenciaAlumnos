package modelo.asignatura;

/**
 * Created by francis on 4/01/18.
 */

public class Asignatura
{
    private int id;
    private String nombre, nombreabreviado, curso, ciclo;

    public Asignatura(int id, String nombre, String nombreabreviado, String curso, String ciclo) {
        this.id = id;
        this.nombre = nombre;
        this.nombreabreviado = nombreabreviado;
        this.curso = curso;
        this.ciclo = ciclo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombreAbreviado() {
        return nombreabreviado;
    }

    public void setNombreAbreviado(String nombreabreviado) {
        this.nombreabreviado = nombreabreviado;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    @Override
    public String toString() {
        return "Asignatura{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", nombreabreviado='" + nombreabreviado + '\'' +
                ", curso='" + curso + '\'' +
                ", ciclo='" + ciclo + '\'' +
                '}';
    }
}
