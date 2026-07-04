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
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.GlyphVector;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.Timer;

import java.beans.BeanProperty;
import java.beans.PropertyChangeSupport;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.Date;

import static java.awt.BasicStroke.CAP_ROUND;
import static java.awt.BasicStroke.CAP_SQUARE;
import static java.awt.BasicStroke.JOIN_ROUND;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static java.awt.geom.AffineTransform.getRotateInstance;
import static java.lang.Math.*;

/**
 *
 * @author Luis Colorado {@code <luiscoloradourcola@gmail.com>}
 */
public class JChrono extends JComponent {

    public static final String
            MAJOR_HOURS_FONT_PROPERTY_NAME      = "majorHoursFont",
            MINOR_HOURS_FONT_PROPERTY_NAME      = "minorHoursFont",
            MINUTES_FONT_PROPERTY_NAME          = "minutesFont",
            HOUR_NAMES_PROPERTY_NAME            = "hourNames",
            MIN_NAMES_PROPERTY_NAME             = "minNames",
            TICK_START_PROPERTY_NAME            = "tickStart",
            TICK_200MS_END_PROPERTY_NAME        = "tick200msEnd",
            TICK_1S_END_PROPERTY_NAME           = "tick1sEnd",
            TICK_5S_END_PROPERTY_NAME           = "tick5sEnd",
            TICK_10S_END_PROPERTY_NAME          = "tick10sEnd",
            SECS_LABEL_RADIUS_PROPERTY_NAME     = "secsLabelRadius",
            TICK_1H_START_PROPERTY_NAME         = "tick1hStart",
            TICK_1H_MAJOR_END_PROPERTY_NAME     = "tick1hMajorEnd",
            TICK_1H_MINOR_END_PROPERTY_NAME     = "tick1hMinorEnd",
            HOUR_LABEL_RADIUS_PROPERTY_NAME     = "hourLabelRadius",
            SMALL_CIRCLE_RADIUS_PROPERTY_NAME   = "smallCircleRadius",
            TICK_200MS_WIDTH_PROPERTY_NAME      = "tick200msWidth",
            TICK_1S_WIDTH_PROPERTY_NAME         = "tick1sWidth",
            TICK_5S_WIDTH_PROPERTY_NAME         = "tick5sWidth",
            TICK_10S_WIDTH_PROPERTY_NAME        = "tick10sWidth",
            TICK_1H_MINOR_WIDTH_PROPERTY_NAME   = "tick1hMinorWidth",
            TICK_1H_MAJOR_WIDTH_PROPERTY_NAME   = "tick1hMajorWidth",
            HOUR_TAIL_PROPERTY_NAME             = "hourTail",
            MIN_TAIL_PROPERTY_NAME              = "minTail",
            SEC_TAIL_PROPERTY_NAME              = "secTail",
            HOUR_HEAD_PROPERTY_NAME             = "hourHead",
            MIN_HEAD_PROPERTY_NAME              = "minHead",
            SEC_HEAD_PROPERTY_NAME              = "secHead",
            HOUR_HAND_WIDTH_PROPERTY_NAME       = "hourHandWidth",
            MIN_HAND_WIDTH_PROPERTY_NAME        = "minHandWidth",
            SEC_HAND_WIDTH_PROPERTY_NAME        = "secHandWidth",
            MINUTES_FONT_SIZE_PROPERTY_NAME     = "minutesFontSize",
            HOURS_MINOR_FONT_SIZE_PROPERTY_NAME = "hoursMinorFontSize",
            HOURS_MAJOR_FONT_SIZE_PROPERTY_NAME = "hoursMajorFontSize",
            NUM_HOURS_DAY_PROPERTY_NAME         = "numHoursDay",
            NUM_HOURS_CYCLE_PROPERTY_NAME       = "numHoursCycle",
            HIGHLIGHTED_HOURS_PROPERTY_NAME     = "highlightedHours",
            TICK_200MS_COLOR_PROPERTY_NAME      = "tick200msColor",
            TICK_1S_COLOR_PROPERTY_NAME         = "tick1sColor",
            TICK_5S_COLOR_PROPERTY_NAME         = "tick5sColor",
            TICK_10S_COLOR_PROPERTY_NAME        = "tick10sColor",
            TICK_1H_COLOR_PROPERTY_NAME         = "tick1hColor",
            LABEL_5S_COLOR_PROPERTY_NAME        = "label5sColor",
            LABEL_1H_MAJOR_COLOR_PROPERTY_NAME  = "label1hMajorColor",
            LABEL_1H_MINOR_COLOR_PROPERTY_NAME  = "label1hMinorColor",
            HOUR_HAND_COLOR_PROPERTY_NAME       = "hourHandColor",
            MINUTE_HAND_COLOR_PROPERTY_NAME     = "minuteHandColor",
            SECOND_HAND_COLOR_PROPERTY_NAME     = "secondHandColor",
            SMALL_CIRCLE_COLOR_PROPERTY_NAME    = "smallCircleColor",
            HALF_MOON_RADIUS_PROPERTY_NAME      = "halfMoonRadius";

