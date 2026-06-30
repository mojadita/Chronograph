/*
 * Copyright (c) 2026, Luis Colorado {@code <luiscoloradourcola@gmail.com>}.
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

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice.WindowTranslucency;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import javax.swing.JComponent;
import javax.swing.JFrame;
import java.awt.geom.AffineTransform;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.Timer;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.util.Date;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

import java.beans.BeanProperty;
import java.beans.PropertyChangeSupport;
import java.util.Objects;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import static java.awt.BasicStroke.CAP_ROUND;
import static java.awt.BasicStroke.CAP_SQUARE;
import static java.awt.BasicStroke.JOIN_BEVEL;
import static java.lang.Math.*;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;

/**
 *
 * @author Luis Colorado {@code <luiscoloradourcola@gmail.com>}
 */
public class JChrono extends JComponent {
    
    public static final String
            MAJOR_HOURS_FONT_PROPERTY_NAME = "majorHoursFont",
            MINOR_HOURS_FONT_PROPERTY_NAME = "minorHoursFont",
            MINUTES_FONT_PROPERTY_NAME = "minutesFont",
            HOUR_NAMES_PROPERTY_NAME = "hourNames",
            MIN_NAMES_PROPERTY_NAME = "minNames",
            TICK_START_PROPERTY_NAME = "tickStart",
            TICK_200MS_END_PROPERTY_NAME = "tick200msEnd",
            TICK_1S_END_PROPERTY_NAME = "tick1sEnd",
            TICK_5S_END_PROPERTY_NAME = "tick5sEnd",
            TICK_10S_END_PROPERTY_NAME = "tick10sEnd",
            SECS_LABEL_RADIUS_PROPERTY_NAME = "secsLabelRadius",
            TICK_1H_START_PROPERTY_NAME = "tick1hStart",
            TICK_1H_END_PROPERTY_NAME = "tick1hEnd",
            HOUR_LABEL_RADIUS_PROPERTY_NAME = "hourLabelRadius",
            SMALL_CIRCLE_RADIUS_PROPERTY_NAME = "smallCircleRadius",
            TICK_200MS_WIDTH_PROPERTY_NAME = "tick200msWidth",
            TICK_1S_WIDTH_PROPERTY_NAME = "tick1sWidth",
            TICK_5S_WIDTH_PROPERTY_NAME = "tick5sWidth",
            TICK_10S_WIDTH_PROPERTY_NAME = "tick10sWidth",
            TICK_1H_WIDTH_PROPERTY_NAME = "tick1hWidth",
            HOUR_TAIL_PROPERTY_NAME = "hourTail",
            MIN_TAIL_PROPERTY_NAME = "minTail",
            SEC_TAIL_PROPERTY_NAME = "secTail",
            HOUR_HEAD_PROPERTY_NAME = "hourHead",
            MIN_HEAD_PROPERTY_NAME = "minHead",
            SEC_HEAD_PROPERTY_NAME = "secHead",
            HOUR_HAND_WIDTH_PROPERTY_NAME = "hourHandWidth",
            MIN_HAND_WIDTH_PROPERTY_NAME = "minHandWidth",
            SEC_HAND_WIDTH_PROPERTY_NAME = "secHandWidth",
            NUM_HOURS_DAY_PROPERTY_NAME = "numHoursDay",
            NUM_HOURS_CYCLE_PROPERTY_NAME = "numHoursCycle",
            HIGHLIGHTED_HOURS_PROPERTY_NAME = "highligtedHours",
            TICK_200MS_COLOR_PROPERTY_NAME = "tick200msColor",
            TICK_1S_COLOR_PROPERTY_NAME = "tick1sColor",
            TICK_5S_COLOR_PROPERTY_NAME = "tick5sColor",
            TICK_10S_COLOR_PROPERTY_NAME = "tick10sColor",
            TICK_1H_COLOR_PROPERTY_NAME = "tick1hColor",
            LABEL_5S_COLOR_PROPERTY_NAME = "label5sColor",
            LABEL_1H_MAJOR_COLOR_PROPERTY_NAME = "label1hMajorColor",
            LABEL_1H_MINOR_COLOR_PROPERTY_NAME = "label1hMinorColor",
            HOUR_HAND_COLOR_PROPERTY_NAME = "hourHandColor",
            MINUTE_HAND_COLOR_PROPERTY_NAME = "minuteHandColor",
            SECOND_HAND_COLOR_PROPERTY_NAME = "secondHandColor",
            SMALL_CIRCLE_COLOR_PROPERTY_NAME = "smallCircleColor";
            

    private Font majorHoursFont = new Font("Bahnschrift", Font.BOLD,             15);
    private Font minorHoursFont = new Font("Bahnschrift", Font.ITALIC,            9);
    private Font minutesFont    = new Font("Bahnschrift", Font.ROMAN_BASELINE,   11);
    
