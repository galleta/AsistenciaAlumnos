package modelo.asistencia;

/**
 * Created by francis on 24/09/16.
 */

public enum TipoAsistencia
{
    ASISTE("ASISTE"),
    FALTA("FALTA"),
    FALTAJUSTIFICADA("FALTAJUSTIFICADA"),
    RETRASO("RETRASO");

    private String stringValue;

    private TipoAsistencia(String toString)
    {
        stringValue = toString;
    }

    @Override
    public String toString()
    {
        return stringValue;
    }
}
