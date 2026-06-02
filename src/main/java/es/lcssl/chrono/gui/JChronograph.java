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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import org.apache.logging.log4j.Logger;

import static es.lcssl.chrono.gui.ChronographModel.NAME_PROPERTY;
import static es.lcssl.chrono.gui.ChronographModel.RUNNING_PROPERTY;
import static es.lcssl.chrono.gui.ChronographModel.RESET_ACTION;
import static es.lcssl.chrono.gui.ChronographModel.START_ACTION;
import static es.lcssl.chrono.gui.ChronographModel.RESTART_ACTION;
import static es.lcssl.chrono.gui.ChronographModel.LAPSE_ACTION;
import static es.lcssl.chrono.gui.ChronographModel.STOP_ACTION;
import static es.lcssl.chrono.gui.ChronographModel.TOTAL_TIME;
import static es.lcssl.chrono.gui.ChronographModel.LAPSE_TIME;
import static es.lcssl.chrono.gui.ChronographModel.format_timestamp;
import org.apache.logging.log4j.LogManager;

/**
 * This class implements a single chronograph with start/lapse and stop buttons
 *
 * @author Luis Colorado {@code <luiscoloradourcola@gmail.com>}
 */
@SuppressWarnings("serial")
public class JChronograph  extends JPanel {

    public static final int  DEFAULT_INITIAL_DELAY = 5000;
    public static final int  DEFAULT_DELAY         =   47;    
    
    public static final Logger LOG = LogManager.getLogger(JChronograph.class);

    private JLabel           total,
                             lapse;
    private JButtonForChrono startAndLapse,
                             stop,
                             reset;

    private final ChronographModel
                             model;
    private Timer            timer;
    private TitledBorder     title;

    private transient ActionListener
                             timerListener;

    private transient Action startAction,
                             lapseAction;

    private String           format =
            "<html><font size=+2>{0}{1}</font>.{2}";

    @SuppressWarnings("this-escape")
    public JChronograph(
            ChronographModel model,
            LayoutManager    layout,
            boolean          isDoubleBuffered)
    {
        super(layout, isDoubleBuffered);
        this.model = model;
        initialize();
        /* don't put anything after initialize() */
    }

    @SuppressWarnings("this-escape")
    public JChronograph(
            ChronographModel model,
            LayoutManager    layout)
    {
        super(layout);
        this.model = model;
        initialize();
    }

    @SuppressWarnings("this-escape")
    public JChronograph(
            ChronographModel model,
            boolean          isDoubleBuffered)
    {
        super(isDoubleBuffered);
        this.model = model;
        initialize();
    }

    @SuppressWarnings("this-escape")
    public JChronograph(
            ChronographModel model)
    {
        this.model = model;
        initialize();
    }

    @SuppressWarnings("this-escape")
    public JChronograph() {
        this.model = new DefaultChronographModel();
        initialize();
    }

