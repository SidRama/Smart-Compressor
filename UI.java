/* *********Image Compression Tool Using Cluster Analysis**************
 * ******* 						UI Module                    **********
 * ******       Developed by Siddharth Ramachandran            ********
 * ****         Last modified on 19/04/2014,  18:40                ****  
 * ******************************************************************** */

package com.ImageCompressor;

import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class UI extends JFrame implements PropertyChangeListener,
		ActionListener {
	public prg obj;
	private JPanel contentPane;
	private JTextField textField;
	JButton compressB, exitB;
	JProgressBar progressBar;
	JTextField taskOut;

	/**
	 * Launch the application.
	 */
	public void launch() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UI frame = new UI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public UI() {
		System.out.println("UI Created");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 543, 313);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);

		JLabel lblNewLabel = new JLabel("File Path:");
		sl_contentPane.putConstraint(SpringLayout.WEST, lblNewLabel, 66,
				SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblNewLabel, -183,
				SpringLayout.SOUTH, contentPane);
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPane.add(lblNewLabel);

		textField = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, textField, 68,
				SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, textField, 198,
				SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, textField, -79,
				SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblNewLabel, -2,
				SpringLayout.NORTH, textField);
		sl_contentPane.putConstraint(SpringLayout.EAST, lblNewLabel, -24,
				SpringLayout.WEST, textField);
		contentPane.add(textField);
		textField.setColumns(10);

		progressBar = new JProgressBar();
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		sl_contentPane.putConstraint(SpringLayout.NORTH, progressBar, 40,
				SpringLayout.SOUTH, lblNewLabel);
		sl_contentPane.putConstraint(SpringLayout.WEST, progressBar, 98,
				SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, progressBar, -59,
				SpringLayout.EAST, contentPane);
		contentPane.add(progressBar);
		exitB = new JButton("Exit");
		sl_contentPane.putConstraint(SpringLayout.NORTH, exitB, 53,
				SpringLayout.SOUTH, progressBar);
		sl_contentPane.putConstraint(SpringLayout.WEST, exitB, 346,
				SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, exitB, -35,
				SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, exitB, 0,
				SpringLayout.EAST, progressBar);
		compressB = new JButton("Compress");
		sl_contentPane.putConstraint(SpringLayout.WEST, compressB, 98,
				SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, compressB, -132,
				SpringLayout.WEST, exitB);
		sl_contentPane.putConstraint(SpringLayout.NORTH, compressB, 53,
				SpringLayout.SOUTH, progressBar);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, compressB, -35,
				SpringLayout.SOUTH, contentPane);
		compressB.addActionListener(this);
		contentPane.add(compressB);

		exitB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		exitB.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.exit(0);
			}
		});
		contentPane.add(exitB);

		taskOut = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, taskOut, 6,
				SpringLayout.SOUTH, progressBar);
		sl_contentPane.putConstraint(SpringLayout.WEST, taskOut, 0,
				SpringLayout.WEST, progressBar);
		taskOut.setBackground(UIManager.getColor("Button.background"));
		contentPane.add(taskOut);
		taskOut.setColumns(10);

	}

	public void actionPerformed(ActionEvent ae) {
		obj = new prg();
		obj.f = new File(textField.getText());
		System.out.println(obj.f.getAbsolutePath());
		System.out.println("Data recieved by interactModule");
		exitB.setEnabled(false);
		compressB.setEnabled(false);
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		obj.addPropertyChangeListener(this);
		obj.execute();
	}

	public void propertyChange(PropertyChangeEvent evt) {
		String a[] = { "Parsing image...", "Compressing...",
				"Rendering Image...", "Done!" };
		if ("progress" == evt.getPropertyName()) {
			int progress = (Integer) evt.getNewValue();
			progressBar.setValue(progress);
			taskOut.setText("");
			if (obj.getProgress() <= 5)
				taskOut.setText(a[0]);
			else if (obj.getProgress() > 5 && obj.getProgress() <= 90)
				taskOut.setText(a[1]);
			else if (obj.getProgress() > 90 && obj.getProgress() < 100)
				taskOut.setText(a[2]);
			else if (obj.getProgress() == 100) {
				taskOut.setText(a[3]);
				compressB.setEnabled(true);
				exitB.setEnabled(true);
				setCursor(null);
			}

		}

	}
}