    private static final long serialVersionUID = 1L;

    private Font majorHoursFont = new Font("Serif", Font.BOLD, 1);
    private Font minorHoursFont = new Font("Serif", Font.ITALIC, 1);
    private Font minutesFont    = new Font("Serif", Font.ROMAN_BASELINE, 1);

    private String[] hourNames = {
        "0", "1", "2", "3", "4", "5",
        "6", "7", "8", "9", "10",
        "11", "12", "13", "14", "15",
        "16", "17", "18", "19", "20",
        "21", "22", "23",};

    private String[] minNames = {
        "00", "05", "10", "15", "20", "25",
        "30", "35", "40", "45", "50", "55",};

    private double
            tickStart          =  0.990, /* tick radial start positon (1.0 for all) */
            tick200msEnd       =  0.975,
            tick1sEnd          =  0.960,
            tick5sEnd          =  0.945,
            tick10sEnd         =  0.930,
            secsLabelRadius    =  0.820,
            tick1hStart        =  0.710,
            tick1hMajorEnd     =  0.650,
            tick1hMinorEnd     =  0.680,
            hourLabelRadius    =  0.540,
            smallCircleRadius  =  0.030,
            hourTail           = -0.050,
            minTail            = -0.100,
            secTail            = -0.120,
            hourHead           =  0.450,
            minHead            =  0.750,
            secHead            =  0.890,
            hourHandWidth      =  0.030,
            minHandWidth       =  0.020,
            secHandWidth       =  0.008,
            minutesFontSize    =  0.100,
            hoursMinorFontSize =  0.100,
            hoursMajorFontSize =  0.140,
            halfMoonRadius     =  0.040;

    float   tick200msWidth     =  0.005f,
            tick1sWidth        =  0.008f,
            tick5sWidth        =  0.016f,
            tick10sWidth       =  0.020f,
            tick1hMinorWidth   =  0.012f,
            tick1hMajorWidth   =  0.036f;

    int     numHours           = 24,
            numHoursCycle      = 24,
            highlightedHours   =  8;

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

    private transient AbstractDrawable
            staticPart,
            secondHand,
            minuteHand,
            hourHand;

    public JChrono() {
        staticPart = new StaticDrawable();
        hourHand = new SimpleHand(
                hourHandColor,
                CAP_ROUND, hourHead, hourTail,
                (float) hourHandWidth);
        minuteHand = new SimpleHand(
                minuteHandColor,
                CAP_ROUND, minHead, minTail,
                (float) minHandWidth);
        secondHand = new SecondHand(
                secondHandColor,
                CAP_SQUARE, secHead, secTail,
                (float) secHandWidth);
        timer.start();
    }

    Timer timer = new Timer(
            23,
            e -> { invalidate(); repaint(); });

    private abstract class AbstractDrawable {

