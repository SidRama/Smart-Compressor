/* *********Image Compression Tool Using Cluster Analysis************** 
 * ******       Developed by Siddharth Ramachandran            ********
 * ****         Last modified on 19/04/2014,  18:40                ****  
 * ******************************************************************** */

package com.ImageCompressor;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.ejml.simple.SimpleMatrix;

class prg extends SwingWorker<Void, Void> {
	SimpleMatrix A;
	File f;
	SimpleMatrix centroid, cImg;
	static UI obj;
	int count, w, h, size, progress;
	int cluster[];
	ImageIcon icon1 = new ImageIcon("close-icon.png");

	public static void main(String args[]) {

		obj = new UI();
		obj.launch();

		try {
			System.in.read();
		} catch (Exception e) {
		}

	}

	@Override
	public Void doInBackground() {

		progress = 0;
		setProgress(0);

		try {

			BufferedImage image = ImageIO.read(f);
			System.out.println("Data recieved by clusterModule");
			extract(image);

			progress = 5;
			setProgress(progress);
		}

		catch (Exception e) {

			System.out.println("Error in loading image: \n" + e.getMessage());
			JOptionPane.showMessageDialog(obj, "Please enter a correct path",
					"Error", JOptionPane.INFORMATION_MESSAGE, icon1);
			System.exit(0);

		}

		init(); // Cluster intitialization
		// Run KMeans from here
		kMeans();
		System.out.println("Done");
		for (int i = 0; i < 16; i++)
			System.out.println((int) (centroid.get(i, 0) * 255) + " "
					+ (int) (centroid.get(i, 1) * 255) + " "
					+ (int) (centroid.get(i, 2) * 255));

		return null;
	}

	public void extract(BufferedImage image) {
		w = image.getWidth();
		h = image.getHeight();
		size = w * h;
		int r = 0, g = 0, b = 0;
		count = 0;
		A = new SimpleMatrix(size, 3);

		try {

			FileWriter writer = new FileWriter(f.getParent() + File.separator
					+ "dataPre.csv");
			writer.append("Red");
			writer.append(',');
			writer.append("Green");
			writer.append(',');
			writer.append("Blue");
			writer.append('\n');
			for (int x = 0; x < w; x++) {
				for (int y = 0; y < h; y++) {
					int pixel = image.getRGB(x, y);

					b = (pixel) & 0xff; // extract from Java integer
					g = (pixel >> 8) & 0xff;
					r = (pixel >> 16) & 0xff;

					A.set(count, 0, r); // Values stored in matrix
					A.set(count, 1, g);
					A.set(count, 2, b);
					writer.append(Double.toString(A.get(count, 0)));
					writer.append(',');
					writer.append(Double.toString(A.get(count, 1)));
					writer.append(',');
					writer.append(Double.toString(A.get(count, 2)));
					writer.append('\n');

					++count;
				}
			}
			writer.flush();
			writer.close();
			cluster = new int[count];
			System.out.println(w + " " + h);
		} catch (IOException E) {
			System.out.println("Error .csv generation ");
		}
		A = A.divide(255);
		--count;
	}

	void init() {
		centroid = new SimpleMatrix(16, 3);
		Random r = new Random();
		int t = 0;
		for (int i = 0; i < 16; i++) {
			t = r.nextInt(size);
			centroid.set(i, 0, A.get(t, 0));
			centroid.set(i, 1, A.get(t, 1));
			centroid.set(i, 2, A.get(t, 2));

		}

		for (int i = 0; i < 16; i++)
			System.out.println((int) (centroid.get(i, 0) * 255) + " "
					+ (int) (centroid.get(i, 1) * 255) + " "
					+ (int) (centroid.get(i, 2) * 255));
		// Exporting to .csv
		try {
			FileWriter writer = new FileWriter(f.getParent() + File.separator
					+ "clustersInit.csv");
			writer.append("IRed");
			writer.append(',');
			writer.append("IGreen");
			writer.append(',');
			writer.append("IBlue");
			writer.append('\n');
			for (int i = 0; i < 16; i++) {
				writer.append(Integer.toString((int) (centroid.get(i, 0) * 255)));
				writer.append(',');
				writer.append(Integer.toString((int) (centroid.get(i, 1) * 255)));
				writer.append(',');
				writer.append(Integer.toString((int) (centroid.get(i, 2) * 255)));
				writer.append('\n');
			}
			writer.flush();
			writer.close();

		} catch (IOException e1) {
			System.out.println("Error generating .csv cluster init");
		}

	}

