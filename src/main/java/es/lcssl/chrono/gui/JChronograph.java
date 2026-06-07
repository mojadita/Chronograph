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
import java.awt.FlowLayout;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.ImageIcon;
import org.apache.logging.log4j.Logger;
import java.awt.Font;
import org.apache.logging.log4j.LogManager;

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
import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class implements a single chronograph with start/lapse, stop and
 * reset buttons.
 *
 * @author Luis Colorado {@code <luiscoloradourcola@gmail.com>}
 */
@SuppressWarnings("serial")
public class JChronograph  extends JPanel {

    /**
     * Delay to be hold the automatic updater from starting to update
     * continuously timestamps, to allow the user to properly read the
     * total and lapse intervals.
     */
    public static final int  DEFAULT_INITIAL_DELAY = 5000;

    /**
     * Inter update time (millisecs) to hold the updater between updates.
     * The value used is something allowing a refresh time of 20 updates per
     * second, but also prime with that value to avoid aliasing problems making
     * some digits to synchronize with the screen refresh.
     */
    public static final int  DEFAULT_DELAY         =   47;

    /**
     * {@link Logger} used for logging purposes.
     */
    public static final Logger
                             LOG = LogManager.getLogger(JChronograph.class);

    /**
     * {@link ResourceBundle} instance to provide localized strings for user
     * messages.
     */
    private ResourceBundle   INTL;

    /**
     * {@link JLabel} to represent the total time interval.
     */
    private JLabel           total;

    /**
     * {@link JLabel} to represent the lapse time interval.
     */
    private JLabel           lapse;

    /**
     * {@link JButtonForChrono} button to allow the event processing to
     * access the button press timestamp associated to the button click,
     * instead of the button release that marks the action event.
     *
     * This button serves two functions, as start button and lapse button.
     *
     * When the button is configured as start button it holds the start
     * {@link Action} instance; When configured as a lapse button (as it
     * is started) it is configured with the lapse {@link Action}.
     */
    private JButtonForChrono startAndLapse;

    /**
     * {@link JButtonForChrono} button to generate stop events.
     *
     * @see #startAndLapse button.
     */
    private JButtonForChrono stop;

    /**
     * {@link JButtonForChrono} button to generate reset events.
     *
     * @see #startAndLapse button.
     */
    private JButtonForChrono reset;

    /**
     * {@link ChronographModel} model for this instance.
     *
     * Several {@link JChronograph}s can share the same model,
     * generating events for it and receiving the updates from it.
     */
    private final ChronographModel
                             model;
    /**
     * {@link Timer} instance to continuously update the chronograph while it
     * runs.
     *
     * The timer instance restarts the timer with a different start time when
     * some event produces intervals that must be read by the user.  But when
     * the chronograph starts from a stopped state, the updates start
     * immediately.
     *
     * @see #DEFAULT_INITIAL_DELAY
     * @see #DEFAULT_DELAY
     */
    private Timer            timer;

    /**
     * {@link TitledBorder} used to delimit the chronograph limits and to show
     * the {@link ChronographModel} name in the top left corner.
     *
     * When a {@code null} name is used as the model name, the border is still
     * drawn, but no title is shown then.
     */
    private TitledBorder     title;

    /**
     * {@link Action} to be associated to the {@link #startAndLapse} button,
     * when it acts as a start button.
     *
     * @see #initialize() method to see how these {@link Action}s are
     * initialized.
     */
    private transient Action startAction;

    /**
     * {@link Action} to be associated to the {@link #startAndLapse} button,
     * when it acts as a lapse button.
     *
     * @see #initialize() method to see how these {@link Action}s are
     * initialized.
     */
    private transient Action lapseAction;

    /**
     * Default format to use in the {@link JChronograph} widget.
     *
     * The default format uses HTML tags to make the days, hours, mins, and secs
     * bigger than the milliseconds excess time.
     */
    private String           format =
            "<html><font size=+2>{0}{1}</font>{2}";

    /**
     * Complete constructor with model, layout manager and is double buffered
     * parameters.
     *
     * @param model {@link ChronographModel} to use to hold the chronograph
     * state.
     *
     * @param layout {@link LayoutManager} to be used for placing the internal
     * widgets of the chronograph.
     *
     * @param isDoubleBuffered {@code boolean} value to use double buffered
     * screen updates.
     */
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

