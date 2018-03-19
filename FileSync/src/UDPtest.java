import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class UDPtest {

	private static final int TIMEOUT = 5000;  //���ý������ݵĳ�ʱʱ��
	private static final int MAXNUM = 2;      //�����ط����ݵ�������
	public static void main(String args[])throws IOException{
		String str_send = "Exit| ";
		byte[] buf = new byte[8192];
		//�ͻ�����9000�˿ڼ������յ�������
		DatagramSocket ds = new DatagramSocket(44658);
		//InetAddress loc = InetAddress.getLocalHost();
		InetAddress loc = InetAddress.getByName("255.255.255.255");
		//���������������ݵ�DatagramPacketʵ��
		DatagramPacket dp_send= new DatagramPacket(str_send.getBytes(),str_send.length(),loc,44659);
		//���������������ݵ�DatagramPacketʵ��
		DatagramPacket dp_receive = new DatagramPacket(buf, 1024);
		//���ݷ��򱾵�3000�˿�
		Scanner reader=new Scanner(System.in);
		ds.setSoTimeout(TIMEOUT);              //���ý�������ʱ�������ʱ��
		int tries = 0;                         //�ط����ݵĴ���
		while(true){
			System.out.print("Type in Commend");
			str_send=reader.nextLine();
			if(str_send.equals("ex"))
				break;
			boolean receivedResponse = false;     //�Ƿ���յ����ݵı�־λ
			//ֱ�����յ����ݣ������ط������ﵽԤ��ֵ�����˳�ѭ��
			dp_send= new DatagramPacket(str_send.getBytes(),str_send.length(),loc,44659);
			dp_receive = new DatagramPacket(buf, 8192);
			while(!receivedResponse && tries<MAXNUM){
				//��������
				ds.send(dp_send);
				try{
					//���մӷ���˷��ͻ���������
					ds.receive(dp_receive);
					//������յ������ݲ�������Ŀ���ַ�����׳��쳣
					if(!dp_receive.getAddress().equals(loc)){
						
						//throw new IOException("Received packet from an umknown source");
					}
					//������յ����ݡ���receivedResponse��־λ��Ϊtrue���Ӷ��˳�ѭ��
					receivedResponse = true;
				}catch(InterruptedIOException e){
					//�����������ʱ������ʱ���ط�������һ���ط��Ĵ���
					tries += 1;
					System.out.println("Time out," + (MAXNUM - tries) + " more tries..." );
				}
			}
			if(receivedResponse){
				//����յ����ݣ����ӡ����
				System.out.println("client received data from server��");
				String str_receive = new String(dp_receive.getData(),0,dp_receive.getLength()) + 
						" from " + dp_receive.getAddress().getHostAddress() + ":" + dp_receive.getPort();
				System.out.println(str_receive);
				//����dp_receive�ڽ���������֮�����ڲ���Ϣ����ֵ���Ϊʵ�ʽ��յ���Ϣ���ֽ�����
				//��������Ҫ��dp_receive���ڲ���Ϣ����������Ϊ1024
				dp_receive.setLength(1024);   
			}else{
				//����ط�MAXNUM�����ݺ���δ��÷��������ͻ��������ݣ����ӡ������Ϣ
				System.out.println("No response -- give up.");
				
			}
			tries = 0; 
		}
		ds.close();
	}  
}