	void kMeans() {
		for (int g = 1; g <= 15; g++) {
			System.out.println("In iteration:" + g);
			centroidAssign();
			newMean();
			progress += 6;
			setProgress(progress);
		}
		// Exporting to .csv
		try {

			FileWriter writer = new FileWriter(f.getParent() + File.separator
					+ "clusters.csv");
			writer.append("CRed");
			writer.append(',');
			writer.append("CGreen");
			writer.append(',');
			writer.append("CBlue");
			writer.append('\n');
			for (int i = 0; i < 16; i++) {
				writer.append(Integer.toString((int) (centroid.get(i, 0) * 255)));
				writer.append(',');
				writer.append(Integer.toString((int) (centroid.get(i, 1) * 255)));
				writer.append(',');
				writer.append(Integer.toString((int) (centroid.get(i, 2) * 255)));
				writer.append('\n');
			}
			writer.flush();
			writer.close();

		} catch (IOException e1) {
			System.out.println("Error generating .csv 2");
		}
		// mapping pixels to clusters
		mapPixel();
		System.out.println("Mapping Complete!");
		// image rendering
		imageRender();

	}

	void centroidAssign() {
		SimpleMatrix m1 = new SimpleMatrix(1, 3);
		SimpleMatrix m2 = new SimpleMatrix(1, 3);
		SimpleMatrix m3 = new SimpleMatrix(1, 3);
		for (int i = 0; i < count; i++) {
			double min = 1000;
			double dist = 0;
			m1 = A.extractMatrix(i, i + 1, 0, 3);
			for (int k = 0; k < 16; k++) {

				m2 = centroid.extractMatrix(k, k + 1, 0, 3);

				m3 = m1.minus(m2);
				m3 = m3.elementMult(m3);// perform per element multiplication
				dist = m3.elementSum();
				if (dist < min) {
					min = dist;
					cluster[i] = k;
				}

			}
		}

	}

	void mapPixel() {
		compImage();
		cImg = new SimpleMatrix(count, 3);
		for (int i = 0; i < count; i++) {
			cImg.set(i, 0, centroid.get(cluster[i], 0));
			cImg.set(i, 1, centroid.get(cluster[i], 1));
			cImg.set(i, 2, centroid.get(cluster[i], 2));
		}
		progress += 2;
		setProgress(progress);
	}

	void newMean() {
		int chk[] = new int[16];
		SimpleMatrix sum = new SimpleMatrix(16, 3);
		for (int i = 0; i < 16; i++) {
			chk[i] = 0;
			sum.set(i, 0, 0);
			sum.set(i, 1, 0);
			sum.set(i, 2, 0);

		}

		for (int i = 0; i < count; i++) {
			sum.set(cluster[i], 0, (sum.get(cluster[i], 0) + A.get(i, 0)));
			sum.set(cluster[i], 1, (sum.get(cluster[i], 1) + A.get(i, 1)));
			sum.set(cluster[i], 2, (sum.get(cluster[i], 2) + A.get(i, 2)));
			chk[cluster[i]] = chk[cluster[i]] + 1;
		}
		// divide to find mean
		for (int i = 0; i < 16; i++) {
			centroid.set(i, 0, (sum.get(i, 0) / chk[i]));
			centroid.set(i, 1, (sum.get(i, 1) / chk[i]));
			centroid.set(i, 2, (sum.get(i, 2) / chk[i]));

		}
	}

	void imageRender() {
		int t = 0;
		int chk = 0;
		BufferedImage outImg = new BufferedImage(w, h,
				BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				if (t < count) {
					Color c = new Color((int) (cImg.get(t, 0) * 255),
							(int) (cImg.get(t, 1) * 255),
							(int) (cImg.get(t, 2) * 255));
					outImg.setRGB(x, y, c.getRGB());

					++t;
				} else {
					++chk;
				}
			}
		}
		System.out.println(chk);
		try {
			ImageIO.write(outImg, "jpg", new File(f.getParent()
					+ File.separator + "test.jpg"));
			progress = 100;
			setProgress(progress);
		} catch (IOException E) {
			System.out.println("Error in rendering!");
		}

	}

	void compImage() {
		try {
			FileWriter writer3 = new FileWriter(f.getParent() + File.separator
					+ "compressedImage.csv");
			for (int i = 0; i < 16; i++) {
				writer3.append(Integer.toString((int) (centroid.get(i, 0) * 255)));
				writer3.append(',');
				writer3.append(Integer.toString((int) (centroid.get(i, 1) * 255)));
				writer3.append(',');
				writer3.append(Integer.toString((int) (centroid.get(i, 2) * 255)));
				writer3.append('\n');
			}
			for (int i = 0; i < count; i++) {
				writer3.append(Integer.toString((int) (cluster[i])));
				writer3.append('\n');
			}
			writer3.flush();
			writer3.close();

		} catch (IOException e3) {
			System.out.println("Error genereating image base");
		}
	}
}