        protected GeneralPath[]
                shapeToFill,
                shapeToDraw;
        protected Paint[]
                paintToFill,
                paintToDraw;
        protected Stroke[]
                strokeToDraw;
        protected Font[]
                fontToRender;

        private double oldRadius = 0.0;

        public AbstractDrawable(
                GeneralPath[] p_shape_to_fill,
                GeneralPath[] p_shape_to_draw,
                Paint[]       p_paint_to_fill,
                Paint[]       p_paint_to_draw,
                Stroke[]      p_stroke_to_draw,
                Font[]        p_font_to_render)
        {
            shapeToFill       = p_shape_to_fill;
            shapeToDraw       = p_shape_to_draw;
            paintToFill       = p_paint_to_fill;
            paintToDraw       = p_paint_to_draw;
            strokeToDraw      = p_stroke_to_draw;
            fontToRender      = p_font_to_render;
        }

        protected abstract void buildScaledFigures(
                Graphics2D g2d, double new_radius);

        protected <T> boolean isValid(
                T[] array)
        {
            return     array != null
                    && array.length > 0;
        }

        protected <T> boolean isValid(
                T[] array,
                int p_index)
        {
            return     isValid(array)
                    && array.length > p_index
                    && array[p_index] != null;
        }

        private void consume(
                Shape[]  p_shape,
                Paint[]  p_paint,
                Stroke[] p_stroke,
                Font[]   p_font,
                Graphics2D p_gc,
                AffineTransform p_rotation,
                Consumer<Shape> p_what_to_do)
        {
            if (isValid(p_shape)) {
                for (int i = 0; i < p_shape.length; ++i) {
                    if (!isValid(p_shape, i))
                        continue; /* nothing to do */

                    if (isValid( p_paint, i ))
                        p_gc.setPaint( p_paint[i] );
                    if (isValid( p_stroke, i ))
                        p_gc.setStroke( p_stroke[i] );
                    if (isValid( p_font, i ))
                        p_gc.setFont( p_font[i] );

                    /* and proceed to consume with p_what_to_do */
                    if (p_rotation == null)
                        p_what_to_do.accept(p_shape[i] ); /* draw it!! */
                    else
                        p_what_to_do.accept(p_rotation  /* rotate & draw it!! */
                                .createTransformedShape(
                                        p_shape[i] ) );
                }
            }
        }

        void draw(
                Graphics2D      p_gc,
                AffineTransform p_at,
                double          p_radius,
                Double          p_rotation)
        {
            /* scale figures if radius has been changed */
            if (oldRadius != p_radius) {
                buildScaledFigures( p_gc, p_radius );
                oldRadius = p_radius;
            }

            AffineTransform rotation = null;
            if (p_rotation != null && p_rotation != 0.0) {
                /* we are assuming rotations are done clockwise, so
                 * the parameter must be < 0 to reflect the walking
                 * time.  */
                rotation = getRotateInstance( p_rotation );
            }

            /* filling shapes that must be filled */
            consume( shapeToFill,
                     paintToFill,
                     null,  /* no draw is done here, so Strokes are not used.  */
                     null,
                     p_gc,
                     rotation,
                     p_gc::fill );

            /* drawing shapes that must be drawn */
            consume( shapeToDraw,
                     paintToDraw,
                     strokeToDraw,
                     null,  /* no drawString, so no font info is used */
                     p_gc,
                     rotation,
                     p_gc::draw );
        }
    } /* AbstractDrawable */

    private class StaticDrawable extends AbstractDrawable {

        public static final int
                IX_200MS    = 0,
                IX_1S       = 1,
                IX_5S       = 2,
                IX_10S      = 3,
                IX_1H_MINOR = 4,
                IX_1H_MAJOR = 5,
                IX_COUNT    = 6;