    private void initialize() {

        String zero          = format_timestamp(0, format);
        JPanel panel         = new JPanel();
        LayoutManager layout = panel.getLayout();
        if (layout instanceof FlowLayout) {
            ((FlowLayout) layout).setAlignOnBaseline(true);
        }

        /* CONFIGURE THE PANEL */
        title = new TitledBorder(
                new EtchedBorder(EtchedBorder.LOWERED),
                model.getName());
        panel.setBorder(title);
        model.addPropertyChangeListener(
                NAME_PROPERTY,
                e -> {
                    title.setTitle((String) e.getNewValue());
                });

        /* CONFIGURE THE TOTAL LABEL */
        total = new JLabel("total");
        total.setBorder(
                new TitledBorder(
                        new EtchedBorder(EtchedBorder.LOWERED),
                        "Total"));
        total.setText(zero);
        panel.add(total);

        /* CONFIGURE THE LAPSE LABEL */
        lapse = new JLabel("lapse");
        lapse.setBorder(
                new TitledBorder(
                        new EtchedBorder(EtchedBorder.LOWERED),
                        "Lapse"));
        lapse.setText(zero);
        panel.add(lapse);

        /* ADD THE PROPERTY CHANGE LISTENERS TO THE MODEL */
        model.addPropertyChangeListener(
                RESET_ACTION,
                evt -> {
                    if (model.isRunning()) {
                        update((long[])evt.getOldValue());
                        timer.setInitialDelay(DEFAULT_INITIAL_DELAY);
                        timer.restart();
                    } else {
                        update((long[])evt.getNewValue());
                        timer.setInitialDelay(DEFAULT_DELAY);
                    }
                });
        model.addPropertyChangeListener(
                START_ACTION,
                evt -> {
                    update((long[]) evt.getNewValue());
                    timer.setInitialDelay(DEFAULT_DELAY);
                    timer.start();
                });
        model.addPropertyChangeListener(
                RESTART_ACTION,
                evt -> {
                    timer.stop();
                    update((long[]) evt.getOldValue());
                    timer.setInitialDelay(DEFAULT_INITIAL_DELAY);
                    timer.start();
                });
        model.addPropertyChangeListener(
                LAPSE_ACTION,
                evt -> {
                    timer.stop();
                    update((long[]) evt.getOldValue());
                    timer.setInitialDelay(DEFAULT_INITIAL_DELAY);
                    timer.start();
                });
        model.addPropertyChangeListener(
                STOP_ACTION,
                evt -> {
                    timer.stop();
                    update((long[]) evt.getOldValue());
                });

        /* CONFIGURE THE TIMER */
        timer = new Timer(
                DEFAULT_DELAY,
                e -> {
                    update(model.getIntervals(model.getTimestamp()));
                    timer.setInitialDelay(DEFAULT_INITIAL_DELAY);
                });
        timer.setInitialDelay(DEFAULT_INITIAL_DELAY);
        timer.setCoalesce(true);
        timer.addActionListener(timerListener);

        /* CONFIGURE START/LAPSE BUTTON */
        startAction = new AbstractAction("Start") {
            @Override
            public void actionPerformed(ActionEvent e) {
                //model.start(e.getWhen());
                model.start(startAndLapse.getLastButtonPressEvent().getWhen());
                LOG.debug("{}: button click delay: {}ms.",
                        getName(), 
                        (e.getWhen() 
                                - startAndLapse.getLastButtonPressEvent().getWhen()));
            }
        };
        startAndLapse = new JButtonForChrono(startAction);
        panel.add(startAndLapse);

        /* LAPSE ACTION */
        lapseAction = new AbstractAction("Lapse") {
            @Override
            public void actionPerformed(ActionEvent e) {
                //model.lapse(e.getWhen());
                model.lapse(startAndLapse.getLastButtonPressEvent().getWhen());
                LOG.debug("{}: button click delay: {}ms.",
                        getName(), 
                        (e.getWhen() 
                                - startAndLapse.getLastButtonPressEvent().getWhen()));
            }
        };

        /* CONFIGURE THE RUNNING PROPERTY CHANGE LISTENER */
        model.addPropertyChangeListener(
                RUNNING_PROPERTY,
                evt -> {
                    if (model.isRunning()) {
                        startAndLapse.setAction(lapseAction);
                    } else {
                        startAndLapse.setAction(startAction);
                    }
                });

        /* CONFIGURE THE STOP BUTTON */
        stop = new JButtonForChrono(new AbstractAction("Stop") {
            @Override
            public void actionPerformed(ActionEvent e) {
                //model.stop(e.getWhen());
                model.stop(stop.getLastButtonPressEvent().getWhen());
                LOG.debug("{}: button click delay: {}ms.",
                        getName(), 
                        (e.getWhen() 
                                - stop.getLastButtonPressEvent().getWhen()));
            }
        });
        panel.add(stop);

        /* CONFIGURE THE RESET BUTTON */
        reset = new JButtonForChrono(new AbstractAction("Reset") {
            @Override
            public void actionPerformed(ActionEvent e) {
                //model.reset(e.getWhen());
                model.reset(reset.getLastButtonPressEvent().getWhen());
                LOG.debug("{}: button click delay: {}ms.",
                        getName(), 
                        (e.getWhen() 
                                - reset.getLastButtonPressEvent().getWhen()));
            }
        });
        panel.add(reset);

        /* ADD THE PANEL TO this */
        add(panel);
    }

    private void update(long[] values) {
        total.setText(format_timestamp(values[TOTAL_TIME], format));
        lapse.setText(format_timestamp(values[LAPSE_TIME], format));
    }

    public ChronographModel getModel() {
        return model;
    }

    @Override
    public String getName() {
        return model.getName();
    }

    @Override
    public void setName(String new_name) {
        model.setName(new_name);
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
