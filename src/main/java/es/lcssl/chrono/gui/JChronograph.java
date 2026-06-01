/*
 * Copyright (c) 2026, Luis Colorado <luiscoloradourcola@gmail.com>.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package es.lcssl.chrono.gui;

import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.FlowLayout;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import static java.text.MessageFormat.format;
import static es.lcssl.chrono.gui.ChronographModel.RUNNING_PROPERTY;
import static es.lcssl.chrono.gui.ChronographModel.RESET_ACTION;
import static es.lcssl.chrono.gui.ChronographModel.START_ACTION;
import static es.lcssl.chrono.gui.ChronographModel.RESTART_ACTION;
import static es.lcssl.chrono.gui.ChronographModel.LAPSE_ACTION;
import static es.lcssl.chrono.gui.ChronographModel.STOP_ACTION;
import static es.lcssl.chrono.gui.ChronographModel.TOTAL_TIME;
import static es.lcssl.chrono.gui.ChronographModel.LAPSE_TIME;

/**
 * This class implements a single chronograph with start/lapse and stop buttons
 *
 * @author Luis Colorado {@code <luiscoloradourcola@gmail.com>}
 */
@SuppressWarnings("serial")
public class JChronograph  extends JPanel {

    public static final int DEFAULT_INITIAL_DELAY = 5000;
    public static final int DEFAULT_DELAY         =   47;

    private JLabel           total,
                             lapse;
    private JButton          startAndLapse,
                             stop,
                             reset;

    private ChronographModel model;
    private Timer            timer;

    private transient ActionListener
                             timerListener;

    private transient Action startAction,
                             lapseAction;

    private String           name;

    @SuppressWarnings("this-escape")
    public JChronograph(
            ChronographModel model,
            LayoutManager layout,
            boolean isDoubleBuffered,
            String name) {
        super(layout, isDoubleBuffered);
        this.model = model;
        this.name  = name;
        initialize();
        /* don't put anything after initialize() */
    }

    @SuppressWarnings("this-escape")
    public JChronograph(
            ChronographModel model,
            LayoutManager layout,
            String name) {
        super(layout);
        this.model = model;
        this.name  = name;
        initialize();
    }

    @SuppressWarnings("this-escape")
    public JChronograph(
            ChronographModel model,
            boolean isDoubleBuffered,
            String name) {
        super(isDoubleBuffered);
        this.model = model;
        this.name  = name;
        initialize();
    }

    @SuppressWarnings("this-escape")
    public JChronograph(ChronographModel model, String name) {
        this.model = model;
        this.name  = name;
        initialize();
    }

    @SuppressWarnings("this-escape")
    public JChronograph(String name) {
        this.model = new DefaultChronographModel();
        this.name = name;
        initialize();
    }

    private void initialize() {
        String zero  = format_timestamp(0);
        JPanel panel = new JPanel();
        ((FlowLayout) panel.getLayout()).setAlignOnBaseline(true);
        panel.setBorder(
                new TitledBorder(
                        new EtchedBorder(EtchedBorder.LOWERED),
                        name));
        total = new JLabel("total");
        total.setBorder(
                new TitledBorder(
                        new EtchedBorder(EtchedBorder.LOWERED),
                        "Total"));
        total.setText(zero);
        panel.add(total);

        lapse = new JLabel(format_timestamp(0));
        lapse.setBorder(
                new TitledBorder(
                        new EtchedBorder(EtchedBorder.LOWERED),
                        "Lapse"));
        lapse.setText(zero);
        panel.add(lapse);

        model.addPropertyChangeListener(RESET_ACTION, evt -> {
                    if (model.isRunning()) {
                        update((long[])evt.getOldValue());
                        timer.setInitialDelay(DEFAULT_INITIAL_DELAY);
                        timer.restart();
                    } else {
                        update((long[])evt.getNewValue());
                        timer.setInitialDelay(DEFAULT_DELAY);
                    }
                });
        model.addPropertyChangeListener(START_ACTION, evt -> {
                    update((long[]) evt.getNewValue());
                    timer.setInitialDelay(DEFAULT_DELAY);
                    timer.start();
                });
        model.addPropertyChangeListener(RESTART_ACTION, evt -> {
                    timer.stop();
                    update((long[]) evt.getOldValue());
                    timer.setInitialDelay(DEFAULT_INITIAL_DELAY);
                    timer.start();
                });
        model.addPropertyChangeListener(LAPSE_ACTION, evt -> {
                    timer.stop();
                    update((long[]) evt.getOldValue());
                    timer.setInitialDelay(DEFAULT_INITIAL_DELAY);
                    timer.start();
                });
        model.addPropertyChangeListener(STOP_ACTION, evt -> {
                    timer.stop();
                    update((long[]) evt.getOldValue());
                });

        timer = new Timer(DEFAULT_DELAY, e -> {
            // if (!model.isRunning()) return; // CAN BE SPURIOUS?
            update(model.getIntervals(model.getTimestamp()));
            timer.setInitialDelay(DEFAULT_INITIAL_DELAY);
        });

        timer.setInitialDelay(DEFAULT_INITIAL_DELAY);
        timer.setCoalesce(true);
        timer.addActionListener(timerListener);

        startAction = new AbstractAction("Start") {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.start(e.getWhen());
            }
        };
        startAndLapse = new JButton(startAction);
        panel.add(startAndLapse);

        /* this will be used when changing startAndLapse JButton to this
         * action. */
        lapseAction = new AbstractAction("Lapse") {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.lapse(e.getWhen());
            }
        };

        model.addPropertyChangeListener(RUNNING_PROPERTY, evt ->{
            if (model.isRunning()) {
                startAndLapse.setAction(lapseAction);
            } else {
                startAndLapse.setAction(startAction);
            }
        });

        stop = new JButton(new AbstractAction("Stop") {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.stop(e.getWhen());
            }
        });
        panel.add(stop);

        reset = new JButton(new AbstractAction("Reset") {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.reset(e.getWhen());
            }
        });
        panel.add(reset);

        add(panel);
        model.reset(System.currentTimeMillis());
    }

    private String format_timestamp(long ts) {
        int[] dividers = {1000, 60, 60, 24};
        int[] mods = new int[dividers.length];
        for (int i = 0; i < dividers.length; ++i) {
            mods[i] = (int) (ts % dividers[i]);
            ts /= dividers[i];
        }
        /* ts holds finally the number of days of the time */
        String s1 = ts > 0 ? format("{0,number,##000}d ", ts) : "",
               s2 = format("{0,number,00}:{1,number,00}:{2,number,00}",
                        mods[3], mods[2], mods[1]),
               s3 = format("{0,number,000}",
                        mods[0]);
        return format("<html><font size=+1>{0}{1}</font>.{2}</html>",
                s1, s2, s3);
    }

    private void update(long[] values) {
        total.setText(format_timestamp(values[TOTAL_TIME]));
        lapse.setText(format_timestamp(values[LAPSE_TIME]));
    }

    public ChronographModel getModel() {
        return model;
    }

    public Timer getTimer() {
        return timer;
    }
}
