import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class NazeHelper {

	// byte8 nach int8
	public static int uint8(int[] intArray, int startPointer) {

		int wert8 = (intArray[startPointer]);

		if (intArray[5] >= 128) {
			wert8 = 256 * 256 - wert8;
		}

		return wert8;
	}

	// 2 uint8 in ein int16
	public static int uint8to16(byte[] byteArray, int startPointer) {

		ByteBuffer b = ByteBuffer.wrap(byteArray, startPointer, 2).order(
				ByteOrder.LITTLE_ENDIAN);
		int wert16 = b.getShort();

		return wert16;
	}

	// 4 uint8 in ein uint32
	public static int uint8to32(int[] intArray, int startPointer) {

		int wert32 = (intArray[startPointer] + intArray[startPointer + 1] * 256
				+ intArray[startPointer + 2] * 256 + intArray[startPointer + 3] * 256);
		if (intArray[startPointer] >= 128) {
			wert32 = 256 * 256 - wert32;
		}

		return wert32;

	}

	// 1 uint16 in 2 uint8
	public static byte[] intToByteArray16(int value) {
		return new byte[] { (byte) value, (byte) (value >> 8) };
	}

	// 1 uint32 in 4 uint8
	public static byte[] intToByteArray32(int value) {
		return new byte[] { (byte) value, (byte) (value >> 8),
				(byte) (value >> 16), (byte) (value >> 24) };
	}

	// berechnen der Checksumme aus dem ressponse
	public static int getChecksum(int[] intArray) {
		int checksum = 0;
		for (int x = 0; x < intArray.length; x++) {
			checksum ^= intArray[x];
		}
		return checksum;
	}

	public static void sendenHeader(OutputStream outputStream)
			throws IOException {
		outputStream.write(36); // $
		outputStream.write(77); // M
		outputStream.write(60); // >
		outputStream.write(0); // 0
	}

	public static void sendenHeaderBefehl(OutputStream outputStream)
			throws IOException {
		outputStream.write(36);
		outputStream.write(77);
		outputStream.write(60);
	}

	public static void schreibenEeprom(OutputStream outputStream)
			throws IOException {
		sendenHeader(outputStream);
		outputStream.write(250); // 250
		outputStream.write(250); // 250

		outputStream.flush();
		outputStream.close();

	}

}