    private String[] hourNames = {
        "0", "1", "2", "3", "4", "5",
        "6", "7", "8", "9", "10", 
        "11", "12", "13", "14", "15",
        "16", "17", "18", "19", "20",
        "21", "22", "23",
    };
    
    private String[] minNames = {
        "0", "5", "10", "15", "20", "25",
        "30", "35", "40", "45", "50", "55",
    };
    
    double  tickStart         =  0.990, /* tick radial start positon (1.0 for all) */
            tick200msEnd      =  0.975,
            tick1sEnd         =  0.960,
            tick5sEnd         =  0.945,
            tick10sEnd        =  0.930,
            secsLabelRadius   =  0.820,
            tick1hStart       =  0.710,
            tick1hEnd         =  0.650,
            hourLabelRadius   =  0.540,
            smallCircleRadius =  0.030,
            tick200msWidth    =  0.005,
            tick1sWidth       =  0.008,
            tick5sWidth       =  0.016,
            tick10sWidth      =  0.020,
            tick1hWidth       =  0.012,
            hourTail          = -0.050,
            minTail           = -0.100,
            secTail           = -0.150,
            hourHead          =  0.450,
            minHead           =  0.750,
            secHead           =  0.890,
            hourHandWidth     =  0.030,
            minHandWidth      =  0.020,
            secHandWidth      =  0.008;
    int     numHours          = 24,
            numHoursCycle     = 24,
            hilightedHours    =  8;
    
    Color   tick200msColor,
            tick1sColor,
            tick5sColor,
            tick10sColor,
            tick1hColor,
            lbl5sColor,
            lbl1hMajorColor,
            lbl1hMinorColor,
            hourHandColor,
            minuteHandColor,
            secondHandColor,
            smallCircleColor;
    
    PropertyChangeSupport pcs;

    @BeanProperty
    public Font getMajorHoursFont() {
        return majorHoursFont;
    }

    public void setMajorHoursFont(Font p_majorHoursFont) {
        Font old_majorHoursFont = majorHoursFont;
        majorHoursFont = p_majorHoursFont;
        if (!Objects.equals(majorHoursFont, old_majorHoursFont)) {
            pcs.firePropertyChange(
                    MAJOR_HOURS_FONT_PROPERTY_NAME,
                    old_majorHoursFont, majorHoursFont);
        }
    }

    @BeanProperty
    public Font getMinorHoursFont() {
        return minorHoursFont;
    }

    public void setMinorHoursFont(Font p_minorHoursFont) {
        Font old_minorHoursFont = minorHoursFont;
        minorHoursFont = p_minorHoursFont;
        if (!Objects.equals( old_minorHoursFont, minorHoursFont)) {
            pcs.firePropertyChange(
                    MINOR_HOURS_FONT_PROPERTY_NAME,
                    old_minorHoursFont, minorHoursFont);
        }
    }

    @BeanProperty
    public Font getMinutesFont() {
        return minutesFont;
    }

    public void setMinutesFont(Font p_minutesFont) {
        Font old_minutesFont = minutesFont;
        minutesFont = p_minutesFont;
        if (!Objects.equals( old_minutesFont, minutesFont)) {
            pcs.firePropertyChange(
                    MINUTES_FONT_PROPERTY_NAME,
                    old_minutesFont, minutesFont);
        }
    }

    @BeanProperty
    public String getHourNames(int i) {
        return hourNames[i];
    }

    public void setHourNames(int i, String p_hourNames) {
        String old_hourNames = hourNames[i];
        hourNames[i] = p_hourNames;
        if (!Objects.equals( old_hourNames, p_hourNames)) {
            pcs.fireIndexedPropertyChange(
                    HOUR_NAMES_PROPERTY_NAME, i,
                    old_hourNames, p_hourNames);
        }
    }

    @BeanProperty
    public String getMinNames(int i) {
        return minNames[i];
    }

    public void setMinNames(int i, String p_minNames) {
        String old_minNames = minNames[i];
        minNames[i] = p_minNames;
        if (!Objects.equals( old_minNames, p_minNames)) {
            pcs.fireIndexedPropertyChange(
                    MIN_NAMES_PROPERTY_NAME, i,
                    old_minNames, p_minNames);
        }
    }

    @BeanProperty
    public double getTickStart() {
        return tickStart;
    }

    public void setTickStart(double p_tickStart) {
        double old_tickStart = tickStart;
        tickStart = p_tickStart;
        if (old_tickStart != p_tickStart) {
            pcs.firePropertyChange(
                    TICK_START_PROPERTY_NAME,
                    old_tickStart, p_tickStart);
        }
    }

