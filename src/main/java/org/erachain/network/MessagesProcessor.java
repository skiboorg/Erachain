package org.erachain.network;

import org.erachain.controller.Controller;
import org.erachain.core.BlockChain;
import org.erachain.datachain.DCSet;
import org.erachain.network.message.*;
import org.erachain.utils.MonitoredThread;
import org.json.simple.JSONObject;
import org.mapdb.Fun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class MessagesProcessor extends MonitoredThread {

    private final static boolean USE_MONITOR = true;
    private static final boolean LOG_UNCONFIRMED_PROCESS = BlockChain.DEVELOP_USE? false : false;
    private boolean runned;

    private Network network;
    private static final Logger LOGGER = LoggerFactory.getLogger(MessagesProcessor.class);

    private static final int QUEUE_LENGTH = BlockChain.DEVELOP_USE ? 100 : 100;
    BlockingQueue<Message> blockingQueue = new ArrayBlockingQueue<Message>(QUEUE_LENGTH);

    private long unconfigmedMessageTimingAverage;

    public MessagesProcessor(Network network) {

        this.network = network;

        this.setName("Messages Processor[" + this.getId() + "]");

        this.start();
    }

    /**
     * @param message
     */
    public boolean offerMessage(Message message) {
        boolean result = blockingQueue.offer(message);
        if (!result) {
            this.network.missedMessages.incrementAndGet();
        }
        return result;
    }

    public void processMessage(Message message) {

        if (message == null || !runned)
            return;

        long timeCheck = System.nanoTime();
        long onMessageProcessTiming = timeCheck;

        switch (message.getType()) {
            case Message.TELEGRAM_GET_TYPE:
                // GET telegrams
                //address
                JSONObject address = ((TelegramGetMessage) message).getAddress();
                // create ansver
                ArrayList<String> addressFilter = new ArrayList<String>();
                Set keys = address.keySet();
                for (int i = 0; i < keys.size(); i++) {

                    addressFilter.add((String) address.get(i));
                }
                Message answer = MessageFactory.getInstance().createTelegramGetAnswerMessage(addressFilter);
                answer.setId(message.getId());
                // send answer
                message.getSender().offerMessage(answer);
                return;

            case Message.TELEGRAM_ANSWER_TYPE:
                // Answer to get telegrams
                ((TelegramAnswerMessage) message).saveToWallet();

                return;

            case Message.GET_HWEIGHT_TYPE:

                Fun.Tuple2<Integer, Long> HWeight = Controller.getInstance().getBlockChain().getHWeightFull(DCSet.getInstance());
                if (HWeight == null)
                    HWeight = new Fun.Tuple2<Integer, Long>(-1, -1L);

                HWeightMessage response = (HWeightMessage) MessageFactory.getInstance().createHWeightMessage(HWeight);
                // CREATE RESPONSE WITH SAME ID
                response.setId(message.getId());

                timeCheck = System.currentTimeMillis() - timeCheck;
                if (timeCheck > 10) {
                    LOGGER.debug(message.getSender() + ": " + message + " solved by period: " + timeCheck);
                }

                //SEND BACK TO SENDER
                message.getSender().offerMessage(response);

                break;

            //GETPEERS
            case Message.GET_PEERS_TYPE:

                this.network.onMessagePeers(message.getSender(), message.getId());

                break;


            case Message.FIND_MYSELF_TYPE:

                FindMyselfMessage findMyselfMessage = (FindMyselfMessage) message;

                this.network.onMessageMySelf(message.getSender(), findMyselfMessage.getFoundMyselfID());

                break;

            //SEND TO CONTROLLER
            default:

                Controller.getInstance().onMessage(message);
                break;

        }

            onMessageProcessTiming = System.nanoTime() - onMessageProcessTiming;
        if (onMessageProcessTiming < 999999999999l) {
            // при переполнении может быть минус
            // в миеросекундах подсчет делаем
            onMessageProcessTiming /= 1000;
            this.unconfigmedMessageTimingAverage = ((this.unconfigmedMessageTimingAverage << 8)
                    + onMessageProcessTiming - this.unconfigmedMessageTimingAverage) >> 8;
        }

        return;
    }

    public void run() {

        runned = true;
        //Message message;
        while (runned) {
            try {
                processMessage(blockingQueue.take());
            } catch (OutOfMemoryError e) {
                LOGGER.error(e.getMessage(), e);
                Controller.getInstance().stopAll(56);
                return;
            } catch (IllegalMonitorStateException e) {
                break;
            } catch (InterruptedException e) {
                break;
            }

        }

        LOGGER.info("Messages Processor halted");
    }

    public void halt() {
        this.runned = false;
    }

}