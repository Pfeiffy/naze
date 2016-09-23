import gnu.io.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream; //import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.TooManyListenersException;

//import OeffnenUndSenden.serialPortEventListener;

public class SeriellTest implements SerialPortEventListener {

	private CommPortIdentifier portID;
	private SerialPort serport;
	private InputStream inputStream;
	private OutputStream outputStream;
	static String empfangen;
	static String gesendet; // was habe ich gesendet
	static String temp1;
	static String temp2;
	public static int msp = 108;

	public static void main(String[] args) throws InterruptedException {
		SeriellTest st = new SeriellTest();

		for (int x = 1; x < 100; x++) {
			st.schreibeAufSeriell();
			Thread.sleep(200);
		}

	}

	public SeriellTest() {
		try {
			portID = CommPortIdentifier.getPortIdentifier("COM21"); // oder
			System.out.println(CommPortIdentifier.getPortIdentifier("COM21").isCurrentlyOwned());

			// // /dev/ttyS0
			serport = (SerialPort) portID.open("foobar", 2000);

			serport.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			inputStream = serport.getInputStream();
			outputStream = serport.getOutputStream();
			serport.addEventListener(this);
			serport.notifyOnDataAvailable(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void serialEvent(SerialPortEvent evt) {
		if (evt.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				int length = inputStream.read();

				byte[] response = new byte[length];

				int zaehler = 0;
				int intArray[] = new int[length];
				for (int i = 0; i < length && inputStream.available() > 0; i++) {
					int value = inputStream.read();
					response[i] = (byte) value;
//					System.out.println("Value: " + i + " bi: " + Integer.toBinaryString(value) + " int: " + value
//							+ " byte: " + response[i]);
					intArray[i] = value;
					zaehler++;
				}
				System.out.println("------------------");

				int pointer = 4;

				int angx = 0;
				int angy = 0;
				int heading = 0;

				angx = intArray[4];
				angx += response[5] * 256;

				angy = intArray[6];
				angy += response[7] * 256;

				heading = intArray[8];
				heading += response[9] * 256;

				System.out.println("angx: " + angx + " angy: " + angy + " heading: " + heading);

//				System.exit(0);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void schreibeAufSeriell() {
		try {
			// // 24 4d 3c 00 01 01
			// outputStream.write(0x24);//36
			// outputStream.write(0x4d);//77
			// outputStream.write(0x3c);
			// outputStream.write(0x00);
			// outputStream.write(0x01);
			// outputStream.write(0x01);

			// // 24 4d 3c 00 01 01
			// outputStream.write(0x24);
			// outputStream.write(0x4d);
			// outputStream.write(0x3c);
			// outputStream.write(0x00);
			// outputStream.write(0x6c);
			// outputStream.write(0x6c);

			// 24 4d 3c 00 01 01
			outputStream.write(36);
			outputStream.write(77);
			outputStream.write(60);
			outputStream.write(0);
			outputStream.write(msp);
			outputStream.write(msp);

			outputStream.flush();
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



}
