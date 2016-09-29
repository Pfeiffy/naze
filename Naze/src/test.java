import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// int befehlArray[] = { 36, 77, 60, 11, 204, 177, 100, 33, 44, 55, 66,
		// 52, 100, 220, 5, 84, 183, 36, 77, 60, 0,
		// 105, 105 };
		int befehlArray[] = { 36, 77, 60, 11, 204, 177, 100, 33, 44, 55, 66, 52, 100, 220, 5, 84, 183 };
		int erg = getChecksum(befehlArray);
		System.out.println(erg);

		byte[] byteArray = new byte[] { (byte) 240, (byte) 5 };

		ByteBuffer b = ByteBuffer.wrap(byteArray).order(ByteOrder.LITTLE_ENDIAN);
		int wert16 = b.getShort();

		System.out.println("Wert16: " + wert16);

	}

	private static int getChecksum(int[] intArray) {
		int checksum = 0;
		for (int x = 3; x < intArray.length - 1; x++) {
			checksum ^= intArray[x];
		}
		return checksum;
	}

	private int uint8to16() {

		byte[] byteArray = new byte[] { (byte) 7, (byte) 7 };

		ByteBuffer b = ByteBuffer.wrap(byteArray).order(ByteOrder.LITTLE_ENDIAN);
		int wert16 = b.getShort();

		return wert16;
	}

}