    @BeanProperty
    public double getTick200msEnd() {
        return tick200msEnd;
    }

    public void setTick200msEnd(double p_tick200msEnd) {
        double old_tick200msEnd = tick200msEnd;
        tick200msEnd = p_tick200msEnd;
        if (old_tick200msEnd != p_tick200msEnd) {
            pcs.firePropertyChange(
                    TICK_200MS_END_PROPERTY_NAME,
                    old_tick200msEnd, p_tick200msEnd);
        }
    }

    @BeanProperty
    public double getTick1sEnd() {
        return tick1sEnd;
    }

    public void setTick1sEnd(double p_tick1sEnd) {
        double old_ticks1sEnd = tick1sEnd;
        tick1sEnd = p_tick1sEnd;
        if (old_ticks1sEnd != p_tick1sEnd) {
            pcs.firePropertyChange(
                    TICK_1S_END_PROPERTY_NAME,
                    old_ticks1sEnd, p_tick1sEnd);
        }
    }

    @BeanProperty
    public double getTick5sEnd() {
        return tick5sEnd;
    }

    public void setTick5sEnd(double p_tick5sEnd) {
        double old_tick5sEnd = tick5sEnd;
        tick5sEnd = p_tick5sEnd;
        if (old_tick5sEnd != p_tick5sEnd) {
            pcs.firePropertyChange(
                    TICK_5S_END_PROPERTY_NAME,
                    old_tick5sEnd, p_tick5sEnd);
        }
    }

    @BeanProperty
    public double getTick10sEnd() {
        return tick10sEnd;
    }

    public void setTick10sEnd(double p_tick10sEnd) {
        double old_tick10sEnd = tick10sEnd;
        tick10sEnd = p_tick10sEnd;
        if (old_tick10sEnd != p_tick10sEnd) {
            pcs.firePropertyChange(
                    TICK_10S_END_PROPERTY_NAME,
                    old_tick10sEnd, p_tick10sEnd);
        }
    }

    @BeanProperty
    public double getSecsLabelRadius() {
        return secsLabelRadius;
    }

    public void setSecsLabelRadius(double p_secsLabelRadius) {
        double old_secsLabelRadius = secsLabelRadius;
        secsLabelRadius = p_secsLabelRadius;
        if (old_secsLabelRadius != p_secsLabelRadius) {
            pcs.firePropertyChange(
                    SECS_LABEL_RADIUS_PROPERTY_NAME,
                    old_secsLabelRadius, p_secsLabelRadius);
        }
    }

    @BeanProperty
    public double getTick1hStart() {
        return tick1hStart;
    }

    public void setTick1hStart(double p_tick1hStart) {
        double old_tick1hStart = tick1hStart;
        tick1hStart = p_tick1hStart;
        if (old_tick1hStart != p_tick1hStart) {
            pcs.firePropertyChange(
                    TICK_1H_START_PROPERTY_NAME,
                    old_tick1hStart, p_tick1hStart);
        }
    }

    @BeanProperty
    public double getTick1hEnd() {
        return tick1hEnd;
    }

    public void setTick1hEnd(double p_tick1hEnd) {
        double old_tick1hEnd = tick1hEnd;
        tick1hEnd = p_tick1hEnd;
        if (old_tick1hEnd != p_tick1hEnd) {
            pcs.firePropertyChange(
                    TICK_1H_END_PROPERTY_NAME,
                    old_tick1hEnd, p_tick1hEnd);
        }
    }

    @BeanProperty
    public double getHourLabelRadius() {
        return hourLabelRadius;
    }

    public void setHourLabelRadius(double p_hourLabelRadius) {
        double old_hourLabelRadius = hourLabelRadius;
        hourLabelRadius = p_hourLabelRadius;
        if (old_hourLabelRadius != p_hourLabelRadius) {
            pcs.firePropertyChange(
                    HOUR_LABEL_RADIUS_PROPERTY_NAME,
                    old_hourLabelRadius, p_hourLabelRadius);
        }
    }

    @BeanProperty
    public double getSmallCircRadius() {
        return smallCircleRadius;
    }

    public void setSmallCircRadius(double p_smallCircRadius) {
        double old_smallCircleRadius = smallCircleRadius;
        smallCircleRadius = p_smallCircRadius;
        if (old_smallCircleRadius != p_smallCircRadius) {
            pcs.firePropertyChange(
                    SMALL_CIRCLE_RADIUS_PROPERTY_NAME,
                    old_smallCircleRadius, p_smallCircRadius);
        }
    }

    @BeanProperty
    public double getTick200msWidth() {
        return tick200msWidth;
    }

    public void setTick200msWidth(double p_tick200msWidth) {
        double old_tick200msWidth = tick200msWidth;
        tick200msWidth = p_tick200msWidth;
        if (old_tick200msWidth != p_tick200msWidth) {
            pcs.firePropertyChange( TICK_200MS_END_PROPERTY_NAME,
                    old_tick200msWidth, p_tick200msWidth);
        }
    }

