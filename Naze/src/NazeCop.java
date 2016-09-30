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
	private static OutputStream outputStream;
	static String port = "COM4";
	static String empfangen;
	static String temp1;
	static String temp2;

	static int ident = 0;
	static int angx = 0;
	static int angy = 0;
	static int heading = 0;
	static int altitude = 0;
	static int vario = 0;
	static int RC_Rate = 0;
	static int RC_Expo = 0;
	static int Roll_Rate = 0;
	static int Pitch_Rate = 0;
	static int Yaw_Rate = 0;
	static int TPA = 0;
	static int Throttle_MID = 0;
	static int Throttle_EXPO = 0;
	static int TPA_BR_1 = 0;
	static int TPA_BR_2 = 0;
	static int TPA_BR = 0;
	static int RC_Yaw__EXPO = 0;

	//AbfrageBefehle
	static int MSP_IDENT = 100;
	static int MSP_RC_TUNING = 111;
	static int MSP_ATTITUDE = 108;
	static int MSP_ALTITUDE = 109;
	static int MSP_MISC = 114;

	//Kommandobefehle
	static int MSP_SET_RC_TUNING = 204;
	static int MSP_SET_MISC = 207;

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

	public static void main(String[] args) throws InterruptedException,
			IOException {
		NazeCop st = new NazeCop();

		// sendeBefehlArray();
		st.sendeBefehlMSP_SET_RC_TUNING();
		Thread.sleep(200);
		st.sendeAbfrageNaze(MSP_RC_TUNING);
		Thread.sleep(10);
		System.exit(0);

	}

	public void serialEvent(SerialPortEvent evt) {
		if (evt.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				int length = inputStream.read();

				byte[] response = new byte[length];

				int zaehler = 0;
				int intArray[] = new int[length];
				System.out.println("--------------------");

				for (int i = 0; i < length && inputStream.available() > 0; i++) {
					int value = inputStream.read();
					response[i] = (byte) value;
					System.out.println("Value: " + i + " bi: "
							+ Integer.toBinaryString(value) + " int: " + value
							+ " byte: " + response[i]);
					intArray[i] = value;
					zaehler++;
				}

				if (intArray[3] == MSP_RC_TUNING) {
					getMSP_RC_TUNING(response, intArray);
				}
				if (intArray[3] == 108) {
					getAttitude(response, intArray);
				}
				if (intArray[3] == 109) {
					getAltitude(response, intArray);
				}
				if (intArray[3] == MSP_MISC) {
					getMSP_MISC(response, intArray);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void sendeBefehlArray() throws IOException {

		int befehlArray[] = { 36, 77, 60, 11, 204, 177, 100, 33, 44, 55, 66,
				52, 100, 220, 5, 84, 183 };

		for (int x = 0; x < befehlArray.length; x++) {
			outputStream.write(befehlArray[x]);
		}

		outputStream.flush();
		outputStream.close();

	}

	private void sendeBefehlMSP_SET_RC_TUNING() throws IOException {

		int TPA_BR = 1200;
		byte[] TPA_b_ar = NazeHelper.intToByteArray16(TPA_BR);
		int datalengt = 11;

		int RC_Rate = 11;
		int RC_EXPO = 12;
		int Roll_Rate = 13;
		int Pitch_Rate = 13;
		int Yaw_Rate = 14;
		int TPA = 15;
		int Throttle_MID = 16;
		int Throttle_EXPO = 17;
		int TPA_BR_1 = TPA_b_ar[0];
		int TPA_BR_2 = TPA_b_ar[1];
		int RC_Yaw__EXPO = 17;

		int[] arrChecksum = { datalengt, MSP_SET_RC_TUNING, RC_Rate, RC_EXPO,
				Roll_Rate, Pitch_Rate, Yaw_Rate, TPA, Throttle_MID,
				Throttle_EXPO, TPA_BR_1, TPA_BR_2, RC_Yaw__EXPO };
		int checksum = NazeHelper.getChecksum(arrChecksum);

		NazeHelper.sendenHeaderBefehl(outputStream);
		outputStream.write(datalengt);
		outputStream.write(MSP_SET_RC_TUNING);
		outputStream.write(RC_Rate);
		outputStream.write(RC_EXPO);
		outputStream.write(Roll_Rate);
		outputStream.write(Pitch_Rate);
		outputStream.write(Yaw_Rate);
		outputStream.write(TPA);
		outputStream.write(Throttle_MID);
		outputStream.write(Throttle_EXPO);
		outputStream.write(TPA_BR_1);
		outputStream.write(TPA_BR_2);
		outputStream.write(RC_Yaw__EXPO);
		outputStream.write(checksum);

		outputStream.flush();
		outputStream.close();

		NazeHelper.schreibenEeprom(outputStream);

	}

	private void getMSP_MISC(byte[] response, int[] intArray) {
		// TODO Auto-generated method stub

	}

	// Lageposition und Kompasswinkel
	private void getMSP_IDENT(byte[] response, int[] intArray) {
		ident = NazeHelper.uint8(intArray, 4);

		System.out.println("Ident: " + ident);
	}

	// Lageposition und Kompasswinkel
	private void getMSP_RC_TUNING(byte[] response, int[] intArray) {
		RC_Rate = NazeHelper.uint8(intArray, 4);
		RC_Expo = NazeHelper.uint8(intArray, 5);

		Roll_Rate= NazeHelper.uint8(intArray, 6);
		Pitch_Rate = NazeHelper.uint8(intArray, 7);
		Yaw_Rate = NazeHelper.uint8(intArray, 8);
		TPA = NazeHelper.uint8(intArray, 9);
		Throttle_MID = NazeHelper.uint8(intArray, 10);
		Throttle_EXPO = NazeHelper.uint8(intArray, 11);
		TPA_BR = NazeHelper.uint8to16(response, 12);
		RC_Yaw__EXPO = NazeHelper.uint8(intArray, 14);
		
		System.out.println("RC_RATE: " + RC_Rate + " RC_EXPO: " + RC_Expo+ " "+);
	}

	// Lageposition und Kompasswinkel
	private void getAttitude(byte[] response, int[] intArray) {

		angx = NazeHelper.uint8to16(response, 4);

		angy = NazeHelper.uint8to16(response, 6);

		heading = NazeHelper.uint8to16(response, 8);

		System.out.println("angx: " + angx + " angy: " + angy + " heading: "
				+ heading);
	}

	// H�he
	private void getAltitude(byte[] response, int[] intArray) {

		altitude = NazeHelper.uint8to32(intArray, 4);

		System.out.println("altitude: " + altitude + " vario: " + vario);
	}

	// reine Abfrage
	private void sendeAbfrageNaze(int befehl) {
		try {

			NazeHelper.sendenHeader(outputStream);
			outputStream.write(befehl);
			outputStream.write(befehl);

			outputStream.flush();
			outputStream.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
