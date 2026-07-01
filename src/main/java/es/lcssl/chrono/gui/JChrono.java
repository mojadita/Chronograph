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
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.Stroke;
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
import java.awt.font.TextLayout;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.Date;

import java.beans.BeanProperty;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.JScrollPane;

import static java.awt.BasicStroke.CAP_ROUND;
import static java.awt.BasicStroke.CAP_SQUARE;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static java.awt.geom.AffineTransform.getRotateInstance;
import static java.awt.geom.AffineTransform.getScaleInstance;
import static java.lang.Math.*;

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
            MINUTES_FONT_SIZE_PROPERTY_NAME = "minutesFontSize",
            HOURS_MINOR_FONT_SIZE_PROPERTY_NAME = "hoursMinorFontSize",
            HOURS_MAJOR_FONT_SIZE_PROPERTY_NAME = "hoursMajorFontSize",
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

    private static final long serialVersionUID = 1L;

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
            secHandWidth      =  0.008,
            minutesFontSize   =  0.120,
            hoursMinorFontSize=  0.160,
            hoursMajorFontSize=  0.220,
            halfMoonRadius    = -0.150;
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

    List<AbstractDrawable> staticParts;
    AbstractDrawable
            secondHand,
            minuteHand,
            hourHand;

    public JChrono () {
        staticParts = new ArrayList<>();
        staticParts.add(new Ticks200ms(tick200msColor, (float) tick200msWidth));
        staticParts.add(new Ticks1s(tick1sColor, (float) tick1sWidth));
        staticParts.add(new Ticks5s(tick5sColor, (float) tick5sWidth));
        staticParts.add(new Ticks10s(tick10sColor, (float) tick10sWidth ));

        secondHand = new SecondsHand(
                secondHandColor,
                secondHandColor,
                CAP_SQUARE,
                (float) secHandWidth,
                secHead,
                secTail,
                smallCircleRadius,
                halfMoonRadius);
        minuteHand = new SimpleHand(
                minuteHandColor,
                minuteHandColor,
                CAP_ROUND,
                (float) minHandWidth,
                minHead,
                minTail);
        hourHand = new SimpleHand(
                hourHandColor,
                hourHandColor,
                CAP_ROUND,
                (float) hourHandWidth,
                hourHead,
                hourTail);
        timer.start();
    }

    Timer timer = new Timer(23,
            e -> { invalidate(); repaint(); } );

    private abstract class AbstractDrawable {

        protected Shape
                shapeToDraw,
                shapeToFill;
        private Color
                colorToDraw,
                colorToFill;
        private Stroke
                strokeToDraw;

        public AbstractDrawable(
                Color p_color_to_draw,
                Color p_color_to_fill,
                int   p_stroke_cap,
                float p_line_width)
        {
            colorToDraw = p_color_to_draw;
            colorToFill = p_color_to_fill;
            strokeToDraw  = new BasicStroke(
                    p_line_width, p_stroke_cap,
                    BasicStroke.JOIN_BEVEL );
        }


        void draw(
                Graphics2D      p_gc,
                AffineTransform p_at,
                Double          p_rotation)
        {
            if (p_rotation != null) {
                p_at = (AffineTransform) p_at.clone();
                p_at.concatenate( getRotateInstance( -p_rotation ) );
            }
            if (shapeToFill != null) {
                if (colorToFill != null)
                    p_gc.setColor( colorToFill );
                p_gc.fill( p_at.createTransformedShape( shapeToFill ) );
            }
            if (shapeToDraw != null) {
                if (colorToDraw != null)
                    p_gc.setColor( colorToDraw );
                p_gc.draw(p_at.createTransformedShape(shapeToDraw ) );
            }
        }
    }

    private class SimpleHand extends AbstractDrawable {

        public SimpleHand(
                Color  p_color_to_draw,
                Color  p_color_to_fill,
                int    p_stroke_cap,
                float  p_line_width,
                double p_head_radius,
                double p_tail_radius)
        {
            super( p_color_to_draw, p_color_to_fill, p_stroke_cap, p_line_width );
            GeneralPath shape_to_draw = new GeneralPath();
            shape_to_draw.moveTo( 0.0, -p_head_radius );
            shape_to_draw.lineTo( 0.0, -p_tail_radius );
            shapeToDraw = shape_to_draw;
        }
    }

    private class SecondsHand extends SimpleHand {
        public SecondsHand(
                Color p_color_to_draw,
                Color p_color_to_fill,
                int   p_stroke_cap,
                float p_line_width,
                double p_head_radius,
                double p_tail_radius,
                double p_small_circle_radius,
                double p_half_moon_radius)
        {
            super(  p_color_to_draw,
                    p_color_to_fill,
                    p_stroke_cap,
                    p_line_width,
                    p_head_radius,
                    p_tail_radius );

            GeneralPath shape_to_fill = new GeneralPath();
            double half_moon_diameter = 2.0 * p_half_moon_radius;
            Area half_moon = new Area(
                    new Ellipse2D.Double(
                            -p_half_moon_radius, -p_tail_radius,
                            half_moon_diameter, half_moon_diameter));
            half_moon.subtract( new Area (
                    new Ellipse2D.Double(
                            -p_half_moon_radius,
                            -p_tail_radius - p_half_moon_radius / 2.0,
                            half_moon_diameter, half_moon_diameter)));
            shape_to_fill.append(half_moon, false);
            //shape_to_fill.closePath();
            double small_circle_diameter = 2.0 * p_small_circle_radius;
            shape_to_fill.append(
                    new Ellipse2D.Double(
                            -p_small_circle_radius,
                            -p_small_circle_radius,
                            small_circle_diameter,
                            small_circle_diameter),
                    true);
        }
    }

    private class Ticks200ms extends AbstractDrawable {

        public Ticks200ms(
                Color p_color_to_draw,
                float p_line_width)
        {
            super(  p_color_to_draw,
                    null,
                    BasicStroke.CAP_ROUND,
                    p_line_width );

            GeneralPath shape_path = new GeneralPath();
            shapeToDraw = shape_path;
            final int N = 300;  /* 300 200ms ticks */

            for (int i = 0; i < N; ++i) {
                if (i % 5 == 0) continue;
                double angle = 2.0 * PI * i / N;
                double sin_angle = sin(angle);
                double cos_angle = cos(angle);
                shape_path.moveTo(
                         sin_angle * tickStart,
                        -cos_angle * tickStart );
                shape_path.lineTo(
                         sin_angle * tick200msEnd,
                        -cos_angle * tick200msEnd );
            }
        }
    }

    private class Ticks1s extends AbstractDrawable {

        public Ticks1s(
                Color p_color_to_draw,
                float p_line_width)
        {
            super(  p_color_to_draw,
                    null,
                    BasicStroke.CAP_ROUND,
                    p_line_width );

            GeneralPath shape_path = new GeneralPath();
            shapeToDraw = shape_path;
            final int N = 60;  /* 60 1s ticks */

            for (int i = 0; i < N; ++i) {
                if (i % 5 == 0) continue;
                double angle = 2.0 * PI * i / N;
                double sin_angle = sin(angle);
                double cos_angle = cos(angle);
                shape_path.moveTo(
                         sin_angle * tickStart,
                        -cos_angle * tickStart );
                shape_path.lineTo(
                         sin_angle * tick1sEnd,
                        -cos_angle * tick1sEnd );
            }
        }
    }

    private class Ticks5s extends AbstractDrawable {

        protected static final int N = 12;

        public Ticks5s(
                Color p_color_to_draw,
                float p_line_width)
        {
            super(  p_color_to_draw,
                    null,  // p_color_to_fill
                    BasicStroke.CAP_SQUARE,
                    p_line_width );

            GeneralPath shape_path = new GeneralPath();
            GeneralPath fill_path  = new GeneralPath();

            shapeToDraw = shape_path;
            shapeToFill = fill_path;

            for (int i = 0; i < N; ++i) {
                if (i % 2 == 0) continue;
                double angle = 2.0 * PI * i / N;
                double sin_angle = sin(angle);
                double cos_angle = cos(angle);
                shape_path.moveTo(
                         sin_angle * tickStart,
                        -cos_angle * tickStart );
                shape_path.lineTo(
                         sin_angle * tick5sEnd,
                        -cos_angle * tick5sEnd );
            }
        }

        @Override
        void draw(
                Graphics2D p_gc,
                AffineTransform p_at,
                Double p_rotation)  /* rotation is ignored here */
        {
            GeneralPath shape_to_fill;
            if (shapeToFill == null) {
                shape_to_fill = new GeneralPath();
                shapeToFill   = shape_to_fill;
                minutesFont   = minutesFont.deriveFont( (float)minutesFontSize );
                for (int i = 0; i < N; ++i) {
                    double angle = 2.0 * PI * i / N,
                           sin_angle = sin(angle),
                           cos_angle = cos(angle);
                    FontRenderContext frc = p_gc.getFontRenderContext();
                    Shape gv = minutesFont.createGlyphVector(
                            frc, minNames[i] ).getOutline();
                    Rectangle2D bounds = gv.getBounds2D();
                    shape_to_fill.moveTo(
                              sin_angle - bounds.getCenterX(),
                            - cos_angle - bounds.getCenterY() );
                    shape_to_fill.append( gv, false );
                }
                shape_to_fill.closePath();
            }
            super.draw( p_gc, p_at, p_rotation );
        }

    }

    private class Ticks10s extends AbstractDrawable {

        public Ticks10s(
                Color p_color_to_draw,
                float p_line_width)
        {
            super(  p_color_to_draw,
                    null,
                    BasicStroke.CAP_SQUARE,
                    p_line_width );

            GeneralPath shape_path = new GeneralPath();
            shapeToDraw = shape_path;
            GeneralPath fill_path = new GeneralPath();
            shapeToFill = fill_path;
            final int N = 6;  /* 6 10s ticks */

            for (int i = 0; i < N; ++i) {
                double angle     = 2.0 * PI * i / N;
                double sin_angle = sin(angle);
                double cos_angle = cos(angle);

                shape_path.moveTo(
                         sin_angle * tickStart,
                        -cos_angle * tickStart );
                shape_path.lineTo(
                         sin_angle * tick10sEnd,
                        -cos_angle * tick10sEnd );
            }
        }
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
        AffineTransform scale = getScaleInstance( radius, radius );

        for (AbstractDrawable d: staticParts) {
            d.draw( g2d, scale, null);  /* not rotating */
        }

        long timestamp = System.currentTimeMillis();
        timestamp -= new Date( timestamp ).getTimezoneOffset() * 60_000;

        /* hours hand */
        double hour_angle = (timestamp % 86_400_000) * 2.0 * PI / 86_400_000.0;
        hourHand.draw( g2d, scale, -hour_angle ); /* to get clockwise movement */

        /* minutes hand */
        double min_angle = (timestamp % 3_600_000) * 2.0 * PI / 3_600_000.0;
        minuteHand.draw( g2d, scale, -min_angle ); /* negative to get clockwise movement */

        /* seconds hand */
        double sec_angle = (timestamp % 60_000) * 2.0 * PI / 60_000.0;
        secondHand.draw( g2d, scale, -sec_angle);
    }

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
    public double getMinutesFontSize() {
        return minutesFontSize;
    }

    public void setMinutesFontSize(double p_minutesFontSize) {
        double old_minutesFontSize = minutesFontSize;
        minutesFontSize = p_minutesFontSize;
        if (old_minutesFontSize != p_minutesFontSize) {
            pcs.firePropertyChange(
                    MINUTES_FONT_SIZE_PROPERTY_NAME,
                    old_minutesFontSize, p_minutesFontSize );
        }
    }

    @BeanProperty
    public double getHoursMinorFontSize() {
        return hoursMinorFontSize;
    }

    public void setHoursMinorFontSize(double p_hoursMinorFontSize) {
        double old_hoursMinorFontSize = hoursMinorFontSize;
        hoursMinorFontSize = p_hoursMinorFontSize;
        if (old_hoursMinorFontSize != p_hoursMinorFontSize) {
            pcs.firePropertyChange(
                    HOURS_MINOR_FONT_SIZE_PROPERTY_NAME,
                    old_hoursMinorFontSize, p_hoursMinorFontSize );
        }
    }

    @BeanProperty
    public double getHoursMajorFontSize() {
        return hoursMajorFontSize;
    }

    public void setHoursMajorFontSize(double p_hoursMajorFontSize) {
        double old_hoursMajorFontSize = hoursMajorFontSize;
        hoursMajorFontSize = p_hoursMajorFontSize;
        if (old_hoursMajorFontSize != p_hoursMajorFontSize) {
            pcs.firePropertyChange(
                    HOURS_MAJOR_FONT_SIZE_PROPERTY_NAME,
                    old_hoursMajorFontSize, p_hoursMajorFontSize );
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
            int N = 2000;
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
