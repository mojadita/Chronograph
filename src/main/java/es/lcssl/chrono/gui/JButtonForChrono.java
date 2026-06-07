/* JButtonForChrono.java -- JButton para un cronometro.
 * Author: Luis Colorado <luiscoloradourcola@gmail.com>
 * Date: Tue Jun  2 09:43:13 -05 2026
 * Copyright: (c) 2026 Luis Colorado.  All rights reserved.
 * License: BSD
 */

package es.lcssl.chrono.gui;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * Small derived class from {@link JButton} to register the {@link MouseEvent}
 * of the button press prior to a click event (this event has the timestamp
 * required to be used in the chronograph, and not the one used in the button
 * click, which represents the timestamp at the button release)
 *
 * @author Luis Colorado {@code <luiscoloradourcola@gmail.com>}
 */
@SuppressWarnings("serial")
public class JButtonForChrono extends JButton {

    /**
     * Saved event of the last button press event.
     */
    MouseEvent lastButtonPressEvent;

    /**
     * In use constructor to use the parent class constructor, and to
     * register a {@link MouseAdapter} to listen for mouse events.
     *
     * Every time a mouse button press event is detected, it is saved for
     * later processing when the button {@link #actionListener} is to be
     * called, to have access to the last button press event and be able to
     * provide events using the button press timestamp, instead of the
     * button release (which is the timestamp that normally appears in the
     * mouse event passed)
     *
     * @param action Action instance to represent the button action to perform
     * when a button click is generated.
     */
    @SuppressWarnings("this-escape")
    public JButtonForChrono(Action action) {
        super(action);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                lastButtonPressEvent = evt;
            }
        });
    }

    /**
     * Getter for the {@link #lastButtonPressEvent} property.
     *
     * @return the {@link MouseEvent} saved for later processing.
     */
    public MouseEvent getLastButtonPressEvent() {
        return lastButtonPressEvent;
    }

    /**
     * main() routine for testing purposes.
     *
     * This main routine is left here to allow to test the
     * {@link JButtonForChrono},
     * creating a single window {@link JFrame} with an instance of this class,
     * to compute the difference between the button activation timestamp and the
     * previous button press mouse event.  This should be equal to the
     * interval between the button press and button release of the mouse.
     *
     * @param args not used.
     */
    public static void main(String[] args) {
        JFrame frame  = new JFrame("Prueba de JButtonForChrono");

        JButtonForChrono button = new JButtonForChrono(null);

        Action action = new AbstractAction("Press me!") {
            @Override
            public void actionPerformed(ActionEvent e) {
                long diff = e.getWhen()
                        - button.getLastButtonPressEvent()
                                .getWhen();
                System.out.println(
                        "difference == "
                        + diff);
            }
        };
        button.setAction(action);
        frame.getContentPane().add(button);
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
