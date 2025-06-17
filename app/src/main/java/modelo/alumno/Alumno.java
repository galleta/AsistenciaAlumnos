package modelo.alumno;

import java.io.Serializable;

/**
 * Created by francis on 21/09/16.
 */

public class Alumno implements Serializable
{
    private int id;
    private String nombre, apellidos;

    public Alumno(int id, String nombre, String apellidos)
    {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
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

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    @Override
    public String toString() {
        return "Alumno{" +
                "\n\tid = " + id +
                "\n\n\tnombre = '" + nombre + '\'' +
                "\n\tapellidos = '" + apellidos + '\'' +
                '}';
    }
}
