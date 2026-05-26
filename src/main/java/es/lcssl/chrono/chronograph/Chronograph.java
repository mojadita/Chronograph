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
package es.lcssl.chrono.chronograph;

import es.lcssl.chrono.gui.ChronographModel;
import java.beans.PropertyChangeSupport;
import java.util.function.Supplier;

/**
 * Class to manage a chronograph. The time is based on a {@code long}
 * milliseconds based method call.  It implements the {@link ChronographModel}
 * required to managee the {@link JChronograph} component class.
 *
 * @author Luis Colorado {@code <luiscoloradourcola@gmail.com>}
 */
public class Chronograph implements ChronographModel {
    
    public static final long serialVersionUID = 0;

    public static final String
            RUNNING_FIELD_NAME = "running",
            LAPSE_FIELD_NAME   = "lapse",
            TOTAL_FIELD_NAME   = "total";

    private long startTime, lapTime;

    private boolean running;

    private final Supplier<Long> timestamper;

    private PropertyChangeSupport pcs;

    public Chronograph(Supplier<Long> ts) {
        timestamper = ts;
        pcs = new PropertyChangeSupport(this);
        reset();
    }

    public Chronograph() {
        this(() -> System.currentTimeMillis());
    }

    protected long registerTimestamp(long ts) {
        long old_lapTime = getLapTime(ts);
        lapTime = ts; /* reset lapTime */
        long new_lapTime = getLapTime(ts);
        if (old_lapTime != new_lapTime)
            pcs.firePropertyChange(LAPSE_FIELD_NAME,
                    old_lapTime, new_lapTime);
        return ts;
    }

    @Override
    public void reset(long ts) {
        long old_total = getTotalTime(ts);
        registerTimestamp(ts);
        startTime = ts;
        pcs.firePropertyChange(TOTAL_FIELD_NAME,
                old_total, getTotalTime(ts));
    }

    @Override
    public void start(long ts) {
        boolean old_running = running;
        startTime += getLapTime(ts);
        registerTimestamp(ts);
        running = true;
        if (old_running != running) {
            if (pcs == null)
                pcs = new PropertyChangeSupport(this);
            pcs.firePropertyChange(RUNNING_FIELD_NAME,
                    old_running, running);
        }
    }

    @Override
    public void restart(long ts) {
        stop(ts);
        start(ts);
    }

    @Override
    public void lapse(long ts) {
        if (running) {
            registerTimestamp(ts);
        }
    }

    @Override
    public void stop(long ts) {
        registerTimestamp(ts);
        running = false;
    }

    @Override
    public long getLapTime(long ts) {
        return ts - lapTime;
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

    public long getLapTime() {
        return getLapTime(timestamper.get());
    }

    public long getStartTime() {
        return startTime;
    }
}
