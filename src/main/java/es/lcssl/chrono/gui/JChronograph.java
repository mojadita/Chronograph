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

import es.lcssl.chrono.chronograph.Chronograph;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeSupport;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import javax.swing.JFrame;

import static java.text.MessageFormat.format;
import static javax.swing.JFrame.EXIT_ON_CLOSE;

/**
 * This class implements a single chronograph with start/lapse and stop buttons
 *
 * @author Luis Colorado {@code <luiscoloradourcola@gmail.com>}
 */
public class JChronograph extends JPanel {

    public static final long serialVersionUID = 0;

    public static final int DEFAULT_INITIAL_DELAY = 3000;
    public static final int DEFAULT_DELAY = 47;

    private JLabel total, lapse;
    private JButton startAndLapse, stop, reset;

    private ChronographModel model;
    private Timer timer;

    private ActionListener timerAction;

    private Action startAction,
            lapseAction,
            stopAction,
            resetAction;

    PropertyChangeSupport propertyChangeSupport
            = new PropertyChangeSupport(this);

    public JChronograph(
            ChronographModel model,
            LayoutManager layout,
            boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
        this.model = model;
        initialize();
    }

    public JChronograph(
            ChronographModel model,
            LayoutManager layout) {
        super(layout);
        this.model = model;
        initialize();
    }

    public JChronograph(
            ChronographModel model,
            boolean isDoubleBuffered) {
        super(isDoubleBuffered);
        this.model = model;
        initialize();
    }

    public JChronograph(ChronographModel model) {
        this.model = model;
        initialize();
    }

    public JChronograph() {
        initialize();
    }

    private void initialize() {
        total = new JLabel("total");
        lapse = new JLabel("lapse");

        timerAction = (ActionEvent e) -> {
            if (model.isRunning()) // CAN BE SPURIOUS?
            {
                update(e.getWhen());
            }
            timer.setInitialDelay(DEFAULT_INITIAL_DELAY);
        };

        startAction = new AbstractAction("Start") {
            @Override
            public void actionPerformed(ActionEvent e) {
                startAndLapse.setAction(lapseAction);
                model.start(e.getWhen());
                timer.setInitialDelay(DEFAULT_DELAY);
                timer.start();
                timer.addActionListener(timerAction);
            }
        };

        lapseAction = new AbstractAction("Lapse") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!timer.isRunning()
                        || timer.getInitialDelay() == DEFAULT_INITIAL_DELAY) {
                    update(e.getWhen());
                }
                model.lapse(e.getWhen());
                timer.restart();
            }
        };

        stopAction = new AbstractAction("Stop") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (timer.isRunning()) {
                    update(e.getWhen());
                }
                timer.stop();
                model.stop(e.getWhen());
                startAndLapse.setAction(startAction);
            }
        };

        resetAction = new AbstractAction("Reset") {
            @Override
            public void actionPerformed(ActionEvent e) {
                timer.stop();
                boolean running = model.isRunning();
                if (running) {
                    update(e.getWhen());
                }
                model.reset(e.getWhen());
                if (!running) {
                    update(e.getWhen());
                } else {
                    timer.start();
                }
            }
        };

        startAndLapse = new JButton(startAction);
        stop = new JButton(stopAction);
        reset = new JButton(resetAction);

        timer = new Timer(DEFAULT_DELAY, timerAction);
        timer.setInitialDelay(DEFAULT_INITIAL_DELAY);
        timer.setCoalesce(true);
        timer.addActionListener(timerAction);

        JPanel panel = new JPanel();
        panel.add(total);
        panel.add(lapse);
        panel.add(startAndLapse);
        panel.add(stop);
        panel.add(reset);

        long ts = System.currentTimeMillis();
        model.reset(ts);
        update(ts);
        this.add(panel);
    }

    private String format_timestamp(long ts) {
        int[] dividers = {1000, 60, 60, 24};
        int[] mods = new int[dividers.length];
        for (int i = 0; i < dividers.length; ++i) {
            mods[i] = (int) (ts % dividers[i]);
            ts /= dividers[i];
        }
        String s1 = ts > 0 ? format("{0,number,##000d}d ", ts) : "",
                s2 = format("{0,number,00}:{1,number,00}:{2,number,00}",
                        mods[3], mods[2], mods[1]),
                s3 = format("{0,number,000}",
                        mods[0]);
        return format("<html><font size=+1>{0}{1}</font>.{2}</html>",
                s1, s2, s3);
    }

    private void update(long ts) {
        total.setText(format_timestamp(model.getTotalTime(ts)));
        lapse.setText(format_timestamp(model.getLapTime(ts)));
    }

    public JLabel getTotal() {
        return total;
    }

    public void setTotal(JLabel total) {
        this.total = total;
    }

    public JLabel getLapse() {
        return lapse;
    }

    public void setLapse(JLabel lapse) {
        this.lapse = lapse;
    }

    public JButton getStartAndLapse() {
        return startAndLapse;
    }

    public void setStartAndLapse(JButton startAndLapse) {
        this.startAndLapse = startAndLapse;
    }

    public JButton getStop() {
        return stop;
    }

    public void setStop(JButton stop) {
        this.stop = stop;
    }

    public JButton getReset() {
        return reset;
    }

    public void setReset(JButton reset) {

    }

    public ChronographModel getModel() {
        return model;
    }

    public void setModel(ChronographModel model) {
        this.model = model;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer.removeActionListener(timerAction);
        Timer old = this.timer;
        this.timer = timer;

        this.timer.addActionListener(timerAction);
    }
}
