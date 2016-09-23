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

	public static void main(String[] args) {
		SeriellTest st = new SeriellTest();


		st.schreibeAufSeriell();

		//
		// st.schreibeAufSeriell("0");
		// st.schreibeAufSeriell("100");
		// st.schreibeAufSeriell("100");

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

	// byte[] response = new byte[length];
	public void serialEvent(SerialPortEvent evt) {
		if (evt.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				int length = inputStream.read();

				byte[] response = new byte[length];

				int zaehler=0;
				int intArray[] = new int[length];
				for (int i = 0; i < length && inputStream.available() > 0; i++) {
					int value = inputStream.read();
					response[i] = (byte) value;
					System.out.println("Value: "+ i+ " bi: "+ Integer.toBinaryString(value)+ " int: " + value+ " byte: " + response[i]);
					intArray[i] = value;
					zaehler++;
				}
				System.out.println("------------------");
				for (int x = 4; x < zaehler; x++) {
					System.out.println("response: " + x + " " + response[x]);
					String bi = Integer.toBinaryString((int) response[x]);
					// System.out.println(bi);
				}
				int pointer = 4;

				int angx=0;
				int angy=0;
				int heading=0;
				
				angx =  intArray[4];
				angx += response[5] * 256;
				
				angy =  intArray[6];
				angy += response[7] * 256;
				
				heading =  intArray[8];
				heading += response[9] * 256;
				

				
				
//				int angx = response[pointer];
//				pointer += 2;
//				int angy = (response[pointer++] & 0xFF) | ((response[pointer++] & 0xFF) << 8);
//
//				int heading = (response[pointer++] & 0xFF) | ((response[pointer++] & 0xFF) << 8);

				System.out.println("angx: " + angx + " angy: " + angy + " heading: " + heading);

				// int c;
				// // lies bis \r\n
				// ArrayList<Integer> aList = new ArrayList<Integer>();
				// while ((c = inputStream.read()) != -1) {
				// System.out.print(c);
				// System.out.print(" ");
				// System.out.println(Character.toString((char) c));
				// if (c != 13) {
				// // System.out.println("empfange");
				// readBuffer.append((char) c);
				// aList.add(c);
				// }
				// }
				// inputStream.close();
				//
				// // MSP_ATTITUDE 109
				// if (aList.get(4) == msp) {
				//
				// // String angx = Integer.toBinaryString(aList.get(5)) +
				// // Integer.toBinaryString(aList.get(6));
				// System.out.println("angx: "
				// + int8toint16(aList.get(6), aList.get(5)));
				// }

				System.exit(0);
				// empfangen = readBuffer.toString();

				// mach was mit den eingelesenen Daten
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

	private static int int8toint16(int loByte, int hiByte) {
		String high = String.format("%08d", Integer.parseInt(Integer.toBinaryString(hiByte)));
		String low = String.format("%08d", Integer.parseInt(Integer.toBinaryString(loByte)));

		String bi16 = high + low;
		int int16 = Integer.parseInt(bi16, 2);
		System.out.println(bi16);
		return int16;
	}

}
