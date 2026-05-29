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
import java.util.function.Supplier;

import static java.text.MessageFormat.format;

/**
 * Class to manage a chronograph. The time is based on a {@code long}
 * milliseconds based method call.  It implements the {@link ChronographModel}
 * required to manage the {@link JChronograph} component class.
 *
 * @author Luis Colorado {@code <luiscoloradourcola@gmail.com>}
 */
public class DefaultChronographModel implements ChronographModel {

    public static final long serialVersionUID = 0;

    private long                  startTime,
                                  lapseTime,
                                  stopTime;

    private boolean               running;

    private final Supplier<Long>  timestamper;

    private PropertyChangeSupport pcs;

    @SuppressWarnings("this-escape")
    public DefaultChronographModel(Supplier<Long> ts) {
        timestamper = ts;
        pcs         = new PropertyChangeSupport(this);
    }

    public DefaultChronographModel() {
        this(System::currentTimeMillis);
    }
    
    @Override
    public void reset(long ts) {
        long old_totalTime = getTotalTime(ts);
        long old_lapseTime = getLapseTime(ts);
        startTime = lapseTime = stopTime = ts;
        // notify all listeners.
        System.out.println(format(
                "{0}.reset({1,number,0}), total={2,number,0}, old_lapse= {3,number,0}, lapse={3,number,0}",
                getClass().getSimpleName(),
                ts,
                getTotalTime(ts),
                old_lapseTime,
                getLapseTime(ts)));
        pcs.firePropertyChange(TOTAL_PROPERTY,
                old_totalTime, getTotalTime(ts));
        /* this binds TOTAL_PROPERTY */
        pcs.firePropertyChange(LAPSE_PROPERTY,
                0, 0); /* this binds LAPSE_PROPERTY */
    }

    @Override
    public void start(long ts) { /* this binds TOTAL_PROPERTY */
        boolean old_running   = running;
        long    old_totalTime = getTotalTime(ts),
                old_lapseTime = getLapseTime(ts),
                stopped       = ts - stopTime;
        startTime += stopped;
        lapseTime += stopped;
        running    = true;
        /* this binds RUNNING_PROPERTY */
        if (!old_running) {
            System.out.println(format(
                "{0}.start({1,number,0}), total={2,number,0}, old_lapse= {3,number,0}, lapse={3,number,0}",
                    getClass().getSimpleName(),
                    ts,
                    getTotalTime(ts),
                    old_lapseTime,
                    getLapseTime(ts)));
            pcs.firePropertyChange(RUNNING_PROPERTY,
                    old_running, running);
            /* this binds TOTAL_PROPERTY */
            pcs.firePropertyChange(TOTAL_PROPERTY,
                    old_totalTime, getTotalTime(ts));
            /* this binds TOTAL_PROPERTY */
            pcs.firePropertyChange(LAPSE_PROPERTY,
                    old_lapseTime, getLapseTime(ts));
        }
    }

    @Override
    public void stop(long ts) {
        boolean old_running   = running;
        long    old_totalTime = getTotalTime(ts),
                old_lapseTime = getLapseTime(ts);
        
        running     = false;
        lapseTime   = ts;
        stopTime    = ts;
        
        if (old_running) {
            /* binds RUNNING_PROPERTY */
            System.out.println(format(
                "{0}.stop({1,number,0}), total={2,number,0}, old_lapse= {3,number,0}, lapse={3,number,0}",
                    getClass().getSimpleName(),
                    ts,
                    getTotalTime(ts),
                    old_lapseTime,
                    getLapseTime(ts)));
            pcs.firePropertyChange(RUNNING_PROPERTY,
                    old_running, running);
            pcs.firePropertyChange(TOTAL_PROPERTY,
                    old_totalTime, getTotalTime(ts));
            pcs.firePropertyChange(LAPSE_PROPERTY,
                    old_lapseTime, getLapseTime(ts));
        }
    }

    @Override
    public void restart(long ts) {
        long old_lapseTime = getLapseTime(ts);
        stop(ts);
        start(ts);
        System.out.println(format(
                "{0}.restart({1,number,0}), total={2,number,0}, old_lapse= {3,number,0}, lapse={3,number,0}",
                getClass().getSimpleName(),
                ts,
                getTotalTime(ts),
                old_lapseTime,
                getLapseTime(ts)));
    }

    @Override
    public void lapse(long ts) {
        long old_totalTime = getTotalTime(ts),
             old_lapseTime = DefaultChronographModel.this.getLapseTime(ts);
        lapseTime = ts;
        System.out.println(format(
                "{0}.lapse({1,number,0}), total={2,number,0}, old_lapse= {3,number,0}, lapse={3,number,0}",
                getClass().getSimpleName(),
                ts,
                getTotalTime(ts),
                old_lapseTime,
                getLapseTime(ts)));
        pcs.firePropertyChange(TOTAL_PROPERTY,
                old_totalTime, getTotalTime(ts));
        pcs.firePropertyChange(LAPSE_PROPERTY,
                old_lapseTime, DefaultChronographModel.this.getLapseTime(ts));
    }

    @Override
    public long getLapseTime(long ts) {
        return ts - lapseTime;
    }

    @Override
    public long getTotalTime(long ts) {
        return ts - startTime;
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

    public long getTotalTime() {
        return getTotalTime(timestamper.get());
    }

    public long getLapseTime() {
        return getLapseTime(timestamper.get());
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