    @BeanProperty
    public double getTick1sWidth() {
        return tick1sWidth;
    }

    public void setTick1sWidth(double p_tick1sWidth) {
        double old_tick1sWidth = tick1sWidth;
        tick1sWidth = p_tick1sWidth;
        if (old_tick1sWidth != p_tick1sWidth) {
            pcs.firePropertyChange(
                    TICK_1S_WIDTH_PROPERTY_NAME,
                    old_tick1sWidth, p_tick1sWidth);
        }
    }

    @BeanProperty
    public double getTick5sWidth() {
        return tick5sWidth;
    }

    public void setTick5sWidth(double p_tick5sWidth) {
        double old_setTick5sWidth = tick5sWidth;
        tick5sWidth = p_tick5sWidth;
        if (old_setTick5sWidth != p_tick5sWidth) {
            pcs.firePropertyChange(
                    TICK_5S_WIDTH_PROPERTY_NAME,
                    old_setTick5sWidth, p_tick5sWidth);
        }
    }

    @BeanProperty
    public double getTick10sWidth() {
        return tick10sWidth;
    }

    public void setTick10sWidth(double p_tick10sWidth) {
        double old_tick10sWidth = tick10sWidth;
        tick10sWidth = p_tick10sWidth;
        if (old_tick10sWidth != p_tick10sWidth) {
            pcs.firePropertyChange(
                    TICK_10S_WIDTH_PROPERTY_NAME,
                    old_tick10sWidth, p_tick10sWidth);
        }
    }

    @BeanProperty
    public double getTick1hWidth() {
        return tick1hWidth;
    }

    public void setTick1hWidth(double p_tick1hWidth) {
        double old_tick1hWidth = tick1hWidth;
        tick1hWidth = p_tick1hWidth;
        if (old_tick1hWidth != p_tick1hWidth) {
            pcs.firePropertyChange(
                    TICK_1H_WIDTH_PROPERTY_NAME,
                    old_tick1hWidth, p_tick1hWidth);
        }
    }

    @BeanProperty
    public double getHourTail() {
        return hourTail;
    }

    public void setHourTail(double p_hourTail) {
        double old_hourTail = hourTail;
        hourTail = p_hourTail;
        if (old_hourTail != p_hourTail) {
            pcs.firePropertyChange(
                    HOUR_TAIL_PROPERTY_NAME,
                    old_hourTail, p_hourTail);
        }
    }

    @BeanProperty
    public double getMinTail() {
        return minTail;
    }

    public void setMinTail(double p_minTail) {
        double old_minTail = minTail;
        minTail = p_minTail;
        if (old_minTail != p_minTail) {
            pcs.firePropertyChange(
                    MIN_TAIL_PROPERTY_NAME,
                    old_minTail, p_minTail);
        }
    }

    @BeanProperty
    public double getSecTail() {
        return secTail;
    }

    public void setSecTail(double p_secTail) {
        double old_secTail = secTail;
        secTail = p_secTail;
        if (old_secTail != p_secTail) {
            pcs.firePropertyChange(
                    SEC_TAIL_PROPERTY_NAME,
                    old_secTail, p_secTail);
        }
    }

    @BeanProperty
    public double getHourHead() {
        return hourHead;
    }

    public void setHourHead(double p_hourHead) {
        double old_hourHead = hourHead;
        hourHead = p_hourHead;
        if (old_hourHead != p_hourHead) {
            pcs.firePropertyChange(
                    HOUR_HEAD_PROPERTY_NAME,
                    old_hourHead, p_hourHead);
        }
    }

    @BeanProperty
    public double getMinHead() {
        return minHead;
    }

    public void setMinHead(double p_minHead) {
        double old_minHead = minHead;
        minHead = p_minHead;
        if (old_minHead != p_minHead) {
            pcs.firePropertyChange( 
                    MIN_HEAD_PROPERTY_NAME,
                    old_minHead, p_minHead);
        }
    }

    @BeanProperty
    public double getSecHead() {
        return secHead;
    }

    public void setSecHead(double p_secHead) {
        double old_secHead = secHead;
        secHead = p_secHead;
        if (old_secHead != p_secHead) {
            pcs.firePropertyChange(
                    SEC_HEAD_PROPERTY_NAME,
                    old_secHead, p_secHead );
        }
    }

    @BeanProperty
    public double getHourHandWidth() {
        return hourHandWidth;
    }

