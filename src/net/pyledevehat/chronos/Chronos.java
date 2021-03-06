/*
 *
 *   Copyright 2012 Pierre-Yves Le Dévéhat
 *
 *     This file is part of Chronos.
 *
 *
 *
 *   Chronos is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Chronos is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with Chronos.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.pyledevehat.chronos;

import android.app.Activity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.view.View.OnClickListener;
import android.graphics.Color;
import android.graphics.Typeface;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class Chronos extends Activity
{

    private ScrollView sv;
    private LinearLayout l1;


    private char etat = 'd';
    private boolean cont = false;


    private TextView dec;

    private Button dem;
    private Button vider;

    private long tempsDepart;
    private long tempsActuel;
    private long tempsPasse;
    private long tempsTotal;
    private Calendar cal = Calendar.getInstance();


    private Timer t = new Timer();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        dem = (Button) this.findViewById(R.id.demarrer);
        vider = (Button) this.findViewById(R.id.vider);
        Button mem = (Button) this.findViewById(R.id.memoriser);
        Button reinit = (Button) this.findViewById(R.id.reinitialiser);
        Button quit = (Button) this.findViewById(R.id.quitter);

        dec = (TextView) this.findViewById(R.id.decompte);

        sv = (ScrollView) this.findViewById(R.id.liste);
        l1 = (LinearLayout) this.findViewById(R.id.cont_liste);

        launch();

        dem.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                switch(etat) {
                    case 'd': 
                        etat = 'a';
                        Chronos.this.dem.setText(R.string.stoper);
                        Chronos.this.tempsTotal = 0;
                        Chronos.this.tempsDepart = System.currentTimeMillis();
                        Chronos.this.dec.setTextColor(Color.parseColor("#FF6666"));
                        cont = true;
                        break;
                    case 'a': 
                        etat = 'r';
                        Chronos.this.dem.setText(R.string.redemarrer);
                        cont = false;
                        Chronos.this.tempsTotal += tempsPasse;
                        Chronos.this.dec.setTextColor(Color.parseColor("#888888"));
                        break;
                    case 'r': 
                        etat = 'a';
                        Chronos.this.dem.setText(R.string.stoper);
                        Chronos.this.tempsDepart = System.currentTimeMillis();
                        cont = true;
                        Chronos.this.dec.setTextColor(Color.parseColor("#FF6666"));
                }

            }
        });

        mem.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {

                TextView tv = new TextView(Chronos.this);
                tv.setText(Integer.toString(Chronos.this.l1.getChildCount() + 1)
                        + ".   "
                        + Chronos.format(Chronos.this.cal.get(Calendar.HOUR_OF_DAY))
                        + ":"
                        + Chronos.format(Chronos.this.cal.get(Calendar.MINUTE))
                        + "'"
                        + Chronos.format(Chronos.this.cal.get(Calendar.SECOND))
                        + "''"
                        + Chronos.format((int) Chronos.this.cal.get(Calendar.MILLISECOND) / 10));
                
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                tv.setTypeface(Typeface.DEFAULT_BOLD);
                
                Chronos.this.l1.addView(tv);

                Chronos.this.vider.setEnabled(true);

                Chronos.this.sv.post(new Runnable() {
                    public void run() {
                        Chronos.this.sv.fullScroll(View.FOCUS_DOWN);
                    }
                });

            }
        });

        reinit.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                Chronos.this.cont = false;
                Chronos.this.dem.setText(R.string.demarrer);
                Chronos.this.dec.setTextColor(Color.parseColor("#888888"));
                Chronos.this.etat = 'd';
                Chronos.this.dec.setText("00:00'00\"00");
                cal.setTimeInMillis(0);
            }
        });

        quit.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                Chronos.this.finish();
            }
        });

        vider.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                Chronos.this.l1.removeAllViews();
                Chronos.this.vider.setEnabled(false);
            }
        });

    }



        @Override
    protected void onDestroy() {
        super.onDestroy();
        t.cancel();
    }

    private void launch() {
        t.schedule(new TimerTask(){
            @Override
            public void run() {

                Chronos.this.runOnUiThread(new Runnable() {
                    public void run() {
                        if(cont) {
                            Chronos.this.tempsActuel = System.currentTimeMillis();
                            Chronos.this.tempsPasse = Chronos.this.tempsActuel - Chronos.this.tempsDepart;
                            Chronos.this.cal.setTimeInMillis(tempsTotal + tempsPasse);
                            Chronos.this.dec.setText(Chronos.format(Chronos.this.cal.get(Calendar.HOUR_OF_DAY))
                                    + ":"
                                    + Chronos.format(Chronos.this.cal.get(Calendar.MINUTE))
                                    + "'"
                                    + Chronos.format(Chronos.this.cal.get(Calendar.SECOND))
                                    + "\""
                                    + Chronos.format((int) Chronos.this.cal.get(Calendar.MILLISECOND) / 10));
                        }
                    }
                });
            }
        }, 0, 100);
    }


    private static String format(int i) {
        String s = Integer.toString(i);
        if(s.length() == 1) s = "0" + s;
        return s;
    }

}
