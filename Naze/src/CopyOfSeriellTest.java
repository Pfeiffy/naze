import gnu.io.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream; //import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.TooManyListenersException;

//import OeffnenUndSenden.serialPortEventListener;



public class CopyOfSeriellTest implements SerialPortEventListener {

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
		CopyOfSeriellTest st = new CopyOfSeriellTest();

		// uint8_t checksum = 0;
		//
		// Serial.write((byte *)"$M<", 3);
		// Serial.write(n_bytes); --> 0
		// checksum ^= n_bytes;
		//
		// Serial.write(opcode);
		// checksum ^= opcode;
		//
		// Serial.write(checksum);

		st.schreibeAufSeriell();

		//
		// st.schreibeAufSeriell("0");
		// st.schreibeAufSeriell("100");
		// st.schreibeAufSeriell("100");

	}

	public CopyOfSeriellTest() {
		try {
			portID = CommPortIdentifier.getPortIdentifier("COM3"); // oder
			System.out.println(CommPortIdentifier.getPortIdentifier("COM3")
					.isCurrentlyOwned());

			// // /dev/ttyS0
			serport = (SerialPort) portID.open("foobar", 2000);

			serport.setSerialPortParams(115200, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
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
				StringBuffer readBuffer = new StringBuffer();
				int c;
				// lies bis \r\n
				ArrayList<Integer> aList = new ArrayList<Integer>();
				while ((c = inputStream.read()) != -1) {
					System.out.print(c);
					System.out.print(" ");
					System.out.println(Character.toString((char) c));
					if (c != 13) {
						// System.out.println("empfange");
						readBuffer.append((char) c);
						aList.add(c);
					}
				}
				inputStream.close();

				// MSP_ATTITUDE 109
				if (aList.get(4) == msp) {

					// String angx = Integer.toBinaryString(aList.get(5)) +
					// Integer.toBinaryString(aList.get(6));
					System.out.println("angx: "
							+ int8toint16(aList.get(6), aList.get(5)));
				}

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
		String high = String.format("%08d",
				Integer.parseInt(Integer.toBinaryString(hiByte)));
		String low = String.format("%08d",
				Integer.parseInt(Integer.toBinaryString(loByte)));

		String bi16 = high + low;
		int int16 = Integer.parseInt(bi16, 2);
		System.out.println(bi16);
		return int16;
	}

}