    public void setHourHandWidth(double p_hourHandWidth) {
        double old_hourHandWidth = hourHandWidth;
        hourHandWidth = p_hourHandWidth;
        if (old_hourHandWidth != p_hourHandWidth) {
            pcs.firePropertyChange(
                    HOUR_HAND_WIDTH_PROPERTY_NAME,
                    old_hourHandWidth, p_hourHandWidth);
        }
    }

    @BeanProperty
    public double getMinHandWidth() {
        return minHandWidth;
    }

    public void setMinHandWidth(double p_minHandWidth) {
        double old_minHandWidth = minHandWidth;
        minHandWidth = p_minHandWidth;
        if (old_minHandWidth != p_minHandWidth) {
            pcs.firePropertyChange(
                    MIN_HAND_WIDTH_PROPERTY_NAME,
                    old_minHandWidth, p_minHandWidth);
        }
    }

    @BeanProperty
    public double getSecHandWidth() {
        return secHandWidth;
    }

    public void setSecHandWidth(double p_secHandWidth) {
        double old_secHandWidth = secHandWidth;
        secHandWidth = p_secHandWidth;
        if (old_secHandWidth != p_secHandWidth) {
            pcs.firePropertyChange(
                    SEC_HAND_WIDTH_PROPERTY_NAME,
                    old_secHandWidth, p_secHandWidth);
        }
    }

    @BeanProperty
    public int getNumHours() {
        return numHours;
    }

    public void setNumHours(int p_numHours) {
        int old_numHours = numHours;
        numHours = p_numHours;
        if (old_numHours != p_numHours) {
            pcs.firePropertyChange( 
                    NUM_HOURS_DAY_PROPERTY_NAME,
                    old_numHours,
                    p_numHours);
        }
    }

    @BeanProperty
    public int getNumHoursCycle() {
        return numHoursCycle;
    }

    public void setNumHoursCycle(int p_numHoursCycle) {
        int old_numHoursCycle = numHoursCycle;
        numHoursCycle = p_numHoursCycle;
        if (old_numHoursCycle != p_numHoursCycle) {
            pcs.firePropertyChange(
                    NUM_HOURS_CYCLE_PROPERTY_NAME,
                    old_numHoursCycle, p_numHoursCycle);
        }
    }

    @BeanProperty
    public int getHilightedHours() {
        return hilightedHours;
    }

    public void setHilightedHours(int p_hilightedHours) {
        int old_hilightedHours = hilightedHours;
        hilightedHours = p_hilightedHours;
        if (old_hilightedHours != p_hilightedHours) {
            pcs.firePropertyChange(
                    HIGHLIGHTED_HOURS_PROPERTY_NAME,
                    old_hilightedHours, p_hilightedHours);
        }
    }

    @BeanProperty
    public Color getTick200msColor() {
        return tick200msColor;
    }

    public void setTick200msColor(Color p_tick200msColor) {
        Color old_tick200msColor = tick200msColor;
        tick200msColor = p_tick200msColor;
        if (!Objects.equals( old_tick200msColor, p_tick200msColor)) {
            pcs.firePropertyChange(
                    TICK_200MS_COLOR_PROPERTY_NAME,
                    old_tick200msColor, p_tick200msColor);
        }
    }

    @BeanProperty
    public Color getTick1sColor() {
        return tick1sColor;
    }

    public void setTick1sColor(Color p_tick1sColor) {
        Color old_tick1sColor = tick1sColor;
        tick1sColor = p_tick1sColor;
        if (!Objects.equals( old_tick1sColor, p_tick1sColor)) {
            pcs.firePropertyChange(
                    TICK_1S_COLOR_PROPERTY_NAME,
                    old_tick1sColor, p_tick1sColor);
        }
    }

    @BeanProperty
    public Color getTick5sColor() {
        return tick5sColor;
    }

    public void setTick5sColor(Color p_tick5sColor) {
        Color old_tick5sColor = tick5sColor;
        tick5sColor = p_tick5sColor;
        if (!Objects.equals(old_tick5sColor, p_tick5sColor)) {
            pcs.firePropertyChange(
                    TICK_5S_COLOR_PROPERTY_NAME,
                    old_tick5sColor, p_tick5sColor);
        }
    }

    @BeanProperty
    public Color getTick10sColor() {
        return tick10sColor;
    }

    public void setTick10sColor(Color p_tick10sColor) {
        Color old_tick10sColor = tick10sColor;
        tick10sColor = p_tick10sColor;
        if (!Objects.equals( old_tick10sColor, p_tick10sColor)) {
            pcs.firePropertyChange(
                    TICK_10S_COLOR_PROPERTY_NAME,
                    old_tick10sColor, p_tick10sColor);
        }
    }

    @BeanProperty
    public Color getTick1hColor() {
        return tick1hColor;
    }

