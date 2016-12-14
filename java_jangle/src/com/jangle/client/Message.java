package com.jangle.client;

import java.net.MalformedURLException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import com.jangle.communicate.CommUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.image.Image;
import javafx.scene.web.WebView;
//import com.jangle.communicate.CommUtil.*;

/**
 * Created by Jess on 9/28/2016.
 */
public class Message {

	private int userID;
    private int messageID;
	private String messageContent;
	private long timeStamp;
	private int serverID;
	private int channelID;
    private boolean hasImg;
    private Image mImage;
    private WebView mWebView;
    private boolean isPlaying;

	public Message(int userID, String messageContent, long timeStamp, int serverID, int channelID, int messageID) {
		this.channelID = channelID;
		this.userID = userID;
		this.messageContent = messageContent;
		this.timeStamp = timeStamp;
		this.serverID = serverID;
        this.messageID = messageID;

        if (isImg()){
            hasImg = true;
            mImage = new Image(messageContent, 500, 250, true, true);
        }

        if (isYoutube()) {
            WebView webView = new WebView();
            String[] urls = messageContent.split(" ");
            webView.getEngine().load(urls[0] + "&autoplay=0");
            webView.setPrefSize(512, 288);
        }
	}

	public Message(int userID, String messageContent, int serverID, int channelID) {
		this.userID = userID;
		this.messageContent = messageContent;
		this.serverID = serverID;
		this.channelID = channelID;
		this.timeStamp = 0;

        if (isImg()){
            hasImg = true;
            mImage = new Image(messageContent, 500, 250, true, true);
        }
        if (isYoutube()) {
            WebView webView = new WebView();
            String[] urls = messageContent.split(" ");
            webView.getEngine().load(urls[0] + "&autoplay=0");
            webView.setPrefSize(512, 288);
        }
	}

	/**
	 * Generate a Message object from a byte array received from the server.
	 * This message is received in the form of a 17 opcode
	 * 
	 * @param data
	 *            the byte array received from the object. The byte array still
	 *            as the opcode in it
	 */
	public Message(byte[] data) {
		byte[] chan = new byte[4];
		byte[] user = new byte[4];
		byte[] server = new byte[4];
        byte[] messageID = new byte[4];
		byte[] time = new byte[4];
		byte[] content = new byte[data.length - 21];
		
		for (int i = 0; i < 4; i++) {
			server[i] = data[i + 1];
			chan[i] = data[i + 5];
			user[i] = data[i + 9];
			messageID[i] = data[i + 13];
            time[i] = data[i + 17];
		}

		content = Arrays.copyOfRange(data, 21, data.length);

		
		
		this.userID = CommUtil.byteToInt(user);
		this.channelID = CommUtil.byteToInt(chan);
		this.serverID = CommUtil.byteToInt(server);
		this.timeStamp = (long) CommUtil.byteToInt(time);
		this.messageContent = new String(content);
        this.messageID = CommUtil.byteToInt(messageID);
        this.isPlaying = false;

        if (isImg()){
            hasImg = true;
            mImage = new Image(messageContent, 500, 250, true, true);
        }
        if (isYoutube()) {
            String[] urls = messageContent.split(" ");
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    mWebView = new WebView();
                    mWebView.getEngine().load(urls[0] + "&autoplay=0");
                    mWebView.setPrefSize(512, 288);
                }
            });
            String[] ids = urls[0].split("=");
            mImage = new Image("https://img.youtube.com/vi/" + ids[1] +"/0.jpg");
        }

	}

	public Message() {
		this.channelID = 0;
		this.userID = 0;
		this.messageContent = null;
		this.timeStamp = 0;
		this.serverID = 0;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public String getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public int getServerID() {
		return serverID;
	}

	public void setServerID(int serverID) {
		this.serverID = serverID;
	}

	public int getChannelID() {
		return channelID;
	}

	public void setChannelID(int channelID) {
		this.channelID = channelID;
	}

	public String toString() {
        Date date = new Date(timeStamp * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");
		return userID + sdf.format(date) + "\n" + messageContent;
	}

    public boolean isHasImg() {
        return hasImg;
    }

    public Image getImage() {
        return mImage;
    }

    public int getMessageID() {
        return messageID;
    }

    /**
	 * Creates a byte array of this message, in the format required to send the
	 * message to the server.
	 * 
	 * @return the byte array to send to the server.
	 */
	public byte[] getByteArray() {

		byte[] ret = new byte[messageContent.length() + 13];
		byte[] serverid = CommUtil.intToByteArr(serverID);
		byte[] channelid = CommUtil.intToByteArr(channelID);
		byte[] userid = CommUtil.intToByteArr(userID);
		int j = 0;

		ret[0] = CommUtil.MESSAGE_TO_SERVER;

		for (int i = 0; i < 4; i++) {
			if (serverid.length > i) {
				ret[i + 1] = serverid[i];
			}
			else {
				ret[i + 1] = (byte) 0;
			}

			if (channelid.length > i) {
				ret[i + 5] = channelid[i];
			}
			else {
				ret[i + 5] = (byte) 0;
			}
			if (userid.length > i) {
				ret[i + 9] = userid[i];
			}
			else {
				ret[i + 9] = (byte) 0;
			}

		}

		for (int i = 0; i < messageContent.length(); i++) {
			ret[i + 13] = (byte) messageContent.charAt(i);
		}

		return ret;

	}

    public boolean isImg() {
        return messageContent.contains("http://") && (messageContent.contains(".png") || messageContent.contains(".jpg") || messageContent.contains(".gif") || messageContent.contains("jpeg") || messageContent.contains(".bmp"));
    }

    public boolean isYoutube() {
        return messageContent.startsWith("https://www.youtube.com/watch") || messageContent.startsWith("https://youtu.be");
    }

    public WebView getWebView() {
        return mWebView;
    }

    public String getTime() {
        Date date = new Date(timeStamp * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");
        return sdf.format(date);
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}