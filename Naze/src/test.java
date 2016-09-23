public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		byte hiByte = 82;
		byte loByte = 1;

	int ss = (int)	((hiByte & 0xFF) | (loByte & 0xFF) << 8);
	
	
		System.out.println(ss);
		
		
	}

}
