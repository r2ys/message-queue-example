package top.r2ys.messagequeueexamplesender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @program: message-queue-example
 * @description:
 * @author: HU
 * @create: 2019-05-29 17:23
 */
@RestController
@RequestMapping("/sender")
public class SendController {

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/queryMsgStatus/${msg}")
    public String queryMsgStatus(@PathVariable("msg") String msg) {
        String msgStatus = "confirmed";

        switch (msgStatus) {
            case "confirmed":

                break;
            case "unconfirmed":

                break;
            case "finished":

                break;
            default:
                break;
        }

        return msgStatus;
    }

    @RequestMapping("/sendMsg/${msg}")
    public String sendMsg(@PathVariable("msg") String msg) {
        // sendMsg(to mq service)
        ResponseEntity<String> saveUnconfirmedMsgResponse = null;
        try {
            saveUnconfirmedMsgResponse = restTemplate.getForEntity(new URI("http://127.0.0.1:17059/mq/saveUnconfirmedMsg/" + msg), String.class);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        System.out.println(saveUnconfirmedMsgResponse.getBody());

        // saveMsgLocally(self)
        if (saveUnconfirmedMsgResponse.getBody().equals("ok")) {
            boolean savedMsgResult = true;
            System.out.println("saved " + msg);

            if (savedMsgResult == true) {
                //
                boolean confirmMsgResult = confirmMsg(msg);
                System.out.println("confirmMsg result:" + confirmMsgResult);
                //
                if (confirmMsgResult == true) {
                    // update
                    boolean updateMsgStatus = true;
                    System.out.println("updateMsg Status to: confirmed");
                    return "executing";
                }
            } else {
                boolean undoMsgResult = undoMsg(msg);
                System.out.println("undoMsg result:" + undoMsgResult);

                if (undoMsgResult == true) {
                    // update
                    boolean updateMsgStatus = true;
                    System.out.println("updateMsg Status to: unconfirmed");
                    return "failed to execute msg";
                }
            }
        }

        System.out.println("failed to send msg");
        return "fail";
    }

    private boolean confirmMsg(@PathVariable("msg") String msg) {
        // confirmMsgOrCheck (to mq service)
        ResponseEntity<String> confirmMsgResponse = null;
        try {
            confirmMsgResponse = restTemplate.getForEntity(new URI("http://127.0.0.1:17059/mq/confirmMsgOrCheck/" + msg), String.class);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        System.out.println(confirmMsgResponse.getBody());

        if (confirmMsgResponse.getBody().equals("ok")) {
            return true;
        } else {
            return false;
        }
    }

    private boolean undoMsg(@PathVariable("msg") String msg) {
        // undoMsgResponse(to mq service)
        ResponseEntity<String> undoMsgResponse = null;
        try {
            undoMsgResponse = restTemplate.getForEntity(new URI("http://127.0.0.1:17059/mq/undoMsg/" + msg), String.class);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        System.out.println(undoMsgResponse.getBody());

        if (undoMsgResponse.getBody().equals("ok")) {
            return true;
        } else {
            return false;
        }
    }
}
