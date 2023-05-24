package hust.cs2007.qpphust.test;

import hust.cs2007.qpphust.app.message.LoginResponseMessage;
import hust.cs2007.qpphust.app.message.Message;

/**
 * ClassName: TestMessage
 * PackageName:hust.cs2007.qpphust.test
 * Description:
 * date: 2022/9/28 17:18
 *
 * @author: 邱攀攀
 * @version: since JDK 1.8
 */
public class TestMessage {
    public static void main(String[] args) {
        new LoginResponseMessage(true, "111").getSequenceId();
        new Message() {

            @Override
            public int getMessageType() {
                return 0;
            }
        }.getSequenceId();
    }
}
