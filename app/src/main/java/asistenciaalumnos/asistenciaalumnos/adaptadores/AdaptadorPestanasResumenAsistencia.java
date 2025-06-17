package asistenciaalumnos.asistenciaalumnos.adaptadores;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import asistenciaalumnos.asistenciaalumnos.dialogos.FragmentResumenAsistenciaPorDia;
import asistenciaalumnos.asistenciaalumnos.dialogos.FragmentResumenAsistenciaTotalAsignatura;

/**
 * Created by francis on 25/09/16.
 */

public class AdaptadorPestanasResumenAsistencia extends FragmentStatePagerAdapter
{
    int mNumOfTabs;

    public AdaptadorPestanasResumenAsistencia(FragmentManager fm, int NumOfTabs)
    {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position)
    {
        switch (position)
        {
            case 0:
                FragmentResumenAsistenciaPorDia tab1 = new FragmentResumenAsistenciaPorDia();
                return tab1;
            case 1:
                FragmentResumenAsistenciaTotalAsignatura tab2 = new FragmentResumenAsistenciaTotalAsignatura();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount()
    {
        return mNumOfTabs;
    }
}
