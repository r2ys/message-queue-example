通过消息队列进行服务间的调用，如何确保一致性
https://www.cnblogs.com/jajian/p/10014145.html

sender service:
1.sendMsg(to mq service)->saveMsgLocally(self)
3.confirmMsg(to mq service)->updateMsgLocally(self)
3'.undoMsg(to mq service)
10.queryMsgStatus(from mq service)


.notifyMsgResult

mq service:
2.saveUnconfirmedMsg(from sender service)
4.confirmMsgOrCheck(from sender service)
4'.undoMsg(from sender service)
5.sendMsgToMq(to mq)[transactional with step 4]
8.finishConfirmedMsg(from consumer)
9.pollingUnconfirmedMsg(self)
11. ->4(4')->5
12.pollingSentMsg(self)
13. ->5


.finishNotify

consumer service:
6.receiveMsg(from mq)
7.ackMsg(to mq service)
14.consume


.finishNotify
.queryTaskStatus