        public StaticDrawable() {

            super(  new GeneralPath[IX_COUNT], /* shapeToFill */
                    new GeneralPath[IX_COUNT], /* shapeToDraw */
                    new Paint[IX_COUNT],       /* paintToFill */
                    new Paint[] {              /* p_paint_to_draw */
                        tick200msColor,
                        tick1sColor,
                        tick5sColor,
                        tick10sColor,
                        lbl1hMinorColor,
                        lbl1hMajorColor},
                    new BasicStroke[IX_COUNT], /* p_stroke_to_draw (need
                                                * to be built on
                                                * scaling) */
                    new Font[IX_COUNT]);       /* p_font_to_fill (need
                                                * to be built on
                                                * scaling) */
        }

        @Override
        protected void buildScaledFigures(
                Graphics2D p_g2d,
                double     p_new_radius) {
            /* no rotation */
            /* generate the SECONDS ticks */
            double[] tick_start_radius = {
                tickStart, tickStart, tickStart,
                tickStart, tick1hStart, tick1hStart,
            };
            double[] tick_end_radius = {
                tick200msEnd, tick1sEnd, tick5sEnd,
                tick10sEnd, tick1hMinorEnd, tick1hMajorEnd,
            };
            float[] tick_width = {
                tick200msWidth, tick1sWidth, tick5sWidth,
                tick10sWidth, tick1hMinorWidth, tick1hMajorWidth,
            };
            int[] tick_cap = {
                CAP_ROUND, CAP_ROUND, CAP_SQUARE, CAP_SQUARE,
                CAP_ROUND, CAP_ROUND
            };

            shapeToDraw[IX_200MS]   = new GeneralPath();
            shapeToDraw[IX_1S]      = new GeneralPath();
            shapeToDraw[IX_5S]      = new GeneralPath();
            shapeToDraw[IX_10S]     = new GeneralPath();
            shapeToDraw[IX_1H_MINOR]= new GeneralPath();
            shapeToDraw[IX_1H_MAJOR]= new GeneralPath();
            for (int i = 0; i < IX_COUNT; ++i) {
                strokeToDraw[i] = new BasicStroke(
                        (float)(tick_width[i]*p_new_radius),
                        tick_cap[i], JOIN_ROUND);
            }

            final int N1 = 300;
            int which_to_add;
            for (int i = 0; i < N1; ++i) {
                double angle = 2.0 * PI * i / N1;
                double x_scaled =  sin(angle) * p_new_radius,
                       y_scaled = -cos(angle) * p_new_radius;
                     if (i % 50 == 0) which_to_add = IX_10S;
                else if (i % 25 == 0) which_to_add = IX_5S;
                else if (i %  5 == 0) which_to_add = IX_1S;
                else                  which_to_add = IX_200MS;

                shapeToDraw[which_to_add].moveTo(
                        x_scaled * tick_start_radius[which_to_add],
                        y_scaled * tick_start_radius[which_to_add]);
                shapeToDraw[which_to_add].lineTo(
                        x_scaled * tick_end_radius[which_to_add],
                        y_scaled * tick_end_radius[which_to_add]);
            }

            /* now the figures associated with minutes & seconds */
            shapeToFill[IX_5S]  = new GeneralPath();
            Font font_to_render = minutesFont.deriveFont(
                     (float)(p_new_radius * minutesFontSize));
            FontRenderContext font_render_context
                    = p_g2d.getFontRenderContext();

            final int N2 = 12;
            for (int i = 0; i < N2; ++i) {

                double angle = 2.0 * PI * i / N2;
                double x_scaled =  sin(angle) * p_new_radius,
                       y_scaled = -cos(angle) * p_new_radius;

                GlyphVector glyph_vector = font_to_render
                        .createGlyphVector(
                                font_render_context, minNames[i] );
                Rectangle2D bounds = glyph_vector.getVisualBounds();
                shapeToFill[IX_5S].append( glyph_vector.getOutline(
                        (float)(x_scaled * secsLabelRadius
                                - bounds.getCenterX()),
                        (float)(y_scaled * secsLabelRadius
                                - bounds.getCenterY() )),
                        false );
            }
            shapeToFill[IX_5S].closePath();

            /* time for the hours */
            shapeToDraw[IX_1H_MAJOR] = new GeneralPath();
            shapeToFill[IX_1H_MAJOR] = new GeneralPath();
            shapeToDraw[IX_1H_MINOR] = new GeneralPath();
            shapeToFill[IX_1H_MINOR] = new GeneralPath();

            Font major_font = majorHoursFont.deriveFont(
                    (float)( p_new_radius * hoursMajorFontSize));
            Font minor_font = minorHoursFont.deriveFont(
                    (float)( p_new_radius * hoursMinorFontSize));

            for (int i = 0; i < numHoursCycle; ++i) {
                boolean is_major = i % highlightedHours == 0;
                Font which_font = is_major ? major_font : minor_font;
                int which_shape = is_major ? IX_1H_MAJOR : IX_1H_MINOR;
                double angle = i * 2.0 * PI / numHoursCycle,
                        x_scaled =  sin(angle) * p_new_radius,
                        y_scaled = -cos(angle) * p_new_radius;

                /* draw the ticks */
                shapeToDraw[which_shape].moveTo(
                        x_scaled * tick_start_radius[which_shape],
                        y_scaled * tick_start_radius[which_shape]);
                shapeToDraw[which_shape].lineTo(
                        x_scaled * tick_end_radius[which_shape],
                        y_scaled * tick_end_radius[which_shape]);

                /* fill the shapes */
                font_render_context
                        = p_g2d.getFontRenderContext();
                GlyphVector gv = which_font.createGlyphVector(
                        font_render_context, hourNames[i] );
                Rectangle2D bounds = gv.getVisualBounds();

                shapeToFill[which_shape].append( gv.getOutline(
                        (float)(x_scaled * hourLabelRadius
                                - bounds.getCenterX()),
                        (float)(y_scaled * hourLabelRadius
                                - bounds.getCenterY())),
                        false);
            }
        }
    } /* StaticPartDrawable */

