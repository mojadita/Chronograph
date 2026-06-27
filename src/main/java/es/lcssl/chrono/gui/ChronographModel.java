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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import static java.text.MessageFormat.format;
import java.util.function.Supplier;

/**
 * This is the ChronographModel interface.
 *
 * @author Luis Colorado {@code <luiscoloradourcola@gmail.com>}
 */
public interface ChronographModel {

    /**
     * Index element to hold the total time in arrays returned by
     * {@link #getIntervals(long) }.
     *
     * @see #getIntervals(long)
     */
    static final int TOTAL_TIME = 0;

    /**
     * Index element to hold the lapse time in arrays returned by
     * {@link #getIntervals(long) }.
     */
    static final int LAPSE_TIME = 1;

    /**
     * Property name of the {@link #getName() } bounded attribute.
     */
    static final String NAME_PROPERTY    = "name";

    /**
     * Property name of the {@link #isRunning() } bounded attribute.
     */
    static final String RUNNING_PROPERTY = "running";

    /**
     * Property name of the {@link #reset(long) } bounded action.
     */
    static final String RESET_ACTION     = "reset";

    /**
     * Property name of the {@link #start(long) } bounded action.
     */
    static final String START_ACTION     = "start";

    /**
     * Property name of the {@link #restart(long) } bounded action.
     */
    static final String RESTART_ACTION   = "restart";

    /**
     * Property name of the {@link #lapse(long) } bounded action.
     */
    static final String LAPSE_ACTION     = "lapse";

    /**
     * Property name of the {@link #stop(long) } bounded action.
     */
    static final String STOP_ACTION      = "stop";

    /**
     * Reset action.  When a reset action is executed, {@link PropertyChangeEvent}s
     * are fired to all listeners of this action, signaling the appropiate
     * timestamps in total and lapse times.
     *
     * @param ts the timestamp of the time at which reset occurs.
     */
    void reset(long ts);

    /**
     * Start action.  When a start action is executed, {@link PropertyChangeEvent}s
     * are fired to all listeners of this action, signaling the appropiate
     * timestamps in total and lapse times.
     *
     * @param ts the timestamp of the time at which start occurs.
     */
    void start(long ts);

    /**
     * Restart action.  When a restart action is executed,
     * {@link PropertyChangeEvent}s
     * are fired to all listeners of this action, signaling the appropiate
     * timestamps in total and lapse times.
     *
     * @param ts the timestamp of the time at which restart occurs.
     */
    void restart(long ts);

    /**
     * Lapse action.  When a lapse action is executed,
     * {@link PropertyChangeEvent}s
     * are fired to all listeners of this action, signaling the appropiate
     * timestamps in total and lapse times.
     *
     * @param ts the timestamp of the time at which lapse occurs.
     */
    void lapse(long ts);

    /**
     * Stop action.  When a stop action is executed,
     * {@link PropertyChangeEvent}s
     * are fired to all listeners of this action, signaling the appropiate
     * timestamps in total and lapse times.
     *
     * @param ts the timestamp of the time at which stop occurs.
     */
    void stop(long ts);

    /**
     * Getter for the {@code running} property.  This property is bound, meaning
     * that any change on it will trigger the delivery of
     * {@link PropertyChangeEvent}s to the listeners of this property.
     *
     * @return a {@code boolean} result indicating if the
     * {@link ChronographModel} is running.
     */
    boolean isRunning(); /* bound property */

    /**
     * Getter for the current values of the timestamps for Total and Lapse
     * intervals.
     *
     * @param ts is the current timestamp at which the intervals are to be
     * measured.
     *
     * @return a pair of {@code long}s in a {@code long[]} array of two values.
     * The corresponding values of the values is {@link #TOTAL_TIME} for the
     * total time measured and {@link #LAPSE_TIME} for the lapse time.
     */
    long[] getIntervals(long ts); /* bound property */

    /**
     * This method asks the {@link ChronographModel} to get a timestamp from the
     * internal timestamp {@link Supplier}{@code <}{@link Long}{@code >}.
     *
     * @return a {@code long} value indicating the current timestamp.
     */
    long getTimestamp();

    /**
     * Getter for the {@code name} property.  This property should bound.
     * @return the {@link String} value of the property.
     */
    String getName();

    /**
     * Setter for {@code name} property.  This property should be bound,
     * meaning that a {@link PropertyChangeEvent} is fired when this
     * property changes.
     *
     * @param new_name is the {@link String} new name to set the model.
     */
    void setName(String new_name);

    /**
     * Property change events registry function.  It registers a
     * {@link PropertyChangeListener} to be notified when a property change
     * is detected.
     *
     * @param property_name  the name of the property involved in the change or
     * the action that triggered it.
     *
     * @param listener the {@link PropertyChangeListener} reference of the
     * listener to be notified when the event required is triggered.
     */
    void addPropertyChangeListener(
            String property_name, PropertyChangeListener listener);

    /**
     * Unregisters a {@link PropertyChangeListener}.
     * @param property_name is the {@link String} property name which is to be
     * used to unregister.
     *
     * @param listener the {@link PropertyChangeListener} to be unregistered.
     */
    void removePropertyChangeListener(
            String property_name, PropertyChangeListener listener);

    /**
     * Convenience function to format timestamps in a way that is convenient to
     * use them in the different modules that will use it.
     *
     * @param interval  The time interval to be formatted (the time duration
     * given in milliseconds)
     *
     * @param format The formatting string.  This {@link String} needs to have
     * three parameter substitutions ({@code {0}}), {@code {1}}) and {@code {2}})
     * to format the number of days (optional, appears as {@code ""} if no
     * days are to be included), the time excess for one day (in the format
     * {@code 00:00:00}) and the excess in milliseconds (to the second, in
     * the format {@code .000})  to allow formatting to include HTML code to
     * enlarge one or several fields, or change color, by interspersing html
     * tags between the three strings.
     *
     * @return a {@link String} with the formatted interval.
     */
    public static String  format_timestamp(long interval, String format) {
        int[] dividers = {1000, 60, 60, 24};
        int[] mods = new int[dividers.length];
        for (int i = 0; i < dividers.length; ++i) {
            mods[i] = (int) (interval % dividers[i]);
            interval /= dividers[i];
        }
        /* ts holds finally the number of days of the time */
        String s1 = interval > 0 ? format("{0,number,##0}d ", interval) : "",
               s2 = format("{0,number,00}:{1,number,00}:{2,number,00}",
                        mods[3], mods[2], mods[1]),
               s3 = format(".{0,number,000}", mods[0]);
        return format(format, s1, s2, s3);
    }
}
