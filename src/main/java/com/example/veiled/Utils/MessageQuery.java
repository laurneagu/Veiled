package com.example.veiled.Utils;

import com.example.veiled.MessageViewer.MessageViewer;
import com.example.veiled.Utils.DatabaseTable.Message;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Laur on 10/27/2014.
 */
public class MessageQuery implements TableQueryCallback {
    List<Message> m_messages;
    MessageViewer m_activity;

    public MessageQuery(MessageViewer currentActivity){
        m_messages = new ArrayList<Message>();
        m_activity = currentActivity;
    }

    @Override
    public void onCompleted(List list, int i, Exception e, ServiceFilterResponse serviceFilterResponse) {
        m_messages = list;

        m_activity.AddMessages(m_messages);
    }
}