    /**
     * Convenience constructor using a {@link LayoutManager} and a
     * {@link ChronographModel} only.
     *
     * The {@code isDoubleBuffered} property is assumed by default as
     * configured in the superclass constructor.
     *
     * @param model {@link ChronographModel} to use initially.
     * @param layout {@link LayoutManager} to use initially.
     */
    @SuppressWarnings("this-escape")
    public JChronograph(
            ChronographModel model,
            LayoutManager    layout)
    {
        super(layout);
        this.model = model;
        initialize();
    }

    /**
     * Convenience constructor using only the {@link #model} and
     * the {@code isDoubleBuffered} properties.
     *
     * @param model {@link ChronographModel} to be used as state data holder.
     *
     * @param isDoubleBuffered {@code boolean} value indicating to do double
     * buffering of contents to the screen.
     */
    @SuppressWarnings("this-escape")
    public JChronograph(
            ChronographModel model,
            boolean          isDoubleBuffered)
    {
        super(isDoubleBuffered);
        this.model = model;
        initialize();
    }

    /**
     * Simple convenience constructor using only the {@code model} reference.
     *
     * @param model reference to the {@link ChronographModel} instance to use
     * as state data.
     */
    @SuppressWarnings("this-escape")
    public JChronograph(
            ChronographModel model)
    {
        this.model = model;
        initialize();
    }

    /**
     * Default, parameter less constructor.
     *
     * A simple default model is created for the instance and the normal
     * initialization routines are called.
     */
    @SuppressWarnings("this-escape")
    public JChronograph() {
        this.model = new DefaultChronographModel();
        initialize();
    }

    /**
     * Auxiliary method to get a {@link String} value from a {@link String}
     * property name from a locale based {@code .properties} file.
     *
     * @param name {@link String} parameter name to be searched.
     * @return the {@link String} parameter value from the {@code .properties}
     * file.
     */
    private String _res_(String name) {
        return INTL.getString(name);
    }

