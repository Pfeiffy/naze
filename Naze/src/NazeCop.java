import gnu.io.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class NazeCop implements SerialPortEventListener {

	private CommPortIdentifier portID;
	private SerialPort serport;
	private InputStream inputStream;
	private OutputStream outputStream;
	static String port = "COM4";
	static String empfangen;
	static String temp1;
	static String temp2;
	static int RC_RATE = 0;
	static int RC_EXPO = 0;
	static int ident = 0;
	static int angx = 0;
	static int angy = 0;
	static int heading = 0;
	static int altitude = 0;
	static int vario = 0;
	static int MSP_IDENT = 100;
	static int MSP_RC_TUNING = 111;
	static int MSP_ATTITUDE = 108;
	static int MSP_ALTITUDE = 109;
	static int MSP_MISC = 114;

	static int MSP_SET_RC_TUNING = 204;
	static int MSP_SET_MISC = 207;

	public static void main(String[] args) throws InterruptedException,
			IOException {
		NazeCop st = new NazeCop();

		st.sendeBefehlMSP_SET_RC_TUNING();
		Thread.sleep(40);
		st.sendeAbfrageNaze(MSP_RC_TUNING);

	}

	private void sendeBefehlMSP_SET_RC_TUNING() throws IOException {

		int datalengt = 7;

		int RC_Rate = 11;
		int RC_EXPO = 12;
		int RollPitchRate = 13;
		int YawRate = 14;
		int DynThrPID = 15;
		int Throttle_MID = 16;
		int Throttle_EXPO = 17;

		int[] arrChecksum = { datalengt, MSP_SET_RC_TUNING, RC_Rate, RC_EXPO,
				RollPitchRate, YawRate, DynThrPID, Throttle_MID, Throttle_EXPO };
		int checksum = getChecksum(arrChecksum);

		// sendenHeaderBefehl();
		// outputStream.write(7);
		// outputStream.write(108);
		// outputStream.write(11);
		// outputStream.write(12);
		// outputStream.write(13);
		// outputStream.write(14);
		// outputStream.write(15);
		// outputStream.write(16);
		// outputStream.write(17);
		// outputStream.write(checksum);
		//
		sendenHeaderBefehl();
		outputStream.write(datalengt);
		outputStream.write(MSP_SET_RC_TUNING);
		outputStream.write(RC_Rate);
		outputStream.write(RC_EXPO);
		outputStream.write(RollPitchRate);
		outputStream.write(YawRate);
		outputStream.write(DynThrPID);
		outputStream.write(Throttle_MID);
		outputStream.write(Throttle_EXPO);
		outputStream.write(checksum);
		//
		//
		//
		//
		// 6 6c ff ff 3 0 70 0 19
		// 6 108 255 255 3 0 112 0 25

		// sendenHeaderBefehl();
		// outputStream.write(6);
		// outputStream.write(108);
		// outputStream.write(255);
		// outputStream.write(255);
		// outputStream.write(3);
		// outputStream.write(0);
		// outputStream.write(112);
		// outputStream.write(0);
		// outputStream.write(25);

		// int []arrChecksum = {6,108,255,255,3,0,112,0};
		// int checksum = getChecksum(arrChecksum);

		outputStream.flush();
		outputStream.close();

		// System.exit(0);

	}

	public NazeCop() {
		try {
			portID = CommPortIdentifier.getPortIdentifier(port); // oder
			System.out.println(CommPortIdentifier.getPortIdentifier(port)
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
					System.out.println("Value: " + i + " bi: "
							+ Integer.toBinaryString(value) + " int: " + value
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
				if (intArray[3] == MSP_MISC) {
					getMisc(response, intArray);
				}

				//System.exit(0);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void getMisc(byte[] response, int[] intArray) {
		// TODO Auto-generated method stub

	}

	// Lageposition und Kompasswinkel
	private void getMSP_IDENT(byte[] response, int[] intArray) {
		ident = uint8(intArray, 4);

		System.out.println("Ident: " + ident);
	}

	// Lageposition und Kompasswinkel
	private void getMSP_RC_TUNING(byte[] response, int[] intArray) {
		RC_RATE = uint8(intArray, 4);
		RC_EXPO = uint8(intArray, 5);

		System.out.println("RC_RATE: " + RC_RATE + " RC_EXPO: " + RC_EXPO);
	}

	// Lageposition und Kompasswinkel

	private void getAttitude(byte[] response, int[] intArray) {

		angx = uint8to16(response, 4);

		angy = uint8to16(response, 6);

		heading = uint8to16(response, 8);

		System.out.println("angx: " + angx + " angy: " + angy + " heading: "
				+ heading);
	}

	// H�he
	private void getAltitude(byte[] response, int[] intArray) {

		altitude = uint8to32(intArray, 4);

		System.out.println("altitude: " + altitude + " vario: " + vario);
	}

	private int uint8(int[] intArray, int startPointer) {

		int wert8 = (intArray[startPointer]);

		if (intArray[5] >= 128) {
			wert8 = 256 * 256 - wert8;
		}

		return wert8;
	}

	private int uint8to16(byte[] byteArray, int startPointer) {

		ByteBuffer b = ByteBuffer.wrap(byteArray, startPointer, 2).order(
				ByteOrder.LITTLE_ENDIAN);
		int wert16 = b.getShort();

		return wert16;
	}

	private int uint8to32(int[] intArray, int startPointer) {

		int wert32 = (intArray[startPointer] + intArray[startPointer + 1] * 256
				+ intArray[startPointer + 2] * 256 + intArray[startPointer + 3] * 256);
		if (intArray[startPointer] >= 128) {
			wert32 = 256 * 256 - wert32;
		}

		return wert32;

	}

	private void sendeAbfrageNaze(int befehl) {
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
			outputStream.write(befehl);
			outputStream.write(befehl);

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

	private void sendenHeaderBefehl() throws IOException {
		outputStream.write(36);
		outputStream.write(77);
		outputStream.write(60);
	}

	// berechnen der Checksumme aus dem ressponse
	private static int getChecksum(int[] intArray) {
		int checksum = 0;
		for (int x = 0; x < intArray.length; x++) {
			checksum ^= intArray[x];
		}
		return checksum;
	}

}