    public void setTick1hColor(Color p_tick1hColor) {
        Color old_tick1hColor = tick1hColor;
        tick1hColor = p_tick1hColor;
        if (!Objects.equals( old_tick1hColor, p_tick1hColor)) {
            pcs.firePropertyChange(
                    TICK_1H_COLOR_PROPERTY_NAME,
                    old_tick1hColor, p_tick1hColor);
        }
    }

    @BeanProperty
    public Color getLbl5sColor() {
        return lbl5sColor;
    }

    public void setLbl5sColor(Color p_lbl5sColor) {
        Color old_lbl5sColor = lbl5sColor;
        lbl5sColor = p_lbl5sColor;
        if (!Objects.equals( old_lbl5sColor, p_lbl5sColor)) {
            pcs.firePropertyChange(
                    LABEL_5S_COLOR_PROPERTY_NAME,
                    old_lbl5sColor, p_lbl5sColor);
        }
    }

    @BeanProperty
    public Color getLbl1hMajorColor() {
        return lbl1hMajorColor;
    }

    public void setLbl1hMajorColor(Color p_lbl1hMajorColor) {
        Color old_lbl1hMajorColor = lbl1hMajorColor;
        lbl1hMajorColor = p_lbl1hMajorColor;
        if (!Objects.equals( old_lbl1hMajorColor, p_lbl1hMajorColor)) {
            pcs.firePropertyChange(
                    LABEL_1H_MAJOR_COLOR_PROPERTY_NAME,
                    old_lbl1hMajorColor, p_lbl1hMajorColor);
        }
    }

    @BeanProperty
    public Color getLbl1hMinorColor() {
        return lbl1hMinorColor;
    }

    public void setLbl1hMinorColor(Color p_lbl1hMinorColor) {
        Color old_lbl1hMinorColor = lbl1hMinorColor;
        lbl1hMinorColor = p_lbl1hMinorColor;
        if (!Objects.equals( old_lbl1hMinorColor, p_lbl1hMinorColor)) {
            pcs.firePropertyChange(
                    LABEL_1H_MINOR_COLOR_PROPERTY_NAME,
                    old_lbl1hMinorColor, p_lbl1hMinorColor);
        }
    }

    @BeanProperty
    public Color getHourHandColor() {
        return hourHandColor;
    }

    public void setHourHandColor(Color p_hourHandColor) {
        Color old_hourHandColor = hourHandColor;
        hourHandColor = p_hourHandColor;
        if (!Objects.equals( old_hourHandColor, p_hourHandColor)) {
            pcs.firePropertyChange(
                    HOUR_HAND_COLOR_PROPERTY_NAME,
                    old_hourHandColor, p_hourHandColor);
        }
    }

    @BeanProperty
    public Color getMinuteHandColor() {
        return minuteHandColor;
    }

    public void setMinuteHandColor(Color p_minuteHandColor) {
        Color old_minuteHandColor = minuteHandColor;
        minuteHandColor = p_minuteHandColor;
        if (!Objects.equals( old_minuteHandColor, p_minuteHandColor)) {
            pcs.firePropertyChange(
                    MINUTE_HAND_COLOR_PROPERTY_NAME,
                    old_minuteHandColor, p_minuteHandColor);
        }
    }

    @BeanProperty
    public Color getSecondHandColor() {
        return secondHandColor;
    }

    public void setSecondHandColor(Color p_secondHandColor) {
        Color old_secondHandColor = secondHandColor;
        secondHandColor = p_secondHandColor;
        if (!Objects.equals( old_secondHandColor, p_secondHandColor)) {
            pcs.firePropertyChange(
                    SECOND_HAND_COLOR_PROPERTY_NAME,
                    old_secondHandColor, p_secondHandColor);
        }
    }

    @BeanProperty
    public Color getSmallCircleColor() {
        return smallCircleColor;
    }

    public void setSmallCircleColor(Color p_smallCircleColor) {
        Color old_smallCircleColor = smallCircleColor;
        smallCircleColor = p_smallCircleColor;
        if (!Objects.equals( old_smallCircleColor, p_smallCircleColor)) {
            pcs.firePropertyChange(
                    SMALL_CIRCLE_COLOR_PROPERTY_NAME,
                    old_smallCircleColor, p_smallCircleColor);
        }
    }
            
    public static final int
                TICKS_200MS = 0,
                TICKS_1S    = 1,
                TICKS_5S    = 2,
                TICKS_10S   = 3,
                TICKS_1H    = 4;
    
    private GeneralPath[] theTicks;
    private BasicStroke[] theStrokes;
    private GeneralPath hourHand        = new GeneralPath(),
                        minuteHand      = new GeneralPath(),
                        secondHand      = new GeneralPath();
    