    /**
     * Initialization routine.
     *
     * Common routine to initialize all the instance fields in every
     * constructor.
     */
    private void initialize() {

        INTL = ResourceBundle.getBundle(getClass().getName());
        Font font = Font.getFont(_res_("LABELS_FONT"));
        String zero          = format_timestamp(0, format);
        LayoutManager layout = getLayout();
        if (layout instanceof FlowLayout) {
            ((FlowLayout) layout).setAlignOnBaseline(true);
        }

        format =
            "<html><font size=+2>{0}{1}</font>{2}";

        /* CONFIGURE THE PANEL */
        title = new TitledBorder(
                new EtchedBorder(EtchedBorder.LOWERED),
                model.getName());
        setBorder(title);
        model.addPropertyChangeListener(
                NAME_PROPERTY,
                evt -> {
                    title.setTitle((String) evt.getNewValue());
                });

        /* CONFIGURE THE TOTAL LABEL */
        total = new JLabel("total");
        total.setBorder(
                new TitledBorder(
                        new EtchedBorder(EtchedBorder.LOWERED),
                        _res_("TOTAL_TITLE")));
        total.setText(zero);
        total.setFont(font);
        add(total);

        /* CONFIGURE THE LAPSE LABEL */
        lapse = new JLabel("lapse");
        lapse.setBorder(
                new TitledBorder(
                        new EtchedBorder(EtchedBorder.LOWERED),
                        _res_("LAPSE_TITLE")));
        lapse.setText(zero);
        lapse.setFont(font);

        add(lapse);

        /* ADD THE PROPERTY CHANGE LISTENERS TO THE MODEL */
        model.addPropertyChangeListener(
                RESET_ACTION,
                evt -> {
                    if (model.isRunning()) {
                        timer.stop();
                        update((long[])evt.getOldValue());
                        timer.setInitialDelay(DEFAULT_INITIAL_DELAY);
                        timer.start();
                    } else {
                        update((long[])evt.getNewValue());
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

        /* CONFIGURE START/LAPSE BUTTON */
        final String BUTTON_CLICK_DELAY_MESSAGE_FORMAT =
                _res_("BUTTON_CLICK_DELAY_MESSAGE_FORMAT");
        startAction = new AbstractAction(
                _res_("START_ACTION_NAME"),
                new ImageIcon(getClass()
                        .getClassLoader()
                        .getResource(INTL.getString("START_ICON"))))
        {
            @Override
            public void actionPerformed(ActionEvent e) {
                //model.start(e.getWhen());
                model.start(startAndLapse.getLastButtonPressEvent().getWhen());
                LOG.debug(BUTTON_CLICK_DELAY_MESSAGE_FORMAT,
                        getName(),
                        (e.getWhen()
                                - startAndLapse.getLastButtonPressEvent()
                                        .getWhen()));
            }
        };
        startAndLapse = new JButtonForChrono(startAction);
        startAndLapse.setHorizontalTextPosition(JLabel.CENTER);
        startAndLapse.setVerticalTextPosition(JLabel.BOTTOM);
        add(startAndLapse);

        /* LAPSE ACTION */
        lapseAction = new AbstractAction(
                _res_("LAPSE_ACTION_NAME"),
                new ImageIcon(getClass()
                        .getClassLoader()
                        .getResource(INTL.getString("LAPSE_ICON"))))
        {
            @Override
            public void actionPerformed(ActionEvent e) {
                //model.lapse(e.getWhen());
                model.lapse(startAndLapse.getLastButtonPressEvent().getWhen());
                LOG.debug(BUTTON_CLICK_DELAY_MESSAGE_FORMAT,
                        getName(),
                        (e.getWhen()
                                - startAndLapse.getLastButtonPressEvent()
                                        .getWhen()));
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
        stop = new JButtonForChrono(new AbstractAction(
                _res_("STOP_ACTION_NAME"),
                new ImageIcon(getClass()
                        .getClassLoader()
                        .getResource(INTL.getString("STOP_ICON"))))
        {
            @Override
            public void actionPerformed(ActionEvent e) {
                //model.stop(e.getWhen());
                model.stop(stop.getLastButtonPressEvent().getWhen());
                LOG.debug(BUTTON_CLICK_DELAY_MESSAGE_FORMAT,
                        getName(),
                        (e.getWhen()
                                - stop.getLastButtonPressEvent().getWhen()));
            }
        });
        stop.setHorizontalTextPosition(JLabel.CENTER);
        stop.setVerticalTextPosition(JLabel.BOTTOM);
        add(stop);

        /* CONFIGURE THE RESET BUTTON */
        reset = new JButtonForChrono(new AbstractAction(
                _res_("RESET_ACTION_NAME"),
                new ImageIcon(getClass()
                        .getClassLoader()
                        .getResource(INTL.getString("RESET_ICON")))) {
            @Override
            public void actionPerformed(ActionEvent e) {
                //model.reset(e.getWhen());
                model.reset(reset.getLastButtonPressEvent().getWhen());
                LOG.debug(BUTTON_CLICK_DELAY_MESSAGE_FORMAT,
                        getName(),
                        (e.getWhen()
                                - reset.getLastButtonPressEvent().getWhen()));
            }
        });
        reset.setHorizontalTextPosition(JLabel.CENTER);
        reset.setVerticalTextPosition(JLabel.BOTTOM);

        add(reset);
    }

    /**
     * Update the interval values passed in the {@code values} parameter to
     * both {@link JLabel} instances for total and lapse times.
     *
     * @param values a {@code long[]} array of two interval values
     * (measured in milliseconds) to represent total and lapse times.
     */
    private void update(long[] values) {
        total.setText(format_timestamp(values[TOTAL_TIME], format));
        lapse.setText(format_timestamp(values[LAPSE_TIME], format));
    }

    /**
     * Getter for the {@code model} parameter.
     *
     * @return a {@link ChronographModel} representing the model used by this
     * instance.
     */
    public ChronographModel getModel() {
        return model;
    }

    /**
     * Getter for the name attribute.  The name used by this instance is the
     * same as the model name, so the widget name is overriden by the name of
     * the model set as widget model.
     *
     * @return a {@link String} name from the model.
     */
    @Override
    public String getName() {
        return model.getName();
    }

    /**
     * Setter for the name attribute.  The name used by this instance is the one
     * used by the model meaning that when we change the name we are also changing
     * the model's name (which is bound, so modifying one changes the other)
     *
     * This property is bound in the model, so this instance is tied to the model
     * on changes for this property.
     *
     * @param new_name is the {@link String} new name to be used in the model.
     */
    @Override
    public void setName(String new_name) {
        model.setName(new_name);
    }

    /**
     * Getter for the format to be used in the times shown in the labels.
     *
     * Both labels use the same format, so they cannot be given different
     * formatting.
     *
     * @return the {@link String} representing the format used to display
     * the intervals.
     */
    public String getFormat() {
        return format;
    }

    /**
     * Setter for the format to use in displaying the intervals of the total
     * and lapse times.
     *
     * @param format is the {@link String} value for the new format to use.
     */
    public void setFormat(String format) {
        this.format = format;
    }
}
