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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static es.lcssl.chrono.gui.ChronographModel.format_timestamp;

/**
 * Class to manage a chronograph. The time is based on a {@code long}
 * milliseconds based method call.  It implements the {@link ChronographModel}
 * required to manage the {@link JChronograph} component class.
 *
 * @author Luis Colorado {@code <luiscoloradourcola@gmail.com>}
 */
public class DefaultChronographModel implements ChronographModel {

    private static final Logger   log =
            LogManager.getLogger(DefaultChronographModel.class);

    private static final String FORMAT = "{0}{1}.{2}";

    private long                  startTime,
                                  lapseTime,
                                  stopTime;

    private boolean               running;

    private final Supplier<Long>  timestamper;

    private PropertyChangeSupport pcs;

    private String name;

    @SuppressWarnings("this-escape")
    public DefaultChronographModel(Supplier<Long> ts_provider) {
        timestamper = ts_provider;
        pcs         = new PropertyChangeSupport(this);
    }

    public DefaultChronographModel() {
        this(System::currentTimeMillis);
        name        = null;
    }

    @SuppressWarnings("this-escape")
    public DefaultChronographModel(
            Supplier<Long> ts_provider,
            String name) {
        timestamper = ts_provider;
        pcs         = new PropertyChangeSupport(this);
        this.name   = name;
    }

    public DefaultChronographModel(String name) {
        this(System::currentTimeMillis);
        this.name   = name;
    }

    @Override
    public void reset(long ts) {
        long[] old_values = getIntervals(ts);
        startTime = lapseTime = stopTime = ts;
        long[] new_values = getIntervals(ts);
        pcs.firePropertyChange(RESET_ACTION,
                old_values, new_values);
        log.info("{}: {}: ts={}, OLD: Total={},"
                + " Lapse={}; NEW: Total={}, Lapse={}",
                name, RESET_ACTION, ts,
                format_timestamp(old_values[TOTAL_TIME], FORMAT),
                format_timestamp(old_values[LAPSE_TIME], FORMAT),
                format_timestamp(new_values[TOTAL_TIME], FORMAT),
                format_timestamp(new_values[LAPSE_TIME], FORMAT));
    }

    @Override
    public void start(long ts) { /* this binds TOTAL_PROPERTY */
        if (running) return;

        long[] old_values = getIntervals(ts);
        long time_stopped = ts - stopTime;
        startTime += time_stopped;
        lapseTime += time_stopped;
        running    = true;
        long[] new_values = getIntervals(ts);
        pcs.firePropertyChange(RUNNING_PROPERTY,
                false, running);
        pcs.firePropertyChange(START_ACTION,
                old_values, new_values);
        log.info("{}: {}: ts={}, OLD: Total={},"
                + " Lapse={}; NEW: Total={}, Lapse={}",
                name, START_ACTION, ts,
                format_timestamp(old_values[TOTAL_TIME], FORMAT),
                format_timestamp(old_values[LAPSE_TIME], FORMAT),
                format_timestamp(new_values[TOTAL_TIME], FORMAT),
                format_timestamp(new_values[LAPSE_TIME], FORMAT));
    }

    @Override
    public void stop(long ts) {
        if (!running) return;

        long[] old_values = getIntervals(ts);
        running  = false;
        stopTime = ts;
        long[]  new_values    = getIntervals(ts);

        pcs.firePropertyChange(RUNNING_PROPERTY,
                true, false);
        pcs.firePropertyChange(STOP_ACTION,
                old_values, new_values);
        log.info("{}: {}: ts={}, OLD: Total={},"
                + " Lapse={}; NEW: Total={}, Lapse={}",
                name, STOP_ACTION, ts,
                format_timestamp(old_values[TOTAL_TIME], FORMAT),
                format_timestamp(old_values[LAPSE_TIME], FORMAT),
                format_timestamp(new_values[TOTAL_TIME], FORMAT),
                format_timestamp(new_values[LAPSE_TIME], FORMAT));
    }

    @Override
    public void restart(long ts) {
        if (!running) return;

        long[] old_values = getIntervals(ts);
        startTime = lapseTime = ts;
        long[] new_values = getIntervals(ts);
        pcs.firePropertyChange(RESTART_ACTION,
                old_values, new_values);
        log.info("{}: {}: ts={}, OLD: Total={},"
                + " Lapse={}; NEW: Total={}, Lapse={}",
                name, RESTART_ACTION, ts,
                format_timestamp(old_values[TOTAL_TIME], FORMAT),
                format_timestamp(old_values[LAPSE_TIME], FORMAT),
                format_timestamp(new_values[TOTAL_TIME], FORMAT),
                format_timestamp(new_values[LAPSE_TIME], FORMAT));
    }

    @Override
    public void lapse(long ts) {
        long[] old_values = getIntervals(ts);
        lapseTime = ts;
        long[] new_values = getIntervals(ts);
        pcs.firePropertyChange(LAPSE_ACTION,
                old_values, new_values);
        log.info("{}: {}: ts={}, OLD: Total={},"
                + " Lapse={}; NEW: Total={}, Lapse={}",
                name, LAPSE_ACTION, ts,
                format_timestamp(old_values[TOTAL_TIME], FORMAT),
                format_timestamp(old_values[LAPSE_TIME], FORMAT),
                format_timestamp(new_values[TOTAL_TIME], FORMAT),
                format_timestamp(new_values[LAPSE_TIME], FORMAT));
    }

    @Override
    public long[] getIntervals(long ts) {
        return new long[] { ts - startTime, ts - lapseTime };
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        String old_name = getName();
        this.name = name;
        String new_name = getName();
        if (!Objects.equals(old_name, new_name))
            pcs.firePropertyChange(NAME_PROPERTY,
                    old_name, new_name);
    }

    public void reset() {
        reset(timestamper.get());
    }

    public void start() {
        start(timestamper.get());
    }

    public void restart() {
        restart(timestamper.get());
    }

    public void lapse() {
        lapse(timestamper.get());
    }

    public void stop() {
        stop(timestamper.get());
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public long getTimestamp() {
        return timestamper.get();
    }

    public long getStartTime() {
        return startTime;
    }

    @Override
    public void addPropertyChangeListener(
            String                 property_name,
            PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(property_name, listener);
    }

    @Override
    public void removePropertyChangeListener(
            String                 property_name,
            PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(property_name, listener);
    }
}