    public JChrono () {
        GeneralPath[] the_ticks = new GeneralPath [] {
            new GeneralPath(), /* 200ms ticks */
            new GeneralPath(), /*   1s  ticks */
            new GeneralPath(), /*   5s  ticks */
            new GeneralPath(), /*  10s  ticks */
            new GeneralPath(), /*   1h  ticks */
        };
        theTicks = the_ticks;
        double ticks_end[] = {
            tick200msEnd,
            tick1sEnd,
            tick5sEnd,
            tick10sEnd,
        };
        
        /* seconds/200ms ticks */
        final int TOTAL = 300;
        for (int i = 0; i < TOTAL; ++i) {
            int which =
                      (i % 50 == 0)
                        ? TICKS_10S
                    : (i % 25 == 0)
                        ? TICKS_5S
                    : (i % 5 == 0)
                        ? TICKS_1S
                    : TICKS_200MS;
            
            double angle = i * 2.0 * PI / TOTAL;
            double sin_angle = sin(angle),
                   cos_angle = cos(angle);
            the_ticks[which].moveTo(
                    tickStart * sin_angle,
                    tickStart * cos_angle);
            the_ticks[which].lineTo(
                    ticks_end[which] * sin_angle,
                    ticks_end[which] * cos_angle);
        }
    
        for (int i = 0; i < numHoursCycle; ++i) {
            double angle = i * 2.0 * PI / numHoursCycle;
            double sin_angle = sin(angle),
                   cos_angle = cos(angle);
            the_ticks[TICKS_1H].moveTo(
                    tick1hStart * sin_angle,
                    tick1hStart * cos_angle );
            the_ticks[TICKS_1H].lineTo(
                    tick1hEnd * sin_angle,
                    tick1hEnd * cos_angle);
        }
        hourHand   = new GeneralPath();
        minuteHand = new GeneralPath();
        secondHand = new GeneralPath();
        hourHand  .moveTo(0.0, hourTail);
        hourHand  .lineTo(0.0, 0.0);
        hourHand  .lineTo(0.0, hourHead);
        minuteHand.moveTo(0.0, minTail);
        minuteHand.lineTo(0.0, minHead);
        secondHand.moveTo(0.0, secTail);
        secondHand.lineTo(0.0, secHead);
        
        timer.start();
    }

    Timer timer = new Timer(23,
            e -> { invalidate(); repaint(); } );

    private void drawTicks(
            Graphics2D g,
            Shape ticks,
            int cap,
            double radius,
            double width,
            Color color,
            AffineTransform transf)
    {
            g.setStroke(new BasicStroke(
                    (float) (width * radius), cap, JOIN_BEVEL ) );
            g.setColor(color);
            g.draw( transf.createTransformedShape( ticks ) );
    }
    