    private class SimpleHand extends AbstractDrawable {

        double head, tail, width;
        int    capStyle;

        public SimpleHand(
                Color p_color,
                int   p_stroke_cap,
                double p_head,
                double p_tail,
                float p_line_width)
        {
            super(  null,                  /* p_shape_to_fill */
                    new GeneralPath[1],    /* p_shape_to_draw */
                    new Color[] {p_color}, /* p_paint_to_fill */
                    new Color[] {p_color}, /* p_paint_to_draw */
                    new Stroke[1],         /* p_stroke_to_draw */
                    null);                 /* p_font_to_render */

            head     = p_head;
            tail     = p_tail;
            width    = p_line_width;
            capStyle = p_stroke_cap;
        }

        @Override
        protected void buildScaledFigures(
                Graphics2D p_g2d,
                double     p_new_radius)
        {
            GeneralPath path = shapeToDraw[0] = new GeneralPath();
            strokeToDraw[0] = new BasicStroke(
                    (float)(width * p_new_radius),
                    capStyle, CAP_ROUND);
            path.moveTo( 0.0, -head * p_new_radius);
            path.lineTo( 0.0, -tail * p_new_radius);
        }
    } /* SimpleHand */

    private class SecondHand extends SimpleHand {

        public SecondHand(
                Color p_color,
                int p_stroke_cap,
                double p_head,
                double p_tail,
                float p_line_width)
        {
            super(  secondHandColor,
                    p_stroke_cap,
                    p_head,
                    p_tail,
                    p_line_width);
            shapeToFill = new GeneralPath[1];
            paintToFill = new Paint[1];
        }

