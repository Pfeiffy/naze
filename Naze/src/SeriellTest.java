import gnu.io.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream; //import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
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
	static int RC_RATE = 0;
	static int RC_EXPO =0;
	static int ident = 0;
	static int angx = 0;
	static int angy = 0;
	static int heading = 0;
	static int altitude = 0;
	static int vario = 0;
	static int[] MSP_IDENT = {100,100};
	static int[] MSP_RC_TUNING = {111,111};
	static int[] MSP_ATTITUDE = { 108, 108 };
	static int[] MSP_ALTITUDE = { 109, 109 };

	public static void main(String[] args) throws InterruptedException {
		SeriellTest st = new SeriellTest();

		st.sendeNaze(MSP_RC_TUNING);

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
					System.out.println("Value: " + i + " bi: " + Integer.toBinaryString(value) + " int: " + value
							+ " byte: " + response[i]);
					intArray[i] = value;
					zaehler++;
				}

				if (intArray[3] == 111) {
					getMSP_RC_TUNING(response, intArray);
				}
				if (intArray[3] == 108) {
					getAttitude(response, intArray);
				}
				if (intArray[3] == 109) {
					getAltitude(response, intArray);
				}
				System.exit(0);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	// Lageposition und Kompasswinkel
	private void getMSP_IDENT(byte[] response, int[] intArray) {
		ident = uint8(intArray, 4);


		System.out.println("Ident: " + ident );
	}
	
	// Lageposition und Kompasswinkel
	private void getMSP_RC_TUNING(byte[] response, int[] intArray) {
		RC_RATE = uint8(intArray, 4);
		RC_EXPO = uint8(intArray, 5);


		System.out.println("RC_RATE: " + RC_RATE+  " RC_EXPO: " + RC_EXPO );
	}
	
	
	
	
	
	
	// Lageposition und Kompasswinkel

	private void getAttitude(byte[] response, int[] intArray) {

		angx = uint8to16(intArray, 4);

		angy = uint8to16(intArray, 6);

		heading = uint8to16(intArray, 8);

		System.out.println("angx: " + angx + " angy: " + angy + " heading: " + heading);
	}

	// Höhe
	private void getAltitude(byte[] response, int[] intArray) {

		altitude = uint8to32(intArray, 4);

		System.out.println("altitude: " + altitude + " vario: " + vario);
	}

	private int uint8(int[] intArray, int startPointer) {

		int wert8 = (intArray[startPointer] );

		if (intArray[5] >= 128) {
			wert8 = 256 * 256 - wert8;
		}

		return wert8;
	}

	private int uint8to16(int[] intArray, int startPointer) {

		int wert16 = (intArray[startPointer] + intArray[startPointer + 1] * 256);

		if (intArray[5] >= 128) {
			wert16 = 256 * 256 - wert16;
		}

		return wert16;
	}

	private int uint8to32(int[] intArray, int startPointer) {

		int wert32 = (intArray[startPointer] + intArray[startPointer+1] * 256 + intArray[startPointer+2] * 256
				+ intArray[startPointer+3] * 256);
		if (intArray[startPointer] >= 128) {
			wert32 = 256 * 256 - wert32;
		}

		return wert32;


	}

	private void sendeNaze(int[] befehlArray) {
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
			sendenHeader();
			for (int i = 0; i < befehlArray.length; i++) {
				outputStream.write(befehlArray[i]);
			}

			outputStream.flush();
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendenHeader() throws IOException {
		outputStream.write(36);
		outputStream.write(77);
		outputStream.write(60);
		outputStream.write(0);
	}

}
