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

public class JButtonForChrono extends JButton {

		MouseEvent lastButtonPressEvent;

		public JButtonForChrono(Action action) {
			super(action);
			addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent evt) {
                    lastButtonPressEvent = evt;
                }
            });
		}

    public MouseEvent getLastButtonPressEvent() {
        return lastButtonPressEvent;
    }
    
    public static void main(String[] args) {
        JFrame frame  = new JFrame("Prueba de JButtonForChrono");
        
        JButtonForChrono button = new JButtonForChrono(null);
        
        Action action = new AbstractAction("Press me!") {
            @Override
            public void actionPerformed(ActionEvent e) {
                long diff = e.getWhen() - button.getLastButtonPressEvent().getWhen();
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
