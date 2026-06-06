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
import static java.text.MessageFormat.format;

/**
 * This is the ChronographModel interface.
 * @author Luis Colorado {@code <luiscoloradourcola@gmail.com>}
 */
public interface ChronographModel {

    public static final int TOTAL_TIME = 0,
                            LAPSE_TIME = 1;

    String
            NAME_PROPERTY    = "name",
            RUNNING_PROPERTY = "running",
            RESET_ACTION     = "reset",
            START_ACTION     = "start",
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

    String getName();

    void setName(String new_name);

    void addPropertyChangeListener(
            String property_name, PropertyChangeListener listener);

    void removePropertyChangeListener(
            String property_name, PropertyChangeListener listener);

    public static String  format_timestamp(long ts, String format) {
        int[] dividers = {1000, 60, 60, 24};
        int[] mods = new int[dividers.length];
        for (int i = 0; i < dividers.length; ++i) {
            mods[i] = (int) (ts % dividers[i]);
            ts /= dividers[i];
        }
        /* ts holds finally the number of days of the time */
        String s1 = ts > 0 ? format("{0,number,##0}d ", ts) : "",
               s2 = format("{0,number,00}:{1,number,00}:{2,number,00}",
                        mods[3], mods[2], mods[1]),
               s3 = format(".{0,number,000}", mods[0]);
        return format(format, s1, s2, s3);
    }
}