        @Override
        protected void buildScaledFigures(
                Graphics2D p_g2d,
                double     p_new_radius)
        {
            shapeToFill[0] = new GeneralPath();
            paintToFill[0] = secondHandColor;
            double half_moon_diameter = halfMoonRadius * 2.0 * p_new_radius;
            Area half_moon = new Area(new Ellipse2D.Double(
                    -halfMoonRadius            * p_new_radius,
                    (-secTail) * p_new_radius,
                    half_moon_diameter, half_moon_diameter));
            half_moon.subtract(new Area(new Ellipse2D.Double(
                    (-halfMoonRadius + halfMoonRadius/2) * p_new_radius,
                    (-secTail + halfMoonRadius/2)        * p_new_radius,
                    half_moon_diameter, half_moon_diameter)));
            shapeToFill[0].append(half_moon, false);

            /* add small circle in the origin */
            double  small_circle_radius   = smallCircleRadius * p_new_radius,
                    small_circle_diameter = small_circle_radius * 2.0;
            shapeToFill[0].append(new Ellipse2D.Double(
                    -small_circle_radius, -small_circle_radius,
                    small_circle_diameter, small_circle_diameter),
                    false);
            super.buildScaledFigures(p_g2d, p_new_radius);
        }
    } /* SecondHand */

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        /* get a new Graphics2D so we can mangle it */
        g2d = (Graphics2D) g2d.create();