    private void drawHand(
            Graphics2D      g,
            Shape           path,
            AffineTransform scale,
            double          angle,
            float           l_w,
            int             cap,
            Color           color)
    {
        AffineTransform scale_n_rotate = (AffineTransform) scale.clone();
        scale_n_rotate.concatenate(AffineTransform.getRotateInstance(-angle ) );
        g.setStroke( new BasicStroke(l_w, cap, JOIN_BEVEL) );
        g.setColor(color);
        g.draw( scale_n_rotate.createTransformedShape( path ) );
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g;
        
        /* get a new graphics2D so we can mangle it */
        g2d = (Graphics2D) g2d.create();
        

        g2d.setRenderingHint(
                KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        

        /* we don't paint on the insets zone */
        Insets insets = getInsets();
        int width = getWidth(), height = getHeight();

        width  -= insets.left + insets.right;
        height -= insets.top  + insets.bottom;

        
        double radius = Math.min(width, height)/2.0;
        g2d.translate(insets.left + width/2, insets.top + height/2);

        /* Draw the ticks */
 		/* 200ms ticks */
        AffineTransform scale = AffineTransform.getScaleInstance( radius, -radius );

        drawTicks(g2d, theTicks[TICKS_200MS], CAP_ROUND,
                radius, tick200msWidth, tick200msColor, scale );
        drawTicks(g2d, theTicks[TICKS_1S],    CAP_ROUND,
                radius, tick1sWidth, tick1sColor,    scale );
        drawTicks(g2d, theTicks[TICKS_5S],    CAP_SQUARE,
                radius, tick5sWidth, tick5sColor,    scale );
        drawTicks(g2d, theTicks[TICKS_10S],   CAP_SQUARE,
                radius, tick10sWidth, tick10sColor,   scale );
        drawTicks(g2d, theTicks[TICKS_1H],    CAP_SQUARE,
                radius, tick1hWidth, tick1hColor,    scale );
        g2d.setColor( getForeground());
        
        /* the figures */
        FontRenderContext frc = g2d.getFontRenderContext();
        GeneralPath       gp  = new GeneralPath();
        for (int i = 0; i < numHoursCycle; ++i) {
            Font hour_font = i % hilightedHours == 0
                    ? majorHoursFont
                    : minorHoursFont;
            Color hour_color = i % hilightedHours == 0
                    ? lbl1hMajorColor
                    : lbl1hMinorColor;
            double      angle     = 2.0 * PI * i / numHoursCycle;
            double      sin_angle = sin(angle),
                        cos_angle = cos(angle);
 
            TextLayout  tl        = new TextLayout(hourNames[i], hour_font, frc);
            Rectangle2D b         = tl.getBounds();
            double      factor    = hourLabelRadius * radius;
            double      x0        =  sin_angle*factor - b.getCenterX();
            double      y0        = -cos_angle*factor - b.getCenterY();
            g2d.setFont(hour_font);
            g2d.setColor(hour_color);
            g2d.drawString(hourNames[i], (float)x0, (float)y0);
        }
        g2d.setColor(getForeground());
        for (int i = 0; i < 12; ++i) {
            double angle = 2.0 * PI * i / 12;
            double sin_angle = sin( angle ),
                    cos_angle = cos( angle );

            TextLayout tl = new TextLayout( minNames[i], minutesFont, frc );
            Rectangle2D b = tl.getBounds();
            double factor = secsLabelRadius * radius;
            double x0 = sin_angle * factor - b.getCenterX();
            double y0 = -cos_angle * factor - b.getCenterY();
            g2d.setFont( minutesFont );
            g2d.setColor( lbl5sColor );
            g2d.drawString( minNames[i], (float) x0, (float) y0 );
        }
        g2d.setColor(getForeground());

        long timestamp = System.currentTimeMillis();
        timestamp -= new Date( timestamp ).getTimezoneOffset() * 60_000;
        drawHand(g2d, hourHand, scale,
                timestamp % 86_400_000L * PI * numHours / numHoursCycle / 43.2E6,
                (float) (hourHandWidth * radius), CAP_ROUND, hourHandColor );
        drawHand(g2d, minuteHand, scale,
                timestamp % 3_600_000L * PI / 1.8E6,
                (float) (minHandWidth * radius), CAP_SQUARE, minuteHandColor );
        drawHand(g2d, secondHand, scale,
                timestamp % 60_000L * PI / 30.0E3,
                (float) (secHandWidth * radius), CAP_SQUARE, secondHandColor );
        g2d.setColor( smallCircleColor );
        g2d.fill(scale.createTransformedShape(new Ellipse2D.Double(
                -smallCircleRadius, -smallCircleRadius,
                2 * smallCircleRadius, 2 * smallCircleRadius ) ) );
        g2d.setColor( getForeground());
    }

    public static void main(String[] args) {
        GraphicsEnvironment ge = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        Font[] fonts = ge.getAllFonts();
        for (Font f : fonts) {
            System.out.println( f.getName() );
        }
        if (ge.isHeadlessInstance()) {
            System.err.println("Cannot display, headless execution");
            System.exit( 0);
        }
        EventQueue.invokeLater( () -> {
            JFrame frame = new JFrame( "Example" );
            JComponent contentPane = (JComponent) frame.getContentPane();
            contentPane.setLayout( new BorderLayout(10, 10));
            JChrono crono = new JChrono();
            JScrollPane scp = new JScrollPane(crono);
//            contentPane.setBackground( Color.black );
//            contentPane.setForeground( Color.white );
//            scp.setBackground( Color.black );
//            scp.setForeground( Color.white );
//            crono.setBackground( Color.black );
//            crono.setForeground( Color.white );
//            contentPane.setBackground( Color.BLACK);
//            contentPane.setForeground( Color.GREEN);
//            crono.hourHandColor   = Color.ORANGE;
//            crono.minuteHandColor = Color.BLUE;
//            crono.secondHandColor = new Color(255, 0, 0);
//            crono.tick200msColor  = Color.YELLOW;
//            crono.tick1sColor     = new Color(192, 255, 192); 
//            crono.tick5sColor     = new Color(192, 255, 192);
//            crono.tick10sColor    = Color.WHITE;
//            crono.tick1hColor     = new Color(192, 128, 0);
//            crono.lbl1hMajorColor = Color.YELLOW;
//            crono.lbl1hMinorColor = Color.CYAN;
//            crono.lbl5sColor      = Color.LIGHT_GRAY;
            contentPane.setBorder(
                    new TitledBorder( new LineBorder( Color.blue ),
                            "Hola como estas?" ) );
            int N = 144;
            crono.setPreferredSize( new Dimension(N, N));
            contentPane.add(scp, BorderLayout.CENTER);
            contentPane.add(
                    new JLabel("Se está quemando la serreríaaa!!!",
                            JLabel.CENTER),
                    BorderLayout.SOUTH);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}
