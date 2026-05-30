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

import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;

/**
 * This is the ChronographModel interface.
 * @author Luis Colorado {@code <luiscoloradourcola@gmail.com>}
 */
public interface ChronographModel {
    
    int TOTAL_TIME = 0,
        LAPSE_TIME = 1;

    String
            RUNNING_PROPERTY = "running",
            RESET_ACTION     = "reset",
            START_ACTION     = "timing",
            RESTART_ACTION   = "restart",
            LAPSE_ACTION     = "lapse",
            STOP_ACTION      = "stop";

    void reset(long ts);

    void start(long ts);

    void restart(long ts);

    void lapse(long ts);

    void stop(long ts);

    boolean isRunning(); /* bound property */

    long[] getIntervals(long ts); /* bound property */
    
    long getTimestamp();
    
    void addPropertyChangeListener(
            String property_name, PropertyChangeListener listener);
    void removePropertyChangeListener(
            String property_name, PropertyChangeListener listener);
}