        g2d.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);

        /* We don't paint on the insets zone */
        Insets insets = getInsets();
        int width = getWidth(), height = getHeight();

        width  -= insets.left + insets.right;
        height -= insets.top  + insets.bottom;

        double radius = min(width, height) / 2.0;
        g2d.translate(insets.left + width / 2.0, insets.top + height / 2.0);

        staticPart.draw(g2d, null, radius, null);

        long timestamp = System.currentTimeMillis();
        timestamp -= new Date(timestamp).getTimezoneOffset() * 60_000L;

        timestamp %= 86_400_000L;
        hourHand.draw(g2d, null, radius,
                2.0*PI*timestamp/86_400_000.0);
        timestamp %= 3_600_000L;
        minuteHand.draw(g2d, null, radius,
                2.0*PI*timestamp/3_600_000.0);
        timestamp %= 60_000L;
        secondHand.draw(g2d, null, radius,
                2.0*PI*timestamp/60_000.0);

    }


    /* ****************************************************************/
    /* ********************* GETTERS AND SETTERS **********************/

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
        if (!Objects.equals(old_minorHoursFont, minorHoursFont)) {
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
        if (!Objects.equals(old_minutesFont, minutesFont)) {
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
        if (!Objects.equals(old_hourNames, p_hourNames)) {
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
        if (!Objects.equals(old_minNames, p_minNames)) {
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
    public double getTick1hMajorEnd() {
        return tick1hMajorEnd;
    }

    public void setTick1hMajorEnd(double p_tick1hEnd) {
        double old_tick1hEnd = tick1hMajorEnd;
        tick1hMajorEnd = p_tick1hEnd;
        if (old_tick1hEnd != p_tick1hEnd) {
            pcs.firePropertyChange(TICK_1H_MAJOR_END_PROPERTY_NAME,
                    old_tick1hEnd, p_tick1hEnd);
        }
    }

    @BeanProperty
    public double getTick1hMinorEnd() {
        return tick1hMinorEnd;
    }

    public void setTick1hMinorEnd(double p_tick1hMinorEnd) {
        double old_tick1hMinorEnd = tick1hMinorEnd;
        tick1hMinorEnd = p_tick1hMinorEnd;
        if (old_tick1hMinorEnd != p_tick1hMinorEnd) {
            pcs.firePropertyChange(TICK_1H_MINOR_END_PROPERTY_NAME,
                    old_tick1hMinorEnd, p_tick1hMinorEnd);
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
    public float getTick200msWidth() {
        return tick200msWidth;
    }

    public void setTick200msWidth(float p_tick200msWidth) {
        double old_tick200msWidth = tick200msWidth;
        tick200msWidth = p_tick200msWidth;
        if (old_tick200msWidth != p_tick200msWidth) {
            pcs.firePropertyChange(TICK_200MS_END_PROPERTY_NAME,
                    old_tick200msWidth, p_tick200msWidth);
        }
    }

    @BeanProperty
    public float getTick1sWidth() {
        return tick1sWidth;
    }

    public void setTick1sWidth(float p_tick1sWidth) {
        float old_tick1sWidth = tick1sWidth;
        tick1sWidth = p_tick1sWidth;
        if (old_tick1sWidth != p_tick1sWidth) {
            pcs.firePropertyChange(
                    TICK_1S_WIDTH_PROPERTY_NAME,
                    old_tick1sWidth, p_tick1sWidth);
        }
    }

    @BeanProperty
    public float getTick5sWidth() {
        return tick5sWidth;
    }

    public void setTick5sWidth(float p_tick5sWidth) {
        float old_setTick5sWidth = tick5sWidth;
        tick5sWidth = p_tick5sWidth;
        if (old_setTick5sWidth != p_tick5sWidth) {
            pcs.firePropertyChange(
                    TICK_5S_WIDTH_PROPERTY_NAME,
                    old_setTick5sWidth, p_tick5sWidth);
        }
    }

    @BeanProperty
    public float getTick10sWidth() {
        return tick10sWidth;
    }

    public void setTick10sWidth(float p_tick10sWidth) {
        float old_tick10sWidth = tick10sWidth;
        tick10sWidth = p_tick10sWidth;
        if (old_tick10sWidth != p_tick10sWidth) {
            pcs.firePropertyChange(
                    TICK_10S_WIDTH_PROPERTY_NAME,
                    old_tick10sWidth, p_tick10sWidth);
        }
    }

    @BeanProperty
    public float getTick1hMinorWidth() {
        return tick1hMinorWidth;
    }

    public void setTick1hMinorWidth(float p_tick1hMinorWidth) {
        float old_tick1hMinorWidth = tick1hMinorWidth;
        tick1hMinorWidth = p_tick1hMinorWidth;
        if (old_tick1hMinorWidth != p_tick1hMinorWidth) {
            pcs.firePropertyChange(
                    TICK_1H_MINOR_WIDTH_PROPERTY_NAME,
                    old_tick1hMinorWidth, p_tick1hMinorWidth);
        }
    }

    @BeanProperty
    public float getTick1hMajorWidth() {
        return tick1hMajorWidth;
    }

    public void setTick1hMajorWidth(float p_tick1hMajorWidth) {
        float old_tick1hMajorWidth = tick1hMajorWidth;
        tick1hMajorWidth = p_tick1hMajorWidth;
        if (old_tick1hMajorWidth != p_tick1hMajorWidth) {
            pcs.firePropertyChange(
                    TICK_1H_MAJOR_WIDTH_PROPERTY_NAME,
                    old_tick1hMajorWidth, p_tick1hMajorWidth);
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
                    old_secHead, p_secHead);
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
                    old_minutesFontSize, p_minutesFontSize);
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
                    old_hoursMinorFontSize, p_hoursMinorFontSize);
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
                    old_hoursMajorFontSize, p_hoursMajorFontSize);
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
    public int getHighlightedHours() {
        return highlightedHours;
    }

    public void setHighlightedHours(int p_highlightedHours) {
        int old_highlightedHours = highlightedHours;
        highlightedHours = p_highlightedHours;
        if (old_highlightedHours != p_highlightedHours) {
            pcs.firePropertyChange(
                    HIGHLIGHTED_HOURS_PROPERTY_NAME,
                    old_highlightedHours, p_highlightedHours);
        }
    }

    @BeanProperty
    public Color getTick200msColor() {
        return tick200msColor;
    }

    public void setTick200msColor(Color p_tick200msColor) {
        Color old_tick200msColor = tick200msColor;
        tick200msColor = p_tick200msColor;
        if (!Objects.equals(old_tick200msColor, p_tick200msColor)) {
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
        if (!Objects.equals(old_tick1sColor, p_tick1sColor)) {
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
        if (!Objects.equals(old_tick10sColor, p_tick10sColor)) {
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
        if (!Objects.equals(old_tick1hColor, p_tick1hColor)) {
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
        if (!Objects.equals(old_lbl5sColor, p_lbl5sColor)) {
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
        if (!Objects.equals(old_lbl1hMajorColor, p_lbl1hMajorColor)) {
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
        if (!Objects.equals(old_lbl1hMinorColor, p_lbl1hMinorColor)) {
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
        if (!Objects.equals(old_hourHandColor, p_hourHandColor)) {
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
        if (!Objects.equals(old_minuteHandColor, p_minuteHandColor)) {
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
        if (!Objects.equals(old_secondHandColor, p_secondHandColor)) {
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
        if (!Objects.equals(old_smallCircleColor, p_smallCircleColor)) {
            pcs.firePropertyChange(
                    SMALL_CIRCLE_COLOR_PROPERTY_NAME,
                    old_smallCircleColor, p_smallCircleColor);
        }
    }

    @BeanProperty
    public double getSmallCircleRadius() {
        return smallCircleRadius;
    }

    public void setSmallCircleRadius(double p_smallCircleRadius) {
        double old_smallCircleRadius = smallCircleRadius;
        smallCircleRadius = p_smallCircleRadius;
        if (old_smallCircleRadius != p_smallCircleRadius) {
            pcs.firePropertyChange(
                    SMALL_CIRCLE_RADIUS_PROPERTY_NAME,
                    old_smallCircleRadius, p_smallCircleRadius);
        }
    }

    @BeanProperty
    public double getHalfMoonRadius() {
        return halfMoonRadius;
    }

    public void setHalfMoonRadius(double p_halfMoonRadius) {
        double old_halfMoonRadius = halfMoonRadius;
        halfMoonRadius = p_halfMoonRadius;
        if (old_halfMoonRadius != p_halfMoonRadius) {
            pcs.firePropertyChange(
                    HALF_MOON_RADIUS_PROPERTY_NAME,
                    old_halfMoonRadius, p_halfMoonRadius);
        }
    }


    /* ********************* GETTERS AND SETTERS **********************/
    /* ****************************************************************/

    public static void main(String[] args) {
        GraphicsEnvironment ge = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        Font[] fonts = ge.getAllFonts();
        for (Font f : fonts) {
            System.out.println(f.getName());
        }
        if (ge.isHeadlessInstance()) {
            System.err.println("Cannot display, headless execution");
            System.exit(0);
        }
        EventQueue.invokeLater(() -> {
            JFrame frame = new JFrame("Example");
            JComponent contentPane = (JComponent) frame.getContentPane();
            contentPane.setLayout(new BorderLayout(10, 10));
            JChrono crono = new JChrono();
            JScrollPane scp = new JScrollPane(crono);
//            crono.hourHandColor   = Color.ORANGE;
//            crono.minuteHandColor = Color.BLUE;
//            crono.secondHandColor = Color.RED;
//            crono.tick200msColor  = Color.YELLOW;
//            crono.tick1sColor     = new Color(192, 255, 192);
//            crono.tick5sColor     = new Color(192, 255, 192);
//            crono.tick10sColor    = Color.WHITE;
//            crono.tick1hColor     = new Color(192, 128, 0);
//            crono.lbl1hMajorColor = Color.YELLOW;
//            crono.lbl1hMinorColor = Color.CYAN;
//            crono.lbl5sColor      = Color.LIGHT_GRAY;
//            contentPane.setBorder(
//                    new TitledBorder(new LineBorder(Color.blue),
//                            "Hola como estas?"));
            int N = 500;
            crono.setPreferredSize(new Dimension(N, N));
            contentPane.add(scp, BorderLayout.CENTER);
//            contentPane.add(
//                    new JLabel("Se está quemando la serreríaaa!!!",
//                            JLabel.CENTER),
//                    BorderLayout.SOUTH);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    } /* main(String[]) */
} /* JCrono */
